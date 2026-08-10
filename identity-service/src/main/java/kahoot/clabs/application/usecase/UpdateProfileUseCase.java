package kahoot.clabs.application.usecase;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import kahoot.clabs.application.command.UpdateProfileCommand;
import kahoot.clabs.application.dto.UserProfileResponse;
import kahoot.clabs.application.port.AssetsStoragePort;
import kahoot.clabs.application.port.write.UserProjectionPort;
import kahoot.clabs.application.readmodel.UserReadModels;
import kahoot.clabs.domain.aggregate.Role;
import kahoot.clabs.domain.aggregate.User;
import kahoot.clabs.domain.entity.UserImages;
import kahoot.clabs.domain.exception.UserNotFoundException;
import kahoot.clabs.domain.repository.RoleRepository;
import kahoot.clabs.domain.repository.UserRepository;
import kahoot.clabs.domain.shared.DomainException;
import kahoot.clabs.domain.valueobject.UserProfile;

@ApplicationScoped
public class UpdateProfileUseCase {

    private static final int MAX_IMAGE_SIZE_BYTES = 5 * 1024 * 1024;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AssetsStoragePort avatarStoragePort;
    private final UserProjectionPort userProjectionPort;

    public UpdateProfileUseCase(
            UserRepository userRepository,
            RoleRepository roleRepository,
            AssetsStoragePort avatarStoragePort,
            UserProjectionPort userProjectionPort) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.avatarStoragePort = avatarStoragePort;
        this.userProjectionPort = userProjectionPort;
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

        User saved = userRepository.save(user);
        userProjectionPort.save(UserReadModels.from(saved, resolveRole(saved)));
        return UserProfileResponse.from(saved);
    }

    private Role resolveRole(User user) {
        if (user.getRoleId() == null) {
            return null;
        }
        return roleRepository.findById(user.getRoleId()).orElse(null);
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
