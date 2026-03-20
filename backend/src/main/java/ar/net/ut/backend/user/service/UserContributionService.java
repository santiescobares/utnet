package ar.net.ut.backend.user.service;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.exception.impl.ResourceNotFoundException;
import ar.net.ut.backend.user.User;
import ar.net.ut.backend.user.event.contribution.UserContributionBulkCreateEvent;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserContributionService {

    private final UserService userService;

    private final UserContributionRepository contributionRepository;

    private final UserContributionMapper contributionMapper;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public UserContributionDTO createContribution(UUID userId, ResourceType resourceType, String resourceId, int awardedPoints) {
        User user = userService.getById(userId);

        UserContribution contribution = createContribution(user, resourceType, resourceId, awardedPoints);
        contributionRepository.save(contribution);

        eventPublisher.publishEvent(new UserContributionCreateEvent(contribution));

        return contributionMapper.toDTO(contribution);
    }

    @Transactional
    public List<UserContributionDTO> bulkCreateContributions(
            Collection<UUID> userIds,
            ResourceType resourceType,
            String resourceId,
            int awardedPoints
    ) {
        List<UserContribution> contributions = new ArrayList<>();
        for (UUID userId : userIds) {
            contributions.add(createContribution(userService.getReferenceById(userId), resourceType, resourceId, awardedPoints));
        }
        contributionRepository.saveAll(contributions);

        eventPublisher.publishEvent(new UserContributionBulkCreateEvent(contributions));

        return contributionMapper.toDTOList(contributions);
    }

    @Transactional
    public void deleteContribution(Long id) {
        UserContribution contribution = contributionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.USER_CONTRIBUTION, "id", Long.toString(id)));

        contributionRepository.delete(contribution);

        eventPublisher.publishEvent(new UserContributionDeleteEvent(contribution));
    }

    @Transactional
    public void deleteContribution(UUID userId, ResourceType resourceType, String resourceId) {
        contributionRepository.deleteByUserIdAndResourceTypeAndResourceId(userId, resourceType, resourceId);
    }

    private UserContribution createContribution(User user, ResourceType resourceType, String resourceId, int awardedPoints) {
        UserContribution contribution = new UserContribution();
        contribution.setUser(user);
        contribution.setResourceType(resourceType);
        contribution.setResourceId(resourceId);
        contribution.setAwardedPoints(awardedPoints);
        return contribution;
    }
}
