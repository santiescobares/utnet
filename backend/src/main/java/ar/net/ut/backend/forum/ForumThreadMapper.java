package ar.net.ut.backend.forum;

import ar.net.ut.backend.forum.dto.ForumThreadDTO;
import ar.net.ut.backend.forum.dto.ForumThreadUpdateDTO;
import ar.net.ut.backend.forum.entity.ForumThread;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ForumThreadMapper {

    @Mapping(target = "forumId", source = "forum.id")
    @Mapping(target = "postedById", source = "postedBy.id")
    @Mapping(target = "rootId", source = "root.id")
    ForumThreadDTO toDTO(ForumThread forumThread);

    @Mapping(target = "forum", ignore = true)
    @Mapping(target = "postedBy", ignore = true)
    @Mapping(target = "root", ignore = true)
    void updateFromDTO(@MappingTarget ForumThread forumThread, ForumThreadUpdateDTO dto);
}
