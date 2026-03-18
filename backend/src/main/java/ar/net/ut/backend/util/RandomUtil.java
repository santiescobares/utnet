package ar.net.ut.backend.util;

import lombok.experimental.UtilityClass;

import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
public final class RandomUtil {

    public long randomLongId(long length) {
        if (length < 1 || length > 18) {
            throw new IllegalArgumentException("Length must be between 1 and 18");
        }
        return ThreadLocalRandom.current().nextLong(
                (long) Math.pow(10, length - 1),
                (long) Math.pow(10, length)
        );
    }
}
