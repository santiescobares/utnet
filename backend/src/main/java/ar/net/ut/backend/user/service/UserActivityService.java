package ar.net.ut.backend.user.service;

import ar.net.ut.backend.context.RequestContextHolder;
import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.user.dto.activity.UserActivityDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static ar.net.ut.backend.Global.RedisKeys.USER_RECENT_ACTIVITY;

@Service
@RequiredArgsConstructor
public class UserActivityService {

    private static final ResourceType[] TRACKED_RESOURCES = new ResourceType[] {
            ResourceType.STUDY_RECORD, ResourceType.SUBJECT, ResourceType.COURSE, ResourceType.FORUM_THREAD, ResourceType.FORUM_TOPIC
    };
    private static final int MAX_HISTORY_PER_RESOURCE = 5;

    private final StringRedisTemplate redisTemplate;

    public void addUserRecentActivity(UserActivityDTO activity) {
        String key = String.format(
                USER_RECENT_ACTIVITY.toString(),
                RequestContextHolder.getCurrentSession().userId(),
                activity.resourceType()
        );

        redisTemplate.opsForZSet().add(key, activity.resourceId(), System.currentTimeMillis());
        redisTemplate.opsForZSet().removeRange(key, 0, -(MAX_HISTORY_PER_RESOURCE + 1));
        redisTemplate.expire(key, 7, TimeUnit.DAYS);
    }

    public List<UserActivityDTO> getUserRecentActivity() {
        List<UserActivityDTO> allActivities = new ArrayList<>();

        for (ResourceType type : TRACKED_RESOURCES) {
            String key = String.format(USER_RECENT_ACTIVITY.toString(), RequestContextHolder.getCurrentSession().userId(), type);
            Set<String> resourceIds = redisTemplate.opsForZSet().reverseRange(key, 0, -1);

            if (resourceIds != null) {
                resourceIds.forEach(id -> allActivities.add(new UserActivityDTO(type, id)));
            }
        }

        return allActivities;
    }
}
