package ar.net.ut.backend.subject.dto;

import ar.net.ut.backend.career.dto.CareerDTO;

import java.util.List;

public record SubjectDTO(
        Long id,
        String name,
        String shortName,
        String color,
        int sortPosition,
        List<CareerDTO> careers,
        List<SubjectDTO> correlatives
) {
}
