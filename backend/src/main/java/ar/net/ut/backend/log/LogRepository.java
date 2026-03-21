package ar.net.ut.backend.log;

import ar.net.ut.backend.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.UUID;

public interface LogRepository extends JpaRepository<Log, Long> {

    Page<Log> findAllByResourceType(ResourceType resourceType, Pageable pageable);

    Page<Log> findAllByAction(Log.Action action, Pageable pageable);

    Page<Log> findAllByResourceTypeAndAction(ResourceType resourceType, Log.Action action, Pageable pageable);

    Page<Log> findAllByUserIdAndActionNotIn(UUID userId, Collection<Log.Action> actions, Pageable pageable);
}
