package kahoot.clabs.infrastructure.seed;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import kahoot.clabs.application.port.PasswordHasher;
import kahoot.clabs.domain.aggregate.Role;
import kahoot.clabs.domain.aggregate.User;
import kahoot.clabs.domain.entity.Permission;
import kahoot.clabs.domain.repository.PermissionRepository;
import kahoot.clabs.domain.repository.RoleRepository;
import kahoot.clabs.domain.repository.UserRepository;
import kahoot.clabs.domain.valueobject.Email;
import kahoot.clabs.domain.valueobject.FullName;
import kahoot.clabs.domain.valueobject.Password;
import kahoot.clabs.domain.valueobject.RoleType;
import kahoot.clabs.domain.valueobject.UserProfile;
import kahoot.clabs.domain.valueobject.UserStatus;

@ApplicationScoped
public class IdentityReferenceDataSeeder implements DataSeeder {

    private static final Logger LOG = Logger.getLogger(IdentityReferenceDataSeeder.class);

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final String adminEmail;
    private final String adminPassword;
    private final String adminFirstName;
    private final String adminLastName;

    @Inject
    public IdentityReferenceDataSeeder(
            PermissionRepository permissionRepository,
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            @ConfigProperty(name = "app.seed.admin.email") String adminEmail,
            @ConfigProperty(name = "app.seed.admin.password") String adminPassword,
            @ConfigProperty(name = "app.seed.admin.first-name") String adminFirstName,
            @ConfigProperty(name = "app.seed.admin.last-name") String adminLastName) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
        this.adminFirstName = adminFirstName;
        this.adminLastName = adminLastName;
    }

    @Override
    public int order() {
        return 10;
    }

    @Override
    public String name() {
        return "identity-reference-data";
    }

    @Override
    public void seed() {
        if (userRepository.findByEmail(adminEmail).isPresent()) {
            LOG.info("Identity seed skipped (admin user already exists)");
            return;
        }
        Map<String, Permission> permissions = seedPermissions();
        Map<RoleType, Role> roles = seedRoles(permissions);
        seedUsers(roles);
    }

    private Map<String, Permission> seedPermissions() {
        record PermissionSeed(String name, String description, String module) {
        }

        List<PermissionSeed> definitions = List.of(
                new PermissionSeed("PLATFORM_FULL_ACCESS", "Acceso total a la plataforma", "platform"),
                new PermissionSeed("ORGANIZATION_EDIT", "Editar organización", "organization"),
                new PermissionSeed("ORGANIZATION_STATUS_ASSIGN", "Asignar status de organización", "organization"),
                new PermissionSeed("MEMBER_MANAGE", "Gestionar miembros de organización", "organization"),
                new PermissionSeed("MEMBER_STATUS_ASSIGN", "Asignar status de miembros", "organization"),
                new PermissionSeed("QUIZ_CREATE", "Crear quizzes", "quiz"),
                new PermissionSeed("QUIZ_EDIT", "Editar quizzes", "quiz"),
                new PermissionSeed("QUIZ_STATUS_CHANGE", "Cambiar estatus de quizzes", "quiz"),
                new PermissionSeed("QUIZ_ANSWER_EDIT", "Editar respuestas de quizzes", "quiz"),
                new PermissionSeed("QUESTION_STATUS_CHANGE", "Cambiar estado de preguntas", "quiz"),
                new PermissionSeed("ANSWER_STATUS_CHANGE", "Cambiar estado de respuestas", "quiz"),
                new PermissionSeed("PROFILE_EDIT", "Editar datos del propio perfil", "user"),
                new PermissionSeed("SESSION_JOIN_ANYTIME", "Ingresar a sesiones en cualquier momento", "session"),
                new PermissionSeed(
                        "SESSION_JOIN_WHEN_ENABLED",
                        "Ingresar a sesiones solo cuando estén habilitadas",
                        "session"));

        Map<String, Permission> byName = new HashMap<>();
        for (PermissionSeed definition : definitions) {
            Permission existing = permissionRepository
                    .findByNameAndModule(definition.name(), definition.module())
                    .orElse(null);
            Permission saved = existing != null
                    ? existing
                    : permissionRepository.save(
                            Permission.create(definition.name(), definition.description(), definition.module()));
            byName.put(saved.getName(), saved);
        }
        return byName;
    }

    private Map<RoleType, Role> seedRoles(Map<String, Permission> permissions) {
        record RoleSeed(UUID id, RoleType type, String name, String description, List<String> permissionNames) {
        }

        List<RoleSeed> definitions = List.of(
                new RoleSeed(
                        SeedIds.ROLE_ADMIN,
                        RoleType.ADMIN,
                        "Administrator",
                        "Creador de la plataforma con acceso total",
                        List.of("PLATFORM_FULL_ACCESS")),
                new RoleSeed(
                        SeedIds.ROLE_OWNER,
                        RoleType.OWNER_ORGANIZATION,
                        "Organization Owner",
                        "Poder sobre su organización: quizzes, miembros, statuses y sesiones",
                        List.of(
                                "ORGANIZATION_EDIT",
                                "ORGANIZATION_STATUS_ASSIGN",
                                "MEMBER_MANAGE",
                                "MEMBER_STATUS_ASSIGN",
                                "QUIZ_CREATE",
                                "QUIZ_EDIT",
                                "QUIZ_ANSWER_EDIT",
                                "SESSION_JOIN_ANYTIME",
                                "PROFILE_EDIT")),
                new RoleSeed(
                        SeedIds.ROLE_RH,
                        RoleType.RH_ORGANIZATION,
                        "Organization HR",
                        "Gestión de quizzes, estados y miembros; acceso a sesiones en cualquier momento",
                        List.of(
                                "QUIZ_CREATE",
                                "QUIZ_EDIT",
                                "QUIZ_STATUS_CHANGE",
                                "QUESTION_STATUS_CHANGE",
                                "ANSWER_STATUS_CHANGE",
                                "MEMBER_STATUS_ASSIGN",
                                "SESSION_JOIN_ANYTIME",
                                "PROFILE_EDIT")),
                new RoleSeed(
                        SeedIds.ROLE_MEMBER,
                        RoleType.COMMON_MEMBER,
                        "Common Member",
                        "Editar perfil e ingresar a sesiones solo cuando estén habilitadas",
                        List.of("PROFILE_EDIT", "SESSION_JOIN_WHEN_ENABLED")));

        Map<RoleType, Role> byType = new HashMap<>();
        for (RoleSeed definition : definitions) {
            Role role = roleRepository.findByType(definition.type()).orElseGet(() -> {
                Role created = Role.rehydrate(
                        definition.id(), definition.name(), definition.type(), definition.description(), List.of());
                for (String permissionName : definition.permissionNames()) {
                    Permission permission = permissions.get(permissionName);
                    if (permission != null) {
                        created.addPermission(permission);
                    }
                }
                return roleRepository.save(created);
            });
            byType.put(role.getType(), role);
        }
        return byType;
    }

    private void seedUsers(Map<RoleType, Role> roles) {
        Password.assertValidRaw(adminPassword);
        String hashedPassword = passwordHasher.hash(adminPassword);
        LocalDateTime now = LocalDateTime.now();

        record UserSeed(UUID id, RoleType roleType, String email, String firstName, String lastName) {
        }

        List<UserSeed> core = List.of(
                new UserSeed(
                        SeedIds.USER_ADMIN,
                        RoleType.ADMIN,
                        adminEmail,
                        adminFirstName,
                        adminLastName),
                new UserSeed(
                        SeedIds.USER_OWNER,
                        RoleType.OWNER_ORGANIZATION,
                        "owner@kahoot-clabs.local",
                        "Org",
                        "Owner"),
                new UserSeed(
                        SeedIds.USER_RH,
                        RoleType.RH_ORGANIZATION,
                        "rh@kahoot-clabs.local",
                        "Org",
                        "HR"),
                new UserSeed(
                        SeedIds.USER_MEMBER,
                        RoleType.COMMON_MEMBER,
                        "member@kahoot-clabs.local",
                        "Common",
                        "Member"));

        for (UserSeed definition : core) {
            saveUserIfAbsent(definition.id(), roles.get(definition.roleType()).getId(), definition.email(),
                    definition.firstName(), definition.lastName(), hashedPassword, now);
        }

        for (DemoPerson person : demoPeople()) {
            saveUserIfAbsent(
                    SeedIds.demoUser(person.email()),
                    roles.get(RoleType.COMMON_MEMBER).getId(),
                    person.email(),
                    person.firstName(),
                    person.lastName(),
                    hashedPassword,
                    now);
        }
    }

    private void saveUserIfAbsent(
            UUID id,
            UUID roleId,
            String email,
            String firstName,
            String lastName,
            String hashedPassword,
            LocalDateTime now) {
        if (userRepository.findByEmail(email).isPresent()) {
            return;
        }
        User user = User.rehydrate(
                id,
                roleId,
                Email.of(email),
                FullName.of(firstName, lastName),
                Password.fromHashed(hashedPassword),
                UserProfile.empty(),
                UserStatus.ACTIVE,
                List.of(),
                null,
                now,
                now);
        userRepository.save(user);
    }

    private static List<DemoPerson> demoPeople() {
        return List.of(
                new DemoPerson("Valentina", "Ríos", "valentina.rios@clabs.local"),
                new DemoPerson("Camila", "Vargas", "camila.vargas@clabs.local"),
                new DemoPerson("Sofía", "Mendoza", "sofia.mendoza@clabs.local"),
                new DemoPerson("Isabella", "Castro", "isabella.castro@clabs.local"),
                new DemoPerson("Mariana", "Paredes", "mariana.paredes@clabs.local"),
                new DemoPerson("Lucía", "Herrera", "lucia.herrera@clabs.local"),
                new DemoPerson("Andrés", "Salazar", "andres.salazar@clabs.local"),
                new DemoPerson("Mateo", "Guzmán", "mateo.guzman@clabs.local"),
                new DemoPerson("Santiago", "Ortega", "santiago.ortega@clabs.local"),
                new DemoPerson("Diego", "Navarro", "diego.navarro@clabs.local"),
                new DemoPerson("Julián", "Peña", "julian.pena@clabs.local"),
                new DemoPerson("Sebastián", "Rojas", "sebastian.rojas@clabs.local"));
    }

    private record DemoPerson(String firstName, String lastName, String email) {
    }
}
