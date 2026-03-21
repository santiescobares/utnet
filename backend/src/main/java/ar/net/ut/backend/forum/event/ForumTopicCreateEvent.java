package ar.net.ut.backend.forum.event;

import ar.net.ut.backend.forum.ForumTopic;

public class ForumTopicCreateEvent extends ForumTopicEvent {

    public ForumTopicCreateEvent(ForumTopic forumTopic) {
        super(forumTopic);
    }
}
