package ar.net.ut.backend.report;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.exception.impl.InvalidOperationException;
import ar.net.ut.backend.exception.impl.ResourceNotFoundException;
import ar.net.ut.backend.report.dto.ReportCreateDTO;
import ar.net.ut.backend.report.dto.ReportDTO;
import ar.net.ut.backend.report.dto.ReportResolveDTO;
import ar.net.ut.backend.report.event.ReportAcceptedEvent;
import ar.net.ut.backend.report.event.ReportCreateEvent;
import ar.net.ut.backend.report.event.ReportDeleteEvent;
import ar.net.ut.backend.report.event.ReportVoteEvent;
import ar.net.ut.backend.user.User;
import ar.net.ut.backend.user.UserInteraction;
import ar.net.ut.backend.user.enums.Role;
import ar.net.ut.backend.user.repository.UserInteractionRepository;
import ar.net.ut.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private static final int DEFAULT_REQUIRED_VOTES = 3;

    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;
    private final UserService userService;
    private final UserInteractionRepository userInteractionRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ReportDTO createReport(ReportCreateDTO dto) {
        User reporter = userService.getCurrentUser();

        if (reportRepository.existsByReporterIdAndResourceTypeAndResourceId(
                reporter.getId(), dto.resourceType(), dto.resourceId())) {
            throw new InvalidOperationException("Ya reportaste este contenido y el reporte está pendiente de resolución");
        }

        Report report = new Report();
        report.setReporter(reporter);
        report.setResourceType(dto.resourceType());
        report.setResourceId(dto.resourceId());
        report.setReason(dto.reason());
        report.setStatus(Report.Status.UNRESOLVED);
        report.setRequiredVotes(DEFAULT_REQUIRED_VOTES);

        reportRepository.save(report);
        eventPublisher.publishEvent(new ReportCreateEvent(report));

        return reportMapper.toDTO(report);
    }

    @Transactional(readOnly = true)
    public Page<ReportDTO> getReports(Report.Status status, Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        assertCanView(currentUser);

        return reportRepository.findAllByStatus(status, pageable)
                .map(reportMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public ReportDTO getReportById(Long id) {
        User currentUser = userService.getCurrentUser();
        assertCanView(currentUser);

        return reportMapper.toDTO(getById(id));
    }

    @Transactional(readOnly = true)
    public List<ReportDTO> getMyReports() {
        User currentUser = userService.getCurrentUser();

        return reportRepository.findAllByReporterId(currentUser.getId())
                .stream()
                .map(reportMapper::toDTO)
                .toList();
    }

    @Transactional
    public ReportDTO voteOnReport(Long id, boolean inFavor) {
        User voter = userService.getCurrentUser();
        assertCanView(voter);

        Report report = getById(id);

        if (report.getStatus() != Report.Status.UNRESOLVED) {
            throw new InvalidOperationException("No se puede votar en un reporte que ya fue resuelto");
        }

        if (report.getReporter().getId().equals(voter.getId())) {
            throw new InvalidOperationException("No podés votar tu propio reporte");
        }

        if (hasAlreadyVoted(voter, id)) {
            throw new InvalidOperationException("Ya votaste en este reporte");
        }

        UserInteraction interaction = new UserInteraction();
        interaction.setUser(voter);
        interaction.setType(inFavor ? UserInteraction.Type.VOTED_IN_FAVOR : UserInteraction.Type.VOTED_AGAINST);
        interaction.setResourceType(ResourceType.REPORT);
        interaction.setResourceId(id.toString());
        userInteractionRepository.save(interaction);

        if (inFavor) {
            report.setVotesInFavor(report.getVotesInFavor() + 1);
        } else {
            report.setVotesAgainst(report.getVotesAgainst() + 1);
        }
        reportRepository.save(report);

        eventPublisher.publishEvent(new ReportVoteEvent(report, voter));

        if (report.getVotesInFavor() >= report.getRequiredVotes()) {
            report.setStatus(Report.Status.RESOLVED_ACCEPTED);
            reportRepository.save(report);
            eventPublisher.publishEvent(new ReportAcceptedEvent(report));
        }

        return reportMapper.toDTO(report);
    }

    @Transactional
    public ReportDTO resolveReport(Long id, ReportResolveDTO dto) {
        User currentUser = userService.getCurrentUser();
        assertCanResolve(currentUser);

        if (dto.resolution() == Report.Status.UNRESOLVED) {
            throw new InvalidOperationException("La resolución debe ser RESOLVED_ACCEPTED o RESOLVED_DENIED");
        }

        Report report = getById(id);

        if (report.getStatus() != Report.Status.UNRESOLVED) {
            throw new InvalidOperationException("Este reporte ya fue resuelto");
        }

        report.setStatus(dto.resolution());
        reportRepository.save(report);

        if (dto.resolution() == Report.Status.RESOLVED_ACCEPTED) {
            eventPublisher.publishEvent(new ReportAcceptedEvent(report));
        }

        return reportMapper.toDTO(report);
    }

    @Transactional
    public void deleteReport(Long id) {
        User currentUser = userService.getCurrentUser();
        assertCanResolve(currentUser);

        Report report = getById(id);

        reportRepository.delete(report);
        eventPublisher.publishEvent(new ReportDeleteEvent(report));
    }

    public Report getById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.REPORT, "id", Long.toString(id)));
    }

    private boolean hasAlreadyVoted(User voter, Long reportId) {
        String resourceId = reportId.toString();
        return userInteractionRepository.existsByUserIdAndTypeAndResourceTypeAndResourceId(
                voter.getId(), UserInteraction.Type.VOTED_IN_FAVOR, ResourceType.REPORT, resourceId)
                || userInteractionRepository.existsByUserIdAndTypeAndResourceTypeAndResourceId(
                voter.getId(), UserInteraction.Type.VOTED_AGAINST, ResourceType.REPORT, resourceId);
    }

    private void assertCanView(User user) {
        if (user.getRole().ordinal() < Role.CONTRIBUTOR_2.ordinal()) {
            throw new InvalidOperationException("Se requiere ser Contribuidor Nivel 2 para ver y votar reportes");
        }
    }

    private void assertCanResolve(User user) {
        if (user.getRole().ordinal() < Role.CONTRIBUTOR_3.ordinal()) {
            throw new InvalidOperationException("Se requiere ser Contribuidor Nivel 3 para resolver o eliminar reportes directamente");
        }
    }
}
