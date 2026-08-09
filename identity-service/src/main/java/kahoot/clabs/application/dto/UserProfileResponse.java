package kahoot.clabs.application.dto;

import java.time.LocalDate;
import java.util.UUID;

import kahoot.clabs.application.readmodel.UserImageReadModel;
import kahoot.clabs.application.readmodel.UserReadModel;
import kahoot.clabs.domain.aggregate.User;
import kahoot.clabs.domain.entity.UserImages;
import kahoot.clabs.domain.valueobject.UserProfile;

public record UserProfileResponse(
        UUID id,
        UUID roleId,
        String email,
        String firstName,
        String lastName,
        String status,
        String phoneNumber,
        LocalDate birthDate,
        String bio,
        String location,
        String profileImageUrl
) {

    public static UserProfileResponse from(User user) {
        UserProfile profile = user.getProfile();
        return new UserProfileResponse(
                user.getId(),
                user.getRoleId(),
                user.getEmail().value(),
                user.getFullName().firstName(),
                user.getFullName().lastName(),
                user.getStatus().name(),
                profile.phoneNumber(),
                profile.birthDate(),
                profile.bio(),
                profile.location(),
                user.profileImageUrl().orElse(null));
    }

    public static UserProfileResponse from(UserReadModel readModel) {
        String profileImageUrl = readModel.getImages().stream()
                .filter(image -> UserImages.TYPE_PROFILE.equalsIgnoreCase(image.getType()))
                .map(UserImageReadModel::getUrl)
                .findFirst()
                .orElse(null);
        UUID roleId = readModel.getRole() != null ? readModel.getRole().getId() : null;
        return new UserProfileResponse(
                readModel.getId(),
                roleId,
                readModel.getEmail(),
                readModel.getFirstName(),
                readModel.getLastName(),
                readModel.getStatus(),
                readModel.getPhoneNumber(),
                readModel.getBirthDate(),
                readModel.getBio(),
                readModel.getLocation(),
                profileImageUrl);
    }
}
