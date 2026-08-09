package kahoot.clabs.quiz.infrastructure.seed;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Well-known IDs shared logically across microservices for local demo seeding.
 * Duplicated per service on purpose (no shared kernel of business IDs).
 */
public final class SeedIds {

    private SeedIds() {
    }

    public static final UUID ORG_CLABS = UUID.fromString("11111111-1111-1111-1111-111111111111");

    public static final UUID ROLE_ADMIN = UUID.fromString("22222222-2222-2222-2222-222222222221");
    public static final UUID ROLE_OWNER = UUID.fromString("22222222-2222-2222-2222-222222222222");
    public static final UUID ROLE_RH = UUID.fromString("22222222-2222-2222-2222-222222222223");
    public static final UUID ROLE_MEMBER = UUID.fromString("22222222-2222-2222-2222-222222222224");

    public static final UUID USER_ADMIN = UUID.fromString("33333333-3333-3333-3333-333333333331");
    public static final UUID USER_OWNER = UUID.fromString("33333333-3333-3333-3333-333333333332");
    public static final UUID USER_RH = UUID.fromString("33333333-3333-3333-3333-333333333333");
    public static final UUID USER_MEMBER = UUID.fromString("33333333-3333-3333-3333-333333333334");

    public static final UUID QUIZ_JAVA = UUID.fromString("44444444-4444-4444-4444-444444444441");
    public static final UUID QUIZ_CULTURE = UUID.fromString("44444444-4444-4444-4444-444444444442");
    public static final UUID QUIZ_DEVOPS = UUID.fromString("44444444-4444-4444-4444-444444444443");

    public static final UUID SESSION_LOBBY = UUID.fromString("55555555-5555-5555-5555-555555555551");
    public static final UUID SESSION_FINISHED = UUID.fromString("55555555-5555-5555-5555-555555555552");
    public static final UUID SESSION_CANCELLED = UUID.fromString("55555555-5555-5555-5555-555555555553");

    /** Deterministic demo-user id from email (same value in every microservice SeedIds). */
    public static UUID demoUser(String email) {
        return UUID.nameUUIDFromBytes(("seed-user:" + email).getBytes(StandardCharsets.UTF_8));
    }
}
