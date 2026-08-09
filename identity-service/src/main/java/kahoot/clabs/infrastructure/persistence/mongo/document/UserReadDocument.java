package kahoot.clabs.infrastructure.persistence.mongo.document;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bson.codecs.pojo.annotations.BsonId;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MongoEntity(collection = "users")
@Getter
@Setter
@NoArgsConstructor
public class UserReadDocument {

    @BsonId
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
    private UserRoleEmbed role;
    private List<UserImageEmbed> images = new ArrayList<>();
    private Instant createdAt;
    private Instant updatedAt;
}
