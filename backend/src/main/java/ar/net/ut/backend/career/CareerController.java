package ar.net.ut.backend.career;

import ar.net.ut.backend.Global;
import ar.net.ut.backend.career.dto.CareerCreateDTO;
import ar.net.ut.backend.career.dto.CareerDTO;
import ar.net.ut.backend.career.dto.CareerUpdateDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Global.API_VERSION_PATH + "/careers")
@RequiredArgsConstructor
public class CareerController {

    private final CareerService careerService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<CareerDTO> createCareer(@RequestBody @Valid CareerCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(careerService.createCareer(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<CareerDTO> updateCareer(@PathVariable Long id, @RequestBody @Valid CareerUpdateDTO dto) {
        return ResponseEntity.ok(careerService.updateCareer(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<Void> deleteCareer(@PathVariable Long id) {
        careerService.deleteCareer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<CareerDTO>> getAllCareers() {
        return ResponseEntity.ok(careerService.getAllCareersAsDTOs());
    }
}
