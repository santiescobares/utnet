package ar.net.ut.backend.log;

import ar.net.ut.backend.auth.event.login.PostLoginEvent;
import ar.net.ut.backend.auth.event.logout.PostLogoutEvent;
import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.model.event.LoggableEvent;
import ar.net.ut.backend.user.User;
import ar.net.ut.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogListener {

    private final UserService userService;
    private final LogService logService;

    @EventListener
    public void onLoggableEvent(LoggableEvent<?> event) {
        logService.createLog(
                event.getUser(),
                event.getResourceType(),
                event.getResourceId(),
                event.getAction(),
                event.getEntity().toString()
        );
    }

    @EventListener
    public void onUserLogIn(PostLoginEvent event) {
        logService.createLog(event.getUser(), ResourceType.USER, event.getUser().getId().toString(), Log.Action.LOG_IN, null);
    }

    @EventListener
    public void onUserLogOut(PostLogoutEvent event) {
        User user = userService.getCurrentUser();
        logService.createLog(user, ResourceType.USER, user.getId().toString(), Log.Action.LOG_OUT, null);
    }
}
