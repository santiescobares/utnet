package ar.net.ut.backend.studyrecord;

import ar.net.ut.backend.studyrecord.dto.StudyRecordDTO;
import ar.net.ut.backend.studyrecord.dto.StudyRecordUpdateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StudyRecordMapper {

    @Mapping(source = "createdBy.id", target = "createdById")
    @Mapping(source = "subject.id", target = "subjectId")
    @Mapping(source = "type.color", target = "typeColor")
    StudyRecordDTO toDTO(StudyRecord studyRecord);

    void updateFromDTO(@MappingTarget StudyRecord studyRecord, StudyRecordUpdateDTO dto);
}
