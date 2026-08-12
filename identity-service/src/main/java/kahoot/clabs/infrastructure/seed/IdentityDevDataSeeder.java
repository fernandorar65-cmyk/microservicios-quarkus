package kahoot.clabs.infrastructure.seed;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import kahoot.clabs.application.event.PermissionProjectionSnapshot;
import kahoot.clabs.application.event.PermissionUpsertedEvent;
import kahoot.clabs.application.event.RoleProjectionSnapshot;
import kahoot.clabs.application.event.RoleUpsertedEvent;
import kahoot.clabs.application.event.UserIntegrationEvent;
import kahoot.clabs.application.event.UserProjectionSnapshot;
import kahoot.clabs.application.port.integration.UserEventPublisher;
import kahoot.clabs.application.port.write.PasswordHasher;
import kahoot.clabs.domain.aggregate.Role;
import kahoot.clabs.domain.aggregate.User;
import kahoot.clabs.domain.entity.Permission;
import kahoot.clabs.domain.repository.PermissionRepository;
import kahoot.clabs.domain.repository.RoleRepository;
import kahoot.clabs.domain.repository.UserRepository;
import kahoot.clabs.domain.valueobject.Password;
import kahoot.clabs.domain.valueobject.RoleType;

@ApplicationScoped
public class IdentityDevDataSeeder {

    private static final Logger LOG = Logger.getLogger(IdentityDevDataSeeder.class);
    private static final String DEMO_PASSWORD = "Admin123!";

    private static final List<PermissionSpec> PERMISSIONS = List.of(
            new PermissionSpec("PLATFORM_FULL_ACCESS", "Acceso total a la plataforma", "platform"),
            new PermissionSpec("ORGANIZATION_EDIT", "Editar organización", "organization"),
            new PermissionSpec("MEMBER_MANAGE", "Gestionar miembros", "organization"),
            new PermissionSpec("QUIZ_CREATE", "Crear quizzes", "quiz"),
            new PermissionSpec("QUIZ_EDIT", "Editar quizzes", "quiz"),
            new PermissionSpec("PROFILE_EDIT", "Editar perfil", "user"),
            new PermissionSpec("SESSION_JOIN_ANYTIME", "Ingresar a sesiones siempre", "session"),
            new PermissionSpec("SESSION_JOIN_WHEN_ENABLED", "Ingresar a sesiones habilitadas", "session"));

    @Inject
    PermissionRepository permissionRepository;

    @Inject
    RoleRepository roleRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    PasswordHasher passwordHasher;

    @Inject
    UserEventPublisher userEventPublisher;

    @ConfigProperty(name = "app.seed.enabled", defaultValue = "false")
    boolean seedEnabled;

    void onStart(@Observes StartupEvent event) {
        if (!seedEnabled) {
            return;
        }
        SeedBatch batch = seedPostgres();
        publishKafka(batch);
        LOG.info("Identity seed completed (Postgres committed; Mongo via Kafka consumers)");
    }

    @Transactional
    SeedBatch seedPostgres() {
        LOG.info("Seeding identity write model (Postgres only)");
        List<Permission> permissions = new ArrayList<>();
        for (PermissionSpec spec : PERMISSIONS) {
            permissions.add(ensurePermission(spec.name(), spec.description(), spec.module()));
        }
        Map<String, Permission> byName = permissions.stream()
                .collect(java.util.stream.Collectors.toMap(Permission::getName, p -> p, (a, b) -> a));

        List<Role> roles = List.of(
                ensureRole("Administrator", RoleType.ADMIN, "Acceso total",
                        List.of(byName.get("PLATFORM_FULL_ACCESS"))),
                ensureRole("Organization Owner", RoleType.OWNER_ORGANIZATION, "Dueño de organización",
                        List.of(
                                byName.get("ORGANIZATION_EDIT"),
                                byName.get("MEMBER_MANAGE"),
                                byName.get("QUIZ_CREATE"),
                                byName.get("QUIZ_EDIT"),
                                byName.get("PROFILE_EDIT"),
                                byName.get("SESSION_JOIN_ANYTIME"))),
                ensureRole("Organization HR", RoleType.RH_ORGANIZATION, "RRHH de organización",
                        List.of(byName.get("MEMBER_MANAGE"), byName.get("PROFILE_EDIT"))),
                ensureRole("Common Member", RoleType.COMMON_MEMBER, "Miembro común",
                        List.of(byName.get("PROFILE_EDIT"), byName.get("SESSION_JOIN_WHEN_ENABLED"))));

        Map<RoleType, Role> rolesByType = roles.stream()
                .collect(java.util.stream.Collectors.toMap(Role::getType, r -> r, (a, b) -> a));

        List<UserSeed> users = List.of(
                ensureUser("admin@kahoot-clabs.local", "System", "Admin", rolesByType.get(RoleType.ADMIN)),
                ensureUser("owner@kahoot-clabs.local", "Org", "Owner", rolesByType.get(RoleType.OWNER_ORGANIZATION)),
                ensureUser("rh@kahoot-clabs.local", "Org", "HR", rolesByType.get(RoleType.RH_ORGANIZATION)),
                ensureUser("member@kahoot-clabs.local", "Common", "Member", rolesByType.get(RoleType.COMMON_MEMBER)));

        return new SeedBatch(permissions, roles, users);
    }

    private void publishKafka(SeedBatch batch) {
        for (Permission permission : batch.permissions()) {
            userEventPublisher.publish(PermissionUpsertedEvent.of(PermissionProjectionSnapshot.from(permission)));
        }
        for (Role role : batch.roles()) {
            userEventPublisher.publish(RoleUpsertedEvent.of(RoleProjectionSnapshot.from(role)));
        }
        for (UserSeed userSeed : batch.users()) {
            userEventPublisher.publish(UserIntegrationEvent.userCreated(
                    UserProjectionSnapshot.from(userSeed.user(), userSeed.role())));
            LOG.infof("Published UserCreated email=%s id=%s", userSeed.user().getEmail().value(), userSeed.user().getId());
        }
    }

    private Permission ensurePermission(String name, String description, String module) {
        return permissionRepository.findByNameAndModule(name, module).orElseGet(() -> {
            Permission created = Permission.create(name, description, module);
            return permissionRepository.save(created);
        });
    }

    private Role ensureRole(String name, RoleType type, String description, List<Permission> rolePermissions) {
        Role role = roleRepository.findByType(type).orElseGet(() -> {
            Role created = Role.create(name, type, description);
            rolePermissions.stream().filter(p -> p != null).forEach(created::addPermission);
            return roleRepository.save(created);
        });
        boolean changed = false;
        for (Permission permission : rolePermissions) {
            if (permission != null && !role.hasPermission(permission.getName())) {
                role.addPermission(permission);
                changed = true;
            }
        }
        if (changed) {
            role = roleRepository.save(role);
        }
        return role;
    }

    private UserSeed ensureUser(String email, String firstName, String lastName, Role role) {
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            Password.assertValidRaw(DEMO_PASSWORD);
            Password hashed = Password.fromHashed(passwordHasher.hash(DEMO_PASSWORD));
            User created = User.create(email, firstName, lastName, hashed);
            created.changeRole(role.getId());
            return userRepository.save(created);
        });
        if (user.getRoleId() == null || !user.getRoleId().equals(role.getId())) {
            user.changeRole(role.getId());
            user = userRepository.save(user);
        }
        return new UserSeed(user, role);
    }

    private record PermissionSpec(String name, String description, String module) {
    }

    private record UserSeed(User user, Role role) {
    }

    private record SeedBatch(List<Permission> permissions, List<Role> roles, List<UserSeed> users) {
    }
}
