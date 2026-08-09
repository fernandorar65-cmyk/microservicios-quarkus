# Kahoot CLABS — microservicios Quarkus

Arquitectura hexagonal / DDD / CQRS:

| Servicio | Puerto | Swagger UI | OpenAPI |
|---|---|---|---|
| identity-service | 8081 | http://localhost:8081/swagger-ui | http://localhost:8081/q/openapi |
| organization-service | 8082 | http://localhost:8082/swagger-ui | http://localhost:8082/q/openapi |
| quiz-service | 8083 | http://localhost:8083/swagger-ui | http://localhost:8083/q/openapi |
| gameplay-service | 8084 | http://localhost:8084/swagger-ui | http://localhost:8084/q/openapi |

Infra local: `infrastructure/docker-compose.yml` (Postgres `5433`, Mongo `27018`, Kafka `9092`).

## Seeders (demo local)

Por defecto desactivados (`APP_SEED_ENABLED=false`). Para cargar datos demo:

1. Levantar infra Docker.
2. En cada `.env` (o entorno): `APP_SEED_ENABLED=true`.
3. Arrancar en orden: **identity → organization → quiz → gameplay**.

IDs estables en `SeedIds` (duplicados por servicio a propósito; sin DB compartida).

Credenciales demo por defecto:

- Admin: `admin@kahoot-clabs.local` / `Admin123!`
- Owner: `owner@kahoot-clabs.local` / misma password
- Member: `member@kahoot-clabs.local` / misma password

Los tests fuerzan `%test.app.seed.enabled=false`.
