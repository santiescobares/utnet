package ar.net.ut.backend.report;

import ar.net.ut.backend.report.dto.ReportDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ReportMapper {

    @Mapping(source = "reporter.id", target = "reporterId")
    ReportDTO toDTO(Report report);
}
