package kahoot.clabs.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import kahoot.clabs.domain.valueobject.MemberStatus;
import kahoot.clabs.domain.shared.AuditableEntity;
import kahoot.clabs.domain.shared.DomainException;

public class OrganizationMember extends AuditableEntity {

    private final UUID organizationId;
    private final UUID userId;
    private UUID roleId;
    private MemberStatus status;
    private LocalDateTime joinedAt;

    private OrganizationMember(
            UUID id,
            UUID organizationId,
            UUID userId,
            UUID roleId,
            MemberStatus status,
            LocalDateTime joinedAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
        if (organizationId == null) {
            throw new DomainException("Organization id is required");
        }
        if (userId == null) {
            throw new DomainException("User id is required");
        }
        this.organizationId = organizationId;
        this.userId = userId;
        this.roleId = roleId;
        this.status = status != null ? status : MemberStatus.ACTIVE;
        this.joinedAt = joinedAt;
        if (this.status == MemberStatus.ACTIVE && this.joinedAt == null) {
            this.joinedAt = LocalDateTime.now();
        }
    }

    public static OrganizationMember active(UUID organizationId, UUID userId, UUID roleId) {
        return new OrganizationMember(null, organizationId, userId, roleId, MemberStatus.ACTIVE, null, null, null);
    }

    public static OrganizationMember invited(UUID organizationId, UUID userId, UUID roleId) {
        return new OrganizationMember(null, organizationId, userId, roleId, MemberStatus.INVITED, null, null, null);
    }

    public static OrganizationMember rehydrate(
            UUID id,
            UUID organizationId,
            UUID userId,
            UUID roleId,
            MemberStatus status,
            LocalDateTime joinedAt) {
        return new OrganizationMember(id, organizationId, userId, roleId, status, joinedAt, null, null);
    }

    public static OrganizationMember rehydrate(
            UUID id,
            UUID organizationId,
            UUID userId,
            UUID roleId,
            MemberStatus status,
            LocalDateTime joinedAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        return new OrganizationMember(
                id, organizationId, userId, roleId, status, joinedAt, createdAt, updatedAt);
    }

    public void acceptInvitation() {
        if (status != MemberStatus.INVITED) {
            throw new DomainException("Only invited members can accept an invitation");
        }
        this.status = MemberStatus.ACTIVE;
        this.joinedAt = LocalDateTime.now();
        touch();
    }

    public void suspend() {
        this.status = MemberStatus.SUSPENDED;
        touch();
    }

    public void reactivate() {
        this.status = MemberStatus.ACTIVE;
        touch();
    }

    public void changeRole(UUID roleId) {
        if (roleId == null) {
            throw new DomainException("Role id is required");
        }
        this.roleId = roleId;
        touch();
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getRoleId() {
        return roleId;
    }

    public MemberStatus getStatus() {
        return status;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }
}
