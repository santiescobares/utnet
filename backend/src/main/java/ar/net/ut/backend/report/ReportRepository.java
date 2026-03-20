package ar.net.ut.backend.report;

import ar.net.ut.backend.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, Long> {

    Page<Report> findAllByStatus(Report.Status status, Pageable pageable);

    List<Report> findAllByReporterId(UUID reporterId);

    boolean existsByReporterIdAndResourceTypeAndResourceId(UUID reporterId, ResourceType resourceType, String resourceId);
}
