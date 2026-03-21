package ar.net.ut.backend.forum.event.topic;

import ar.net.ut.backend.forum.ForumTopic;

public class ForumTopicCreateEvent extends ForumTopicEvent {

    public ForumTopicCreateEvent(ForumTopic forumTopic) {
        super(forumTopic);
    }
}
