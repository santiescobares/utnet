package ar.net.ut.backend.forum.event;

import ar.net.ut.backend.forum.entity.ForumTopic;

public class ForumTopicUpdateEvent extends ForumTopicEvent {

    public ForumTopicUpdateEvent(ForumTopic forumTopic) {
        super(forumTopic);
    }
}
