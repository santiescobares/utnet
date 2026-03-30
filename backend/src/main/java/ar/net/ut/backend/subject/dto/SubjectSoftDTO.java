package ar.net.ut.backend.subject.dto;

import ar.net.ut.backend.career.dto.CareerSoftDTO;

import java.util.List;

public record SubjectSoftDTO(
        Long id,
        String name,
        String shortName,
        String color,
        List<CareerSoftDTO> careers
) {
}
