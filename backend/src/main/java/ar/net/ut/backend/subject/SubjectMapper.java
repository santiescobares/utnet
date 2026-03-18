package ar.net.ut.backend.subject;

import ar.net.ut.backend.career.Career;
import ar.net.ut.backend.career.CareerRepository;
import ar.net.ut.backend.subject.dto.SubjectCreateDTO;
import ar.net.ut.backend.subject.dto.SubjectDTO;
import ar.net.ut.backend.subject.dto.SubjectUpdateDTO;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class SubjectMapper {

    @Autowired
    protected CareerRepository careerRepository;
    @Autowired
    protected SubjectRepository subjectRepository;

    @Mapping(source = "careerIds", target = "careers", qualifiedByName = "careerIdsToCareers")
    @Mapping(source = "correlativeIds", target = "correlatives", qualifiedByName = "subjectIdsToSubjects")
    public abstract Subject createEntity(SubjectCreateDTO dto);

    public abstract SubjectDTO toDTO(Subject subject);

    @Mapping(target = "careers", ignore = true)
    @Mapping(target = "correlatives", ignore = true)
    public abstract void updateFromDTO(@MappingTarget Subject subject, SubjectUpdateDTO dto);

    @AfterMapping
    protected void updateCollections(@MappingTarget Subject subject, SubjectUpdateDTO dto) {
        if (dto.careerIds() != null) {
            subject.setCareers(mapCareerIdsToCareers(dto.careerIds()));
        }
        if (dto.correlativeIds() != null) {
            subject.setCorrelatives(mapSubjectIdsToSubjects(dto.correlativeIds()));
        }
    }

    @Named("careerIdsToCareers")
    protected List<Career> mapCareerIdsToCareers(List<Long> careerIds) {
        List<Career> careers = careerIds != null ? careerRepository.findAllById(careerIds) : null;
        if (careers == null || careers.isEmpty()) {
            throw new IllegalArgumentException("Careers can't be null or empty");
        }
        return careers;
    }

    @Named("subjectIdsToSubjects")
    protected List<Subject> mapSubjectIdsToSubjects(List<Long> subjectIds) {
        if (subjectIds == null) return null;
        return subjectRepository.findAllById(subjectIds);
    }
}
