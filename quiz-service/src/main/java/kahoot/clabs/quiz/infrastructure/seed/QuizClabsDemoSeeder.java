package kahoot.clabs.quiz.infrastructure.seed;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import kahoot.clabs.quiz.domain.aggregate.Quiz;
import kahoot.clabs.quiz.domain.entity.Category;
import kahoot.clabs.quiz.domain.entity.Question;
import kahoot.clabs.quiz.domain.repository.CategoryRepository;
import kahoot.clabs.quiz.domain.repository.QuizRepository;
import kahoot.clabs.quiz.domain.valueobject.EstimatedTime;
import kahoot.clabs.quiz.domain.valueobject.MediaType;
import kahoot.clabs.quiz.domain.valueobject.QuestionType;
import kahoot.clabs.quiz.domain.valueobject.QuizDifficulty;
import kahoot.clabs.quiz.domain.valueobject.QuizSettings;
import kahoot.clabs.quiz.domain.valueobject.QuizStatus;

/**
 * Demo quiz catalog for Clabs. Fixed SeedIds for the three quizzes used by gameplay seed.
 */
@ApplicationScoped
public class QuizClabsDemoSeeder implements DataSeeder {

    private static final Logger LOG = Logger.getLogger(QuizClabsDemoSeeder.class);
    private static final String CDN = "https://cdn.kahoot-clabs.local/seed";

    private final CategoryRepository categoryRepository;
    private final QuizRepository quizRepository;

    @Inject
    public QuizClabsDemoSeeder(CategoryRepository categoryRepository, QuizRepository quizRepository) {
        this.categoryRepository = categoryRepository;
        this.quizRepository = quizRepository;
    }

    @Override
    public int order() {
        return 40;
    }

    @Override
    public String name() {
        return "quiz-clabs-demo";
    }

    @Override
    public void seed() {
        if (quizRepository.existsById(SeedIds.QUIZ_JAVA)) {
            LOG.info("Quiz demo seed skipped (QUIZ_JAVA already exists)");
            return;
        }

        UUID orgId = SeedIds.ORG_CLABS;
        UUID ownerId = SeedIds.USER_OWNER;
        Map<String, UUID> categories = seedCategories(orgId);

        seedJavaFundamentals(orgId, ownerId, categories);
        seedCultureQuiz(orgId, ownerId, categories);
        seedDevOpsEssential(orgId, ownerId, categories);
        seedSpringBootQuiz(orgId, ownerId, categories);
        seedSqlDataQuiz(orgId, ownerId, categories);
        seedSecurityQuiz(orgId, ownerId, categories);
        seedSoftSkillsQuiz(orgId, ownerId, categories);
        seedCloudAwsQuiz(orgId, ownerId, categories);
        seedFrontendQuiz(orgId, ownerId, categories);
        seedArchitectureDraft(orgId, ownerId, categories);
        seedOnboardingArchived(orgId, ownerId, categories);
        LOG.info("Quiz Clabs demo catalog seeded");
    }

    private Map<String, UUID> seedCategories(UUID organizationId) {
        record CategorySeed(String key, String name, String description, String color, String icon) {
        }

        List<CategorySeed> definitions = List.of(
                new CategorySeed("tech", "Tecnología", "Quizzes técnicos de programación y plataformas", "#2563EB", "code"),
                new CategorySeed("culture", "Cultura", "Valores, colaboración y dinámicas de equipo", "#DB2777", "users"),
                new CategorySeed("devops", "DevOps", "CI/CD, cloud y operaciones", "#059669", "cloud"),
                new CategorySeed("backend", "Backend", "APIs, frameworks y servicios del lado servidor", "#7C3AED", "server"),
                new CategorySeed("data", "Datos", "SQL, modelado y pipelines de datos", "#D97706", "database"),
                new CategorySeed("security", "Seguridad", "AuthN/AuthZ, amenazas y buenas prácticas", "#DC2626", "shield"),
                new CategorySeed("frontend", "Frontend", "UI, accesibilidad y frameworks web", "#0891B2", "layout"),
                new CategorySeed("soft", "Soft Skills", "Comunicación, liderazgo y trabajo en equipo", "#EA580C", "message"),
                new CategorySeed("architecture", "Arquitectura", "Diseño de sistemas, DDD y patrones", "#4F46E5", "layers"),
                new CategorySeed("onboarding", "Onboarding", "Contenido de inducción para nuevos ingresos", "#64748B", "rocket"));

        Map<String, UUID> categories = new LinkedHashMap<>();
        for (CategorySeed definition : definitions) {
            Category category = Category.create(organizationId, definition.name());
            category.changeDescription(definition.description());
            category.changeColor(definition.color());
            category.changeIcon(definition.icon());
            categories.put(definition.key(), categoryRepository.save(category).getId());
        }
        return categories;
    }

    private void seedJavaFundamentals(UUID orgId, UUID ownerId, Map<String, UUID> categories) {
        Quiz quiz = baseQuiz(
                SeedIds.QUIZ_JAVA,
                orgId,
                ownerId,
                "Fundamentos de Java",
                "Quiz amplio sobre conceptos esenciales de Java para onboarding técnico en Clabs.",
                QuizDifficulty.EASY,
                15,
                CDN + "/java-quiz.png",
                QuizSettings.of(false, true, true, true, false, true, true),
                List.of(categories.get("tech"), categories.get("backend"), categories.get("onboarding")));

        addMc(quiz, "¿Qué palabra clave declara una constante en Java?", "Selecciona la opción correcta.",
                QuizDifficulty.EASY, 1000, 20,
                opts("final", true, "const", false, "static", false, "immutable", false));
        attachImage(quiz, lastQuestion(quiz), "java-final.png", "Palabra clave final en Java");

        addTf(quiz, "En Java, String es un tipo primitivo.", "Verdadero o falso.",
                QuizDifficulty.EASY, 800, 15, false);

        addMs(quiz, "¿Cuáles de estos son interfaces de colección en Java?", "Puedes marcar más de una opción.",
                QuizDifficulty.MODERATE, 1200, 25,
                opts("List", true, "Set", true, "Array", false, "Map", true));

        addMc(quiz, "¿Cuál es el modificador de acceso más restrictivo?", null,
                QuizDifficulty.EASY, 900, 15,
                opts("private", true, "protected", false, "public", false, "package-private", false));

        addMc(quiz, "¿Qué interfaz representa una secuencia ordenada que admite duplicados?", null,
                QuizDifficulty.EASY, 1000, 20,
                opts("List", true, "Set", false, "Map", false, "Queue solo", false));

        addTf(quiz, "Un método marcado como abstract debe tener cuerpo.", null,
                QuizDifficulty.EASY, 800, 15, false);

        addMs(quiz, "¿Qué características aplican a una clase marcada como final?", "Marca todas las correctas.",
                QuizDifficulty.MODERATE, 1200, 25,
                opts("No puede ser extendida", true, "Sus métodos son abstract por defecto", false,
                        "Sus campos son automáticamente static", false, "Puede instanciarse si no es abstract", true));

        addMc(quiz, "¿Qué excepción es unchecked?", null,
                QuizDifficulty.MODERATE, 1100, 20,
                opts("NullPointerException", true, "IOException", false, "SQLException", false, "FileNotFoundException", false));

        addSa(quiz, "Nombra una estructura de control de repetición en Java.",
                "Ejemplos válidos: for, while, do-while, for-each.",
                QuizDifficulty.EASY, 600, 30);

        addMc(quiz, "¿Qué hace el garbage collector?", null,
                QuizDifficulty.EASY, 1000, 20,
                opts("Libera memoria de objetos no referenciados", true,
                        "Compila bytecode a nativo", false,
                        "Gestiona hilos del sistema operativo", false,
                        "Valida firmas de métodos", false));

        publish(quiz);
    }

    private void seedCultureQuiz(UUID orgId, UUID ownerId, Map<String, UUID> categories) {
        Quiz quiz = baseQuiz(
                SeedIds.QUIZ_CULTURE,
                orgId,
                ownerId,
                "Cultura y trabajo en Clabs",
                "Dinámica sobre colaboración, feedback y cultura de equipo en Clabs.",
                QuizDifficulty.EASY,
                8,
                CDN + "/culture-quiz.png",
                QuizSettings.defaultSettings(),
                List.of(categories.get("culture"), categories.get("soft"), categories.get("onboarding")));

        addMc(quiz, "¿Qué práctica ayuda más a un buen code review?", "Elige la mejor práctica.",
                QuizDifficulty.EASY, 1000, 20,
                opts("Dar feedback concreto y respetuoso", true,
                        "Aprobar sin leer para ir más rápido", false,
                        "Criticar a la persona, no al código", false,
                        "Evitar preguntar dudas", false));

        addTf(quiz, "Documentar decisiones técnicas facilita el onboarding.", null,
                QuizDifficulty.EASY, 800, 15, true);

        addSa(quiz, "Nombra una forma breve de pedir ayuda a tu equipo.",
                "Respuesta libre (evaluación manual / host).",
                QuizDifficulty.EASY, 600, 30);

        addMs(quiz, "¿Qué prácticas refuerzan la seguridad psicológica?", "Selecciona todas las que apliquen.",
                QuizDifficulty.MODERATE, 1200, 25,
                opts("Admitir errores sin castigo", true, "Ridiculizar preguntas básicas", false,
                        "Celebrar aprendizajes", true, "Escuchar activamente", true));

        addMc(quiz, "En una retrospección, ¿qué conviene priorizar?", null,
                QuizDifficulty.EASY, 1000, 20,
                opts("Acciones concretas de mejora", true, "Buscar culpables", false,
                        "Ignorar fricciones", false, "Solo métricas de velocidad", false));

        addTf(quiz, "Pedir feedback temprano suele reducir retrabajo.", null,
                QuizDifficulty.EASY, 800, 15, true);

        addMc(quiz, "¿Cuál es una buena forma de comunicar un bloqueo?", null,
                QuizDifficulty.EASY, 1000, 20,
                opts("Explicar impacto, intento realizado y ayuda necesaria", true,
                        "Esperar en silencio hasta el deadline", false,
                        "Culpar a otro equipo sin contexto", false,
                        "Cambiar de tarea sin avisar", false));

        publish(quiz);
    }

    private void seedDevOpsEssential(UUID orgId, UUID ownerId, Map<String, UUID> categories) {
        Quiz quiz = baseQuiz(
                SeedIds.QUIZ_DEVOPS,
                orgId,
                ownerId,
                "DevOps esencial",
                "Conceptos base de CI/CD, observabilidad y operación de servicios.",
                QuizDifficulty.MODERATE,
                12,
                CDN + "/devops-quiz.png",
                QuizSettings.of(true, true, true, true, false, true, false),
                List.of(categories.get("devops"), categories.get("tech")));

        addMc(quiz, "¿Qué significa CI en CI/CD?", null,
                QuizDifficulty.EASY, 1000, 20,
                opts("Continuous Integration", true, "Cloud Infrastructure", false,
                        "Container Isolation", false, "Code Inspection", false));
        attachImage(quiz, lastQuestion(quiz), "cicd-pipeline.png", "Pipeline CI/CD");

        addTf(quiz, "Un rollback rápido es útil cuando un deploy introduce un incidente.", null,
                QuizDifficulty.EASY, 800, 15, true);

        addMs(quiz, "¿Cuáles prácticas mejoran la confiabilidad de un servicio?", "Selecciona todas las que apliquen.",
                QuizDifficulty.MODERATE, 1200, 25,
                opts("Monitoreo y alertas", true, "Desplegar sin pruebas", false,
                        "Health checks", true, "Feature flags", true));

        addMc(quiz, "¿Qué es un pipeline de CD?", null,
                QuizDifficulty.EASY, 1000, 20,
                opts("Automatización del despliegue a ambientes", true,
                        "Solo compilación local del desarrollador", false,
                        "Un tipo de base de datos", false,
                        "Un balanceador de carga", false));

        addMc(quiz, "¿Para qué sirve un liveness probe?", null,
                QuizDifficulty.MODERATE, 1100, 20,
                opts("Detectar si el proceso debe reiniciarse", true,
                        "Escalar horizontalmente la CPU", false,
                        "Cifrar secretos en tránsito", false,
                        "Versionar esquemas SQL", false));

        addTf(quiz, "Infrastructure as Code reduce la deriva de configuración.", null,
                QuizDifficulty.EASY, 800, 15, true);

        addMs(quiz, "¿Qué señales suelen usarse en observabilidad?", null,
                QuizDifficulty.MODERATE, 1200, 25,
                opts("Métricas", true, "Logs", true, "Traces", true, "Capturas de pantalla del IDE", false));

        addMc(quiz, "¿Qué estrategia reduce el riesgo de un release grande?", null,
                QuizDifficulty.MODERATE, 1100, 20,
                opts("Canary o blue/green", true, "Deploy viernes 23:00 sin monitoreo", false,
                        "Saltarse staging", false, "Hardcodear secrets", false));

        addSa(quiz, "Nombra una herramienta típica de CI.",
                "Ejemplos: GitHub Actions, Jenkins, GitLab CI, CircleCI.",
                QuizDifficulty.EASY, 600, 25);

        publish(quiz);
    }

    private void seedSpringBootQuiz(UUID orgId, UUID ownerId, Map<String, UUID> categories) {
        Quiz quiz = baseQuiz(
                orgId,
                ownerId,
                "Spring Boot en la práctica",
                "Beans, inyección, starters y patrones comunes en aplicaciones Spring Boot.",
                QuizDifficulty.MODERATE,
                14,
                CDN + "/spring-boot-quiz.png",
                QuizSettings.of(false, true, true, true, true, true, false),
                List.of(categories.get("backend"), categories.get("tech"), categories.get("architecture")));

        addMc(quiz, "¿Qué anotación marca un bean de configuración?", null,
                QuizDifficulty.EASY, 1000, 20,
                opts("@Configuration", true, "@RestController only", false, "@Entity", false, "@Document", false));

        addTf(quiz, "@RestController combina @Controller y @ResponseBody.", null,
                QuizDifficulty.EASY, 800, 15, true);

        addMs(quiz, "¿Cuáles capas suelen aparecer en una app hexagonal/DDD con Spring?", null,
                QuizDifficulty.MODERATE, 1300, 25,
                opts("Domain", true, "Application", true, "Infrastructure", true, "ServletFilterDomain", false));

        addMc(quiz, "¿Qué hace @Transactional por defecto ante RuntimeException?", null,
                QuizDifficulty.MODERATE, 1200, 20,
                opts("Hace rollback", true, "Siempre hace commit", false,
                        "Ignora la excepción", false, "Abre un nuevo datasource", false));

        addMc(quiz, "¿Dónde debería vivir la lógica de negocio rica?", null,
                QuizDifficulty.MODERATE, 1100, 20,
                opts("En el dominio / aggregates", true, "En el controller", false,
                        "En el DTO de request", false, "En el mapper de infraestructura", false));

        addTf(quiz, "Un puerto de dominio no debe conocer JPA ni Mongo.", null,
                QuizDifficulty.EASY, 800, 15, true);

        addSa(quiz, "Nombra un starter típico de Spring Boot.",
                "Ejemplos: spring-boot-starter-web, data-jpa, security, validation.",
                QuizDifficulty.EASY, 600, 25);

        addMc(quiz, "¿Qué problema mitiga el patrón Adapter?", null,
                QuizDifficulty.MODERATE, 1100, 20,
                opts("Acoplar el dominio a una tecnología concreta", true,
                        "Eliminar todas las interfaces", false,
                        "Forzar SQL en el dominio", false,
                        "Evitar tests", false));

        publish(quiz);
    }

    private void seedSqlDataQuiz(UUID orgId, UUID ownerId, Map<String, UUID> categories) {
        Quiz quiz = baseQuiz(
                orgId,
                ownerId,
                "SQL y modelado relacional",
                "Consultas, índices, normalización y buenas prácticas de datos.",
                QuizDifficulty.MODERATE,
                12,
                CDN + "/sql-quiz.png",
                QuizSettings.defaultSettings(),
                List.of(categories.get("data"), categories.get("backend")));

        addMc(quiz, "¿Qué cláusula filtra filas después del GROUP BY?", null,
                QuizDifficulty.EASY, 1000, 20,
                opts("HAVING", true, "WHERE", false, "ORDER BY", false, "LIMIT", false));

        addTf(quiz, "Una clave foránea garantiza integridad referencial.", null,
                QuizDifficulty.EASY, 800, 15, true);

        addMs(quiz, "¿Qué ayuda a acelerar búsquedas por columna frecuente?", null,
                QuizDifficulty.MODERATE, 1200, 25,
                opts("Índices", true, "SELECT * siempre", false, "Estadísticas actualizadas", true, "Locks exclusivos permanentes", false));

        addMc(quiz, "¿Qué normalización evita grupos repetitivos?", null,
                QuizDifficulty.MODERATE, 1100, 20,
                opts("1NF", true, "Solo 3NF", false, "Denormalización", false, "Sharding", false));

        addMc(quiz, "¿Qué hace un INNER JOIN?", null,
                QuizDifficulty.EASY, 1000, 20,
                opts("Devuelve filas con coincidencia en ambas tablas", true,
                        "Devuelve todas las filas de la izquierda", false,
                        "Borra duplicados automáticamente", false,
                        "Crea un índice único", false));

        addTf(quiz, "TRANSACCIONES ACID incluyen Atomicity y Durability.", null,
                QuizDifficulty.EASY, 800, 15, true);

        addSa(quiz, "Escribe la palabra clave SQL para eliminar filas.",
                "Respuesta esperada: DELETE",
                QuizDifficulty.EASY, 500, 20);

        addMc(quiz, "¿Cuándo conviene denormalizar?", null,
                QuizDifficulty.HARD, 1300, 25,
                opts("Para optimizar lecturas frecuentes con datos derivados", true,
                        "Siempre, en cualquier tabla", false,
                        "Nunca, bajo ninguna circunstancia", false,
                        "Solo en bases NoSQL", false));

        publish(quiz);
    }

    private void seedSecurityQuiz(UUID orgId, UUID ownerId, Map<String, UUID> categories) {
        Quiz quiz = baseQuiz(
                orgId,
                ownerId,
                "Seguridad de aplicaciones web",
                "OWASP, autenticación, autorización y manejo seguro de secretos.",
                QuizDifficulty.HARD,
                15,
                CDN + "/security-quiz.png",
                QuizSettings.strictSettings(),
                List.of(categories.get("security"), categories.get("tech"), categories.get("backend")));

        addMc(quiz, "¿Qué ataque inyecta código en páginas vistas por otros usuarios?", null,
                QuizDifficulty.MODERATE, 1100, 20,
                opts("XSS", true, "CSRF", false, "SSRF", false, "Clickjacking only", false));

        addTf(quiz, "Las contraseñas deben almacenarse con hash + salt (o mejor, KDF).", null,
                QuizDifficulty.EASY, 800, 15, true);

        addMs(quiz, "¿Cuáles son controles útiles contra CSRF?", null,
                QuizDifficulty.HARD, 1400, 25,
                opts("Tokens anti-CSRF", true, "SameSite cookies", true,
                        "Exponer secrets en query string", false, "Validar origen/referer cuando aplique", true));

        addMc(quiz, "¿Qué principio limita privilegios al mínimo necesario?", null,
                QuizDifficulty.EASY, 1000, 20,
                opts("Least privilege", true, "Defense in depth only", false,
                        "Security through obscurity", false, "Fail open", false));

        addMc(quiz, "¿Qué no debes hacer con un JWT de acceso?", null,
                QuizDifficulty.MODERATE, 1100, 20,
                opts("Guardarlo en localStorage sin mitigaciones", true,
                        "Validar firma y expiración", false,
                        "Usar HTTPS", false,
                        "Rotar secrets de firma", false));

        addTf(quiz, "HTTPS protege la confidencialidad en tránsito.", null,
                QuizDifficulty.EASY, 800, 15, true);

        addSa(quiz, "Nombra una categoría OWASP Top 10.",
                "Ejemplos: Injection, Broken Access Control, SSRF, Cryptographic Failures.",
                QuizDifficulty.MODERATE, 700, 30);

        addMc(quiz, "¿Qué es IDOR?", null,
                QuizDifficulty.HARD, 1200, 25,
                opts("Acceso a recursos de otro usuario cambiando un identificador", true,
                        "Un algoritmo de cifrado simétrico", false,
                        "Un tipo de balanceador", false,
                        "Una política de CORS", false));

        publish(quiz);
    }

    private void seedSoftSkillsQuiz(UUID orgId, UUID ownerId, Map<String, UUID> categories) {
        Quiz quiz = baseQuiz(
                orgId,
                ownerId,
                "Comunicación efectiva en ingeniería",
                "Cómo escribir updates, facilitar reuniones y negociar alcance.",
                QuizDifficulty.EASY,
                10,
                CDN + "/soft-skills-quiz.png",
                QuizSettings.of(false, false, true, true, true, true, true),
                List.of(categories.get("soft"), categories.get("culture")));

        addMc(quiz, "¿Qué estructura ayuda en un update diario?", null,
                QuizDifficulty.EASY, 1000, 20,
                opts("Ayer / Hoy / Bloqueos", true, "Solo emojis", false,
                        "Copiar el backlog completo", false, "Silencio hasta el viernes", false));

        addTf(quiz, "Un buen PR describe el porqué, no solo el qué.", null,
                QuizDifficulty.EASY, 800, 15, true);

        addMs(quiz, "¿Qué mejora una reunión de planning?", null,
                QuizDifficulty.MODERATE, 1200, 25,
                opts("Objetivo claro", true, "Timebox", true, "Agenda compartida", true, "Sin dueño de la reunión", false));

        addMc(quiz, "Ante un scope creep, ¿qué conviene primero?", null,
                QuizDifficulty.MODERATE, 1100, 20,
                opts("Visibilizar impacto en plazo/costo y renegociar", true,
                        "Aceptar todo en silencio", false,
                        "Ignorar al stakeholder", false,
                        "Bajar calidad sin avisar", false));

        addSa(quiz, "Escribe una frase corta para pedir feedback en un PR.",
                "Respuesta libre.",
                QuizDifficulty.EASY, 600, 30);

        addTf(quiz, "Escuchar para entender suele desbloquear conflictos más rápido.", null,
                QuizDifficulty.EASY, 800, 15, true);

        publish(quiz);
    }

    private void seedCloudAwsQuiz(UUID orgId, UUID ownerId, Map<String, UUID> categories) {
        Quiz quiz = baseQuiz(
                orgId,
                ownerId,
                "Cloud essentials (AWS)",
                "Servicios base de cómputo, red, almacenamiento y responsbilidad compartida.",
                QuizDifficulty.MODERATE,
                12,
                CDN + "/aws-quiz.png",
                QuizSettings.defaultSettings(),
                List.of(categories.get("devops"), categories.get("tech")));

        addMc(quiz, "¿Qué servicio es object storage?", null,
                QuizDifficulty.EASY, 1000, 20,
                opts("S3", true, "EC2", false, "RDS", false, "VPC", false));

        addTf(quiz, "En el modelo de responsabilidad compartida, el cliente configura su IAM.", null,
                QuizDifficulty.EASY, 800, 15, true);

        addMs(quiz, "¿Qué componentes suelen usarse para alta disponibilidad?", null,
                QuizDifficulty.MODERATE, 1200, 25,
                opts("Multi-AZ", true, "Load balancer", true, "Single point of failure", false, "Auto Scaling", true));

        addMc(quiz, "¿Para qué sirve una VPC?", null,
                QuizDifficulty.EASY, 1000, 20,
                opts("Red virtual aislada en la nube", true, "Cola de mensajes", false,
                        "CDN global", false, "Registro DNS solo público", false));

        addMc(quiz, "¿Qué servicio gestiona bases relacionales administradas?", null,
                QuizDifficulty.EASY, 1000, 20,
                opts("RDS", true, "S3", false, "CloudFront", false, "SQS", false));

        addTf(quiz, "Un Security Group actúa como firewall a nivel de instancia/ENI.", null,
                QuizDifficulty.MODERATE, 900, 15, true);

        addSa(quiz, "Nombra una región de AWS.",
                "Ejemplos: us-east-1, eu-west-1, sa-east-1.",
                QuizDifficulty.EASY, 500, 20);

        publish(quiz);
    }

    private void seedFrontendQuiz(UUID orgId, UUID ownerId, Map<String, UUID> categories) {
        Quiz quiz = baseQuiz(
                orgId,
                ownerId,
                "Frontend moderno y accesibilidad",
                "HTML semántico, rendimiento percibido y bases de accesibilidad web.",
                QuizDifficulty.MODERATE,
                11,
                CDN + "/frontend-quiz.png",
                QuizSettings.of(true, true, true, true, false, true, true),
                List.of(categories.get("frontend"), categories.get("tech")));

        addMc(quiz, "¿Qué atributo mejora accesibilidad de imágenes?", null,
                QuizDifficulty.EASY, 1000, 20,
                opts("alt", true, "srcset only", false, "defer", false, "crossorigin", false));

        addTf(quiz, "Usar solo divs para todo suele empeorar la semántica.", null,
                QuizDifficulty.EASY, 800, 15, true);

        addMs(quiz, "¿Qué prácticas ayudan al rendimiento percibido?", null,
                QuizDifficulty.MODERATE, 1200, 25,
                opts("Lazy loading de imágenes", true, "Code splitting", true,
                        "Bloquear el hilo principal con JS pesado", false, "Skeleton/placeholders", true));

        addMc(quiz, "¿Qué es el Critical Rendering Path?", null,
                QuizDifficulty.HARD, 1300, 25,
                opts("Secuencia para pintar el primer contenido útil", true,
                        "Un pipeline de CI", false,
                        "Un patrón de DDD", false,
                        "Una política CORS", false));

        addMc(quiz, "¿Qué nivel WCAG suele pedirse en productos serios?", null,
                QuizDifficulty.MODERATE, 1100, 20,
                opts("AA", true, "Ninguno", false, "Solo AAA obligatorio siempre", false, "Solo AAA en botones", false));

        addTf(quiz, "Los labels asociados a inputs mejoran usabilidad y a11y.", null,
                QuizDifficulty.EASY, 800, 15, true);

        addSa(quiz, "Nombra un framework/librería de UI.",
                "Ejemplos: React, Vue, Angular, Svelte.",
                QuizDifficulty.EASY, 500, 20);

        publish(quiz);
    }

    /** Draft quiz (not published) for UI filters / status demos. */
    private void seedArchitectureDraft(UUID orgId, UUID ownerId, Map<String, UUID> categories) {
        Quiz quiz = baseQuiz(
                orgId,
                ownerId,
                "DDD y Clean Architecture (borrador)",
                "Borrador interno: bounded contexts, aggregates y puertos. Aún en revisión.",
                QuizDifficulty.HARD,
                20,
                CDN + "/ddd-draft.png",
                QuizSettings.of(false, false, true, true, true, true, false),
                List.of(categories.get("architecture"), categories.get("backend")));

        addMc(quiz, "¿Qué define un Aggregate Root?", null,
                QuizDifficulty.HARD, 1300, 25,
                opts("El límite de consistencia transaccional del cluster de entidades", true,
                        "Cualquier DTO de API", false,
                        "Una tabla SQL", false,
                        "Un controller REST", false));

        addTf(quiz, "El dominio no debe depender de Spring ni de JPA.", null,
                QuizDifficulty.MODERATE, 900, 15, true);

        addMs(quiz, "¿Qué pertenece típicamente al dominio?", null,
                QuizDifficulty.HARD, 1400, 25,
                opts("Invariantes de negocio", true, "Value Objects", true,
                        "Entity Graph de Hibernate", false, "Domain Events", true));

        addMc(quiz, "¿Para qué sirve un Anti-Corruption Layer?", null,
                QuizDifficulty.HARD, 1300, 25,
                opts("Traducir modelos entre bounded contexts / sistemas externos", true,
                        "Acelerar queries SQL", false,
                        "Reemplazar tests", false,
                        "Evitar value objects", false));

        addSa(quiz, "Nombra un bounded context de este proyecto.",
                "Ejemplos: identity, organization, quiz, gameplay.",
                QuizDifficulty.MODERATE, 700, 30);

        // Intentionally not published
        quizRepository.save(quiz);
    }

    /** Archived published-then-archived content for status demos. */
    private void seedOnboardingArchived(UUID orgId, UUID ownerId, Map<String, UUID> categories) {
        Quiz quiz = baseQuiz(
                orgId,
                ownerId,
                "Bienvenida Clabs 2024 (archivado)",
                "Versión anterior del onboarding cultural. Conservada solo como referencia.",
                QuizDifficulty.EASY,
                5,
                CDN + "/onboarding-archived.png",
                QuizSettings.defaultSettings(),
                List.of(categories.get("onboarding"), categories.get("culture")));

        addMc(quiz, "¿Cuál es el valor principal al pedir ayuda?", null,
                QuizDifficulty.EASY, 900, 15,
                opts("Desbloquear al equipo más rápido", true, "Demostrar debilidad", false,
                        "Evitar documentación", false, "Saltarse reviews", false));

        addTf(quiz, "El onboarding termina el primer día.", null,
                QuizDifficulty.EASY, 700, 10, false);

        addSa(quiz, "Escribe un saludo corto para un nuevo compañero.",
                "Respuesta libre.",
                QuizDifficulty.EASY, 500, 20);

        quiz.publish();
        quiz.archive();
        quizRepository.save(quiz);
    }

    // --- helpers -----------------------------------------------------------------

    private Quiz baseQuiz(
            UUID orgId,
            UUID ownerId,
            String title,
            String description,
            QuizDifficulty difficulty,
            int minutes,
            String thumbnail,
            QuizSettings settings,
            List<UUID> categoryIds) {
        return baseQuiz(null, orgId, ownerId, title, description, difficulty, minutes, thumbnail, settings, categoryIds);
    }

    private Quiz baseQuiz(
            UUID fixedId,
            UUID orgId,
            UUID ownerId,
            String title,
            String description,
            QuizDifficulty difficulty,
            int minutes,
            String thumbnail,
            QuizSettings settings,
            List<UUID> categoryIds) {
        Quiz quiz;
        if (fixedId != null) {
            LocalDateTime now = LocalDateTime.now();
            quiz = Quiz.rehydrate(
                    fixedId,
                    orgId,
                    ownerId,
                    title,
                    null,
                    null,
                    QuizStatus.DRAFT,
                    QuizDifficulty.EASY,
                    null,
                    QuizSettings.defaultSettings(),
                    0,
                    0.0,
                    false,
                    List.of(),
                    List.of(),
                    now,
                    now);
        } else {
            quiz = Quiz.create(orgId, title, ownerId);
        }
        quiz.changeDescription(description);
        quiz.changeDifficulty(difficulty);
        quiz.changeEstimatedTime(EstimatedTime.ofMinutes(minutes));
        quiz.changeThumbnail(thumbnail);
        quiz.changeSettings(settings);
        categoryIds.forEach(quiz::addCategory);
        return quiz;
    }

    private void publish(Quiz quiz) {
        quiz.publish();
        quizRepository.save(quiz);
    }

    private void addMc(
            Quiz quiz,
            String title,
            String description,
            QuizDifficulty difficulty,
            int points,
            int seconds,
            List<OptionSeed> options) {
        Question question = quiz.addQuestion(title, QuestionType.MULTIPLE_CHOICE);
        quiz.updateQuestion(question.getId(), title, description, difficulty, points, seconds);
        options.forEach(option -> quiz.addAnswerOption(question.getId(), option.text(), option.correct()));
    }

    private void addTf(
            Quiz quiz,
            String title,
            String description,
            QuizDifficulty difficulty,
            int points,
            int seconds,
            boolean correctIsTrue) {
        Question question = quiz.addQuestion(title, QuestionType.TRUE_FALSE);
        quiz.updateQuestion(question.getId(), title, description, difficulty, points, seconds);
        quiz.addAnswerOption(question.getId(), "Verdadero", correctIsTrue);
        quiz.addAnswerOption(question.getId(), "Falso", !correctIsTrue);
    }

    private void addMs(
            Quiz quiz,
            String title,
            String description,
            QuizDifficulty difficulty,
            int points,
            int seconds,
            List<OptionSeed> options) {
        Question question = quiz.addQuestion(title, QuestionType.MULTIPLE_SELECT);
        quiz.updateQuestion(question.getId(), title, description, difficulty, points, seconds);
        options.forEach(option -> quiz.addAnswerOption(question.getId(), option.text(), option.correct()));
    }

    private void addSa(
            Quiz quiz,
            String title,
            String description,
            QuizDifficulty difficulty,
            int points,
            int seconds) {
        Question question = quiz.addQuestion(title, QuestionType.SHORT_ANSWER);
        quiz.updateQuestion(question.getId(), title, description, difficulty, points, seconds);
    }

    private void attachImage(Quiz quiz, Question question, String fileName, String alt) {
        quiz.attachAsset(
                question.getId(),
                MediaType.IMAGE,
                CDN + "/" + fileName,
                CDN + "/" + fileName.replace(".png", "-thumb.png"),
                alt,
                null);
    }

    private static Question lastQuestion(Quiz quiz) {
        List<Question> questions = quiz.getQuestions();
        return questions.get(questions.size() - 1);
    }

    private static List<OptionSeed> opts(Object... values) {
        if (values.length % 2 != 0) {
            throw new IllegalArgumentException("opts expects pairs of (text, correct)");
        }
        OptionSeed[] options = new OptionSeed[values.length / 2];
        for (int i = 0; i < values.length; i += 2) {
            options[i / 2] = new OptionSeed((String) values[i], (Boolean) values[i + 1]);
        }
        return List.of(options);
    }

    private record OptionSeed(String text, boolean correct) {
    }
}
