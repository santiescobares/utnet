package ar.net.ut.backend.career;

import ar.net.ut.backend.career.dto.CareerCreateDTO;
import ar.net.ut.backend.career.dto.CareerDTO;
import ar.net.ut.backend.career.dto.CareerUpdateDTO;
import ar.net.ut.backend.career.event.CareerCreateEvent;
import ar.net.ut.backend.career.event.CareerDeleteEvent;
import ar.net.ut.backend.career.event.CareerUpdateEvent;
import ar.net.ut.backend.course.repository.CourseRepository;
import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.exception.BackendException;
import ar.net.ut.backend.exception.impl.ResourceAlreadyExistsException;
import ar.net.ut.backend.exception.impl.ResourceNotFoundException;
import ar.net.ut.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CareerService {

    private final CareerRepository careerRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    private final CareerMapper careerMapper;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public CareerDTO createCareer(CareerCreateDTO dto) {
        String name = dto.name();
        if (careerRepository.existsByNameIgnoreCase(name)) {
            throw new ResourceAlreadyExistsException(ResourceType.CAREER, "name", name);
        }
        char idCharacter = dto.idCharacter();
        if (careerRepository.existsByIdCharacter(idCharacter)) {
            throw new ResourceAlreadyExistsException(ResourceType.CAREER, "idCharacter", Character.toString(idCharacter));
        }

        Career career = careerMapper.createEntity(dto);
        careerRepository.save(career);

        eventPublisher.publishEvent(new CareerCreateEvent(career));

        return careerMapper.toDTO(career);
    }

    @Transactional
    public CareerDTO updateCareer(Long id, CareerUpdateDTO dto) {
        Career career = getById(id);

        String name = dto.name();
        if (name != null && careerRepository.existsByNameIgnoreCase(name)) {
            throw new ResourceAlreadyExistsException(ResourceType.CAREER, "name", name);
        }
        Character idCharacter = dto.idCharacter();
        if (idCharacter != null && careerRepository.existsByIdCharacter(idCharacter)) {
            throw new ResourceAlreadyExistsException(ResourceType.CAREER, "idCharacter", idCharacter.toString());
        }

        careerMapper.updateFromDTO(career, dto);

        eventPublisher.publishEvent(new CareerUpdateEvent(career));

        return careerMapper.toDTO(career);
    }

    @Transactional
    public void deleteCareer(Long id) {
        Career career = getById(id);
        if (courseRepository.existsByCareerId(id)) {
            throw new BackendException(
                    "You must remove all courses within that career in order to delete it",
                    HttpStatus.CONFLICT,
                    "CAREER_DELETE_COURSE_CONFLICT"
            );
        }

        careerRepository.delete(career);
        userRepository.unlinkUsersFromCareer(id);

        eventPublisher.publishEvent(new CareerDeleteEvent(career));
    }

    @Transactional(readOnly = true)
    public List<CareerDTO> getAllCareersAsDTOs() {
        return careerRepository.findAll()
                .stream()
                .map(careerMapper::toDTO)
                .toList();
    }

    public Career getById(Long id) {
        return careerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.CAREER, "id", Long.toString(id)));
    }
}
