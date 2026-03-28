package ar.net.ut.backend.studyrecord;

import ar.net.ut.backend.Global;
import ar.net.ut.backend.config.S3Config;
import ar.net.ut.backend.context.RequestContextData;
import ar.net.ut.backend.context.RequestContextHolder;
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
import ar.net.ut.backend.util.FileUtil;
import ar.net.ut.backend.util.RandomUtil;
import ar.net.ut.backend.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StudyRecordService {

    private static final Set<String> ALLOWED_FILE_FORMATS = Set.of("pdf", "doc", "docx", "png", "jpg", "jpeg");
    private static final long MAX_FILE_SIZE = 31_457_280; // In bytes

    private final SubjectService subjectService;
    private final UserService userService;
    private final StorageService storageService;

    private final StudyRecordRepository studyRecordRepository;

    private final StudyRecordMapper studyRecordMapper;

    private final ApplicationEventPublisher eventPublisher;

    private final S3Config s3Config;

    public StudyRecordDTO createStudyRecord(StudyRecordCreateDTO dto, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File can't be null or empty");
        }

        User currentUser = userService.getCurrentUser();
        assertCanCreate(currentUser);

        FileUtil.validateExtension(file, ALLOWED_FILE_FORMATS);
        FileUtil.validateSize(file, MAX_FILE_SIZE);

        Subject subject = subjectService.getById(dto.subjectId());

        StudyRecord record = new StudyRecord();
        record.setCreatedBy(currentUser);
        record.setSubject(subject);
        record.setTitle(dto.title());
        record.setSlug(generateUniqueSlug(dto.title()));
        record.setDescription(dto.description());
        record.setTags(dto.tags());

        String resourceKey = storageService.uploadFile(file, s3Config.getPrivateBucket(), Global.R2.STUDY_RECORDS_PATH.toString());
        record.setResourceKey(resourceKey);

        studyRecordRepository.save(record);

        eventPublisher.publishEvent(new StudyRecordCreateEvent(record));

        return studyRecordMapper.toDTO(record);
    }

    @Transactional
    public StudyRecordDTO updateStudyRecord(Long id, StudyRecordUpdateDTO dto) {
        StudyRecord record = getById(id);
        assertCanManage(record);

        studyRecordMapper.updateFromDTO(record, dto);
        studyRecordRepository.save(record);

        eventPublisher.publishEvent(new StudyRecordUpdateEvent(record));

        return studyRecordMapper.toDTO(record);
    }

    @Transactional
    public void deleteStudyRecord(Long id) {
        StudyRecord record = getById(id);
        assertCanManage(record);

        studyRecordRepository.delete(record);

        eventPublisher.publishEvent(new StudyRecordDeleteEvent(record));
    }

    @Transactional(readOnly = true)
    public StudyRecordDTO getStudyRecordById(Long id) {
        return studyRecordMapper.toDTO(getById(id));
    }

    @Transactional(readOnly = true)
    public StudyRecordDTO getStudyRecordBySlug(String slug) {
        Optional<StudyRecord> studyRecord = RequestContextHolder.getCurrentSession().role() == Role.ADMINISTRATOR
                ? studyRecordRepository.findBySlug(slug)
                : studyRecordRepository.findBySlugAndHiddenFalse(slug);
        if (studyRecord.isEmpty()) {
            throw new ResourceNotFoundException(ResourceType.STUDY_RECORD, "slug", slug);
        }
        return studyRecordMapper.toDTO(studyRecord.get());
    }

    @Transactional(readOnly = true)
    public Page<StudyRecordDTO> searchStudyRecords(String query, Long subjectId, StudyRecord.Type type, Pageable pageable) {
        return studyRecordRepository.searchStudyRecords(
                query,
                subjectId,
                type,
                RequestContextHolder.getCurrentSession().role() == Role.ADMINISTRATOR,
                pageable
        ).map(studyRecordMapper::toDTO);
    }

    @Transactional
    public StudyRecordDownloadResponseDTO downloadStudyRecord(Long id) {
        StudyRecord record = getById(id);

        if (record.getResourceKey() == null) {
            throw new IllegalStateException("No downloadable resource found for that study record");
        }

        String downloadUrl = storageService.generateDownloadPresignedUrl(
                s3Config.getPrivateBucket(),
                record.getResourceKey(),
                Duration.ofMinutes(3)
        );

        record.setDownloads(record.getDownloads() + 1);

        studyRecordRepository.save(record);

        return new StudyRecordDownloadResponseDTO(downloadUrl);
    }

    public StudyRecord getById(Long id) {
        return studyRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.STUDY_RECORD, "id", Long.toString(id)));
    }

    public StudyRecord getBySlug(String slug) {
        return studyRecordRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.STUDY_RECORD, "slug", slug));
    }

    private String generateUniqueSlug(String title) {
        String baseSlug = StringUtil.normalize(title);
        if (!studyRecordRepository.existsBySlug(baseSlug)) {
            return baseSlug;
        }
        return baseSlug + "-" + RandomUtil.randomHexString().substring(0, 6);
    }

    private void assertCanCreate(User user) {
        if (user.getRole().ordinal() < Role.CONTRIBUTOR_1.ordinal()) {
            throw new InvalidOperationException("You must be a Level 1 Contributor in order to upload study records");
        }
    }

    private void assertCanManage(StudyRecord record) {
        RequestContextData session = RequestContextHolder.getCurrentSession();
        if (!record.getCreatedBy().getId().equals(session.userId()) && session.role().ordinal() < Role.CONTRIBUTOR_3.ordinal()) {
            throw new InvalidOperationException("You don't have permission to edit that study record");
        }
    }
}
