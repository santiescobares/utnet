package ar.net.ut.backend.forum.event.topic;

import ar.net.ut.backend.forum.ForumTopic;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class ForumTopicEvent {

    private final ForumTopic forumTopic;
}
