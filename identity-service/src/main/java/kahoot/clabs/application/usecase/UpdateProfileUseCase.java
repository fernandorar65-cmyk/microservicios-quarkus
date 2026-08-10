package kahoot.clabs.application.usecase;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import kahoot.clabs.application.command.UpdateProfileCommand;
import kahoot.clabs.application.dto.UserProfileResponse;
import kahoot.clabs.application.port.AssetsStoragePort;
import kahoot.clabs.domain.aggregate.User;
import kahoot.clabs.domain.entity.UserImages;
import kahoot.clabs.domain.exception.UserNotFoundException;
import kahoot.clabs.domain.repository.UserRepository;
import kahoot.clabs.domain.shared.DomainException;
import kahoot.clabs.domain.valueobject.UserProfile;

@ApplicationScoped
public class UpdateProfileUseCase {

    private static final int MAX_IMAGE_SIZE_BYTES = 5 * 1024 * 1024;

    private final UserRepository userRepository;
    private final AssetsStoragePort avatarStoragePort;

    public UpdateProfileUseCase(UserRepository userRepository, AssetsStoragePort avatarStoragePort) {
        this.userRepository = userRepository;
        this.avatarStoragePort = avatarStoragePort;
    }

    @Transactional
    public UserProfileResponse execute(
            UUID userId,
            UpdateProfileCommand command,
            byte[] imageContent,
            String imageContentType,
            String imageOriginalFilename) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.updateProfile(UserProfile.builder()
                .phoneNumber(blankToNull(command.phoneNumber()))
                .birthDate(command.birthDate())
                .bio(blankToNull(command.bio()))
                .location(blankToNull(command.location()))
                .build());

        if (imageContent != null && imageContent.length > 0) {
            validateImage(imageContent, imageContentType);
            String key = "users/%s/avatar/%s%s".formatted(
                    user.getId(),
                    UUID.randomUUID(),
                    extension(imageOriginalFilename, imageContentType));
            String url = avatarStoragePort.upload(key, imageContent, imageContentType);
            user.upsertImage(url, UserImages.TYPE_PROFILE, "Profile avatar", UserImages.TYPE_PROFILE);
        }

        return UserProfileResponse.from(userRepository.save(user));
    }

    private void validateImage(byte[] content, String contentType) {
        if (content.length > MAX_IMAGE_SIZE_BYTES) {
            throw new DomainException("Avatar must be at most 5 MB");
        }
        if (!"image/jpeg".equals(contentType)
                && !"image/png".equals(contentType)
                && !"image/webp".equals(contentType)
                && !"image/gif".equals(contentType)) {
            throw new DomainException("Only JPEG, PNG, WebP, and GIF images are allowed");
        }
    }

    private String extension(String filename, String contentType) {
        if (filename != null && filename.lastIndexOf('.') >= 0) {
            return filename.substring(filename.lastIndexOf('.')).toLowerCase();
        }
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".gif";
        };
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
