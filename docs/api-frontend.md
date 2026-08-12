# API Frontend — Kahoot CLABS

Referencia de endpoints REST para construir el frontend.

> **Nota:** hoy no hay API Gateway. Cada microservicio corre en su propio puerto. En local apunta a la base URL correspondiente (o configura un proxy en el frontend).

## Servicios y puertos (local)

| Servicio | Base URL | Swagger |
|----------|----------|---------|
| identity-service | `http://localhost:8081` | `/swagger-ui` |
| organization-service | `http://localhost:8082` | `/swagger-ui` |
| quiz-service | `http://localhost:8083` | `/swagger-ui` |
| gameplay-service | `http://localhost:8084` | `/swagger-ui` |

OpenAPI: `http://localhost:{port}/q/openapi`

## Envelope de respuesta

La mayoría de endpoints devuelven:

```json
{
  "timestamp": "2026-08-11T20:00:00Z",
  "status": 200,
  "message": "User retrieved",
  "data": {},
  "errors": {}
}
```

- Éxito: `data` con el payload.
- Validación (400): `errors` como mapa campo → mensaje.
- Algunos `DELETE` / change-password responden `204 No Content` sin body.

## Content-Types

| Tipo | Uso |
|------|-----|
| `application/json` | Default |
| `multipart/form-data` | Upload de imágenes / crear org / update profile |

---

# 1. Identity Service (`:8081`)

## Auth

### `POST /api/v1/auth/register`

Registrar usuario.

**Body (JSON)**

```json
{
  "email": "user@example.com",
  "firstName": "Ana",
  "lastName": "Pérez",
  "password": "secret123"
}
```

| Campo | Tipo | Reglas |
|-------|------|--------|
| email | string | required, email |
| firstName | string | required, 1–80 |
| lastName | string | required, 1–80 |
| password | string | required, 8–100 |

**Response `201`** — `data: AuthUserResponse`

```json
{
  "userId": "uuid",
  "email": "user@example.com",
  "firstName": "Ana",
  "lastName": "Pérez"
}
```

### `POST /api/v1/auth/login`

**Body (JSON)**

```json
{
  "email": "user@example.com",
  "password": "secret123"
}
```

**Response `200`** — `data: AuthUserResponse` (mismo shape).

> Hoy no hay JWT en la respuesta. El frontend debe guardar `userId` (y luego el auth real cuando se agregue).

## Users

### `GET /api/v1/users/{id}`

Obtener perfil.

**Response `200`** — `data: UserProfileResponse`

```json
{
  "id": "uuid",
  "roleId": "uuid|null",
  "email": "user@example.com",
  "firstName": "Ana",
  "lastName": "Pérez",
  "status": "ACTIVE",
  "phoneNumber": "+57...",
  "birthDate": "1995-01-15",
  "bio": "...",
  "location": "Bogotá",
  "profileImageUrl": "https://..."
}
```

### `PUT /api/v1/users/{id}/profile`

Actualizar perfil + avatar opcional.

**Content-Type:** `multipart/form-data`

| Campo form | Tipo | Notas |
|------------|------|-------|
| phoneNumber | string | max 30 |
| birthDate | string | `YYYY-MM-DD` |
| bio | string | |
| location | string | max 150 |
| avatar | file | opcional (imagen) |

**Response `200`** — `data: UserProfileResponse`

### `PUT /api/v1/users/{id}/password`

**Body (JSON)**

```json
{
  "currentPassword": "oldpass12",
  "newPassword": "newpass12"
}
```

**Response:** `204 No Content`

### `GET /api/v1/users/{id}/roles`

**Response `200`** — `data: UserRoleResponse[]`

```json
[
  { "name": "PERMISSION_NAME", "description": "..." }
]
```

### `PUT /api/v1/users/{id}/role`

Asignar rol.

**Body (JSON)**

```json
{
  "roleType": "COMMON_MEMBER"
}
```

**`roleType` enum**

- `ADMIN`
- `OWNER_ORGANIZATION`
- `RH_ORGANIZATION`
- `COMMON_MEMBER`

**Response `200`** — `data: UserProfileResponse`

---

# 2. Organization Service (`:8082`)

## Organizations

### `POST /api/v1/organizations`

Crear organización.

**Content-Type:** `multipart/form-data`

| Campo | Tipo | Reglas |
|-------|------|--------|
| name | string | required, 2–150 |
| slug | string | required, 2–100 |
| description | string | opcional, max 2000 |
| logo | file | opcional (jpeg/png/webp/gif, ≤5MB) |

**Response `201`** — `data: OrganizationResponse`

### `GET /api/v1/organizations/{id}`

**Response `200`** — `data: OrganizationResponse`

```json
{
  "id": "uuid",
  "name": "CLABS",
  "slug": "clabs",
  "description": "...",
  "logo": "https://...",
  "timezone": "America/Bogota",
  "language": "es",
  "status": "ACTIVE",
  "members": [
    {
      "id": "uuid",
      "userId": "uuid",
      "roleId": "uuid",
      "status": "INVITED",
      "joinedAt": null
    }
  ]
}
```

### `PUT /api/v1/organizations/{id}`

**Body (JSON)**

```json
{
  "name": "CLABS Updated",
  "description": "Nueva descripción"
}
```

**Response `200`** — `data: OrganizationResponse`

### `POST /api/v1/organizations/{id}/invitations`

Invitar miembro por email.

**Body (JSON)**

```json
{
  "email": "member@example.com",
  "roleType": "COMMON_MEMBER"
}
```

**Response `201`** — `data: OrganizationResponse`

---

# 3. Quiz Service (`:8083`)

## Categories

### `POST /api/v1/categories`

**Body (JSON)**

```json
{
  "organizationId": "uuid",
  "name": "Ciencia",
  "description": "Preguntas de ciencia",
  "color": "#00AAFF",
  "icon": "flask"
}
```

**Response `201`** — `data: CategoryResponse`

```json
{
  "id": "uuid",
  "organizationId": "uuid",
  "name": "Ciencia",
  "description": "...",
  "color": "#00AAFF",
  "icon": "flask"
}
```

### `GET /api/v1/categories?organizationId={uuid}`

Listar por organización.

**Query**

| Param | Tipo | Required |
|-------|------|----------|
| organizationId | uuid | sí (para filtrar) |

**Response `200`** — `data: CategoryResponse[]`

### `GET /api/v1/categories/{id}`

**Response `200`** — `data: CategoryResponse`

### `PUT /api/v1/categories/{id}`

**Body (JSON)**

```json
{
  "name": "Ciencia",
  "description": "...",
  "color": "#00AAFF",
  "icon": "flask"
}
```

**Response `200`** — `data: CategoryResponse`

### `DELETE /api/v1/categories/{id}`

**Response:** `204 No Content`

## Quizzes

Base: `/api/v1/organizations/{organizationId}/quizzes`

### `POST /api/v1/organizations/{organizationId}/quizzes`

**Body (JSON)**

```json
{
  "title": "Quiz de onboarding",
  "createdById": "uuid"
}
```

**Response `201`** — `data: QuizResponse`

### `GET /api/v1/organizations/{organizationId}/quizzes`

Listar quizzes de la org.

**Response `200`** — `data: QuizResponse[]` (listado; `questions` puede venir vacío)

### `GET /api/v1/organizations/{organizationId}/quizzes/{quizId}`

Detalle (incluye preguntas/opciones/assets cuando el read model las tenga).

**Response `200`** — `data: QuizResponse`

```json
{
  "id": "uuid",
  "organizationId": "uuid",
  "createdById": "uuid",
  "title": "...",
  "description": "...",
  "thumbnail": "https://...",
  "status": "DRAFT",
  "difficulty": "EASY",
  "estimatedTimeMinutes": 10,
  "playCount": 0,
  "averageRating": 0.0,
  "template": false,
  "categoryIds": ["uuid"],
  "questionCount": 2,
  "questions": [
    {
      "id": "uuid",
      "title": "¿Capital de Colombia?",
      "description": null,
      "type": "MULTIPLE_CHOICE",
      "difficulty": "EASY",
      "points": 1000,
      "timeLimitSeconds": 20,
      "orderIndex": 0,
      "options": [
        { "id": "uuid", "text": "Bogotá", "orderIndex": 0 }
      ],
      "asset": {
        "id": "uuid",
        "type": "IMAGE",
        "url": "https://...",
        "thumbnailUrl": null,
        "altText": "...",
        "durationSeconds": null
      }
    }
  ],
  "createdAt": "2026-08-11T12:00:00",
  "updatedAt": "2026-08-11T12:00:00"
}
```

> En listados/detalle desde read model, las opciones **no** exponen `correct` (seguridad para jugadores).

### `PUT /api/v1/organizations/{organizationId}/quizzes/{quizId}`

**Body (JSON)**

```json
{
  "title": "Quiz actualizado",
  "description": "Descripción",
  "difficulty": "MODERATE",
  "estimatedTimeMinutes": 15,
  "settings": {
    "randomQuestions": false,
    "randomAnswers": true,
    "showCorrectAnswer": true,
    "showRanking": true,
    "allowRetry": false,
    "showTimer": true,
    "musicEnabled": false
  }
}
```

**`difficulty`:** `EASY` | `MODERATE` | `HARD`

**Response `200`** — `data: QuizResponse`

### Categorías del quiz

| Método | Path | Body |
|--------|------|------|
| `POST` | `.../quizzes/{quizId}/categories/{categoryId}` | — |
| `DELETE` | `.../quizzes/{quizId}/categories/{categoryId}` | — → `204` |

### Preguntas

#### `POST .../quizzes/{quizId}/questions`

```json
{
  "title": "¿Capital de Colombia?",
  "description": null,
  "type": "MULTIPLE_CHOICE",
  "difficulty": "EASY",
  "points": 1000,
  "timeLimitSeconds": 20
}
```

**`type`:** `MULTIPLE_CHOICE` | `TRUE_FALSE` | `MULTIPLE_SELECT` | `SHORT_ANSWER`

**Response `201`** — `data: QuizResponse`

#### `PUT .../quizzes/{quizId}/questions/{questionId}`

```json
{
  "title": "...",
  "description": null,
  "difficulty": "EASY",
  "points": 1000,
  "timeLimitSeconds": 20
}
```

#### `PUT .../quizzes/{quizId}/questions/order`

```json
{
  "questionIds": ["uuid-q1", "uuid-q2", "uuid-q3"]
}
```

#### `DELETE .../quizzes/{quizId}/questions/{questionId}` → `204`

### Opciones de respuesta

#### `POST .../questions/{questionId}/options`

```json
{
  "text": "Bogotá",
  "correct": true
}
```

#### `PUT .../questions/{questionId}/options/{optionId}`

Mismo body que create.

#### `PUT .../questions/{questionId}/options/order`

```json
{
  "optionIds": ["uuid-o1", "uuid-o2"]
}
```

#### `DELETE .../questions/{questionId}/options/{optionId}` → `204`

### Assets

#### `POST .../questions/{questionId}/assets`

```json
{
  "type": "IMAGE",
  "url": "https://...",
  "thumbnailUrl": null,
  "altText": "mapa",
  "durationSeconds": null
}
```

**`type`:** `IMAGE` | `VIDEO` | `AUDIO` | `GIF` | `DOCUMENT`

#### `PUT .../questions/{questionId}/assets/{assetId}` — mismo body

#### `DELETE .../questions/{questionId}/assets/{assetId}` → `204`

#### `POST .../questions/{questionId}/assets/images`

**Content-Type:** `multipart/form-data`

| Campo | Tipo |
|-------|------|
| file | file (required) |
| altText | string |

**Response `201`** — `data: QuizResponse`

### Ciclo de vida

| Método | Path | Body | Status |
|--------|------|------|--------|
| `POST` | `.../quizzes/{quizId}/publish` | — | 200 |
| `POST` | `.../quizzes/{quizId}/archive` | — | 200 |
| `POST` | `.../quizzes/{quizId}/duplicate` | `{ "createdById": "uuid" }` | 201 |

> Al publicar un quiz se emite evento Kafka `quiz.events` (gameplay construye snapshot jugable).

---

# 4. Gameplay Service (`:8084`)

Base: `/api/v1/organizations/{organizationId}/sessions`

## Sesiones

### `POST /api/v1/organizations/{organizationId}/sessions`

Crear sesión (lobby).

```json
{
  "quizId": "uuid",
  "hostUserId": "uuid"
}
```

**Response `201`** — `data: GameSessionResponse`

```json
{
  "id": "uuid",
  "organizationId": "uuid",
  "quizId": "uuid",
  "hostUserId": "uuid",
  "status": "LOBBY",
  "currentQuestionIndex": 0,
  "playerCount": 0,
  "questionCount": 5,
  "startedAt": null,
  "finishedAt": null,
  "createdAt": "...",
  "updatedAt": "..."
}
```

**`status`:** `LOBBY` | `QUESTION_OPEN` | `QUESTION_RESULT` | `FINISHED` | `CANCELLED`

### `GET .../sessions`

**Query**

| Param | Tipo | Notas |
|-------|------|-------|
| status | string | filtro opcional |
| quizId | uuid | filtro opcional |

**Response `200`** — `data: GameSessionResponse[]`

### `GET .../sessions/{sessionId}`

**Response `200`** — `data: GameSessionResponse`

## Host — ciclo de vida

Body común (`HostActionCommand`):

```json
{ "hostUserId": "uuid" }
```

| Método | Path | Descripción |
|--------|------|-------------|
| `POST` | `.../sessions/{sessionId}/start` | Iniciar |
| `POST` | `.../sessions/{sessionId}/cancel` | Cancelar |
| `POST` | `.../sessions/{sessionId}/finish` | Terminar |

## Jugadores

### `POST .../sessions/{sessionId}/join`

```json
{
  "userId": "uuid",
  "nickname": "Ana"
}
```

**Response `201`** — `data: GameSessionResponse`

### `POST .../sessions/{sessionId}/leave`

```json
{ "userId": "uuid" }
```

### `GET .../sessions/{sessionId}/players`

**Response `200`** — `data: SessionPlayerResponse[]`

```json
[
  {
    "id": "uuid",
    "userId": "uuid",
    "nickname": "Ana",
    "score": 0,
    "connected": true,
    "joinedAt": "...",
    "leftAt": null
  }
]
```

### `PATCH .../sessions/{sessionId}/players/me`

```json
{
  "userId": "uuid",
  "nickname": "NuevaNick"
}
```

**Response `200`** — `data: SessionPlayerResponse`

## Preguntas en vivo

### `POST .../sessions/{sessionId}/questions/open`

```json
{
  "hostUserId": "uuid",
  "questionIndex": 0
}
```

`questionIndex` opcional; si se omite, abre la actual/siguiente según dominio.

### `POST .../sessions/{sessionId}/questions/close`

```json
{ "hostUserId": "uuid" }
```

### `POST .../sessions/{sessionId}/questions/next`

```json
{ "hostUserId": "uuid" }
```

### `GET .../sessions/{sessionId}/questions?asHost=false`

| Param | Default | Notas |
|-------|---------|-------|
| asHost | `false` | si `true`, puede revelar `correct` en opciones |

**Response `200`** — `data: SessionQuestionResponse[]`

```json
[
  {
    "id": "uuid",
    "orderIndex": 0,
    "points": 1000,
    "timeLimitSeconds": 20,
    "title": "...",
    "description": null,
    "questionType": "MULTIPLE_CHOICE",
    "openedAt": null,
    "closedAt": null,
    "options": [
      {
        "id": "uuid",
        "text": "Bogotá",
        "orderIndex": 0,
        "correct": null
      }
    ]
  }
]
```

### `GET .../sessions/{sessionId}/questions/current`

Pregunta actual abierta.

### `GET .../sessions/{sessionId}/questions/{sessionQuestionId}/result`

**Response `200`** — `data: QuestionResultResponse`

```json
{
  "sessionQuestionId": "uuid",
  "orderIndex": 0,
  "title": "...",
  "correctOptionId": "uuid",
  "totalAnswers": 10,
  "correctAnswers": 7,
  "optionCounts": { "uuid-option": 7 },
  "answers": []
}
```

## Respuestas

### `POST .../sessions/{sessionId}/answers`

```json
{
  "userId": "uuid",
  "sessionAnswerOptionId": "uuid"
}
```

**Response `201`** — `data: PlayerAnswerResponse`

```json
{
  "id": "uuid",
  "sessionQuestionId": "uuid",
  "sessionPlayerId": "uuid",
  "sessionAnswerOptionId": "uuid",
  "correct": true,
  "responseTimeMs": 1234,
  "awardedPoints": 800,
  "answeredAt": "..."
}
```

### `GET .../sessions/{sessionId}/answers/me?userId={uuid}`

**Query:** `userId` (uuid)

**Response `200`** — `data: PlayerAnswerResponse[]`

## Leaderboard

### `GET .../sessions/{sessionId}/leaderboard`

**Response `200`** — `data: LeaderboardEntryResponse[]`

```json
[
  {
    "rank": 1,
    "playerId": "uuid",
    "userId": "uuid",
    "nickname": "Ana",
    "score": 2500,
    "connected": true
  }
]
```

---

# Flujos sugeridos para el frontend

## A) Auth + org

```text
1. POST /auth/register  (o /login)
2. Guardar userId
3. POST /organizations (multipart)  → organizationId
4. PUT  /users/{id}/role  (OWNER_ORGANIZATION si aplica)
5. POST /organizations/{id}/invitations
```

## B) Crear y publicar quiz

```text
1. POST /categories
2. POST /organizations/{orgId}/quizzes
3. POST .../questions (+ options)
4. POST .../categories/{categoryId}
5. PUT  .../quizzes/{quizId}  (settings/difficulty)
6. POST .../publish
```

## C) Partida (host + players)

```text
Host:
1. POST .../sessions                { quizId, hostUserId }
2. Esperar joins
3. POST .../start
4. POST .../questions/open
5. POST .../questions/close
6. GET  .../questions/{id}/result
7. POST .../questions/next  (o finish)

Player:
1. POST .../join
2. GET  .../questions/current
3. POST .../answers
4. GET  .../leaderboard
```

---

# Checklist frontend

- [ ] Configurar base URLs por servicio (o proxy `/identity`, `/organization`, `/quiz`, `/gameplay`)
- [ ] Tipar `ApiResponse<T>`
- [ ] Multipart en: register profile avatar, create org, upload question image
- [ ] Enums tipados: `RoleType`, `QuizDifficulty`, `QuestionType`, `MediaType`, `SessionStatus`
- [ ] Polling (o WebSocket futuro) en gameplay para pregunta actual / leaderboard
- [ ] No confiar en `correct` de opciones salvo `asHost=true` o endpoint de result
- [ ] Swagger UI por servicio para probar rápido

---

# Endpoints sin implementar aún (útil para roadmap UI)

- Listado de organizaciones del usuario
- Auth JWT / refresh / me
- Accept invitation / leave org / list members dedicado
- WebSocket realtime para gameplay
- API Gateway unificado

Cuando existan, este documento debería actualizarse.
