package ar.net.ut.backend.log;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogScheduledTask {

    private final LogRepository logRepository;

    @Scheduled(cron = "0 10 4 * * *")
    @Transactional
    public void deleteOldLogs() {
        int deleted = logRepository.deleteByCreatedAtBefore(Instant.now().minus(3, ChronoUnit.DAYS));
        log.info("Old logs cleanup performed. {} logs deleted", deleted);
    }
}
