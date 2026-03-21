package ar.net.ut.backend.forum.event;

import ar.net.ut.backend.forum.ForumThread;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class ForumThreadEvent {

    private final ForumThread forumThread;
}
