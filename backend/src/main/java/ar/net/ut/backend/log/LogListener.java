package ar.net.ut.backend.log;

import ar.net.ut.backend.auth.event.login.PostLoginEvent;
import ar.net.ut.backend.auth.event.logout.PostLogoutEvent;
import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.user.User;
import ar.net.ut.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
// TODO add details
public class LogListener {

    private final UserService userService;
    private final LogService logService;

    @TransactionalEventListener
    public void onUserLogIn(PostLoginEvent event) {
        logService.createLog(event.getUser(), ResourceType.USER, event.getUser().getId().toString(), Log.Action.LOG_IN, null);
    }

    @TransactionalEventListener
    public void onUserLogOut(PostLogoutEvent event) {
        User user = userService.getCurrentUser();
        logService.createLog(user, ResourceType.USER, user.getId().toString(), Log.Action.LOG_OUT, null);
    }
}
