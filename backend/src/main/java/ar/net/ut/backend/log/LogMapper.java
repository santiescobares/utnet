package ar.net.ut.backend.log;

import ar.net.ut.backend.log.dto.LogDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LogMapper {

    @Mapping(source = "log.user.id", target = "userId")
    @Mapping(
            target = "userFullName",
            expression = "java(log.getUser().getFirstName() + \" \" + log.getUser().getLastName())"
    )
    @Mapping(source = "details", target = "details")
    LogDTO toDTO(Log log, String details);

    @Mapping(source = "log.user.id", target = "userId")
    @Mapping(
            target = "userFullName",
            expression = "java(log.getUser().getFirstName() + \" \" + log.getUser().getLastName())"
    )
    LogDTO toFullDTO(Log log);
}
