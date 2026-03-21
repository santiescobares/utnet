package ar.net.ut.backend.forum.event.topic;

import ar.net.ut.backend.forum.ForumTopic;

public class ForumTopicUpdateEvent extends ForumTopicEvent {

    public ForumTopicUpdateEvent(ForumTopic forumTopic) {
        super(forumTopic);
    }
}
