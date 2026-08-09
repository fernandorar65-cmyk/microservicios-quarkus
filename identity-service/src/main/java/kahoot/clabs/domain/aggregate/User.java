package kahoot.clabs.domain.aggregate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import kahoot.clabs.domain.entity.UserImages;
import kahoot.clabs.domain.valueobject.Email;
import kahoot.clabs.domain.valueobject.FullName;
import kahoot.clabs.domain.valueobject.Password;
import kahoot.clabs.domain.valueobject.UserProfile;
import kahoot.clabs.domain.valueobject.UserStatus;
import kahoot.clabs.domain.shared.AggregateRoot;
import kahoot.clabs.domain.shared.DomainException;

/**
 * Identity of a person in the platform. This aggregate knows nothing about organizations:
 * membership lives in the organization bounded context.
 */
public class User extends AggregateRoot {

    private UUID roleId;

    private FullName fullName;
    private Email email;
    private Password password;
    private UserProfile profile;

    private UserStatus status;
    private final List<UserImages> images = new ArrayList<>();

    private LocalDateTime lastLogin;

    private User(UUID id, Email email, FullName fullName, Password password, LocalDateTime createdAt,
                 LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
        if (password == null) {
            throw new DomainException("Password is required");
        }
        this.email = email;
        this.fullName = fullName;
        this.password = password;
        this.profile = UserProfile.empty();
        this.status = UserStatus.ACTIVE;
    }

    /**
     * Creates a new user with an already-hashed password (Application hashes via PasswordHasher).
     */
    public static User create(
            String email,
            String firstName,
            String lastName,
            Password hashedPassword) {
        User user = new User(
                null,
                Email.of(email),
                FullName.of(firstName, lastName),
                hashedPassword,
                null,
                null);
        return user;
    }

    public static User rehydrate(
            UUID id,
            UUID roleId,
            Email email,
            FullName fullName,
            Password password,
            UserProfile profile,
            UserStatus status,
            List<UserImages> images,
            LocalDateTime lastLogin,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        User user = new User(id, email, fullName, password, createdAt, updatedAt);
        user.roleId = roleId;
        user.profile = profile != null ? profile : UserProfile.empty();
        user.status = status != null ? status : UserStatus.ACTIVE;
        if (images != null) {
            user.images.addAll(images);
        }
        user.lastLogin = lastLogin;
        return user;
    }

    public void changeEmail(String email) {
        this.email = Email.of(email);
        touch();
    }

    public void changeFullName(String firstName, String lastName) {
        this.fullName = FullName.of(firstName, lastName);
        touch();
    }

    public void changePassword(Password password) {
        if (password == null) {
            throw new DomainException("Password is required");
        }
        this.password = password;
        touch();
    }

    public void updateProfile(UserProfile profile) {
        if (profile == null) {
            throw new DomainException("Profile is required");
        }
        this.profile = profile;
        touch();
    }

    /**
     * Adds or replaces an image of the given type (e.g. profile, cover, banner).
     */
    public UserImages upsertImage(String url, String type, String alt, String slug) {
        Optional<UserImages> existing = findImageByType(type);
        if (existing.isPresent()) {
            existing.get().update(url, type, alt, slug);
            touch();
            return existing.get();
        }
        UserImages image = UserImages.create(getId(), url, type, alt, slug);
        images.add(image);
        touch();
        return image;
    }

    public void removeImage(UUID imageId) {
        if (imageId == null) {
            throw new DomainException("Image id is required");
        }
        boolean removed = images.removeIf(image -> imageId.equals(image.getId()));
        if (!removed) {
            throw new DomainException("Image not found for user");
        }
        touch();
    }

    public Optional<UserImages> findImageByType(String type) {
        if (type == null || type.isBlank()) {
            return Optional.empty();
        }
        String normalized = type.trim().toLowerCase();
        return images.stream()
                .filter(image -> image.getType().equalsIgnoreCase(normalized))
                .findFirst();
    }

    public Optional<String> profileImageUrl() {
        return findImageByType(UserImages.TYPE_PROFILE).map(UserImages::getUrl);
    }

    public void changeRole(UUID roleId) {
        if (roleId == null) {
            throw new DomainException("Role id is required");
        }
        this.roleId = roleId;
        touch();
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
        touch();
    }

    public void deactivate() {
        this.status = UserStatus.INACTIVE;
        touch();
    }

    public void suspend() {
        this.status = UserStatus.SUSPENDED;
        touch();
    }

    public void recordLogin() {
        if (status != UserStatus.ACTIVE) {
            throw new DomainException("Only active users can log in");
        }
        this.lastLogin = LocalDateTime.now();
        touch();
    }

    public UUID getRoleId() {
        return roleId;
    }

    public FullName getFullName() {
        return fullName;
    }

    public Email getEmail() {
        return email;
    }

    public Password getPassword() {
        return password;
    }

    public UserProfile getProfile() {
        return profile;
    }

    public UserStatus getStatus() {
        return status;
    }

    public List<UserImages> getImages() {
        return Collections.unmodifiableList(images);
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }
}
