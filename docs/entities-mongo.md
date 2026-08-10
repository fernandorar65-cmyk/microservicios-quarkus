Actúa como Software Architect Senior especializado en Java 21, Quarkus, DDD, Clean Architecture, Hexagonal Architecture, CQRS, MongoDB, PostgreSQL y Event-Driven Architecture.

Estamos implementando el READ SIDE de los cuatro microservicios de Kahoot CLABS:

```text
identity-service
organization-service
quiz-service
gameplay-service
```

PostgreSQL ya representa el Write Model / Source of Truth.

MongoDB será exclusivamente el Read Model de CQRS.

IMPORTANTE:

MongoDB NO debe copiar 1:1 las tablas PostgreSQL.

Las colecciones Mongo deben estar diseñadas para responder eficientemente a Queries y listados.

Debe aceptarse duplicación controlada de información porque los documentos son proyecciones denormalizadas.

---

# OBJETIVO

Configurar MongoDB en los cuatro proyectos Quarkus y crear los modelos de lectura iniciales.

Implementar:

* dependencia MongoDB de Quarkus;
* configuración de conexión;
* Read Documents;
* repositories/adapters Mongo;
* índices Mongo importantes;
* estructura de packages;
* configuración Docker si corresponde.

NO implementar todavía:

* Kafka producers;
* Kafka consumers;
* Outbox Pattern;
* proyección automática desde PostgreSQL;
* Domain Events;
* Commands;
* REST endpoints;
* lógica de escritura sobre Mongo;
* sincronización PostgreSQL → Mongo.

Mongo debe quedar listo para recibir posteriormente las projections.

---

# PRINCIPIO CQRS

Arquitectura:

```text
COMMAND SIDE

Command
   ↓
Domain
   ↓
Repository Port
   ↓
PostgreSQL
```

y:

```text
QUERY SIDE

Query
   ↓
Read Port
   ↓
MongoDB
```

PostgreSQL continúa siendo el Source of Truth.

MongoDB es reconstruible.

Si Mongo se elimina, conceptualmente debe ser posible volver a generar sus documentos desde PostgreSQL y/o eventos.

---

# DATABASES MONGO

Utilizar una instancia MongoDB local con cuatro bases lógicas:

```text
identity-service
→ identity_read_db

organization-service
→ organization_read_db

quiz-service
→ quiz_read_db

gameplay-service
→ gameplay_read_db
```

Los servicios NO deben leer directamente la base Mongo de otro servicio.

---

# DEPENDENCIA QUARKUS

Revisa el `pom.xml` de cada proyecto.

Utilizar la extensión oficial de Quarkus para MongoDB.

Preferir:

```text
quarkus-mongodb-panache
```

si utilizaremos Repository Pattern.

NO utilizar Active Record dentro de los modelos de lectura.

No crear Documents extendiendo innecesariamente:

```java
PanacheMongoEntity
```

Preferir POJOs + Repository Pattern para mantener separación arquitectónica.

MongoDB with Panache pertenece exclusivamente a Infrastructure.

---

# CONFIGURACIÓN

En cada servicio configurar:

```properties
quarkus.mongodb.connection-string=${MONGO_URI:mongodb://localhost:27017}
quarkus.mongodb.database=${MONGO_DATABASE:<database>}
```

Usar:

identity-service:

```properties
quarkus.mongodb.database=${MONGO_DATABASE:identity_read_db}
```

organization-service:

```properties
quarkus.mongodb.database=${MONGO_DATABASE:organization_read_db}
```

quiz-service:

```properties
quarkus.mongodb.database=${MONGO_DATABASE:quiz_read_db}
```

gameplay-service:

```properties
quarkus.mongodb.database=${MONGO_DATABASE:gameplay_read_db}
```

No hardcodear credenciales reales.

---

# ESTRUCTURA DE PACKAGES

Cada servicio debe organizar el Read Side aproximadamente así:

```text
application/
└── port/
    └── out/
        └── read/

infrastructure/
└── persistence/
    └── mongo/
        ├── document/
        ├── repository/
        ├── adapter/
        └── index/
```

Ejemplo:

```text
quiz/
├── application/
│   └── port/
│       └── out/
│           └── read/
│               └── QuizReadPort.java
│
└── infrastructure/
    └── persistence/
        └── mongo/
            ├── document/
            │   └── QuizReadDocument.java
            ├── repository/
            │   └── QuizMongoRepository.java
            └── adapter/
                └── QuizReadAdapter.java
```

---

# REGLA DE MODELOS

Los documentos Mongo:

NO son Aggregates.

NO son Entities de dominio.

NO son JPA Entities.

Son modelos optimizados para lectura.

Por ejemplo:

```text
Quiz
```

del dominio

NO debe ser reutilizado como:

```text
QuizReadDocument
```

Mantener separación:

```text
Domain Model
≠
JPA Model
≠
Mongo Read Model
≠
API DTO
```

---

# ==================================================

# 1. IDENTITY-SERVICE

# ==================================================

Crear inicialmente las siguientes colecciones:

```text
users
roles
```

NO crear una colección Mongo diferente por cada tabla PostgreSQL.

Por ejemplo:

```text
permissions
role_permissions
user_images
```

pueden estar embebidos dentro de documentos de lectura cuando sea conveniente.

---

# USERS READ MODEL

Colección:

```text
users
```

Documento conceptual:

```json
{
  "_id": "UUID",

  "email": "user@email.com",

  "firstName": "Fernando",
  "lastName": "Aquino",
  "fullName": "Fernando Aquino",

  "status": "ACTIVE",

  "phoneNumber": null,
  "birthDate": null,
  "bio": null,
  "location": null,

  "lastLogin": null,

  "role": {
    "id": "UUID",
    "name": "Administrator",
    "type": "ADMIN",
    "permissions": [
      {
        "name": "quiz.create",
        "module": "QUIZ"
      }
    ]
  },

  "images": [
    {
      "id": "UUID",
      "url": "...",
      "type": "AVATAR",
      "alt": "...",
      "slug": "..."
    }
  ],

  "createdAt": "...",
  "updatedAt": "..."
}
```

IMPORTANTE:

NO incluir:

```text
password_hash
```

en Mongo.

Passwords y credenciales nunca pertenecen al Read Model general.

---

# USER ÍNDICES

Crear índices razonables para:

```text
email UNIQUE

status

role.type
```

No crear índices sobre campos que no serán consultados.

---

# ROLES READ MODEL

Colección:

```text
roles
```

Documento:

```json
{
  "_id": "UUID",

  "name": "Administrator",

  "type": "ADMIN",

  "description": "...",

  "permissions": [
    {
      "id": "UUID",
      "name": "quiz.create",
      "description": "...",
      "module": "QUIZ"
    }
  ],

  "createdAt": "...",
  "updatedAt": "..."
}
```

Índice:

```text
type UNIQUE
```

---

# ==================================================

# 2. ORGANIZATION-SERVICE

# ==================================================

Crear inicialmente:

```text
organizations
organization_catalogs
```

---

# ORGANIZATIONS READ MODEL

Colección:

```text
organizations
```

Documento conceptual:

```json
{
  "_id": "UUID",

  "name": "CLABS",

  "slug": "clabs",

  "logoUrl": null,

  "description": "...",

  "timezone": "America/Lima",

  "language": "es",

  "status": "ACTIVE",

  "members": [
    {
      "id": "UUID",
      "userId": "UUID",

      "user": {
        "fullName": "Fernando Aquino",
        "email": "..."
      },

      "roleId": "UUID",

      "status": "ACTIVE",

      "joinedAt": "..."
    }
  ],

  "memberCount": 10,

  "createdAt": "...",
  "updatedAt": "..."
}
```

IMPORTANTE:

El objeto:

```text
user
```

es una PROYECCIÓN local.

NO es el User Aggregate de Identity.

NO importar modelos Java desde `identity-service`.

Posteriormente esos datos podrán sincronizarse mediante eventos o mediante una proyección controlada.

---

# ORGANIZATION ÍNDICES

Considerar:

```text
slug UNIQUE

status

members.userId
```

---

# ORGANIZATION CATALOGS

Colección:

```text
organization_catalogs
```

Puede consolidar:

```text
organization_departments
organization_jobs
organization_statuses
organization_member_statuses
```

en un documento de lectura.

Ejemplo:

```json
{
  "_id": "catalog",

  "departments": [
    {
      "id": "UUID",
      "name": "...",
      "description": "..."
    }
  ],

  "jobs": [],

  "organizationStatuses": [],

  "memberStatuses": []
}
```

No es obligatorio reproducir 4 tablas como 4 colecciones.

---

# ==================================================

# 3. QUIZ-SERVICE

# ==================================================

Crear inicialmente:

```text
quizzes
categories
```

El Read Model del Quiz debe ser fuertemente denormalizado.

---

# QUIZZES READ MODEL

Colección:

```text
quizzes
```

Un documento debe representar un Quiz completo listo para lectura.

Ejemplo:

```json
{
  "_id": "UUID",

  "organizationId": "UUID",

  "createdBy": "UUID",

  "creator": {
    "id": "UUID",
    "name": "Fernando Aquino"
  },

  "title": "...",

  "description": "...",

  "thumbnailUrl": "...",

  "status": "PUBLISHED",

  "difficulty": "MEDIUM",

  "estimatedTimeMinutes": 10,

  "playCount": 0,

  "averageRating": 0,

  "isTemplate": false,

  "settings": {
    "randomQuestions": false,
    "randomAnswers": false,
    "showCorrectAnswer": true,
    "showRanking": true,
    "allowRetry": false,
    "showTimer": true,
    "musicEnabled": true
  },

  "categories": [
    {
      "id": "UUID",
      "name": "Java",
      "color": "...",
      "icon": "..."
    }
  ],

  "questions": [
    {
      "id": "UUID",

      "title": "...",

      "description": "...",

      "type": "MULTIPLE_CHOICE",

      "difficulty": "MEDIUM",

      "explanation": "...",

      "orderIndex": 0,

      "timeLimitSeconds": 30,

      "points": 1000,

      "asset": {
        "id": "UUID",
        "type": "IMAGE",
        "url": "...",
        "thumbnailUrl": "...",
        "altText": "...",
        "durationSeconds": null
      },

      "answerOptions": [
        {
          "id": "UUID",
          "text": "...",
          "isCorrect": true,
          "explanation": "...",
          "orderIndex": 0
        }
      ]
    }
  ],

  "questionCount": 10,

  "createdAt": "...",
  "updatedAt": "..."
}
```

Este documento intencionalmente reemplaza para consultas normales la necesidad de hacer joins entre:

```text
quizzes
questions
answer_options
question_assets
quiz_categories
categories
```

---

# SEGURIDAD DE RESPUESTAS

IMPORTANTE:

El Mongo Read Model puede contener:

```text
isCorrect
```

porque puede ser necesario para administración o gameplay interno.

Pero NO asumir que todos los endpoints deben exponer ese campo.

Posteriormente los API DTOs deben controlar qué información se devuelve a cada consumidor.

Read Model y REST Response NO son la misma cosa.

---

# QUIZ ÍNDICES

Crear índices razonables para:

```text
organizationId

createdBy

status

difficulty

categories.id

title
```

No crear text index automáticamente salvo que exista una query de búsqueda que lo necesite.

Si existe búsqueda textual real, documentar la necesidad antes de crearlo.

---

# CATEGORIES READ MODEL

Colección:

```text
categories
```

Documento:

```json
{
  "_id": "UUID",

  "organizationId": "UUID",

  "name": "...",

  "description": "...",

  "color": "...",

  "icon": "...",

  "quizCount": 15,

  "createdAt": "...",
  "updatedAt": "..."
}
```

Constraint lógico / índice único:

```text
organizationId + name
```

---

# ==================================================

# 4. GAMEPLAY-SERVICE

# ==================================================

Crear inicialmente:

```text
game_sessions
leaderboards
```

NO crear una colección independiente para:

```text
session_players
session_questions
session_answer_options
player_answers
```

salvo que una query concreta lo justifique.

Preferir documentos agregados por sesión.

---

# GAME SESSION READ MODEL

Colección:

```text
game_sessions
```

Documento conceptual:

```json
{
  "_id": "UUID",

  "organizationId": "UUID",

  "quizId": "UUID",

  "hostUserId": "UUID",

  "quiz": {
    "id": "UUID",
    "title": "Java Basics",
    "thumbnailUrl": "..."
  },

  "host": {
    "id": "UUID",
    "name": "Fernando Aquino"
  },

  "status": "LOBBY",

  "currentQuestionIndex": 0,

  "players": [
    {
      "id": "UUID",

      "userId": "UUID",

      "nickname": "Fernando",

      "score": 1000,

      "connected": true,

      "joinedAt": "...",

      "leftAt": null
    }
  ],

  "questions": [
    {
      "id": "UUID",

      "sourceQuestionId": "UUID",

      "orderIndex": 0,

      "points": 1000,

      "timeLimitSeconds": 30,

      "title": "...",

      "description": "...",

      "questionType": "MULTIPLE_CHOICE",

      "openedAt": null,

      "closedAt": null,

      "answerOptions": [
        {
          "id": "UUID",

          "sourceAnswerOptionId": "UUID",

          "text": "...",

          "isCorrect": true,

          "orderIndex": 0
        }
      ]
    }
  ],

  "playerAnswers": [
    {
      "id": "UUID",

      "sessionQuestionId": "UUID",

      "sessionPlayerId": "UUID",

      "sessionAnswerOptionId": "UUID",

      "isCorrect": true,

      "responseTimeMs": 2500,

      "awardedPoints": 850,

      "answeredAt": "..."
    }
  ],

  "playerCount": 10,

  "startedAt": null,

  "finishedAt": null,

  "createdAt": "...",
  "updatedAt": "..."
}
```

Este documento está orientado a consultas completas de sesión.

No significa que cada request deba devolver todos los arrays.

Los Query Handlers posteriormente pueden devolver DTOs parciales.

---

# GAME SESSION ÍNDICES

Considerar:

```text
organizationId

quizId

hostUserId

status

players.userId
```

---

# LEADERBOARD READ MODEL

En PostgreSQL NO existe `session_leaderboard`.

Eso se mantiene.

Mongo sí puede tener un Read Model especializado:

```text
leaderboards
```

porque el ranking es una consulta derivada.

Documento:

```json
{
  "_id": "SESSION_UUID",

  "sessionId": "SESSION_UUID",

  "organizationId": "UUID",

  "updatedAt": "...",

  "ranking": [
    {
      "position": 1,

      "sessionPlayerId": "UUID",

      "userId": "UUID",

      "nickname": "Fernando",

      "score": 10500
    }
  ]
}
```

Esto NO es Source of Truth.

Puede reconstruirse desde:

```text
session_players
+
player_answers
```

del Write Model.

---

# LEADERBOARD ÍNDICE

Crear:

```text
sessionId UNIQUE
```

si `_id` no se utiliza directamente como `sessionId`.

---

# NO DUPLICAR TODO POSTGRESQL

PROHIBIDO asumir:

```text
PostgreSQL table
=
Mongo collection
```

Ejemplo incorrecto:

```text
questions table
→ questions collection

answer_options table
→ answer_options collection

quiz_categories table
→ quiz_categories collection
```

sin analizar las queries.

Preferimos:

```text
QuizReadDocument
```

con todo embebido.

---

# MONGO DOCUMENTS

Utilizar clases específicas como:

```text
UserReadDocument
RoleReadDocument

OrganizationReadDocument
OrganizationCatalogReadDocument

QuizReadDocument
CategoryReadDocument

GameSessionReadDocument
LeaderboardReadDocument
```

NO reutilizar JPA Entities.

---

# MONGO PANACHE

Si se utiliza MongoDB with Panache:

Preferir Repository Pattern.

Ejemplo conceptual:

```java
@MongoEntity(collection = "quizzes")
public class QuizReadDocument {
    public UUID id;
    ...
}
```

y:

```java
@ApplicationScoped
public class QuizMongoRepository
        implements PanacheMongoRepository<QuizReadDocument> {
}
```

No utilizar Active Record dentro de estos modelos salvo decisión explícita.

---

# IDs

Mantener los IDs de negocio como UUID.

No generar IDs Mongo diferentes cuando el documento representa una proyección de un Aggregate existente.

Ejemplo:

```text
Quiz.id PostgreSQL

UUID: 123...
```

debe proyectarse como el identificador correspondiente del:

```text
QuizReadDocument
```

Esto facilita idempotencia y reproyección.

---

# PROJECTIONS

NO implementar todavía listeners ni consumers.

Pero diseñar los Documents pensando en operaciones:

```text
upsert
```

Ejemplo futuro:

```text
QuizCreated
      ↓
upsert QuizReadDocument

QuizUpdated
      ↓
update QuizReadDocument

QuizPublished
      ↓
update status
```

El diseño debe permitir reproyección completa.

---

# REPOSITORIES / READ PORTS

Los contratos deben permanecer en Application.

Ejemplo:

```java
public interface QuizReadPort {

    Optional<QuizReadModel> findById(UUID id);

    List<QuizReadModel> findByOrganization(
        UUID organizationId
    );
}
```

Infrastructure implementará:

```text
QuizMongoReadAdapter
```

Pero NO acoplar Queries directamente a Panache.

Incorrecto:

```java
@Inject
QuizMongoRepository mongoRepository;
```

en QueryHandler.

Correcto:

```java
@Inject
QuizReadPort quizReadPort;
```

---

# IMPORTANTE SOBRE READ MODELS DE APPLICATION

Si el proyecto utiliza modelos de lectura en Application:

```text
QuizReadModel
UserReadModel
GameSessionReadModel
```

separarlos de:

```text
QuizReadDocument
UserReadDocument
GameSessionReadDocument
```

El Adapter Mongo realiza el mapping.

Esto evita que Application dependa de MongoDB.

---

# MONGODB INDEXES

MongoDB no utiliza Flyway.

Crear una estrategia explícita para garantizar índices.

Puede utilizarse:

```text
MongoIndexInitializer
```

o una solución equivalente dentro de Infrastructure.

Debe ser idempotente.

NO meter creación de índices en el Domain Layer.

Antes de implementar un initializer custom, revisar si la versión de Quarkus/Mongo utilizada ofrece una forma más simple y oficial.

---

# NO CREAR LIQUIBASE MONGODB TODAVÍA

No introducir:

```text
Liquibase MongoDB
```

salvo necesidad explícita.

Para esta primera versión, preferir una inicialización pequeña e idempotente de índices.

Mongo no requiere creación previa de colecciones para insertar documentos.

---

# DOCKER COMPOSE

En infraestructura local debe existir una sola instancia MongoDB.

Ejemplo conceptual:

```yaml
mongodb:
  image: mongo
  ports:
    - "27017:27017"
```

No crear cuatro contenedores Mongo.

Utilizar una instancia con:

```text
identity_read_db
organization_read_db
quiz_read_db
gameplay_read_db
```

---

# VARIABLES DE ENTORNO

Cada `.env.example` debe incluir:

```env
MONGO_URI=mongodb://localhost:27017
MONGO_DATABASE=<read_database>
```

Ejemplos:

identity:

```env
MONGO_DATABASE=identity_read_db
```

organization:

```env
MONGO_DATABASE=organization_read_db
```

quiz:

```env
MONGO_DATABASE=quiz_read_db
```

gameplay:

```env
MONGO_DATABASE=gameplay_read_db
```

---

# NO AGREGAR PASSWORDS A READ MODELS

En especial Identity:

PROHIBIDO proyectar:

```text
password_hash
password
refresh_token
secret
```

a Mongo.

Credenciales pertenecen al Write/Security side.

---

# CROSS-SERVICE DATA

Está permitido que un Read Model contenga datos duplicados provenientes de otro contexto.

Ejemplo:

```text
OrganizationReadDocument

member.user.fullName
```

Pero:

NO importar:

```text
identity.domain.User
```

NO consultar directamente:

```text
identity_db
```

NO consultar directamente:

```text
identity_read_db
```

Desde Organization.

La información cross-service deberá llegar posteriormente mediante contratos de integración, REST controlado o Kafka.

---

# COMPILACIÓN

Después de modificar cada proyecto ejecutar:

```bash
./mvnw compile
```

o equivalente.

Verificar:

```text
Mongo dependency correcta

Mongo configuration correcta

Documents compilan

Repositories compilan

Adapters compilan

No imports Spring

No imports de otros microservicios
```

---

# CONEXIÓN

Después intentar:

```bash
./mvnw quarkus:dev
```

Verificar que cada servicio pueda conectarse a MongoDB.

No es necesario tener documentos cargados todavía.

---

# RESULTADO ESPERADO

MongoDB:

```text
MongoDB
│
├── identity_read_db
│   ├── users
│   └── roles
│
├── organization_read_db
│   ├── organizations
│   └── organization_catalogs
│
├── quiz_read_db
│   ├── quizzes
│   └── categories
│
└── gameplay_read_db
    ├── game_sessions
    └── leaderboards
```

Esto es intencionalmente distinto al esquema PostgreSQL.

---

# ENTREGA FINAL

Al terminar responde:

```text
MONGODB CONFIGURATION

IDENTITY-SERVICE
- collections
- documents
- indexes
- connection

ORGANIZATION-SERVICE
- collections
- documents
- indexes
- connection

QUIZ-SERVICE
- collections
- documents
- indexes
- connection

GAMEPLAY-SERVICE
- collections
- documents
- indexes
- connection

FILES CREATED

FILES MODIFIED

MONGO DEPENDENCIES

CROSS-SERVICE DUPLICATED DATA

SECURITY CHECK
- confirmar ausencia de password_hash en Mongo

COMPILATION

CONNECTION TEST

ARCHITECTURAL DECISIONS

NEXT STEP
```

NO continuar automáticamente con Kafka ni con projections.

La tarea termina cuando MongoDB está configurado y los Read Models iniciales están preparados para recibir datos.
