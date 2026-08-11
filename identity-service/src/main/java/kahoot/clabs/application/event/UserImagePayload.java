package kahoot.clabs.application.event;

import java.util.UUID;

public record UserImagePayload(
        UUID id,
        String url,
        String type,
        String alt,
        String slug
) {
}
