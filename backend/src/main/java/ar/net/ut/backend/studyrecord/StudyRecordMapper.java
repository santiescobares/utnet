package ar.net.ut.backend.studyrecord;

import ar.net.ut.backend.studyrecord.dto.StudyRecordCreateDTO;
import ar.net.ut.backend.studyrecord.dto.StudyRecordDTO;
import ar.net.ut.backend.studyrecord.dto.StudyRecordUpdateDTO;
import ar.net.ut.backend.subject.SubjectMapper;
import ar.net.ut.backend.user.mapper.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {UserMapper.class, SubjectMapper.class}
)
public interface StudyRecordMapper {

    @Mapping(source = "subjectIds", target = "subjects", qualifiedByName = "subjectIdsToSubjectSet")
    StudyRecord createEntity(StudyRecordCreateDTO dto);

    @Mapping(source = "type.color", target = "typeColor")
    StudyRecordDTO toDTO(StudyRecord studyRecord);

    @Mapping(source = "subjectIds", target = "subjects", qualifiedByName = "subjectIdsToSubjectSet")
    @Mapping(target = "tags", ignore = true)
    void updateFromDTO(@MappingTarget StudyRecord studyRecord, StudyRecordUpdateDTO dto);
}
