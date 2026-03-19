package ar.net.ut.backend.user;

import ar.net.ut.backend.user.dto.interaction.UserInteractionDTO;
import ar.net.ut.backend.user.entity.UserInteraction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserInteractionMapper {

    @Mapping(source = "user.id", target = "userId")
    UserInteractionDTO toDTO(UserInteraction interaction);

    List<UserInteractionDTO> toDTOList(List<UserInteraction> interactions);
}
