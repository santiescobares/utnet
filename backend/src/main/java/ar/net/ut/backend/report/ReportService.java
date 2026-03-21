package ar.net.ut.backend.report;

import ar.net.ut.backend.context.RequestContextHolder;
import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.exception.impl.InvalidOperationException;
import ar.net.ut.backend.exception.impl.ResourceNotFoundException;
import ar.net.ut.backend.report.dto.ReportCreateDTO;
import ar.net.ut.backend.report.dto.ReportDTO;
import ar.net.ut.backend.report.dto.ReportResolveDTO;
import ar.net.ut.backend.report.event.*;
import ar.net.ut.backend.user.repository.UserInteractionRepository;
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
public class ReportService {

    public static final int DEFAULT_REQUIRED_VOTES = 1; // TODO make it proportional to user count with contributor lvl 2

    private final UserService userService;

    private final ReportRepository reportRepository;
    private final UserInteractionRepository userInteractionRepository;

    private final ReportMapper reportMapper;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ReportDTO createReport(ReportCreateDTO dto) {
        if (reportRepository.existsByReporterIdAndResourceTypeAndResourceId(
                RequestContextHolder.getCurrentSession().userId(), dto.resourceType(), dto.resourceId())
        ) {
            throw new InvalidOperationException("You have already reported that resource");
        }

        Report report = new Report();
        report.setReporter(userService.getCurrentUser());
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
        return reportRepository.findAllByStatus(status, pageable)
                .map(reportMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public ReportDTO getReportById(Long id) {
        return reportMapper.toDTO(getById(id));
    }

    @Transactional(readOnly = true)
    public Page<ReportDTO> getMyReports(Pageable pageable) {
        return reportRepository.findAllByReporterId(RequestContextHolder.getCurrentSession().userId(), pageable)
                .map(reportMapper::toDTO);
    }

    @Transactional
    public ReportDTO voteOnReport(Long id, boolean inFavor) {
        Report report = getById(id);
        if (report.getStatus() != Report.Status.UNRESOLVED) {
            throw new InvalidOperationException("That report was already resolved");
        }
        if (report.getReporter().getId().equals(RequestContextHolder.getCurrentSession().userId())) {
            throw new InvalidOperationException("You can't vote on your own report");
        }
        if (hasAlreadyVoted(id)) {
            throw new InvalidOperationException("You have already voted on that report");
        }

        if (inFavor) {
            report.setVotesInFavor(report.getVotesInFavor() + 1);
        } else {
            report.setVotesAgainst(report.getVotesAgainst() + 1);
        }

        reportRepository.save(report);

        eventPublisher.publishEvent(new ReportVoteEvent(report, inFavor));

        if (inFavor && report.getVotesInFavor() >= report.getRequiredVotes()) {
            report.setStatus(Report.Status.RESOLVED_ACCEPTED);
            reportRepository.save(report);

            eventPublisher.publishEvent(new ReportAcceptedEvent(report, false));
        } else if (report.getVotesAgainst() >= report.getRequiredVotes()) {
            report.setStatus(Report.Status.RESOLVED_DECLINED);
            reportRepository.save(report);

            eventPublisher.publishEvent(new ReportDeclinedEvent(report, false));
        }

        return reportMapper.toDTO(report);
    }

    @Transactional
    public ReportDTO resolveReport(Long id, ReportResolveDTO dto) {
        if (dto.resolution() == Report.Status.UNRESOLVED) {
            throw new InvalidOperationException("Invalid resolution type");
        }

        Report report = getById(id);
        if (report.getStatus() != Report.Status.UNRESOLVED) {
            throw new InvalidOperationException("That report was already resolved");
        }

        UUID userId = RequestContextHolder.getCurrentSession().userId();
        if (report.getReporter().getId().equals(userId)) {
            throw new InvalidOperationException("You can't resolve your own report");
        }

        report.setStatus(dto.resolution());
        reportRepository.save(report);

        eventPublisher.publishEvent(new ReportVoteEvent(report, dto.resolution() == Report.Status.RESOLVED_ACCEPTED));

        if (dto.resolution() == Report.Status.RESOLVED_ACCEPTED) {
            eventPublisher.publishEvent(new ReportAcceptedEvent(report, true));
        } else {
            eventPublisher.publishEvent(new ReportDeclinedEvent(report, true));
        }

        return reportMapper.toDTO(report);
    }

    @Transactional
    public void deleteReport(Long id) {
        Report report = getById(id);
        reportRepository.delete(report);

        eventPublisher.publishEvent(new ReportDeleteEvent(report));
    }

    public Report getById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.REPORT, "id", Long.toString(id)));
    }

    private boolean hasAlreadyVoted(Long reportId) {
        return userInteractionRepository.existsByUserIdAndResourceTypeAndResourceId(
                RequestContextHolder.getCurrentSession().userId(),
                ResourceType.REPORT, reportId.toString()
        );
    }
}
