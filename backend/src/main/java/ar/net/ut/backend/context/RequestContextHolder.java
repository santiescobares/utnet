package ar.net.ut.backend.context;

import java.util.UUID;

public final class RequestContextHolder {

    private static final ThreadLocal<UUID> currentUser = new ThreadLocal<>();

    public static void setCurrentUser(UUID userId) {
        currentUser.set(userId);
    }

    public static UUID getCurrentUser() {
        return currentUser.get();
    }

    public static void clear() {
        currentUser.remove();
    }
}
