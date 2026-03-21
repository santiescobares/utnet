package ar.net.ut.backend.forum.mapper;

import ar.net.ut.backend.Global;
import ar.net.ut.backend.forum.dto.thread.ForumThreadDTO;
import ar.net.ut.backend.forum.dto.thread.ForumThreadUpdateDTO;
import ar.net.ut.backend.forum.ForumThread;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ForumThreadMapper {

    @Mapping(target = "discussionId", source = "discussion.id")
    @Mapping(target = "postedById", source = "postedBy.id")
    @Mapping(target = "rootId", source = "root.id")
    @Mapping(target = "imageURLs", source = "imageKeys", qualifiedByName = "imageKeyToURL")
    ForumThreadDTO toDTO(ForumThread forumThread);

    void updateFromDTO(@MappingTarget ForumThread forumThread, ForumThreadUpdateDTO dto);

    @Named("imageKeyToURL")
    default List<String> mapImageKeysToImageURLs(List<String> imageKeys) {
        if (imageKeys == null) return null;
        return imageKeys.stream()
                .map(key -> Global.R2.PUBLIC_URL + "/" + key)
                .toList();
    }
}
