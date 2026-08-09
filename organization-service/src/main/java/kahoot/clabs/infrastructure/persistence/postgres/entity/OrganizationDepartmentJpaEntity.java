package kahoot.clabs.infrastructure.persistence.postgres.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "organization_departments",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_organization_departments_name",
                columnNames = "name"))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrganizationDepartmentJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "description", nullable = false, length = 100)
    private String description;

    public static OrganizationDepartmentJpaEntity newInstance() {
        return new OrganizationDepartmentJpaEntity();
    }
}
