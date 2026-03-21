package ar.net.ut.backend.forum.event;

import ar.net.ut.backend.forum.ForumTopic;

public class ForumTopicUpdateEvent extends ForumTopicEvent {

    public ForumTopicUpdateEvent(ForumTopic forumTopic) {
        super(forumTopic);
    }
}
