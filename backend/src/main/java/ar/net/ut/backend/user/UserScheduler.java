package ar.net.ut.backend.user;

import ar.net.ut.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserScheduler {

    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 4 * * *")
    public void anonymizeDeletedUsers() {
        int result = userRepository.anonymizeDeletedUsers();
        log.info("User anonymization performed. {} entites affected", result);
    }

    @Scheduled(cron = "0 5 4 * * *")
    public void calculateMonthlyContributionAverages() {
        int result = userRepository.updateMonthlyAverageContributions();
        log.info("User contribution averages updated. {} entites affected", result);
    }
}
