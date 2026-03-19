package ar.net.ut.backend.forum.event;

import ar.net.ut.backend.forum.entity.ForumTopic;

public class ForumTopicCreateEvent extends ForumTopicEvent {

    public ForumTopicCreateEvent(ForumTopic forumTopic) {
        super(forumTopic);
    }
}
