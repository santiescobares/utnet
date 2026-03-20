package ar.net.ut.backend.punishment;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.exception.impl.InvalidOperationException;
import ar.net.ut.backend.exception.impl.ResourceNotFoundException;
import ar.net.ut.backend.punishment.dto.PunishmentCreateDTO;
import ar.net.ut.backend.punishment.dto.PunishmentDTO;
import ar.net.ut.backend.punishment.event.PunishmentCreateEvent;
import ar.net.ut.backend.punishment.event.PunishmentDeleteEvent;
import ar.net.ut.backend.user.User;
import ar.net.ut.backend.user.enums.Role;
import ar.net.ut.backend.user.repository.UserRepository;
import ar.net.ut.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PunishmentService {

    private final PunishmentRepository punishmentRepository;
    private final PunishmentMapper punishmentMapper;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public PunishmentDTO createPunishment(PunishmentCreateDTO dto) {
        User currentUser = userService.getCurrentUser();
        assertIsAdmin(currentUser);

        User targetUser = userService.getById(dto.userId());

        if (punishmentRepository.existsByUserIdAndExpirationDateAfter(dto.userId(), LocalDateTime.now())) {
            throw new InvalidOperationException("Este usuario ya tiene una sanción activa");
        }

        Punishment punishment = new Punishment();
        punishment.setUser(targetUser);
        punishment.setReason(dto.reason());
        punishment.setExpirationDate(dto.expirationDate());
        punishment.setPunishedBy(currentUser);

        punishmentRepository.save(punishment);

        targetUser.setBannedUntil(dto.expirationDate().toInstant(ZoneOffset.UTC));
        userRepository.save(targetUser);

        eventPublisher.publishEvent(new PunishmentCreateEvent(punishment));

        return punishmentMapper.toDTO(punishment);
    }

    @Transactional(readOnly = true)
    public Page<PunishmentDTO> getAllPunishments(Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        assertIsAdmin(currentUser);

        return punishmentRepository.findAll(pageable)
                .map(punishmentMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public PunishmentDTO getPunishmentById(Long id) {
        User currentUser = userService.getCurrentUser();
        assertIsAdmin(currentUser);

        return punishmentMapper.toDTO(getById(id));
    }

    @Transactional(readOnly = true)
    public List<PunishmentDTO> getPunishmentsByUser(UUID userId) {
        User currentUser = userService.getCurrentUser();
        assertIsAdmin(currentUser);

        userService.getById(userId);

        return punishmentRepository.findAllByUserId(userId)
                .stream()
                .map(punishmentMapper::toDTO)
                .toList();
    }

    @Transactional
    public void deletePunishment(Long id) {
        User currentUser = userService.getCurrentUser();
        assertIsAdmin(currentUser);

        Punishment punishment = getById(id);
        User punishedUser = punishment.getUser();

        punishmentRepository.delete(punishment);
        punishmentRepository.flush();

        punishmentRepository
                .findFirstByUserIdAndExpirationDateAfterOrderByExpirationDateDesc(punishedUser.getId(), LocalDateTime.now())
                .ifPresentOrElse(
                        remaining -> {
                            punishedUser.setBannedUntil(remaining.getExpirationDate().toInstant(ZoneOffset.UTC));
                            userRepository.save(punishedUser);
                        },
                        () -> {
                            punishedUser.setBannedUntil(null);
                            userRepository.save(punishedUser);
                        }
                );

        eventPublisher.publishEvent(new PunishmentDeleteEvent(punishment));
    }

    Punishment getById(Long id) {
        return punishmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.PUNISHMENT, "id", id.toString()));
    }

    private void assertIsAdmin(User user) {
        if (user.getRole().ordinal() < Role.ADMINISTRATOR.ordinal()) {
            throw new InvalidOperationException("Se requiere ser Administrador para gestionar sanciones");
        }
    }
}
