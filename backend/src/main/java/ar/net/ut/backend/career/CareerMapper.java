package ar.net.ut.backend.career;

import ar.net.ut.backend.career.dto.CareerCreateDTO;
import ar.net.ut.backend.career.dto.CareerDTO;
import ar.net.ut.backend.career.dto.CareerUpdateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CareerMapper {

    Career createEntity(CareerCreateDTO dto);

    CareerDTO toDTO(Career career);

    List<CareerDTO> toDTOList(List<Career> careers);

    void updateFromDTO(@MappingTarget Career career, CareerUpdateDTO dto);
}
