-- Temporary local setup: one shared Postgres database for all microservices.
-- Later each service will use its own DB again (identity_db, organization_db, ...).
CREATE DATABASE kahoot_db;
