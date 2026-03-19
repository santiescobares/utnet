package ar.net.ut.backend.forum.event;

import ar.net.ut.backend.forum.entity.ForumTopic;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class ForumTopicEvent {

    private final ForumTopic forumTopic;
}
