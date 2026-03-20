package ar.net.ut.backend.user.listener;

import ar.net.ut.backend.career.event.CareerDeleteEvent;
import ar.net.ut.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserListener {

    private final UserRepository userRepository;

    @EventListener
    public void onCareerDelete(CareerDeleteEvent event) {
        userRepository.unlinkUsersFromCareer(event.getCareer().getId());
    }
}
