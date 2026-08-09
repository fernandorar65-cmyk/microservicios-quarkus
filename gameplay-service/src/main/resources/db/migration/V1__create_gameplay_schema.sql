-- Write model schema for gameplay-service
-- External refs (organization_id, quiz_id, host_user_id, user_id, source_*) are UUID only
-- Leaderboard is derived from session_players.score — no dedicated table
-- Playable quiz content is denormalized into session_questions / session_answer_options

-- ---------------------------------------------------------------------------
-- game_sessions
-- ---------------------------------------------------------------------------
CREATE TABLE game_sessions (
    id                      UUID            NOT NULL,
    organization_id         UUID            NOT NULL,
    quiz_id                 UUID            NOT NULL,
    host_user_id            UUID            NOT NULL,
    status                  VARCHAR(20)     NOT NULL,
    join_code               VARCHAR(12)     NOT NULL,
    current_question_index  INTEGER         NOT NULL DEFAULT 0,
    started_at              TIMESTAMPTZ,
    finished_at             TIMESTAMPTZ,
    created_at              TIMESTAMPTZ     NOT NULL,
    updated_at              TIMESTAMPTZ     NOT NULL,

    CONSTRAINT pk_game_sessions PRIMARY KEY (id),
    CONSTRAINT uq_game_sessions_join_code UNIQUE (join_code),
    CONSTRAINT ck_game_sessions_status CHECK (
        status IN ('WAITING', 'ACTIVE', 'FINISHED', 'CANCELLED')
    ),
    CONSTRAINT ck_game_sessions_current_question_index CHECK (current_question_index >= 0),
    CONSTRAINT ck_game_sessions_join_code_not_blank CHECK (btrim(join_code) <> '')
);

CREATE INDEX idx_game_sessions_organization_id ON game_sessions (organization_id);
CREATE INDEX idx_game_sessions_quiz_id ON game_sessions (quiz_id);
CREATE INDEX idx_game_sessions_host_user_id ON game_sessions (host_user_id);
CREATE INDEX idx_game_sessions_status ON game_sessions (status);

-- ---------------------------------------------------------------------------
-- session_players
-- ---------------------------------------------------------------------------
CREATE TABLE session_players (
    id              UUID            NOT NULL,
    session_id      UUID            NOT NULL,
    user_id         UUID,
    nickname        VARCHAR(30)     NOT NULL,
    status          VARCHAR(20)     NOT NULL,
    score           INTEGER         NOT NULL DEFAULT 0,
    joined_at       TIMESTAMPTZ     NOT NULL,
    left_at         TIMESTAMPTZ,
    created_at      TIMESTAMPTZ     NOT NULL,
    updated_at      TIMESTAMPTZ     NOT NULL,

    CONSTRAINT pk_session_players PRIMARY KEY (id),
    CONSTRAINT fk_session_players_session
        FOREIGN KEY (session_id) REFERENCES game_sessions (id) ON DELETE CASCADE,
    CONSTRAINT uq_session_players_session_nickname UNIQUE (session_id, nickname),
    CONSTRAINT ck_session_players_status CHECK (
        status IN ('JOINED', 'LEFT', 'KICKED')
    ),
    CONSTRAINT ck_session_players_score CHECK (score >= 0),
    CONSTRAINT ck_session_players_nickname_not_blank CHECK (btrim(nickname) <> '')
);

CREATE UNIQUE INDEX uq_session_players_session_user
    ON session_players (session_id, user_id)
    WHERE user_id IS NOT NULL;

CREATE INDEX idx_session_players_session_id ON session_players (session_id);
CREATE INDEX idx_session_players_user_id ON session_players (user_id);
CREATE INDEX idx_session_players_session_score ON session_players (session_id, score DESC);

-- ---------------------------------------------------------------------------
-- session_questions (denormalized playable snapshot of a question)
-- ---------------------------------------------------------------------------
CREATE TABLE session_questions (
    id                      UUID            NOT NULL,
    session_id              UUID            NOT NULL,
    source_question_id      UUID            NOT NULL,
    text                    TEXT            NOT NULL,
    type                    VARCHAR(30)     NOT NULL,
    position                INTEGER         NOT NULL,
    time_limit              INTEGER,
    points                  INTEGER,
    status                  VARCHAR(20)     NOT NULL,
    opened_at               TIMESTAMPTZ,
    closed_at               TIMESTAMPTZ,
    created_at              TIMESTAMPTZ     NOT NULL,
    updated_at              TIMESTAMPTZ     NOT NULL,

    CONSTRAINT pk_session_questions PRIMARY KEY (id),
    CONSTRAINT fk_session_questions_session
        FOREIGN KEY (session_id) REFERENCES game_sessions (id) ON DELETE CASCADE,
    CONSTRAINT uq_session_questions_session_position UNIQUE (session_id, position),
    CONSTRAINT ck_session_questions_type CHECK (
        type IN ('SINGLE_CHOICE', 'MULTIPLE_CHOICE', 'TRUE_FALSE')
    ),
    CONSTRAINT ck_session_questions_status CHECK (
        status IN ('PENDING', 'OPEN', 'CLOSED')
    ),
    CONSTRAINT ck_session_questions_position CHECK (position >= 0),
    CONSTRAINT ck_session_questions_time_limit CHECK (time_limit IS NULL OR time_limit > 0),
    CONSTRAINT ck_session_questions_points CHECK (points IS NULL OR points >= 0),
    CONSTRAINT ck_session_questions_text_not_blank CHECK (btrim(text) <> '')
);

CREATE INDEX idx_session_questions_session_id ON session_questions (session_id);

-- ---------------------------------------------------------------------------
-- session_answer_options
-- ---------------------------------------------------------------------------
CREATE TABLE session_answer_options (
    id                          UUID            NOT NULL,
    session_question_id         UUID            NOT NULL,
    source_answer_option_id     UUID            NOT NULL,
    text                        TEXT            NOT NULL,
    is_correct                  BOOLEAN         NOT NULL,
    position                    INTEGER         NOT NULL,

    CONSTRAINT pk_session_answer_options PRIMARY KEY (id),
    CONSTRAINT fk_session_answer_options_question
        FOREIGN KEY (session_question_id) REFERENCES session_questions (id) ON DELETE CASCADE,
    CONSTRAINT uq_session_answer_options_question_position UNIQUE (session_question_id, position),
    CONSTRAINT ck_session_answer_options_position CHECK (position >= 0),
    CONSTRAINT ck_session_answer_options_text_not_blank CHECK (btrim(text) <> '')
);

CREATE INDEX idx_session_answer_options_question_id ON session_answer_options (session_question_id);

-- ---------------------------------------------------------------------------
-- player_answers
-- ---------------------------------------------------------------------------
CREATE TABLE player_answers (
    id                          UUID            NOT NULL,
    session_id                  UUID            NOT NULL,
    session_player_id           UUID            NOT NULL,
    session_question_id         UUID            NOT NULL,
    session_answer_option_id    UUID,
    is_correct                  BOOLEAN         NOT NULL,
    response_time_ms            BIGINT,
    points_awarded              INTEGER         NOT NULL,
    answered_at                 TIMESTAMPTZ     NOT NULL,
    created_at                  TIMESTAMPTZ     NOT NULL,

    CONSTRAINT pk_player_answers PRIMARY KEY (id),
    CONSTRAINT fk_player_answers_session
        FOREIGN KEY (session_id) REFERENCES game_sessions (id) ON DELETE CASCADE,
    CONSTRAINT fk_player_answers_player
        FOREIGN KEY (session_player_id) REFERENCES session_players (id) ON DELETE CASCADE,
    CONSTRAINT fk_player_answers_question
        FOREIGN KEY (session_question_id) REFERENCES session_questions (id) ON DELETE CASCADE,
    CONSTRAINT fk_player_answers_option
        FOREIGN KEY (session_answer_option_id) REFERENCES session_answer_options (id) ON DELETE SET NULL,
    CONSTRAINT uq_player_answers_player_question UNIQUE (session_player_id, session_question_id),
    CONSTRAINT ck_player_answers_points CHECK (points_awarded >= 0),
    CONSTRAINT ck_player_answers_response_time CHECK (
        response_time_ms IS NULL OR response_time_ms >= 0
    )
);

CREATE INDEX idx_player_answers_session_id ON player_answers (session_id);
CREATE INDEX idx_player_answers_session_player_id ON player_answers (session_player_id);
CREATE INDEX idx_player_answers_session_question_id ON player_answers (session_question_id);
