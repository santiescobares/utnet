package ar.net.ut.backend.model;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.user.UserInteraction;

import java.util.HashMap;
import java.util.Map;

public interface Interactionable {

    Map<ResourceType, Interactionable> INTERACTIONABLE_RESOURCES = new HashMap<>();

    void onInteractionCreate(String resourceId, UserInteraction.Type interactionType);

    void onInteractionDelete(String resourceId, UserInteraction.Type interactionType);

    static Interactionable getByResource(ResourceType type) {
        return Interactionable.INTERACTIONABLE_RESOURCES.get(type);
    }

    static Interactionable getSafeByResource(ResourceType type) {
        Interactionable interactionable = getByResource(type);
        if (interactionable == null) {
            throw new IllegalArgumentException("Resource type " + type + " doesn't support interactions");
        }
        return interactionable;
    }
}
