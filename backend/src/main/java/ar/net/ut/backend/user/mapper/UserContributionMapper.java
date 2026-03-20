package ar.net.ut.backend.user.mapper;

import ar.net.ut.backend.user.dto.contribution.UserContributionDTO;
import ar.net.ut.backend.user.UserContribution;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserContributionMapper {

    UserContributionDTO toDTO(UserContribution contribution);
}
