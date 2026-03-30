package ar.net.ut.backend.subject;

import ar.net.ut.backend.career.Career;
import ar.net.ut.backend.career.CareerMapper;
import ar.net.ut.backend.career.CareerRepository;
import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.exception.impl.ResourceNotFoundException;
import ar.net.ut.backend.subject.dto.SubjectCreateDTO;
import ar.net.ut.backend.subject.dto.SubjectDTO;
import ar.net.ut.backend.subject.dto.SubjectSoftDTO;
import ar.net.ut.backend.subject.dto.SubjectUpdateDTO;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {CareerMapper.class}
)
public abstract class SubjectMapper {

    @Autowired
    protected SubjectRepository subjectRepository;
    @Autowired
    protected CareerRepository careerRepository;

    @Mapping(source = "careerIds", target = "careers", qualifiedByName = "careerIdsToCareers")
    @Mapping(source = "correlativeIds", target = "correlatives", qualifiedByName = "subjectIdsToSubjectList")
    public abstract Subject createEntity(SubjectCreateDTO dto);

    public abstract SubjectDTO toDTO(Subject subject);

    public abstract SubjectSoftDTO toSoftDTO(Subject subject);

    public abstract List<SubjectDTO> toDTOList(List<Subject> subjects);

    @Mapping(target = "careers", ignore = true)
    @Mapping(target = "correlatives", ignore = true)
    public abstract void updateFromDTO(@MappingTarget Subject subject, SubjectUpdateDTO dto);

    @AfterMapping
    public void updateCollections(@MappingTarget Subject subject, SubjectUpdateDTO dto) {
        if (dto.careerIds() != null) {
            subject.setCareers(mapCareerIdsToCareers(dto.careerIds()));
        }
        if (dto.correlativeIds() != null) {
            subject.setCorrelatives(mapSubjectIdsToSubjectList(dto.correlativeIds()));
        }
    }

    @Named("subjectIdToSubject")
    public Subject mapSubjectIdToSubject(Long subjectId) {
        if (subjectId == null) return null;
        return subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.SUBJECT, "id", subjectId.toString()));
    }

    @Named("subjectIdsToSubjectList")
    public List<Subject> mapSubjectIdsToSubjectList(Collection<Long> subjectIds) {
        if (subjectIds == null) return null;
        return subjectRepository.findAllById(subjectIds);
    }

    @Named("subjectIdsToSubjectSet")
    public Set<Subject> mapSubjectIdsToSubjectSet(Collection<Long> subjectIds) {
        if (subjectIds == null) return null;
        return new HashSet<>(subjectRepository.findAllById(subjectIds));
    }

    @Named("careerIdsToCareers")
    protected List<Career> mapCareerIdsToCareers(List<Long> careerIds) {
        List<Career> careers = careerIds != null ? careerRepository.findAllById(careerIds) : null;
        if (careers == null || careers.isEmpty()) {
            throw new IllegalArgumentException("Careers can't be null or empty");
        }
        return careers;
    }
}
