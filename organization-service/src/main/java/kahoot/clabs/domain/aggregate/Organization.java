package kahoot.clabs.domain.aggregate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import kahoot.clabs.domain.entity.OrganizationMember;
import kahoot.clabs.domain.valueobject.OrganizationName;
import kahoot.clabs.domain.valueobject.OrganizationSlug;
import kahoot.clabs.domain.valueobject.OrganizationStatus;
import kahoot.clabs.domain.shared.AggregateRoot;
import kahoot.clabs.domain.shared.DomainException;

public class Organization extends AggregateRoot {

    private static final String DEFAULT_TIMEZONE = "America/Bogota";
    private static final String DEFAULT_LANGUAGE = "es";

    private OrganizationName name;
    private OrganizationSlug slug;
    private String logo;
    private String description;
    private String timezone;
    private String language;
    private OrganizationStatus status;

    private final List<OrganizationMember> members = new ArrayList<>();

    private Organization(UUID id, OrganizationName name, OrganizationSlug slug, LocalDateTime createdAt,
                         LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
        this.name = name;
        this.slug = slug;
        this.status = OrganizationStatus.ACTIVE;
        this.timezone = DEFAULT_TIMEZONE;
        this.language = DEFAULT_LANGUAGE;
    }

    public static Organization create(String name, String slug) {
        return new Organization(null, OrganizationName.of(name), OrganizationSlug.of(slug), null, null);
    }

    public static Organization rehydrate(
            UUID id,
            String name,
            String slug,
            String logo,
            String description,
            String timezone,
            String language,
            OrganizationStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            List<OrganizationMember> members) {
        Organization organization = new Organization(
                id, OrganizationName.of(name), OrganizationSlug.of(slug), createdAt, updatedAt);
        organization.logo = logo;
        organization.description = description;
        if (timezone != null && !timezone.isBlank()) {
            organization.timezone = timezone;
        }
        if (language != null && !language.isBlank()) {
            organization.language = language;
        }
        organization.status = status != null ? status : OrganizationStatus.ACTIVE;
        if (members != null) {
            organization.members.addAll(members);
        }
        return organization;
    }

    public void updateDetails(String name, String description) {
        this.name = OrganizationName.of(name);
        this.description = description;
        touch();
    }

    public void changeSlug(String slug) {
        this.slug = OrganizationSlug.of(slug);
        touch();
    }

    public void changeLogo(String logo) {
        this.logo = logo;
        touch();
    }

    public void changeLocalization(String timezone, String language) {
        if (timezone != null && !timezone.isBlank()) {
            this.timezone = timezone.trim();
        }
        if (language != null && !language.isBlank()) {
            this.language = language.trim();
        }
        touch();
    }

    public void activate() {
        this.status = OrganizationStatus.ACTIVE;
        touch();
    }

    public void deactivate() {
        this.status = OrganizationStatus.INACTIVE;
        touch();
    }

    public void suspend() {
        this.status = OrganizationStatus.SUSPENDED;
        touch();
    }

    public OrganizationMember addMember(UUID userId, UUID roleId) {
        ensureCanAcceptMembers();
        ensureNotAlreadyMember(userId);
        OrganizationMember member = OrganizationMember.active(getId(), userId, roleId);
        members.add(member);
        touch();
        return member;
    }

    public OrganizationMember inviteMember(UUID userId, UUID roleId) {
        ensureCanAcceptMembers();
        ensureNotAlreadyMember(userId);
        OrganizationMember member = OrganizationMember.invited(getId(), userId, roleId);
        members.add(member);
        touch();
        return member;
    }

    public void acceptInvitation(UUID userId) {
        requireMember(userId).acceptInvitation();
        touch();
    }

    public void changeMemberRole(UUID userId, UUID roleId) {
        requireMember(userId).changeRole(roleId);
        touch();
    }

    public void suspendMember(UUID userId) {
        requireMember(userId).suspend();
        touch();
    }

    public void removeMember(UUID userId) {
        OrganizationMember member = requireMember(userId);
        members.remove(member);
        touch();
    }

    public boolean hasMember(UUID userId) {
        return findMember(userId).isPresent();
    }

    private void ensureCanAcceptMembers() {
        if (status == OrganizationStatus.SUSPENDED) {
            throw new DomainException("A suspended organization cannot accept members");
        }
    }

    private void ensureNotAlreadyMember(UUID userId) {
        if (hasMember(userId)) {
            throw new DomainException("User is already a member of this organization: " + userId);
        }
    }

    private OrganizationMember requireMember(UUID userId) {
        return findMember(userId)
                .orElseThrow(() -> new DomainException("User is not a member of this organization: " + userId));
    }

    private Optional<OrganizationMember> findMember(UUID userId) {
        return members.stream()
                .filter(member -> member.getUserId().equals(userId))
                .findFirst();
    }

    public OrganizationName getName() {
        return name;
    }

    public OrganizationSlug getSlug() {
        return slug;
    }

    public String getLogo() {
        return logo;
    }

    public String getDescription() {
        return description;
    }

    public String getTimezone() {
        return timezone;
    }

    public String getLanguage() {
        return language;
    }

    public OrganizationStatus getStatus() {
        return status;
    }

    public List<OrganizationMember> getMembers() {
        return Collections.unmodifiableList(members);
    }
}
