package ar.net.ut.backend.log;

import ar.net.ut.backend.context.RequestContextHolder;
import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.exception.impl.InvalidOperationException;
import ar.net.ut.backend.exception.impl.ResourceNotFoundException;
import ar.net.ut.backend.log.dto.LogDTO;
import ar.net.ut.backend.log.event.LogCreateEvent;
import ar.net.ut.backend.user.User;
import ar.net.ut.backend.user.enums.Role;
import ar.net.ut.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class LogService {

    private final UserService userService;

    private final LogRepository logRepository;

    private final LogMapper logMapper;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void createLog(User user, ResourceType resourceType, String resourceId, Log.Action action, String details) {
        Log log = new Log();
        log.setUser(user);
        log.setResourceType(resourceType);
        log.setResourceId(resourceId);
        log.setAction(action);
        log.setDetails(details);

        logRepository.save(log);

        eventPublisher.publishEvent(new LogCreateEvent(log));
    }

    @Transactional(readOnly = true)
    public Page<LogDTO> getAllLogs(Pageable pageable, ResourceType resourceType, Log.Action action) {
        boolean isAdmin = RequestContextHolder.getCurrentSession().role() == Role.ADMINISTRATOR;
        if ((action == Log.Action.LOG_IN || action == Log.Action.LOG_OUT) && !isAdmin) {
            throw new InvalidOperationException("You don't have permission to watch those log types");
        }

        Function<Log, LogDTO> function = log -> isAdmin ? logMapper.toDTO(log, log.getDetails()) : logMapper.toDTO(log, null);

        if (resourceType != null && action != null) {
            return logRepository.findAllByResourceTypeAndAction(resourceType, action, pageable).map(function);
        } else if (resourceType != null) {
            return logRepository.findAllByResourceType(resourceType, pageable).map(function);
        } else if (action != null) {
            return logRepository.findAllByAction(action, pageable).map(function);
        }

        return logRepository.findAll(pageable).map(function);
    }

    @Transactional(readOnly = true)
    public LogDTO getLogById(Long id) {
        return logMapper.toFullDTO(getById(id));
    }

    @Transactional(readOnly = true)
    public Page<LogDTO> getLogsByUser(UUID userId, Pageable pageable) {
        userService.getById(userId);

        boolean isAdmin = RequestContextHolder.getCurrentSession().role() == Role.ADMINISTRATOR;
        return logRepository.findAllByUserIdAndActionNotIn(
                userId,
                isAdmin ? List.of(Log.Action.LOG_IN, Log.Action.LOG_OUT) : List.of(),
                pageable
        ).map(log -> isAdmin
                ? logMapper.toDTO(log, log.getDetails())
                : logMapper.toDTO(log, null)
        );
    }

    public Log getById(Long id) {
        return logRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.LOG, "id", id.toString()));
    }
}
