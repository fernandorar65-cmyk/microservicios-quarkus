package kahoot.clabs.organization.infrastructure.persistence.postgres.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import kahoot.clabs.organization.infrastructure.persistence.postgres.enums.InvitationStatusJpa;

@Entity
@Table(
        name = "organization_invitations",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_organization_invitations_token",
                columnNames = "token"))
public class OrganizationInvitationJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private OrganizationJpaEntity organization;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    /** External reference to identity-service user. No FK. */
    @Column(name = "invited_by", nullable = false, updatable = false)
    private UUID invitedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private InvitationStatusJpa status;

    @Column(name = "token", length = 255)
    private String token;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected OrganizationInvitationJpaEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public OrganizationJpaEntity getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationJpaEntity organization) {
        this.organization = organization;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UUID getInvitedBy() {
        return invitedBy;
    }

    public void setInvitedBy(UUID invitedBy) {
        this.invitedBy = invitedBy;
    }

    public InvitationStatusJpa getStatus() {
        return status;
    }

    public void setStatus(InvitationStatusJpa status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
