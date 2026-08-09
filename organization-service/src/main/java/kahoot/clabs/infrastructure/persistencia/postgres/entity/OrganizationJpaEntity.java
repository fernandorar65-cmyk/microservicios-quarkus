package kahoot.clabs.organization.infrastructure.persistence.postgres.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import kahoot.clabs.organization.infrastructure.persistence.postgres.enums.OrganizationStatusJpa;

@Entity
@Table(
        name = "organizations",
        uniqueConstraints = @UniqueConstraint(name = "uq_organizations_slug", columnNames = "slug"))
public class OrganizationJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "slug", nullable = false, length = 100)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrganizationStatusJpa status;

    /** External reference to identity-service user. No FK. */
    @Column(name = "created_by", nullable = false, updatable = false)
    private UUID createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(
            mappedBy = "organization",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<OrganizationDepartmentJpaEntity> departments = new ArrayList<>();

    @OneToMany(
            mappedBy = "organization",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<OrganizationJobJpaEntity> jobs = new ArrayList<>();

    @OneToMany(
            mappedBy = "organization",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<OrganizationMemberJpaEntity> members = new ArrayList<>();

    @OneToMany(
            mappedBy = "organization",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<OrganizationInvitationJpaEntity> invitations = new ArrayList<>();

    protected OrganizationJpaEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public OrganizationStatusJpa getStatus() {
        return status;
    }

    public void setStatus(OrganizationStatusJpa status) {
        this.status = status;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
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

    public List<OrganizationDepartmentJpaEntity> getDepartments() {
        return departments;
    }

    public void setDepartments(List<OrganizationDepartmentJpaEntity> departments) {
        this.departments = departments;
    }

    public List<OrganizationJobJpaEntity> getJobs() {
        return jobs;
    }

    public void setJobs(List<OrganizationJobJpaEntity> jobs) {
        this.jobs = jobs;
    }

    public List<OrganizationMemberJpaEntity> getMembers() {
        return members;
    }

    public void setMembers(List<OrganizationMemberJpaEntity> members) {
        this.members = members;
    }

    public List<OrganizationInvitationJpaEntity> getInvitations() {
        return invitations;
    }

    public void setInvitations(List<OrganizationInvitationJpaEntity> invitations) {
        this.invitations = invitations;
    }
}
