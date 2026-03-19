package ar.net.ut.backend;

import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class Global {

    public final String API_VERSION_PATH = "/v1";
    public final String APP_URL = "https://ut.net.ar";

    public final String ACCESS_TOKEN_COOKIE = "access_token";

    @AllArgsConstructor
    public enum R2 {
        PUBLIC_URL("https://pub-fa1fe6bf17ea4df8be4a643dc20d8669.r2.dev"),
        PROFILE_PICTURES_PATH("profile-pictures/");

        final String value;

        @Override
        public String toString() {
            return value;
        }
    }

    @AllArgsConstructor
    public enum RedisKeys {
        TOKEN_BLACKLIST("token_blacklist:"),
        FORCED_LOGOUT("forced_logout:");

        final String value;

        @Override
        public String toString() {
            return value;
        }
    }
}
