package ar.net.ut.backend.user.service;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.exception.impl.ResourceNotFoundException;
import ar.net.ut.backend.user.mapper.UserContributionMapper;
import ar.net.ut.backend.user.dto.contribution.UserContributionDTO;
import ar.net.ut.backend.user.UserContribution;
import ar.net.ut.backend.user.event.contribution.UserContributionCreateEvent;
import ar.net.ut.backend.user.event.contribution.UserContributionDeleteEvent;
import ar.net.ut.backend.user.repository.UserContributionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserContributionService {

    private final UserService userService;

    private final UserContributionRepository contributionRepository;

    private final UserContributionMapper contributionMapper;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public UserContributionDTO createContribution(ResourceType resourceType, String resourceId, int awardedPoints) {
        UserContribution contribution = new UserContribution();
        contribution.setUser(userService.getCurrentUser());
        contribution.setResourceType(resourceType);
        contribution.setResourceId(resourceId);
        contribution.setAwardedPoints(awardedPoints);

        contributionRepository.save(contribution);

        eventPublisher.publishEvent(new UserContributionCreateEvent(contribution));

        return contributionMapper.toDTO(contribution);
    }

    @Transactional
    public void deleteContribution(Long id) {
        UserContribution contribution = contributionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.USER_CONTRIBUTION, "id", Long.toString(id)));

        contributionRepository.delete(contribution);

        eventPublisher.publishEvent(new UserContributionDeleteEvent(contribution));
    }
}
