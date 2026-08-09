-- Write model schema for identity-service
-- One user has at most one role (users.role_id). No user_roles join table.

-- ---------------------------------------------------------------------------
-- roles
-- ---------------------------------------------------------------------------
CREATE TABLE roles (
    id              UUID            NOT NULL,
    name            VARCHAR(100)    NOT NULL,
    type            VARCHAR(30)     NOT NULL,
    description     TEXT,
    created_at      TIMESTAMPTZ     NOT NULL,
    updated_at      TIMESTAMPTZ     NOT NULL,

    CONSTRAINT pk_roles PRIMARY KEY (id),
    CONSTRAINT uq_roles_name UNIQUE (name),
    CONSTRAINT uq_roles_type UNIQUE (type),
    CONSTRAINT ck_roles_type CHECK (
        type IN ('ADMIN', 'OWNER_ORGANIZATION', 'RH_ORGANIZATION', 'COMMON_MEMBER')
    ),
    CONSTRAINT ck_roles_name_not_blank CHECK (btrim(name) <> '')
);

-- ---------------------------------------------------------------------------
-- permissions
-- ---------------------------------------------------------------------------
CREATE TABLE permissions (
    id              UUID            NOT NULL,
    name            VARCHAR(100)    NOT NULL,
    description     TEXT,
    created_at      TIMESTAMPTZ     NOT NULL,
    updated_at      TIMESTAMPTZ     NOT NULL,

    CONSTRAINT pk_permissions PRIMARY KEY (id),
    CONSTRAINT uq_permissions_name UNIQUE (name),
    CONSTRAINT ck_permissions_name_not_blank CHECK (btrim(name) <> '')
);

-- ---------------------------------------------------------------------------
-- role_permissions (M:N)
-- ---------------------------------------------------------------------------
CREATE TABLE role_permissions (
    role_id         UUID            NOT NULL,
    permission_id   UUID            NOT NULL,

    CONSTRAINT pk_role_permissions PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permissions_role
        FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE,
    CONSTRAINT fk_role_permissions_permission
        FOREIGN KEY (permission_id) REFERENCES permissions (id) ON DELETE CASCADE
);

CREATE INDEX idx_role_permissions_permission_id ON role_permissions (permission_id);

-- ---------------------------------------------------------------------------
-- users
-- ---------------------------------------------------------------------------
CREATE TABLE users (
    id              UUID            NOT NULL,
    email           VARCHAR(255)    NOT NULL,
    password_hash   VARCHAR(255)    NOT NULL,
    first_name      VARCHAR(80)     NOT NULL,
    last_name       VARCHAR(80)     NOT NULL,
    phone_number    VARCHAR(30),
    birth_date      DATE,
    bio             TEXT,
    location        VARCHAR(150),
    status          VARCHAR(20)     NOT NULL,
    role_id         UUID,
    last_login_at   TIMESTAMPTZ,
    created_at      TIMESTAMPTZ     NOT NULL,
    updated_at      TIMESTAMPTZ     NOT NULL,

    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT fk_users_role
        FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE SET NULL,
    CONSTRAINT ck_users_status CHECK (
        status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED', 'PENDING')
    ),
    CONSTRAINT ck_users_email_not_blank CHECK (btrim(email) <> ''),
    CONSTRAINT ck_users_password_hash_not_blank CHECK (btrim(password_hash) <> ''),
    CONSTRAINT ck_users_first_name_not_blank CHECK (btrim(first_name) <> ''),
    CONSTRAINT ck_users_last_name_not_blank CHECK (btrim(last_name) <> '')
);

CREATE INDEX idx_users_role_id ON users (role_id);
CREATE INDEX idx_users_status ON users (status);

-- ---------------------------------------------------------------------------
-- user_images
-- ---------------------------------------------------------------------------
CREATE TABLE user_images (
    id                  UUID            NOT NULL,
    user_id             UUID            NOT NULL,
    url                 TEXT            NOT NULL,
    storage_provider    VARCHAR(50),
    image_type          VARCHAR(50)     NOT NULL DEFAULT 'PROFILE',
    created_at          TIMESTAMPTZ     NOT NULL,

    CONSTRAINT pk_user_images PRIMARY KEY (id),
    CONSTRAINT fk_user_images_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT ck_user_images_url_not_blank CHECK (btrim(url) <> ''),
    CONSTRAINT ck_user_images_type CHECK (
        image_type IN ('PROFILE', 'COVER', 'OTHER')
    )
);

CREATE INDEX idx_user_images_user_id ON user_images (user_id);
