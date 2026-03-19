package ar.net.ut.backend.forum.event;

import ar.net.ut.backend.forum.entity.ForumTopic;

public class ForumTopicDeleteEvent extends ForumTopicEvent {

    public ForumTopicDeleteEvent(ForumTopic forumTopic) {
        super(forumTopic);
    }
}
