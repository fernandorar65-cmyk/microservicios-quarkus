package kahoot.clabs.identity.infrastructure.persistence.postgres.entity;

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

import kahoot.clabs.identity.infrastructure.persistence.postgres.enums.UserImageTypeJpa;

@Entity
@Table(name = "user_images")
public class UserImageJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user;

    @Column(name = "url", nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(name = "storage_provider", length = 50)
    private String storageProvider;

    @Enumerated(EnumType.STRING)
    @Column(name = "image_type", nullable = false, length = 50)
    private UserImageTypeJpa imageType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected UserImageJpaEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UserJpaEntity getUser() {
        return user;
    }

    public void setUser(UserJpaEntity user) {
        this.user = user;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStorageProvider() {
        return storageProvider;
    }

    public void setStorageProvider(String storageProvider) {
        this.storageProvider = storageProvider;
    }

    public UserImageTypeJpa getImageType() {
        return imageType;
    }

    public void setImageType(UserImageTypeJpa imageType) {
        this.imageType = imageType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
