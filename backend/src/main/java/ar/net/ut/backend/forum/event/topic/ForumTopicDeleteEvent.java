package ar.net.ut.backend.forum.event.topic;

import ar.net.ut.backend.forum.ForumTopic;

public class ForumTopicDeleteEvent extends ForumTopicEvent {

    public ForumTopicDeleteEvent(ForumTopic forumTopic) {
        super(forumTopic);
    }
}
