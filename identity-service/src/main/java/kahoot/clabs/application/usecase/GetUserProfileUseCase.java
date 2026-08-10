package kahoot.clabs.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import kahoot.clabs.application.dto.UserProfileResponse;
import kahoot.clabs.application.port.read.UserReadPort;
import kahoot.clabs.application.query.GetUserProfileQuery;
import kahoot.clabs.domain.exception.UserNotFoundException;

@ApplicationScoped
public class GetUserProfileUseCase {

    private final UserReadPort userReadPort;

    public GetUserProfileUseCase(UserReadPort userReadPort) {
        this.userReadPort = userReadPort;
    }

    public UserProfileResponse execute(GetUserProfileQuery query) {
        return userReadPort.findById(query.userId())
                .map(UserProfileResponse::from)
                .orElseThrow(() -> new UserNotFoundException(query.userId()));
    }
}
