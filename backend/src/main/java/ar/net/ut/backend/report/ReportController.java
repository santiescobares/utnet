package ar.net.ut.backend.report;

import ar.net.ut.backend.Global;
import ar.net.ut.backend.report.dto.ReportCreateDTO;
import ar.net.ut.backend.report.dto.ReportDTO;
import ar.net.ut.backend.report.dto.ReportResolveDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Global.API_VERSION_PATH + "/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<ReportDTO> createReport(@RequestBody @Valid ReportCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reportService.createReport(dto));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('CONTRIBUTOR_2', 'CONTRIBUTOR_3', 'ADMINISTRATOR')")
    public ResponseEntity<Page<ReportDTO>> getReports(
            @RequestParam(defaultValue = "UNRESOLVED") Report.Status status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(reportService.getReports(status, pageable));
    }

    @GetMapping("/me")
    public ResponseEntity<Page<ReportDTO>> getMyReports(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(reportService.getMyReports(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CONTRIBUTOR_2', 'CONTRIBUTOR_3', 'ADMINISTRATOR')")
    public ResponseEntity<ReportDTO> getReportById(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.getReportById(id));
    }

    @PostMapping("/{id}/vote/in-favor")
    @PreAuthorize("hasAnyAuthority('CONTRIBUTOR_2', 'CONTRIBUTOR_3', 'ADMINISTRATOR')")
    public ResponseEntity<ReportDTO> voteInFavor(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.voteOnReport(id, true));
    }

    @PostMapping("/{id}/vote/against")
    @PreAuthorize("hasAnyAuthority('CONTRIBUTOR_2', 'CONTRIBUTOR_3', 'ADMINISTRATOR')")
    public ResponseEntity<ReportDTO> voteAgainst(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.voteOnReport(id, false));
    }

    @PostMapping("/{id}/resolve")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<ReportDTO> resolveReport(
            @PathVariable Long id,
            @RequestBody @Valid ReportResolveDTO dto
    ) {
        return ResponseEntity.ok(reportService.resolveReport(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
}
