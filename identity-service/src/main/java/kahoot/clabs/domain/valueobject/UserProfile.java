package kahoot.clabs.domain.valueobject;

import java.time.LocalDate;
import java.util.Objects;

public final class UserProfile {

    private final String phoneNumber;
    private final LocalDate birthDate;
    private final String bio;
    private final String location;

    private UserProfile(Builder builder) {
        this.phoneNumber = builder.phoneNumber;
        this.birthDate = builder.birthDate;
        this.bio = builder.bio;
        this.location = builder.location;
    }

    public static UserProfile empty() {
        return new Builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public String phoneNumber() {
        return phoneNumber;
    }

    public LocalDate birthDate() {
        return birthDate;
    }

    public String bio() {
        return bio;
    }

    public String location() {
        return location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserProfile that)) {
            return false;
        }
        return Objects.equals(phoneNumber, that.phoneNumber)
                && Objects.equals(birthDate, that.birthDate)
                && Objects.equals(bio, that.bio)
                && Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phoneNumber, birthDate, bio, location);
    }

    public static final class Builder {
        private String phoneNumber;
        private LocalDate birthDate;
        private String bio;
        private String location;

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder birthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public Builder bio(String bio) {
            this.bio = bio;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public UserProfile build() {
            return new UserProfile(this);
        }
    }
}
