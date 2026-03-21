package ar.net.ut.backend.career;

import ar.net.ut.backend.career.dto.CareerCreateDTO;
import ar.net.ut.backend.career.dto.CareerDTO;
import ar.net.ut.backend.career.dto.CareerUpdateDTO;
import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.exception.impl.ResourceNotFoundException;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class CareerMapper {

    @Autowired
    protected CareerRepository careerRepository;

    protected abstract Career createEntity(CareerCreateDTO dto);

    protected abstract CareerDTO toDTO(Career career);

    protected abstract List<CareerDTO> toDTOList(List<Career> careers);

    protected abstract void updateFromDTO(@MappingTarget Career career, CareerUpdateDTO dto);

    @Named("careerIdToCareer")
    public Career mapCareerIdToCareer(Long careerId) {
        if (careerId == null) return null;
        return careerRepository.findById(careerId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.CAREER, "id", careerId.toString()));
    }
}
