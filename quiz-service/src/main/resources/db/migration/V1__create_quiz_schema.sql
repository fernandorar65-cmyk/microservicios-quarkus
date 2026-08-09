-- Write model schema for quiz-service
-- External references (organization_id, created_by) are UUID only — no cross-service FKs

-- ---------------------------------------------------------------------------
-- quizzes (Aggregate Root)
-- ---------------------------------------------------------------------------
CREATE TABLE quizzes (
    id                      UUID            NOT NULL,
    organization_id         UUID            NOT NULL,
    title                   VARCHAR(200)    NOT NULL,
    description             TEXT,
    thumbnail_url           VARCHAR(500),
    status                  VARCHAR(20)     NOT NULL,
    difficulty              VARCHAR(20),
    estimated_time_minutes  INTEGER,
    random_questions        BOOLEAN         NOT NULL DEFAULT FALSE,
    random_answers          BOOLEAN         NOT NULL DEFAULT FALSE,
    show_correct_answer     BOOLEAN         NOT NULL DEFAULT TRUE,
    show_ranking            BOOLEAN         NOT NULL DEFAULT TRUE,
    allow_retry             BOOLEAN         NOT NULL DEFAULT FALSE,
    show_timer              BOOLEAN         NOT NULL DEFAULT TRUE,
    music_enabled           BOOLEAN         NOT NULL DEFAULT FALSE,
    created_by              UUID            NOT NULL,
    created_at              TIMESTAMPTZ     NOT NULL,
    updated_at              TIMESTAMPTZ     NOT NULL,

    CONSTRAINT pk_quizzes PRIMARY KEY (id),
    CONSTRAINT ck_quizzes_status CHECK (status IN ('DRAFT', 'PUBLISHED', 'ARCHIVED')),
    CONSTRAINT ck_quizzes_difficulty CHECK (
        difficulty IS NULL OR difficulty IN ('EASY', 'MODERATE', 'HARD')
    ),
    CONSTRAINT ck_quizzes_estimated_time CHECK (
        estimated_time_minutes IS NULL OR estimated_time_minutes > 0
    ),
    CONSTRAINT ck_quizzes_title_not_blank CHECK (btrim(title) <> '')
);

CREATE INDEX idx_quizzes_organization_id ON quizzes (organization_id);
CREATE INDEX idx_quizzes_created_by ON quizzes (created_by);
CREATE INDEX idx_quizzes_status ON quizzes (status);
CREATE INDEX idx_quizzes_organization_status ON quizzes (organization_id, status);

-- ---------------------------------------------------------------------------
-- categories (own lifecycle within quiz-service)
-- ---------------------------------------------------------------------------
CREATE TABLE categories (
    id              UUID            NOT NULL,
    name            VARCHAR(100)    NOT NULL,
    description     TEXT,
    slug            VARCHAR(120)    NOT NULL,
    created_at      TIMESTAMPTZ     NOT NULL,
    updated_at      TIMESTAMPTZ     NOT NULL,

    CONSTRAINT pk_categories PRIMARY KEY (id),
    CONSTRAINT uq_categories_slug UNIQUE (slug),
    CONSTRAINT ck_categories_name_not_blank CHECK (btrim(name) <> ''),
    CONSTRAINT ck_categories_slug_not_blank CHECK (btrim(slug) <> '')
);

-- ---------------------------------------------------------------------------
-- questions (owned by Quiz aggregate)
-- ---------------------------------------------------------------------------
CREATE TABLE questions (
    id              UUID            NOT NULL,
    quiz_id         UUID            NOT NULL,
    text            TEXT            NOT NULL,
    type            VARCHAR(30)     NOT NULL,
    position        INTEGER         NOT NULL,
    time_limit      INTEGER,
    points          INTEGER,
    created_at      TIMESTAMPTZ     NOT NULL,
    updated_at      TIMESTAMPTZ     NOT NULL,

    CONSTRAINT pk_questions PRIMARY KEY (id),
    CONSTRAINT fk_questions_quiz
        FOREIGN KEY (quiz_id) REFERENCES quizzes (id) ON DELETE CASCADE,
    CONSTRAINT uq_questions_quiz_position UNIQUE (quiz_id, position),
    CONSTRAINT ck_questions_type CHECK (
        type IN ('SINGLE_CHOICE', 'MULTIPLE_CHOICE', 'TRUE_FALSE')
    ),
    CONSTRAINT ck_questions_position CHECK (position >= 0),
    CONSTRAINT ck_questions_time_limit CHECK (time_limit IS NULL OR time_limit > 0),
    CONSTRAINT ck_questions_points CHECK (points IS NULL OR points >= 0),
    CONSTRAINT ck_questions_text_not_blank CHECK (btrim(text) <> '')
);

CREATE INDEX idx_questions_quiz_id ON questions (quiz_id);

-- ---------------------------------------------------------------------------
-- answer_options (owned by Question within Quiz aggregate)
-- ---------------------------------------------------------------------------
CREATE TABLE answer_options (
    id              UUID            NOT NULL,
    question_id     UUID            NOT NULL,
    text            TEXT            NOT NULL,
    is_correct      BOOLEAN         NOT NULL,
    position        INTEGER         NOT NULL,
    created_at      TIMESTAMPTZ     NOT NULL,
    updated_at      TIMESTAMPTZ     NOT NULL,

    CONSTRAINT pk_answer_options PRIMARY KEY (id),
    CONSTRAINT fk_answer_options_question
        FOREIGN KEY (question_id) REFERENCES questions (id) ON DELETE CASCADE,
    CONSTRAINT uq_answer_options_question_position UNIQUE (question_id, position),
    CONSTRAINT ck_answer_options_position CHECK (position >= 0),
    CONSTRAINT ck_answer_options_text_not_blank CHECK (btrim(text) <> '')
);

CREATE INDEX idx_answer_options_question_id ON answer_options (question_id);

-- ---------------------------------------------------------------------------
-- question_assets (metadata only — no binary storage)
-- ---------------------------------------------------------------------------
CREATE TABLE question_assets (
    id              UUID            NOT NULL,
    question_id     UUID            NOT NULL,
    media_type      VARCHAR(30)     NOT NULL,
    url             TEXT            NOT NULL,
    created_at      TIMESTAMPTZ     NOT NULL,

    CONSTRAINT pk_question_assets PRIMARY KEY (id),
    CONSTRAINT fk_question_assets_question
        FOREIGN KEY (question_id) REFERENCES questions (id) ON DELETE CASCADE,
    CONSTRAINT ck_question_assets_media_type CHECK (
        media_type IN ('IMAGE', 'VIDEO', 'AUDIO')
    ),
    CONSTRAINT ck_question_assets_url_not_blank CHECK (btrim(url) <> '')
);

CREATE INDEX idx_question_assets_question_id ON question_assets (question_id);

-- ---------------------------------------------------------------------------
-- quiz_categories (many-to-many join)
-- ---------------------------------------------------------------------------
CREATE TABLE quiz_categories (
    quiz_id         UUID            NOT NULL,
    category_id     UUID            NOT NULL,

    CONSTRAINT pk_quiz_categories PRIMARY KEY (quiz_id, category_id),
    CONSTRAINT fk_quiz_categories_quiz
        FOREIGN KEY (quiz_id) REFERENCES quizzes (id) ON DELETE CASCADE,
    CONSTRAINT fk_quiz_categories_category
        FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE CASCADE
);

CREATE INDEX idx_quiz_categories_category_id ON quiz_categories (category_id);
