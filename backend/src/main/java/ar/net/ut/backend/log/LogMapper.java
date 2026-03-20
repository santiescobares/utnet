package ar.net.ut.backend.log;

import ar.net.ut.backend.log.dto.LogDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LogMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(
            target = "userFullName",
            expression = "java(log.getUser().getFirstName() + \" \" + log.getUser().getLastName())"
    )
    LogDTO toDTO(Log log);
}
