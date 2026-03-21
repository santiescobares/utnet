package ar.net.ut.backend.forum.event.topic;

import ar.net.ut.backend.forum.ForumTopic;
import ar.net.ut.backend.log.Log;
import ar.net.ut.backend.user.User;

public class ForumTopicCreateEvent extends ForumTopicEvent {

    public ForumTopicCreateEvent(User user, ForumTopic forumTopic) {
        super(user, forumTopic, Log.Action.CREATE);
    }
}
