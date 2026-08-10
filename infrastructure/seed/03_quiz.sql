-- Minimal published quiz for shared kahoot_db (used by gameplay demo).

INSERT INTO quizzes (
  id, organization_id, created_by, title, description, thumbnail_url,
  status, difficulty, estimated_time_minutes, play_count, average_rating, is_template,
  random_questions, random_answers, show_correct_answer, show_ranking, allow_retry, show_timer, music_enabled,
  created_at, updated_at
) VALUES (
  '44444444-4444-4444-4444-444444444441',
  '11111111-1111-1111-1111-111111111111',
  '33333333-3333-3333-3333-333333333332',
  'Java Basics',
  'Quiz demo mínimo de Java',
  NULL,
  'PUBLISHED',
  'EASY',
  5,
  0,
  0,
  FALSE,
  FALSE, FALSE, TRUE, TRUE, FALSE, TRUE, FALSE,
  NOW(),
  NOW()
) ON CONFLICT DO NOTHING;

INSERT INTO questions (
  id, quiz_id, title, description, type, difficulty, explanation, order_index,
  time_limit_seconds, points, created_at, updated_at
) VALUES (
  '66666666-6666-6666-6666-666666666661',
  '44444444-4444-4444-4444-444444444441',
  '¿Qué palabra clave declara una constante en Java?',
  NULL,
  'MULTIPLE_CHOICE',
  'EASY',
  NULL,
  0,
  20,
  1000,
  NOW(),
  NOW()
) ON CONFLICT DO NOTHING;

INSERT INTO answer_options (
  id, question_id, text, is_correct, explanation, order_index, created_at, updated_at
) VALUES
  ('66666666-6666-6666-6666-666666666662',
   '66666666-6666-6666-6666-666666666661', 'final', TRUE, NULL, 0, NOW(), NOW()),
  ('66666666-6666-6666-6666-666666666663',
   '66666666-6666-6666-6666-666666666661', 'const', FALSE, NULL, 1, NOW(), NOW())
ON CONFLICT DO NOTHING;
