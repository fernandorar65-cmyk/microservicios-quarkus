#!/usr/bin/env bash
# Apply demo seeds to the temporary shared local databases.
#
# Prerequisites:
#   1. docker compose up -d
#   2. Start Quarkus services once so Flyway creates tables in kahoot_db
#      (identity, organization, quiz, gameplay — any order)
#   3. Run this script from infrastructure/

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
PG_CONTAINER="${PG_CONTAINER:-kahoot-ms-postgres}"
MONGO_CONTAINER="${MONGO_CONTAINER:-kahoot-mongodb}"
PG_DB="${PG_DB:-kahoot_db}"

echo "==> Seeding Postgres database: ${PG_DB}"
for file in \
  "${ROOT_DIR}/01_identity.sql" \
  "${ROOT_DIR}/02_organization.sql" \
  "${ROOT_DIR}/03_quiz.sql" \
  "${ROOT_DIR}/04_gameplay.sql"
do
  echo "    applying $(basename "$file")"
  docker exec -i "${PG_CONTAINER}" psql -U postgres -d "${PG_DB}" < "${file}"
done

echo "==> Seeding Mongo database: kahoot_read_db"
docker exec -i "${MONGO_CONTAINER}" mongosh --quiet < "${ROOT_DIR}/05_mongo.js"

echo "==> Seed complete"
echo "Demo login: admin@kahoot-clabs.local / Admin123!"
