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
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Page<ReportDTO>> getReports(
            @RequestParam(defaultValue = "UNRESOLVED") Report.Status status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(reportService.getReports(status, pageable));
    }

    @GetMapping("/me")
    public ResponseEntity<List<ReportDTO>> getMyReports() {
        return ResponseEntity.ok(reportService.getMyReports());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportDTO> getReportById(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.getReportById(id));
    }

    @PostMapping("/{id}/vote/favor")
    public ResponseEntity<ReportDTO> voteInFavor(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.voteOnReport(id, true));
    }

    @PostMapping("/{id}/vote/contra")
    public ResponseEntity<ReportDTO> voteAgainst(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.voteOnReport(id, false));
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<ReportDTO> resolveReport(
            @PathVariable Long id,
            @RequestBody @Valid ReportResolveDTO dto
    ) {
        return ResponseEntity.ok(reportService.resolveReport(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
}
