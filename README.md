# Kahoot CLABS — microservicios Quarkus

Arquitectura hexagonal / DDD / CQRS:

| Servicio | Puerto | Swagger UI | OpenAPI |
|---|---|---|---|
| identity-service | 8081 | http://localhost:8081/swagger-ui | http://localhost:8081/q/openapi |
| organization-service | 8082 | http://localhost:8082/swagger-ui | http://localhost:8082/q/openapi |
| quiz-service | 8083 | http://localhost:8083/swagger-ui | http://localhost:8083/q/openapi |
| gameplay-service | 8084 | http://localhost:8084/swagger-ui | http://localhost:8084/q/openapi |

## Infra local

`infrastructure/docker-compose.yml`:

- Postgres `5433` → DB temporal compartida `kahoot_db`
- Mongo `27028` → DB temporal compartida `kahoot`
- Kafka/Redpanda `9092`

Topics: `identity.events`, `organization.events`, `quiz.events`, `gameplay.events`.

Los microservicios Quarkus **no ejecutan seeds**. Solo lógica + Flyway de schema.

## Seeds (infra)

Ver `infrastructure/seed/README.md`.

```bash
cd infrastructure && docker compose up -d
# arrancar los 4 quarkus (para que Flyway cree tablas)
cd seed && ./seed.sh
```

Credenciales demo: `admin@kahoot-clabs.local` / `Admin123!`
