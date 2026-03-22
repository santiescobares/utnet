package ar.net.ut.backend.forum.mapper;

import ar.net.ut.backend.forum.dto.thread.ForumThreadDTO;
import ar.net.ut.backend.forum.dto.thread.ForumThreadUpdateDTO;
import ar.net.ut.backend.forum.ForumThread;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ForumThreadMapper {

    @Mapping(target = "discussionId", source = "discussion.id")
    @Mapping(target = "postedById", source = "postedBy.id")
    @Mapping(target = "rootId", source = "root.id")
    ForumThreadDTO toDTO(ForumThread forumThread);

    void updateFromDTO(@MappingTarget ForumThread forumThread, ForumThreadUpdateDTO dto);
}
