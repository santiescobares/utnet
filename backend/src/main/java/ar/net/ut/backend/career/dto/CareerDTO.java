package ar.net.ut.backend.career.dto;

public record CareerDTO(
        Long id,
        String name,
        char idCharacter,
        int sortPosition,
        String color
) {
}
