package ar.net.ut.backend.punishment;

import ar.net.ut.backend.punishment.dto.PunishmentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PunishmentMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "punishedBy.id", target = "punishedById")
    @Mapping(
            target = "userFullName",
            expression = "java(punishment.getUser().getFirstName() + \" \" + punishment.getUser().getLastName())"
    )
    @Mapping(
            target = "active",
            expression = "java(punishment.getExpirationDate() != null && java.time.LocalDateTime.now().isBefore(punishment.getExpirationDate()))"
    )
    PunishmentDTO toDTO(Punishment punishment);
}
