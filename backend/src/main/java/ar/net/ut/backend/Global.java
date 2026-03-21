package ar.net.ut.backend;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class Global {

    public final String API_VERSION_PATH = "/v1";
    public final String APP_URL = "https://ut.net.ar";

    public final String ACCESS_TOKEN_COOKIE = "access_token";

    @AllArgsConstructor
    public enum R2 {
        PUBLIC_URL("https://pub-fa1fe6bf17ea4df8be4a643dc20d8669.r2.dev"),
        PROFILE_PICTURES_PATH("profile-pictures/"),
        STUDY_RECORDS_PATH("study-records/"),
        FORUM_THREAD_IMAGES_PATH("forum-thread-images/");

        final String value;

        @Override
        public String toString() {
            return value;
        }
    }

    @AllArgsConstructor
    public enum RedisKeys {
        TOKEN_BLACKLIST("token_blacklist:"),
        FORCED_LOGOUT("forced_logout:"),
        COURSE_EVENT_COOLDOWN("course_event_cooldown:");

        final String value;

        @Override
        public String toString() {
            return value;
        }
    }

    @Getter
    @AllArgsConstructor
    public enum Contributions {
        EVENT_CREATE_EDIT(1),
        STUDY_RECORD_UPLOAD(5),
        CONTENT_REPORT(2),
        REPORT_RESOLUTION(5);

        final int points;
    }
}
