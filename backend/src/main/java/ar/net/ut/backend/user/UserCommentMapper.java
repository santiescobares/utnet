package ar.net.ut.backend.user;

import ar.net.ut.backend.user.dto.comment.UserCommentDTO;
import ar.net.ut.backend.user.entity.UserComment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserCommentMapper {

    @Mapping(source = "resource.id", target = "resourceId")
    @Mapping(source = "postedBy.id", target = "postedById")
    UserCommentDTO toDTO(UserComment comment);

    List<UserCommentDTO> toDTOList(List<UserComment> comments);
}
