-- Demo organization data for shared kahoot_db.

INSERT INTO organization_statuses (id, name, description) VALUES
  ('b1000001-0000-0000-0000-000000000001', 'ACTIVE', 'Activo'),
  ('b1000001-0000-0000-0000-000000000002', 'INACTIVE', 'Inactivo'),
  ('b1000001-0000-0000-0000-000000000003', 'SUSPENDED', 'Suspendido'),
  ('b1000001-0000-0000-0000-000000000004', 'PENDING', 'Pendiente de activación')
ON CONFLICT DO NOTHING;

INSERT INTO organization_member_statuses (id, name, description) VALUES
  ('b2000001-0000-0000-0000-000000000001', 'INVITED', 'Invitado'),
  ('b2000001-0000-0000-0000-000000000002', 'ACTIVE', 'Activo'),
  ('b2000001-0000-0000-0000-000000000003', 'SUSPENDED', 'Suspendido')
ON CONFLICT DO NOTHING;

INSERT INTO organization_departments (id, name, description) VALUES
  ('b3000001-0000-0000-0000-000000000001', 'Ingeniería de Software', 'Desarrollo y mantenimiento'),
  ('b3000001-0000-0000-0000-000000000002', 'DevOps', 'CI/CD y releases')
ON CONFLICT DO NOTHING;

INSERT INTO organization_jobs (id, name, description) VALUES
  ('b4000001-0000-0000-0000-000000000001', 'Software Engineer', 'Backend/frontend features'),
  ('b4000001-0000-0000-0000-000000000002', 'DevOps Engineer', 'Deploy y monitoreo')
ON CONFLICT DO NOTHING;

INSERT INTO organizations (
  id, name, slug, logo_url, description, timezone, language, status, created_at, updated_at
) VALUES (
  '11111111-1111-1111-1111-111111111111',
  'Clabs',
  'clabs',
  NULL,
  'Organización demo Kahoot CLABS',
  'America/Bogota',
  'es',
  'ACTIVE',
  NOW(),
  NOW()
) ON CONFLICT DO NOTHING;

INSERT INTO organization_members (
  id, organization_id, user_id, role_id, status, joined_at, created_at, updated_at
) VALUES
  ('b5000001-0000-0000-0000-000000000001',
   '11111111-1111-1111-1111-111111111111',
   '33333333-3333-3333-3333-333333333332',
   '22222222-2222-2222-2222-222222222222',
   'ACTIVE', NOW(), NOW(), NOW()),
  ('b5000001-0000-0000-0000-000000000002',
   '11111111-1111-1111-1111-111111111111',
   '33333333-3333-3333-3333-333333333334',
   '22222222-2222-2222-2222-222222222224',
   'ACTIVE', NOW(), NOW(), NOW())
ON CONFLICT DO NOTHING;
