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

import kahoot.clabs.organization.infrastructure.persistence.postgres.enums.MemberStatusJpa;

@Entity
@Table(
        name = "organization_members",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_organization_members_org_user",
                columnNames = {"organization_id", "user_id"}))
public class OrganizationMemberJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private OrganizationJpaEntity organization;

    /** External reference to identity-service user. No FK. */
    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private MemberStatusJpa status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private OrganizationDepartmentJpaEntity department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    private OrganizationJobJpaEntity job;

    @Column(name = "joined_at")
    private Instant joinedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected OrganizationMemberJpaEntity() {
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

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public MemberStatusJpa getStatus() {
        return status;
    }

    public void setStatus(MemberStatusJpa status) {
        this.status = status;
    }

    public OrganizationDepartmentJpaEntity getDepartment() {
        return department;
    }

    public void setDepartment(OrganizationDepartmentJpaEntity department) {
        this.department = department;
    }

    public OrganizationJobJpaEntity getJob() {
        return job;
    }

    public void setJob(OrganizationJobJpaEntity job) {
        this.job = job;
    }

    public Instant getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Instant joinedAt) {
        this.joinedAt = joinedAt;
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
