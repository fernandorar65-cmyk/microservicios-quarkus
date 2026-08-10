-- Minimal LOBBY session for shared kahoot_db.

INSERT INTO game_sessions (
  id, organization_id, quiz_id, host_user_id, status, current_question_index,
  started_at, finished_at, created_at, updated_at
) VALUES (
  '55555555-5555-5555-5555-555555555551',
  '11111111-1111-1111-1111-111111111111',
  '44444444-4444-4444-4444-444444444441',
  '33333333-3333-3333-3333-333333333332',
  'LOBBY',
  0,
  NULL,
  NULL,
  NOW(),
  NOW()
) ON CONFLICT DO NOTHING;

INSERT INTO session_questions (
  id, session_id, source_question_id, order_index, points, time_limit_seconds,
  title, description, question_type, opened_at, closed_at
) VALUES (
  '77777777-7777-7777-7777-777777777771',
  '55555555-5555-5555-5555-555555555551',
  '66666666-6666-6666-6666-666666666661',
  0,
  1000,
  20,
  '¿Qué palabra clave declara una constante en Java?',
  NULL,
  'MULTIPLE_CHOICE',
  NULL,
  NULL
) ON CONFLICT DO NOTHING;

INSERT INTO session_answer_options (
  id, session_question_id, source_answer_option_id, text, is_correct, order_index
) VALUES
  ('77777777-7777-7777-7777-777777777772',
   '77777777-7777-7777-7777-777777777771',
   '66666666-6666-6666-6666-666666666662',
   'final', TRUE, 0),
  ('77777777-7777-7777-7777-777777777773',
   '77777777-7777-7777-7777-777777777771',
   '66666666-6666-6666-6666-666666666663',
   'const', FALSE, 1)
ON CONFLICT DO NOTHING;

INSERT INTO session_players (
  id, session_id, user_id, nickname, score, connected, joined_at, left_at
) VALUES (
  '77777777-7777-7777-7777-777777777774',
  '55555555-5555-5555-5555-555555555551',
  '33333333-3333-3333-3333-333333333334',
  'Member',
  0,
  TRUE,
  NOW(),
  NULL
) ON CONFLICT DO NOTHING;
