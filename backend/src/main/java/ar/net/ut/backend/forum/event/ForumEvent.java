package ar.net.ut.backend.forum.event;

import ar.net.ut.backend.forum.Forum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class ForumEvent {

    private final Forum forum;
}
