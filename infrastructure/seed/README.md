# Seeds locales (temporales)

Por ahora:

- **1 Postgres**: `kahoot_db`
- **1 Mongo**: `kahoot`
- Las seeds viven **aquí**, no en los microservicios Quarkus

Más adelante volverás a separar DB por servicio.

## Orden

1. Levantar infra:
   ```bash
   cd infrastructure
   docker compose up -d
   ```
2. Arrancar los 4 servicios Quarkus (crean tablas con Flyway en `kahoot_db`).
3. Correr seeds:
   ```bash
   cd infrastructure/seed
   chmod +x seed.sh
   ./seed.sh
   ```

## Credenciales demo

| Usuario | Password |
|---|---|
| `admin@kahoot-clabs.local` | `Admin123!` |
| `owner@kahoot-clabs.local` | `Admin123!` |
| `member@kahoot-clabs.local` | `Admin123!` |

## Nota sobre volumen Postgres

Si el volumen ya existía con `identity_db` / etc., recrea infra:

```bash
docker compose down -v
docker compose up -d
```
