package ar.net.ut.backend.studyrecord;

import ar.net.ut.backend.studyrecord.dto.StudyRecordDTO;
import ar.net.ut.backend.studyrecord.dto.StudyRecordUpdateDTO;
import ar.net.ut.backend.user.mapper.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {UserMapper.class}
)
public interface StudyRecordMapper {

    @Mapping(source = "subject.id", target = "subjectId")
    @Mapping(source = "type.color", target = "typeColor")
    StudyRecordDTO toDTO(StudyRecord studyRecord);

    @Mapping(target = "tags", ignore = true)
    void updateFromDTO(@MappingTarget StudyRecord studyRecord, StudyRecordUpdateDTO dto);
}
