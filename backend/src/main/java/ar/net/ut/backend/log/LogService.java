package ar.net.ut.backend.log;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.exception.impl.InvalidOperationException;
import ar.net.ut.backend.exception.impl.ResourceNotFoundException;
import ar.net.ut.backend.log.dto.LogCreateDTO;
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

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;
    private final LogMapper logMapper;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public LogDTO createLog(LogCreateDTO dto) {
        User currentUser = userService.getCurrentUser();

        Log log = new Log();
        log.setUser(currentUser);
        log.setResourceType(dto.resourceType());
        log.setResourceId(dto.resourceId());
        log.setAction(dto.action());
        log.setDetails(dto.details());

        logRepository.save(log);

        eventPublisher.publishEvent(new LogCreateEvent(log));

        return logMapper.toDTO(log);
    }

    @Transactional(readOnly = true)
    public Page<LogDTO> getAllLogs(Pageable pageable, ResourceType resourceType, Log.Action action) {
        User currentUser = userService.getCurrentUser();
        assertIsAdmin(currentUser);

        if (resourceType != null && action != null) {
            return logRepository.findAllByResourceTypeAndAction(resourceType, action, pageable)
                    .map(logMapper::toDTO);
        } else if (resourceType != null) {
            return logRepository.findAllByResourceType(resourceType, pageable)
                    .map(logMapper::toDTO);
        } else if (action != null) {
            return logRepository.findAllByAction(action, pageable)
                    .map(logMapper::toDTO);
        }

        return logRepository.findAll(pageable)
                .map(logMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public LogDTO getLogById(Long id) {
        User currentUser = userService.getCurrentUser();
        assertIsAdmin(currentUser);

        return logMapper.toDTO(getById(id));
    }

    @Transactional(readOnly = true)
    public Page<LogDTO> getLogsByUser(UUID userId, Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        assertIsAdmin(currentUser);

        userService.getById(userId);

        return logRepository.findAllByUserId(userId, pageable)
                .map(logMapper::toDTO);
    }

    @Transactional
    public void deleteLog(Long id) {
        User currentUser = userService.getCurrentUser();
        assertIsAdmin(currentUser);

        Log log = getById(id);
        logRepository.delete(log);
    }

    Log getById(Long id) {
        return logRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.LOG, "id", id.toString()));
    }

    private void assertIsAdmin(User user) {
        if (user.getRole().ordinal() < Role.ADMINISTRATOR.ordinal()) {
            throw new InvalidOperationException("Se requiere ser Administrador para gestionar logs");
        }
    }
}
