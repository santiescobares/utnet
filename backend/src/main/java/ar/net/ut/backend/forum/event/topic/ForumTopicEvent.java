package ar.net.ut.backend.forum.event.topic;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.forum.ForumTopic;
import ar.net.ut.backend.log.Log;
import ar.net.ut.backend.model.event.LoggableEvent;
import ar.net.ut.backend.user.User;
import lombok.Getter;

@Getter
public abstract class ForumTopicEvent extends LoggableEvent<ForumTopic> {

    private final ForumTopic forumTopic;

    public ForumTopicEvent(User user, ForumTopic forumTopic, Log.Action action) {
        super(user, ResourceType.FORUM_TOPIC, forumTopic.getId().toString(), action);
        this.forumTopic = forumTopic;
    }

    @Override
    public ForumTopic getEntity() { return forumTopic; }
}
