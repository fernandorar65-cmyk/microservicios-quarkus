# Seeds locales (dev)

Los microservicios Quarkus siembran datos en **arranque** cuando `app.seed.enabled=true`
(activo por defecto en perfil `%dev`).

Los UUIDs se generan en el dominio (`UUID.randomUUID()`), no están hardcodeados.

Cada seeder:

1. Persiste el **write model** en PostgreSQL (dentro de JTA)
2. Tras el commit publica Kafka
3. El consumer proyecta Mongo **fuera** de la TX JPA (standalone Mongo no soporta retryable writes / multi-doc TX)

## Orden recomendado

1. Infra:
   ```bash
   cd infrastructure && docker compose up -d
   ```
2. Arrancar **identity-service** (roles, permissions, users → `identity.user.events`)
3. Arrancar **organization-service** (catálogos + org `clabs` → `organization.events`)
4. Arrancar **quiz-service** (categoría Java + quiz `Java Basics` publicado → `quiz.read.events` + `quiz.events`)
5. Arrancar **gameplay-service** (sesión LOBBY; necesita el snapshot de `quiz.events` en Mongo)

Si gameplay arranca antes de que exista el snapshot, reinícialo tras el seed de quiz.

## Credenciales demo

| Usuario | Password |
|---|---|
| `admin@kahoot-clabs.local` | `Admin123!` |
| `owner@kahoot-clabs.local` | `Admin123!` |
| `rh@kahoot-clabs.local` | `Admin123!` |
| `member@kahoot-clabs.local` | `Admin123!` |

Organización demo: slug `clabs`  
Quiz demo: título `Java Basics`

## Flags

```properties
app.seed.enabled=false
%dev.app.seed.enabled=true
```

```yaml
# identity application.yml
app:
  seed:
    enabled: ${APP_SEED_ENABLED:false}
"%dev":
  app:
    seed:
      enabled: true
```

Desactivar: `APP_SEED_ENABLED=false` o quitar perfil `%dev`.

## SQL legacy

Los scripts `01_identity.sql` … `04_gameplay.sql` con UUIDs fijos quedan **obsoletos**
frente a los seeders Quarkus. Puedes ignorarlos o borrarlos más adelante.

## Nota DB compartida

En local, organization/quiz/gameplay resuelven users/org vía SQL en `kahoot_db`
(temporal). Cuando haya DB por servicio, sustituir por REST/ports de integración.
