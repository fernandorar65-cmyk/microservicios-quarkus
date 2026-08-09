-- Write model schema for organization-service
-- External references (created_by, user_id, invited_by) are UUID only — no cross-service FKs

-- ---------------------------------------------------------------------------
-- organizations
-- ---------------------------------------------------------------------------
CREATE TABLE organizations (
    id              UUID            NOT NULL,
    name            VARCHAR(150)    NOT NULL,
    slug            VARCHAR(100)    NOT NULL,
    description     TEXT,
    logo_url        VARCHAR(500),
    status          VARCHAR(20)     NOT NULL,
    created_by      UUID            NOT NULL,
    created_at      TIMESTAMPTZ     NOT NULL,
    updated_at      TIMESTAMPTZ     NOT NULL,

    CONSTRAINT pk_organizations PRIMARY KEY (id),
    CONSTRAINT uq_organizations_slug UNIQUE (slug),
    CONSTRAINT ck_organizations_status CHECK (
        status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED', 'PENDING')
    ),
    CONSTRAINT ck_organizations_name_not_blank CHECK (btrim(name) <> ''),
    CONSTRAINT ck_organizations_slug_not_blank CHECK (btrim(slug) <> '')
);

CREATE INDEX idx_organizations_status ON organizations (status);
CREATE INDEX idx_organizations_created_by ON organizations (created_by);

-- ---------------------------------------------------------------------------
-- organization_departments (scoped per organization)
-- ---------------------------------------------------------------------------
CREATE TABLE organization_departments (
    id                  UUID            NOT NULL,
    organization_id     UUID            NOT NULL,
    name                VARCHAR(150)    NOT NULL,
    description         TEXT,
    created_at          TIMESTAMPTZ     NOT NULL,
    updated_at          TIMESTAMPTZ     NOT NULL,

    CONSTRAINT pk_organization_departments PRIMARY KEY (id),
    CONSTRAINT fk_organization_departments_org
        FOREIGN KEY (organization_id) REFERENCES organizations (id) ON DELETE CASCADE,
    CONSTRAINT uq_organization_departments_org_name UNIQUE (organization_id, name),
    CONSTRAINT ck_organization_departments_name_not_blank CHECK (btrim(name) <> '')
);

CREATE INDEX idx_organization_departments_org_id ON organization_departments (organization_id);

-- ---------------------------------------------------------------------------
-- organization_jobs (scoped per organization)
-- ---------------------------------------------------------------------------
CREATE TABLE organization_jobs (
    id                  UUID            NOT NULL,
    organization_id     UUID            NOT NULL,
    name                VARCHAR(150)    NOT NULL,
    description         TEXT,
    created_at          TIMESTAMPTZ     NOT NULL,
    updated_at          TIMESTAMPTZ     NOT NULL,

    CONSTRAINT pk_organization_jobs PRIMARY KEY (id),
    CONSTRAINT fk_organization_jobs_org
        FOREIGN KEY (organization_id) REFERENCES organizations (id) ON DELETE CASCADE,
    CONSTRAINT uq_organization_jobs_org_name UNIQUE (organization_id, name),
    CONSTRAINT ck_organization_jobs_name_not_blank CHECK (btrim(name) <> '')
);

CREATE INDEX idx_organization_jobs_org_id ON organization_jobs (organization_id);

-- ---------------------------------------------------------------------------
-- organization_members
-- ---------------------------------------------------------------------------
CREATE TABLE organization_members (
    id                  UUID            NOT NULL,
    organization_id     UUID            NOT NULL,
    user_id             UUID            NOT NULL,
    status              VARCHAR(20)     NOT NULL,
    department_id       UUID,
    job_id              UUID,
    joined_at           TIMESTAMPTZ,
    created_at          TIMESTAMPTZ     NOT NULL,
    updated_at          TIMESTAMPTZ     NOT NULL,

    CONSTRAINT pk_organization_members PRIMARY KEY (id),
    CONSTRAINT fk_organization_members_org
        FOREIGN KEY (organization_id) REFERENCES organizations (id) ON DELETE CASCADE,
    CONSTRAINT fk_organization_members_department
        FOREIGN KEY (department_id) REFERENCES organization_departments (id) ON DELETE SET NULL,
    CONSTRAINT fk_organization_members_job
        FOREIGN KEY (job_id) REFERENCES organization_jobs (id) ON DELETE SET NULL,
    CONSTRAINT uq_organization_members_org_user UNIQUE (organization_id, user_id),
    CONSTRAINT ck_organization_members_status CHECK (
        status IN ('INVITED', 'ACTIVE', 'SUSPENDED', 'REMOVED')
    )
);

CREATE INDEX idx_organization_members_org_id ON organization_members (organization_id);
CREATE INDEX idx_organization_members_user_id ON organization_members (user_id);
CREATE INDEX idx_organization_members_status ON organization_members (status);

-- ---------------------------------------------------------------------------
-- organization_invitations
-- ---------------------------------------------------------------------------
CREATE TABLE organization_invitations (
    id                  UUID            NOT NULL,
    organization_id     UUID            NOT NULL,
    email               VARCHAR(255)    NOT NULL,
    invited_by          UUID            NOT NULL,
    status              VARCHAR(20)     NOT NULL,
    token               VARCHAR(255),
    expires_at          TIMESTAMPTZ,
    created_at          TIMESTAMPTZ     NOT NULL,
    updated_at          TIMESTAMPTZ     NOT NULL,

    CONSTRAINT pk_organization_invitations PRIMARY KEY (id),
    CONSTRAINT fk_organization_invitations_org
        FOREIGN KEY (organization_id) REFERENCES organizations (id) ON DELETE CASCADE,
    CONSTRAINT uq_organization_invitations_token UNIQUE (token),
    CONSTRAINT ck_organization_invitations_status CHECK (
        status IN ('PENDING', 'ACCEPTED', 'EXPIRED', 'REVOKED')
    ),
    CONSTRAINT ck_organization_invitations_email_not_blank CHECK (btrim(email) <> '')
);

CREATE INDEX idx_organization_invitations_org_id ON organization_invitations (organization_id);
CREATE INDEX idx_organization_invitations_email ON organization_invitations (email);
CREATE INDEX idx_organization_invitations_status ON organization_invitations (status);
