package ar.net.ut.backend.user;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.exception.impl.ResourceNotFoundException;
import ar.net.ut.backend.user.dto.contribution.UserContributionCreateDTO;
import ar.net.ut.backend.user.dto.contribution.UserContributionDTO;
import ar.net.ut.backend.user.entity.User;
import ar.net.ut.backend.user.entity.UserContribution;
import ar.net.ut.backend.user.event.contribution.UserContributionCreateEvent;
import ar.net.ut.backend.user.event.contribution.UserContributionDeleteEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserContributionService {

    private final UserContributionRepository contributionRepository;
    private final UserContributionMapper contributionMapper;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public UserContributionDTO createContribution(UserContributionCreateDTO dto) {
        User currentUser = userService.getCurrentUser();

        UserContribution contribution = new UserContribution();
        contribution.setResourceType(dto.resourceType());
        contribution.setResourceId(dto.resourceId());
        contribution.setAwardedPoints(dto.awardedPoints());
        contribution.setUser(currentUser);

        contributionRepository.save(contribution);

        eventPublisher.publishEvent(new UserContributionCreateEvent(contribution));

        return contributionMapper.toDTO(contribution);
    }

    @Transactional(readOnly = true)
    public List<UserContributionDTO> getContributionsByUser(UUID userId) {
        userService.getById(userId);
        return contributionMapper.toDTOList(contributionRepository.findAllByUser_Id(userId));
    }

    @Transactional
    public void deleteContribution(Long id) {
        UserContribution contribution = contributionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.USER_CONTRIBUTION, "id", Long.toString(id)));

        contributionRepository.delete(contribution);

        eventPublisher.publishEvent(new UserContributionDeleteEvent(contribution));
    }
}
