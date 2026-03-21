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

    private final UserService userService;

    private final PunishmentRepository punishmentRepository;
    private final UserRepository userRepository;

    private final PunishmentMapper punishmentMapper;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public PunishmentDTO createPunishment(PunishmentCreateDTO dto) {
        User currentUser = userService.getCurrentUser();
        User targetUser = userService.getById(dto.userId());

        if (targetUser.equals(currentUser)) {
            throw new InvalidOperationException("You can't punish yourself");
        }
        if (targetUser.getRole() == Role.ADMINISTRATOR) {
            throw new InvalidOperationException("You can't punish that user");
        }
        if (punishmentRepository.existsByUserIdAndExpirationDateAfter(dto.userId(), LocalDateTime.now())) {
            throw new InvalidOperationException("That user already has an active punishment");
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
        return punishmentRepository.findAll(pageable).map(punishmentMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public PunishmentDTO getPunishmentById(Long id) {
        return punishmentMapper.toDTO(getById(id));
    }

    @Transactional(readOnly = true)
    public List<PunishmentDTO> getPunishmentsByUser(UUID userId) {
        userService.getById(userId);
        return punishmentMapper.toDTOList(punishmentRepository.findAllByUserId(userId));
    }

    @Transactional
    public void deletePunishment(Long id) {
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

    public Punishment getById(Long id) {
        return punishmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.PUNISHMENT, "id", id.toString()));
    }
}
