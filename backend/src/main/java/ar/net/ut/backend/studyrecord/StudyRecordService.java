package ar.net.ut.backend.studyrecord;

import ar.net.ut.backend.config.S3Config;
import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.exception.impl.InvalidOperationException;
import ar.net.ut.backend.exception.impl.ResourceNotFoundException;
import ar.net.ut.backend.service.StorageService;
import ar.net.ut.backend.studyrecord.dto.StudyRecordCreateDTO;
import ar.net.ut.backend.studyrecord.dto.StudyRecordDownloadResponseDTO;
import ar.net.ut.backend.studyrecord.dto.StudyRecordDTO;
import ar.net.ut.backend.studyrecord.dto.StudyRecordUpdateDTO;
import ar.net.ut.backend.studyrecord.event.StudyRecordCreateEvent;
import ar.net.ut.backend.studyrecord.event.StudyRecordDeleteEvent;
import ar.net.ut.backend.studyrecord.event.StudyRecordUpdateEvent;
import ar.net.ut.backend.subject.Subject;
import ar.net.ut.backend.subject.SubjectService;
import ar.net.ut.backend.user.User;
import ar.net.ut.backend.user.enums.Role;
import ar.net.ut.backend.user.service.UserService;
import ar.net.ut.backend.util.RandomUtil;
import ar.net.ut.backend.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StudyRecordService {

    private static final String STUDY_RECORDS_PATH = "study-records/";
    private static final Duration DOWNLOAD_URL_EXPIRATION = Duration.ofMinutes(15);
    private static final long MAX_FILE_SIZE = 52_428_800L; // 50 MB
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "doc", "docx", "png", "jpg", "jpeg");

    private final SubjectService subjectService;
    private final UserService userService;
    private final StudyRecordRepository studyRecordRepository;
    private final StudyRecordMapper studyRecordMapper;
    private final StorageService storageService;
    private final S3Config s3Config;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public StudyRecordDTO createStudyRecord(StudyRecordCreateDTO dto, MultipartFile file) {
        User currentUser = userService.getCurrentUser();
        assertCanCreate(currentUser);

        Subject subject = subjectService.getById(dto.subjectId());

        StudyRecord record = new StudyRecord();
        record.setCreatedBy(currentUser);
        record.setSubject(subject);
        record.setTitle(dto.title());
        record.setSlug(generateUniqueSlug(dto.title()));
        record.setDescription(dto.description());
        record.setTags(dto.tags());

        if (file != null && !file.isEmpty()) {
            validateFile(file);
            String resourceKey = storageService.uploadFile(file, s3Config.getPrivateBucket(), STUDY_RECORDS_PATH);
            record.setResourceKey(resourceKey);
        }

        studyRecordRepository.save(record);
        eventPublisher.publishEvent(new StudyRecordCreateEvent(record));

        return studyRecordMapper.toDTO(record);
    }

    @Transactional
    public StudyRecordDTO updateStudyRecord(Long id, StudyRecordUpdateDTO dto) {
        StudyRecord record = getById(id);
        User currentUser = userService.getCurrentUser();
        assertCanManage(currentUser, record);

        if (dto.title() != null && !dto.title().equals(record.getTitle())) {
            record.setSlug(generateUniqueSlug(dto.title()));
        }

        studyRecordMapper.updateFromDTO(record, dto);
        studyRecordRepository.save(record);
        eventPublisher.publishEvent(new StudyRecordUpdateEvent(record));

        return studyRecordMapper.toDTO(record);
    }

    @Transactional
    public void deleteStudyRecord(Long id) {
        StudyRecord record = getById(id);
        User currentUser = userService.getCurrentUser();
        assertCanManage(currentUser, record);

        studyRecordRepository.delete(record);
        eventPublisher.publishEvent(new StudyRecordDeleteEvent(record));
    }

    @Transactional(readOnly = true)
    public List<StudyRecordDTO> getStudyRecordsBySubject(Long subjectId) {
        subjectService.getById(subjectId);
        User currentUser = userService.getCurrentUser();

        List<StudyRecord> records = canSeeAllRecords(currentUser)
                ? studyRecordRepository.findBySubjectId(subjectId)
                : studyRecordRepository.findBySubjectIdAndHiddenFalse(subjectId);

        return records.stream()
                .map(studyRecordMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public StudyRecordDTO getStudyRecordById(Long id) {
        return studyRecordMapper.toDTO(getById(id));
    }

    @Transactional
    public StudyRecordDownloadResponseDTO downloadStudyRecord(Long id) {
        StudyRecord record = getById(id);

        if (record.getResourceKey() == null) {
            throw new IllegalStateException("Este material no tiene archivo adjunto");
        }

        String downloadUrl = storageService.generateDownloadPresignedUrl(
                s3Config.getPrivateBucket(),
                record.getResourceKey(),
                DOWNLOAD_URL_EXPIRATION
        );

        record.setDownloads(record.getDownloads() + 1);
        studyRecordRepository.save(record);

        return new StudyRecordDownloadResponseDTO(downloadUrl);
    }

    public StudyRecord getById(Long id) {
        return studyRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.STUDY_RECORD, "id", Long.toString(id)));
    }

    private String generateUniqueSlug(String title) {
        String baseSlug = StringUtil.normalize(title);
        if (!studyRecordRepository.existsBySlug(baseSlug)) {
            return baseSlug;
        }
        return baseSlug + "-" + RandomUtil.randomHexString().substring(0, 6);
    }

    private void validateFile(MultipartFile file) {
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (extension == null || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("Formato de archivo no permitido. Formatos aceptados: PDF, DOC, DOCX, PNG, JPG");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("El archivo no puede superar los 50 MB");
        }
    }

    private void assertCanCreate(User user) {
        if (user.getRole().ordinal() < Role.CONTRIBUTOR_1.ordinal()) {
            throw new InvalidOperationException("Se requiere ser Contribuidor Nivel 1 para subir material de estudio");
        }
    }

    private void assertCanManage(User user, StudyRecord record) {
        if (!record.getCreatedBy().getId().equals(user.getId())
                && user.getRole().ordinal() < Role.CONTRIBUTOR_3.ordinal()) {
            throw new InvalidOperationException("No tenés permiso para modificar este material");
        }
    }

    private boolean canSeeAllRecords(User user) {
        return user.getRole() == Role.CONTRIBUTOR_3 || user.getRole() == Role.ADMINISTRATOR;
    }
}
