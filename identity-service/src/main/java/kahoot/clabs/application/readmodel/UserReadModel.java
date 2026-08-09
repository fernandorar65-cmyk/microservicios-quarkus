package kahoot.clabs.application.readmodel;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserReadModel {

    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String status;
    private String phoneNumber;
    private LocalDate birthDate;
    private String bio;
    private String location;
    private Instant lastLogin;
    private UserRoleReadModel role;
    private List<UserImageReadModel> images = new ArrayList<>();
    private Instant createdAt;
    private Instant updatedAt;
}
