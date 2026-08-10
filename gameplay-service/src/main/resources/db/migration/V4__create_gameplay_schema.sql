CREATE TABLE game_sessions (
    id UUID NOT NULL PRIMARY KEY,
    organization_id UUID NOT NULL,
    quiz_id UUID NOT NULL,
    host_user_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL,
    current_question_index INTEGER NOT NULL,
    started_at TIMESTAMP,
    finished_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE session_players (
    id UUID NOT NULL PRIMARY KEY,
    session_id UUID NOT NULL,
    user_id UUID NOT NULL,
    nickname VARCHAR(30) NOT NULL,
    score INTEGER NOT NULL,
    connected BOOLEAN NOT NULL,
    joined_at TIMESTAMP NOT NULL,
    left_at TIMESTAMP,
    CONSTRAINT uq_session_players_session_user UNIQUE (session_id, user_id),
    CONSTRAINT uq_session_players_session_nickname UNIQUE (session_id, nickname),
    CONSTRAINT fk_session_players_session FOREIGN KEY (session_id) REFERENCES game_sessions (id)
);

CREATE TABLE session_questions (
    id UUID NOT NULL PRIMARY KEY,
    session_id UUID NOT NULL,
    source_question_id UUID,
    order_index INTEGER NOT NULL,
    points INTEGER NOT NULL,
    time_limit_seconds INTEGER NOT NULL,
    title VARCHAR(500),
    description TEXT,
    question_type VARCHAR(20),
    opened_at TIMESTAMP,
    closed_at TIMESTAMP,
    CONSTRAINT uq_session_questions_session_order UNIQUE (session_id, order_index),
    CONSTRAINT fk_session_questions_session FOREIGN KEY (session_id) REFERENCES game_sessions (id)
);

CREATE TABLE session_answer_options (
    id UUID NOT NULL PRIMARY KEY,
    session_question_id UUID NOT NULL,
    source_answer_option_id UUID,
    text VARCHAR(500) NOT NULL,
    is_correct BOOLEAN NOT NULL,
    order_index INTEGER NOT NULL,
    CONSTRAINT uq_session_answer_options_question_order UNIQUE (session_question_id, order_index),
    CONSTRAINT fk_session_answer_options_session_question
        FOREIGN KEY (session_question_id) REFERENCES session_questions (id)
);

CREATE TABLE player_answers (
    id UUID NOT NULL PRIMARY KEY,
    session_question_id UUID NOT NULL,
    session_player_id UUID NOT NULL,
    session_answer_option_id UUID,
    is_correct BOOLEAN NOT NULL,
    response_time_ms BIGINT NOT NULL,
    awarded_points INTEGER NOT NULL,
    answered_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_player_answers_question_player UNIQUE (session_question_id, session_player_id),
    CONSTRAINT fk_player_answers_session_question
        FOREIGN KEY (session_question_id) REFERENCES session_questions (id),
    CONSTRAINT fk_player_answers_session_player
        FOREIGN KEY (session_player_id) REFERENCES session_players (id),
    CONSTRAINT fk_player_answers_session_answer_option
        FOREIGN KEY (session_answer_option_id) REFERENCES session_answer_options (id)
);

CREATE INDEX idx_game_sessions_organization_id ON game_sessions (organization_id);
CREATE INDEX idx_game_sessions_quiz_id ON game_sessions (quiz_id);
CREATE INDEX idx_game_sessions_host_user_id ON game_sessions (host_user_id);
CREATE INDEX idx_session_players_user_id ON session_players (user_id);
CREATE INDEX idx_session_players_session_id ON session_players (session_id);
CREATE INDEX idx_session_answer_options_session_question_id ON session_answer_options (session_question_id);
CREATE INDEX idx_player_answers_session_player_id ON player_answers (session_player_id);
CREATE INDEX idx_player_answers_session_question_id ON player_answers (session_question_id);