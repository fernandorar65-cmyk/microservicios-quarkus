-- Demo identity data for shared kahoot_db.
-- Password for all users: Admin123!
-- Hash generated with jBCrypt ($2a$).

INSERT INTO permissions (id, name, description, module, created_at, updated_at) VALUES
  ('a1000001-0000-0000-0000-000000000001', 'PLATFORM_FULL_ACCESS', 'Acceso total a la plataforma', 'platform', NOW(), NOW()),
  ('a1000001-0000-0000-0000-000000000002', 'ORGANIZATION_EDIT', 'Editar organización', 'organization', NOW(), NOW()),
  ('a1000001-0000-0000-0000-000000000003', 'MEMBER_MANAGE', 'Gestionar miembros', 'organization', NOW(), NOW()),
  ('a1000001-0000-0000-0000-000000000004', 'QUIZ_CREATE', 'Crear quizzes', 'quiz', NOW(), NOW()),
  ('a1000001-0000-0000-0000-000000000005', 'QUIZ_EDIT', 'Editar quizzes', 'quiz', NOW(), NOW()),
  ('a1000001-0000-0000-0000-000000000006', 'PROFILE_EDIT', 'Editar perfil', 'user', NOW(), NOW()),
  ('a1000001-0000-0000-0000-000000000007', 'SESSION_JOIN_ANYTIME', 'Ingresar a sesiones siempre', 'session', NOW(), NOW()),
  ('a1000001-0000-0000-0000-000000000008', 'SESSION_JOIN_WHEN_ENABLED', 'Ingresar a sesiones habilitadas', 'session', NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO roles (id, name, type, description, created_at, updated_at) VALUES
  ('22222222-2222-2222-2222-222222222221', 'Administrator', 'ADMIN', 'Acceso total', NOW(), NOW()),
  ('22222222-2222-2222-2222-222222222222', 'Organization Owner', 'OWNER_ORGANIZATION', 'Dueño de organización', NOW(), NOW()),
  ('22222222-2222-2222-2222-222222222223', 'Organization HR', 'RH_ORGANIZATION', 'RRHH de organización', NOW(), NOW()),
  ('22222222-2222-2222-2222-222222222224', 'Common Member', 'COMMON_MEMBER', 'Miembro común', NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id) VALUES
  ('22222222-2222-2222-2222-222222222221', 'a1000001-0000-0000-0000-000000000001'),
  ('22222222-2222-2222-2222-222222222222', 'a1000001-0000-0000-0000-000000000002'),
  ('22222222-2222-2222-2222-222222222222', 'a1000001-0000-0000-0000-000000000003'),
  ('22222222-2222-2222-2222-222222222222', 'a1000001-0000-0000-0000-000000000004'),
  ('22222222-2222-2222-2222-222222222222', 'a1000001-0000-0000-0000-000000000005'),
  ('22222222-2222-2222-2222-222222222222', 'a1000001-0000-0000-0000-000000000006'),
  ('22222222-2222-2222-2222-222222222222', 'a1000001-0000-0000-0000-000000000007'),
  ('22222222-2222-2222-2222-222222222224', 'a1000001-0000-0000-0000-000000000006'),
  ('22222222-2222-2222-2222-222222222224', 'a1000001-0000-0000-0000-000000000008')
ON CONFLICT DO NOTHING;

-- bcrypt hash for Admin123!
INSERT INTO users (
  id, role_id, email, password_hash, first_name, last_name, status, created_at, updated_at
) VALUES
  ('33333333-3333-3333-3333-333333333331', '22222222-2222-2222-2222-222222222221',
   'admin@kahoot-clabs.local', '$2a$10$AgZXFNgC2/fE.6Fip6SFZ.9H/.J6WlVousn3EuPlfoTH3ZdsWN.4e',
   'System', 'Admin', 'ACTIVE', NOW(), NOW()),
  ('33333333-3333-3333-3333-333333333332', '22222222-2222-2222-2222-222222222222',
   'owner@kahoot-clabs.local', '$2a$10$AgZXFNgC2/fE.6Fip6SFZ.9H/.J6WlVousn3EuPlfoTH3ZdsWN.4e',
   'Org', 'Owner', 'ACTIVE', NOW(), NOW()),
  ('33333333-3333-3333-3333-333333333333', '22222222-2222-2222-2222-222222222223',
   'rh@kahoot-clabs.local', '$2a$10$AgZXFNgC2/fE.6Fip6SFZ.9H/.J6WlVousn3EuPlfoTH3ZdsWN.4e',
   'Org', 'HR', 'ACTIVE', NOW(), NOW()),
  ('33333333-3333-3333-3333-333333333334', '22222222-2222-2222-2222-222222222224',
   'member@kahoot-clabs.local', '$2a$10$AgZXFNgC2/fE.6Fip6SFZ.9H/.J6WlVousn3EuPlfoTH3ZdsWN.4e',
   'Common', 'Member', 'ACTIVE', NOW(), NOW())
ON CONFLICT DO NOTHING;
