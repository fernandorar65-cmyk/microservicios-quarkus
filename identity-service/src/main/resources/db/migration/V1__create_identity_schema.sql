-- Identity write model — migrated from Kahoot CLABS monolith (V1 identity tables).
-- No cross-service FKs. No user_roles (users.role_id only).

CREATE TABLE permissions (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    module VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_permissions_name_module UNIQUE (name, module)
);

CREATE TABLE roles (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(30) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_roles_type UNIQUE (type)
);

CREATE TABLE role_permissions (
    role_id UUID NOT NULL,
    permission_id UUID NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES roles (id),
    CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES permissions (id)
);

CREATE TABLE users (
    id UUID NOT NULL PRIMARY KEY,
    role_id UUID,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(80) NOT NULL,
    last_name VARCHAR(80) NOT NULL,
    status VARCHAR(20) NOT NULL,
    phone_number VARCHAR(30),
    birth_date DATE,
    bio TEXT,
    location VARCHAR(150),
    last_login TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles (id)
);

CREATE TABLE user_images (
    id UUID NOT NULL PRIMARY KEY,
    user_id UUID NOT NULL,
    url VARCHAR(500) NOT NULL,
    type VARCHAR(100) NOT NULL,
    alt VARCHAR(100) NOT NULL,
    slug VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_user_images_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_user_images_user_id ON user_images (user_id);
CREATE INDEX idx_user_images_user_type ON user_images (user_id, type);
