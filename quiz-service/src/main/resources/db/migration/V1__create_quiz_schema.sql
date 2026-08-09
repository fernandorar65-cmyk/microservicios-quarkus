-- Quiz write model — migrated from Kahoot CLABS monolith (quiz tables from V1, without visibility).
-- External refs (organization_id, created_by, categories.organization_id) are UUID only.

CREATE TABLE quizzes (
    id UUID NOT NULL PRIMARY KEY,
    organization_id UUID NOT NULL,
    created_by UUID NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    thumbnail_url VARCHAR(500),
    status VARCHAR(20) NOT NULL,
    difficulty VARCHAR(20) NOT NULL,
    estimated_time_minutes INTEGER,
    play_count INTEGER NOT NULL,
    average_rating NUMERIC(3, 2) NOT NULL,
    is_template BOOLEAN NOT NULL,
    random_questions BOOLEAN NOT NULL,
    random_answers BOOLEAN NOT NULL,
    show_correct_answer BOOLEAN NOT NULL,
    show_ranking BOOLEAN NOT NULL,
    allow_retry BOOLEAN NOT NULL,
    show_timer BOOLEAN NOT NULL,
    music_enabled BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE categories (
    id UUID NOT NULL PRIMARY KEY,
    organization_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    color VARCHAR(20),
    icon VARCHAR(50),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_categories_organization_name UNIQUE (organization_id, name)
);

CREATE TABLE quiz_categories (
    quiz_id UUID NOT NULL,
    category_id UUID NOT NULL,
    PRIMARY KEY (quiz_id, category_id),
    CONSTRAINT fk_quiz_categories_quiz FOREIGN KEY (quiz_id) REFERENCES quizzes (id),
    CONSTRAINT fk_quiz_categories_category FOREIGN KEY (category_id) REFERENCES categories (id)
);

CREATE TABLE questions (
    id UUID NOT NULL PRIMARY KEY,
    quiz_id UUID NOT NULL,
    title VARCHAR(500) NOT NULL,
    description TEXT,
    type VARCHAR(20) NOT NULL,
    difficulty VARCHAR(20) NOT NULL,
    explanation TEXT,
    order_index INTEGER NOT NULL,
    time_limit_seconds INTEGER NOT NULL,
    points INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_questions_quiz_order UNIQUE (quiz_id, order_index),
    CONSTRAINT fk_questions_quiz FOREIGN KEY (quiz_id) REFERENCES quizzes (id)
);

CREATE TABLE answer_options (
    id UUID NOT NULL PRIMARY KEY,
    question_id UUID NOT NULL,
    text VARCHAR(500) NOT NULL,
    is_correct BOOLEAN NOT NULL,
    explanation TEXT,
    order_index INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_answer_options_question_order UNIQUE (question_id, order_index),
    CONSTRAINT fk_answer_options_question FOREIGN KEY (question_id) REFERENCES questions (id)
);

CREATE TABLE question_assets (
    id UUID NOT NULL PRIMARY KEY,
    question_id UUID NOT NULL UNIQUE,
    type VARCHAR(20) NOT NULL,
    url VARCHAR(1000) NOT NULL,
    thumbnail_url VARCHAR(1000),
    alt_text VARCHAR(255),
    duration_seconds INTEGER,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_question_assets_question FOREIGN KEY (question_id) REFERENCES questions (id)
);

CREATE INDEX idx_quizzes_organization_id ON quizzes (organization_id);
CREATE INDEX idx_quizzes_created_by ON quizzes (created_by);
