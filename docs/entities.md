Actúa como Software Architect Senior especializado en:

* Java 21
* Quarkus
* DDD
* Clean Architecture
* Hexagonal Architecture
* CQRS
* PostgreSQL
* JPA / Hibernate ORM
* Flyway
* Microservicios
* Apache Kafka

Estamos migrando el proyecto existente Kahoot CLABS desde un monolito modular Spring Boot hacia una arquitectura de microservicios con Quarkus.

Los microservicios definidos son:

```text
identity-service
organization-service
quiz-service
gameplay-service
```

Cada uno es un proyecto Quarkus independiente.

Antes de modificar código:

1. Revisa completamente la estructura actual de los proyectos.
2. Revisa las reglas existentes dentro de `.cursor/rules`.
3. Respeta estrictamente DDD, CQRS, Clean Architecture y Hexagonal Architecture.
4. No inventes cambios de dominio.
5. No agregues funcionalidades que no hayan sido solicitadas.
6. Usa el esquema existente del proyecto anterior como referencia funcional.
7. No intentes mejorar o rediseñar silenciosamente el modelo de datos actual.
8. Si detectas algo cuestionable arquitectónicamente, documenta la observación pero conserva el comportamiento actual salvo que sea imposible migrarlo correctamente.

---

# OBJETIVO DE ESTA ITERACIÓN

Comenzar la implementación de la capa de persistencia PostgreSQL de los cuatro microservicios.

Queremos migrar el Write Model existente conservando sus tablas, columnas, constraints y decisiones de negocio.

En esta etapa implementar exclusivamente:

```text
Flyway migrations
JPA entities
Persistence configuration
PostgreSQL datasource
Persistence enums cuando corresponda
Constraints
Indexes
Relationships internas
```

NO implementar todavía:

```text
Domain Aggregates
Value Objects
Commands
Queries
Handlers
REST endpoints
MongoDB
Read Models
Kafka
Producers
Consumers
Outbox Pattern
REST Clients
Integration Ports
JWT
Authentication
AWS
Azure
WebSockets
Notifications
```

No continúes automáticamente con esas etapas.

---

# PRINCIPIO FUNDAMENTAL

Cada microservicio es propietario exclusivo de sus datos.

Una tabla solamente puede tener Foreign Keys hacia tablas pertenecientes al mismo microservicio.

Las referencias hacia otros servicios deben mantenerse como UUID sin Foreign Key.

Ejemplo:

```text
quiz-service

quizzes.organization_id UUID
quizzes.created_by UUID
```

Estos IDs pertenecen conceptualmente a:

```text
organization-service
identity-service
```

Por lo tanto:

PROHIBIDO:

```sql
FOREIGN KEY (organization_id)
REFERENCES organizations(id)
```

PROHIBIDO:

```sql
FOREIGN KEY (created_by)
REFERENCES users(id)
```

Los IDs externos se guardan simplemente como UUID.

---

# BASES DE DATOS

Cada proyecto debe ser propietario de una base lógica independiente:

```text
identity-service
→ identity_db

organization-service
→ organization_db

quiz-service
→ quiz_db

gameplay-service
→ gameplay_db
```

En desarrollo pueden ejecutarse sobre el mismo servidor PostgreSQL.

No deben compartir tablas.

---

# 1. IDENTITY-SERVICE

Debe migrar las tablas existentes de Identity.

Crear exclusivamente:

```text
permissions
roles
role_permissions
users
user_images
```

NO crear:

```text
user_roles
```

El modelo existente utiliza:

```text
users.role_id
```

como relación hacia:

```text
roles.id
```

Conservar ese modelo.

---

## permissions

Crear:

```text
id UUID PRIMARY KEY

name VARCHAR(100) NOT NULL

description VARCHAR(255) NULL

module VARCHAR(50) NOT NULL

created_at TIMESTAMP NOT NULL

updated_at TIMESTAMP NOT NULL
```

Constraint:

```text
UNIQUE(name, module)
```

Nombre sugerido:

```text
uq_permissions_name_module
```

---

## roles

Crear:

```text
id UUID PRIMARY KEY

name VARCHAR(100) NOT NULL

type VARCHAR(30) NOT NULL

description VARCHAR(255) NULL

created_at TIMESTAMP NOT NULL

updated_at TIMESTAMP NOT NULL
```

Constraint:

```text
UNIQUE(type)
```

Conservar representación textual del enum.

Ejemplos existentes:

```text
ADMIN
OWNER_ORGANIZATION
RH_ORGANIZATION
COMMON_MEMBER
```

No convertir a números.

---

## role_permissions

Crear:

```text
role_id UUID NOT NULL
permission_id UUID NOT NULL
```

Primary Key:

```text
PRIMARY KEY(role_id, permission_id)
```

Foreign Keys internas:

```text
role_id
→ roles.id

permission_id
→ permissions.id
```

---

## users

Crear:

```text
id UUID PRIMARY KEY

role_id UUID NULL

email VARCHAR(255) NOT NULL

password_hash VARCHAR(255) NOT NULL

first_name VARCHAR(80) NOT NULL

last_name VARCHAR(80) NOT NULL

status VARCHAR(20) NOT NULL

phone_number VARCHAR(30) NULL

birth_date DATE NULL

bio TEXT NULL

location VARCHAR(150) NULL

last_login TIMESTAMP NULL

created_at TIMESTAMP NOT NULL

updated_at TIMESTAMP NOT NULL
```

Constraints:

```text
UNIQUE(email)
```

Foreign Key interna:

```text
role_id
→ roles.id
```

NO crear tabla `user_roles`.

---

## user_images

Crear:

```text
id UUID PRIMARY KEY

user_id UUID NOT NULL

url VARCHAR(500) NOT NULL

type VARCHAR(100) NOT NULL

alt VARCHAR(100) NOT NULL

slug VARCHAR(100) NOT NULL

created_at TIMESTAMP NOT NULL

updated_at TIMESTAMP NOT NULL
```

Foreign Key:

```text
user_id
→ users.id
```

Índices:

```text
idx_user_images_user_id

idx_user_images_user_type
(user_id, type)
```

---

# 2. ORGANIZATION-SERVICE

Crear exactamente:

```text
organizations
organization_members
organization_departments
organization_jobs
organization_statuses
organization_member_statuses
```

NO crear todavía:

```text
organization_invitations
```

aunque conceptualmente pueda ser útil.

No forma parte del esquema actual que estamos migrando.

---

## organizations

Crear:

```text
id UUID PRIMARY KEY

name VARCHAR(150) NOT NULL

slug VARCHAR(100) NOT NULL

logo_url VARCHAR(500) NULL

description TEXT NULL

timezone VARCHAR(64) NOT NULL

language VARCHAR(10) NOT NULL

status VARCHAR(20) NOT NULL

created_at TIMESTAMP NOT NULL

updated_at TIMESTAMP NOT NULL
```

Constraint:

```text
UNIQUE(slug)
```

---

## organization_members

Crear:

```text
id UUID PRIMARY KEY

organization_id UUID NOT NULL

user_id UUID NOT NULL

role_id UUID NULL

status VARCHAR(20) NOT NULL

joined_at TIMESTAMP NOT NULL

created_at TIMESTAMP NOT NULL

updated_at TIMESTAMP NOT NULL
```

Foreign Key interna:

```text
organization_id
→ organizations.id
```

IMPORTANTE:

```text
user_id
role_id
```

pertenecen conceptualmente a Identity.

NO crear Foreign Keys hacia Identity.

Constraint:

```text
UNIQUE(organization_id, user_id)
```

Índice:

```text
idx_organization_members_user_id
```

---

# CATÁLOGOS DE ORGANIZATION

Las siguientes tablas son catálogos globales del contexto Organization:

```text
organization_departments
organization_jobs
organization_statuses
organization_member_statuses
```

NO agregar `organization_id`.

Mantener la estructura existente:

```text
id UUID PRIMARY KEY

name VARCHAR(150) NOT NULL

description VARCHAR(100) NOT NULL
```

Constraint por cada tabla:

```text
UNIQUE(name)
```

No convertir estos catálogos en entidades específicas por organización.

Eso cambiaría el dominio existente y no forma parte de esta migración.

---

# 3. QUIZ-SERVICE

Este servicio nace de la separación del antiguo Gameplay.

Es responsable exclusivamente de la autoría del Quiz.

Crear exactamente:

```text
quizzes
categories
quiz_categories
questions
answer_options
question_assets
```

---

# quizzes

Crear:

```text
id UUID PRIMARY KEY

organization_id UUID NOT NULL

created_by UUID NOT NULL

title VARCHAR(200) NOT NULL

description TEXT NULL

thumbnail_url VARCHAR(500) NULL

status VARCHAR(20) NOT NULL

difficulty VARCHAR(20) NOT NULL

estimated_time_minutes INTEGER NULL

play_count INTEGER NOT NULL

average_rating NUMERIC(3,2) NOT NULL

is_template BOOLEAN NOT NULL

random_questions BOOLEAN NOT NULL

random_answers BOOLEAN NOT NULL

show_correct_answer BOOLEAN NOT NULL

show_ranking BOOLEAN NOT NULL

allow_retry BOOLEAN NOT NULL

show_timer BOOLEAN NOT NULL

music_enabled BOOLEAN NOT NULL

created_at TIMESTAMP NOT NULL

updated_at TIMESTAMP NOT NULL
```

IMPORTANTE:

NO crear:

```text
visibility
```

Ese campo fue eliminado del diseño actual.

El acceso está asociado a:

```text
organization_id
```

`organization_id` pertenece a Organization Service.

`created_by` pertenece a Identity Service.

Por tanto:

NO crear Foreign Keys para ninguno.

Crear índices:

```text
idx_quizzes_organization_id

idx_quizzes_created_by
```

---

# categories

Las categorías actuales pertenecen a una organización.

Crear:

```text
id UUID PRIMARY KEY

organization_id UUID NOT NULL

name VARCHAR(100) NOT NULL

description VARCHAR(255) NULL

color VARCHAR(20) NULL

icon VARCHAR(50) NULL

created_at TIMESTAMP NOT NULL

updated_at TIMESTAMP NOT NULL
```

`organization_id` pertenece a Organization Service.

NO crear FK.

Constraint:

```text
UNIQUE(organization_id, name)
```

No introducir `slug` porque no forma parte del esquema actual.

---

# quiz_categories

Crear:

```text
quiz_id UUID NOT NULL

category_id UUID NOT NULL
```

Primary Key:

```text
PRIMARY KEY(quiz_id, category_id)
```

Foreign Keys internas:

```text
quiz_id
→ quizzes.id

category_id
→ categories.id
```

---

# questions

Crear:

```text
id UUID PRIMARY KEY

quiz_id UUID NOT NULL

title VARCHAR(500) NOT NULL

description TEXT NULL

type VARCHAR(20) NOT NULL

difficulty VARCHAR(20) NOT NULL

explanation TEXT NULL

order_index INTEGER NOT NULL

time_limit_seconds INTEGER NOT NULL

points INTEGER NOT NULL

created_at TIMESTAMP NOT NULL

updated_at TIMESTAMP NOT NULL
```

Foreign Key:

```text
quiz_id
→ quizzes.id
```

Constraint:

```text
UNIQUE(quiz_id, order_index)
```

Conservar nombres existentes:

```text
order_index
time_limit_seconds
```

No renombrarlos a:

```text
position
time_limit
```

---

# answer_options

Crear:

```text
id UUID PRIMARY KEY

question_id UUID NOT NULL

text VARCHAR(500) NOT NULL

is_correct BOOLEAN NOT NULL

explanation TEXT NULL

order_index INTEGER NOT NULL

created_at TIMESTAMP NOT NULL

updated_at TIMESTAMP NOT NULL
```

Foreign Key:

```text
question_id
→ questions.id
```

Constraint:

```text
UNIQUE(question_id, order_index)
```

---

# question_assets

Actualmente existe una relación 1:1 entre pregunta y asset.

Crear:

```text
id UUID PRIMARY KEY

question_id UUID NOT NULL

type VARCHAR(20) NOT NULL

url VARCHAR(1000) NOT NULL

thumbnail_url VARCHAR(1000) NULL

alt_text VARCHAR(255) NULL

duration_seconds INTEGER NULL

created_at TIMESTAMP NOT NULL

updated_at TIMESTAMP NOT NULL
```

Foreign Key:

```text
question_id
→ questions.id
```

Constraint:

```text
UNIQUE(question_id)
```

No convertir automáticamente esta relación a 1:N.

Conservar el modelo existente.

---

# 4. GAMEPLAY-SERVICE

Gameplay queda encargado exclusivamente de la ejecución de partidas.

Crear exactamente:

```text
game_sessions
session_players
session_questions
session_answer_options
player_answers
```

NO crear:

```text
session_leaderboard
game_pin
playable_quiz_snapshots
```

en esta etapa.

---

# game_sessions

Crear:

```text
id UUID PRIMARY KEY

organization_id UUID NOT NULL

quiz_id UUID NOT NULL

host_user_id UUID NOT NULL

status VARCHAR(20) NOT NULL

current_question_index INTEGER NOT NULL

started_at TIMESTAMP NULL

finished_at TIMESTAMP NULL

created_at TIMESTAMP NOT NULL

updated_at TIMESTAMP NOT NULL
```

Referencias externas:

```text
organization_id
→ organization-service

quiz_id
→ quiz-service

host_user_id
→ identity-service
```

NO crear Foreign Keys para estos campos.

Crear índices:

```text
idx_game_sessions_organization_id

idx_game_sessions_quiz_id

idx_game_sessions_host_user_id
```

NO agregar:

```text
game_pin
join_code
```

El modelo actual no utiliza PIN.

---

# session_players

Crear:

```text
id UUID PRIMARY KEY

session_id UUID NOT NULL

user_id UUID NOT NULL

nickname VARCHAR(30) NOT NULL

score INTEGER NOT NULL

connected BOOLEAN NOT NULL

joined_at TIMESTAMP NOT NULL

left_at TIMESTAMP NULL
```

Foreign Key interna:

```text
session_id
→ game_sessions.id
```

`user_id` pertenece a Identity.

NO crear FK.

Constraints:

```text
UNIQUE(session_id, user_id)

UNIQUE(session_id, nickname)
```

Índices:

```text
idx_session_players_user_id

idx_session_players_session_id
```

---

# session_questions

Esta tabla representa el snapshot congelado de preguntas utilizado por una sesión.

Crear:

```text
id UUID PRIMARY KEY

session_id UUID NOT NULL

source_question_id UUID NULL

order_index INTEGER NOT NULL

points INTEGER NOT NULL

time_limit_seconds INTEGER NOT NULL

title VARCHAR(500) NULL

description TEXT NULL

question_type VARCHAR(20) NULL

opened_at TIMESTAMP NULL

closed_at TIMESTAMP NULL
```

Foreign Key interna:

```text
session_id
→ game_sessions.id
```

`source_question_id` pertenece conceptualmente a Quiz Service.

NO crear FK hacia Quiz.

Constraint:

```text
UNIQUE(session_id, order_index)
```

---

# session_answer_options

Crear:

```text
id UUID PRIMARY KEY

session_question_id UUID NOT NULL

source_answer_option_id UUID NULL

text VARCHAR(500) NOT NULL

is_correct BOOLEAN NOT NULL

order_index INTEGER NOT NULL
```

Foreign Key interna:

```text
session_question_id
→ session_questions.id
```

`source_answer_option_id` pertenece conceptualmente a Quiz Service.

NO crear FK externa.

Constraint:

```text
UNIQUE(session_question_id, order_index)
```

Índice:

```text
idx_session_answer_options_session_question_id
```

---

# player_answers

Crear:

```text
id UUID PRIMARY KEY

session_question_id UUID NOT NULL

session_player_id UUID NOT NULL

session_answer_option_id UUID NULL

is_correct BOOLEAN NOT NULL

response_time_ms BIGINT NOT NULL

awarded_points INTEGER NOT NULL

answered_at TIMESTAMP NOT NULL
```

Foreign Keys internas:

```text
session_question_id
→ session_questions.id

session_player_id
→ session_players.id

session_answer_option_id
→ session_answer_options.id
```

Constraint:

```text
UNIQUE(session_question_id, session_player_id)
```

Índices:

```text
idx_player_answers_session_player_id

idx_player_answers_session_question_id
```

NO agregar `session_id` si no es necesario.

La sesión puede conocerse mediante:

```text
session_question
→ game_session
```

Conservar el modelo existente.

---

# LEADERBOARD

NO crear tabla:

```text
session_leaderboard
```

El modelo actual no la utiliza.

Los rankings serán tratados posteriormente mediante CQRS/read models.

No adelantar esa implementación.

---

# UUID

Todos los IDs principales deben utilizar:

```text
java.util.UUID
```

y PostgreSQL:

```text
UUID
```

No utilizar:

```text
BIGSERIAL
SERIAL
Long autoincremental
```

sin una decisión explícita.

---

# JPA ENTITIES

Las entidades JPA deben existir exclusivamente en Infrastructure.

Ejemplo por servicio:

```text
src/main/java/.../
└── infrastructure/
    └── persistence/
        └── postgres/
            └── entity/
```

Ejemplos:

```text
UserJpaEntity
RoleJpaEntity

OrganizationJpaEntity
OrganizationMemberJpaEntity

QuizJpaEntity
QuestionJpaEntity

GameSessionJpaEntity
SessionPlayerJpaEntity
```

NO crear todavía los Aggregates de dominio.

NO poner:

```java
@Entity
```

en futuros modelos:

```text
domain/model/User
domain/model/Organization
domain/model/Quiz
domain/model/GameSession
```

Persistence Model y Domain Model permanecerán separados.

---

# RELACIONES JPA

Utilizar relaciones solamente dentro del mismo servicio.

Cuando corresponda:

```java
@ManyToOne(fetch = FetchType.LAZY)
@OneToMany(fetch = FetchType.LAZY)
@OneToOne(fetch = FetchType.LAZY)
```

Evitar:

```java
FetchType.EAGER
```

salvo necesidad explícita.

No utilizar:

```java
CascadeType.ALL
```

de manera automática.

Los cascades deben reflejar lifecycle real.

---

# REFERENCIAS CROSS-SERVICE EN JPA

Cuando un identificador pertenezca a otro servicio:

Correcto:

```java
@Column(name = "organization_id", nullable = false)
UUID organizationId;
```

Incorrecto:

```java
@ManyToOne
OrganizationJpaEntity organization;
```

No existen relaciones JPA entre microservicios.

---

# FLYWAY

Cada servicio debe tener:

```text
src/main/resources/db/migration/
```

Crear una migración inicial independiente.

Ejemplo:

```text
identity-service
V1__create_identity_schema.sql

organization-service
V1__create_organization_schema.sql

quiz-service
V1__create_quiz_schema.sql

gameplay-service
V1__create_gameplay_schema.sql
```

Las migraciones deben ejecutarse sobre bases vacías.

Flyway será propietario de la evolución del schema.

---

# CONFIGURACIÓN QUARKUS

Verifica que cada proyecto tenga las extensiones necesarias:

```text
Hibernate ORM
PostgreSQL JDBC
Flyway
```

Agregar solamente las que falten.

Configurar datasource mediante variables de entorno.

Ejemplo:

```properties
quarkus.datasource.db-kind=postgresql

quarkus.datasource.username=${DB_USERNAME:postgres}

quarkus.datasource.password=${DB_PASSWORD:postgres}

quarkus.datasource.jdbc.url=${DB_URL:jdbc:postgresql://localhost:5432/identity_db}

quarkus.flyway.migrate-at-start=true
```

Adaptar el nombre de base para cada servicio.

No hardcodear secretos reales.

---

# HIBERNATE SCHEMA MANAGEMENT

Flyway controla el schema.

No utilizar:

```text
drop-and-create
```

No depender de Hibernate para generar tablas.

Configurar Hibernate de forma compatible con migraciones administradas mediante Flyway.

---

# PANACHE

Si se utiliza Panache, debe permanecer exclusivamente en Infrastructure.

Permitido:

```java
public class QuizPanacheRepository
        implements PanacheRepository<QuizJpaEntity>
```

PROHIBIDO:

```java
public class Quiz extends PanacheEntity
```

para el modelo de dominio.

En esta iteración NO es obligatorio crear repositories Panache.

La prioridad son:

```text
Flyway
JPA Entities
Configuration
Compilation
```

---

# NO CREAR REPOSITORIES DE DOMINIO TODAVÍA

Esta etapa termina en Persistence Infrastructure.

NO crear todavía:

```text
UserRepository
OrganizationRepository
QuizRepository
GameSessionRepository
```

Esos contratos se crearán cuando construyamos el Domain Model.

---

# NO CREAR KAFKA TODAVÍA

Aunque el proyecto futuro utilizará Kafka:

NO agregar todavía:

```text
@Incoming
@Outgoing
Emitter
KafkaProducer
KafkaConsumer
Topics
Integration Events
Outbox
```

Primero consolidaremos dominio y persistencia.

---

# ORDEN DE TRABAJO

Trabaja en este orden:

```text
1. identity-service

2. organization-service

3. quiz-service

4. gameplay-service
```

Para cada servicio:

```text
1. Revisar pom.xml

2. Verificar dependencias necesarias

3. Revisar application.properties

4. Crear migración Flyway

5. Crear JPA Entities

6. Crear relaciones internas

7. Crear constraints e índices

8. Compilar

9. Corregir errores

10. Continuar al siguiente servicio
```

No avances al siguiente servicio dejando errores de compilación conocidos en el anterior.

---

# VALIDACIONES

Después de implementar cada servicio ejecuta:

```bash
./mvnw compile
```

o:

```bash
mvn compile
```

según cómo esté configurado el proyecto.

Si existen tests ya creados y pueden ejecutarse:

```bash
./mvnw test
```

No elimines tests para conseguir que compile.

---

# REVISIÓN DE ARQUITECTURA

Antes de crear cualquier Foreign Key, comprobar:

```text
¿Las dos tablas pertenecen al mismo microservicio?
```

Si SÍ:

```text
FK permitida
```

Si NO:

```text
solo UUID
sin FK
sin @ManyToOne
sin imports cross-service
```

---

# REGLAS IMPORTANTES

No inventar:

```text
user_roles

organization_invitations

quizzes.visibility

categories.slug

game_sessions.game_pin

game_sessions.join_code

session_leaderboard

playable_quiz_snapshots
```

No modificar el dominio actual para hacerlo parecerse a ejemplos genéricos de Kahoot.

Estamos migrando primero el comportamiento existente.

---

# RESULTADO ESPERADO

Al terminar deben existir cuatro Write Models PostgreSQL separados:

```text
IDENTITY_DB

permissions
roles
role_permissions
users
user_images
```

```text
ORGANIZATION_DB

organizations
organization_members
organization_departments
organization_jobs
organization_statuses
organization_member_statuses
```

```text
QUIZ_DB

quizzes
categories
quiz_categories
questions
answer_options
question_assets
```

```text
GAMEPLAY_DB

game_sessions
session_players
session_questions
session_answer_options
player_answers
```

---

# ENTREGA FINAL

Cuando termines, genera un resumen:

```text
IDENTITY-SERVICE
- archivos creados
- archivos modificados
- migración
- tablas
- compilación

ORGANIZATION-SERVICE
- archivos creados
- archivos modificados
- migración
- tablas
- compilación

QUIZ-SERVICE
- archivos creados
- archivos modificados
- migración
- tablas
- compilación

GAMEPLAY-SERVICE
- archivos creados
- archivos modificados
- migración
- tablas
- compilación

REFERENCIAS CROSS-SERVICE DETECTADAS
- listar UUIDs externos
- confirmar que no tienen FK

DECISIONES ARQUITECTÓNICAS
- explicar solamente decisiones relevantes

ERRORES O DEUDA DETECTADA
- listar sin intentar rediseñar todo el proyecto

SIGUIENTE PASO RECOMENDADO
```

No continúes automáticamente con el siguiente paso.

---

# REGLA FINAL

Esta iteración es una MIGRACIÓN ESTRUCTURAL del Write Model existente.

Prioridades:

```text
1. preservar comportamiento actual
2. separar ownership por microservicio
3. eliminar relaciones físicas cross-service
4. mantener DDD preparado para la siguiente etapa
5. evitar sobreingeniería
```

No generes arquitectura nueva solo porque sería técnicamente posible.

Cuando exista duda entre inventar una mejora y conservar el modelo actual:

CONSERVA EL MODELO ACTUAL y documenta la posible mejora para una futura iteración.
