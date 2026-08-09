-- Organization write model — migrated from Kahoot CLABS monolith (V1 + V2 catalogs).
-- External refs (user_id, role_id) are UUID only — no FK to identity-service.
-- No organization_invitations (not in current schema).

CREATE TABLE organizations (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    slug VARCHAR(100) NOT NULL,
    logo_url VARCHAR(500),
    description TEXT,
    timezone VARCHAR(64) NOT NULL,
    language VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_organizations_slug UNIQUE (slug)
);

CREATE TABLE organization_members (
    id UUID NOT NULL PRIMARY KEY,
    organization_id UUID NOT NULL,
    user_id UUID NOT NULL,
    role_id UUID,
    status VARCHAR(20) NOT NULL,
    joined_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_organization_members_org_user UNIQUE (organization_id, user_id),
    CONSTRAINT fk_organization_members_organization FOREIGN KEY (organization_id) REFERENCES organizations (id)
);

CREATE INDEX idx_organization_members_user_id ON organization_members (user_id);

-- Global catalogs (no organization_id) — seeded from Java when needed.
CREATE TABLE organization_departments (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(100) NOT NULL,
    CONSTRAINT uq_organization_departments_name UNIQUE (name)
);

CREATE TABLE organization_jobs (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(100) NOT NULL,
    CONSTRAINT uq_organization_jobs_name UNIQUE (name)
);

CREATE TABLE organization_statuses (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(100) NOT NULL,
    CONSTRAINT uq_organization_statuses_name UNIQUE (name)
);

CREATE TABLE organization_member_statuses (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(100) NOT NULL,
    CONSTRAINT uq_organization_member_statuses_name UNIQUE (name)
);
