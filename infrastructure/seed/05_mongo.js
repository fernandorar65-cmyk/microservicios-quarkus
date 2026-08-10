// Create Mongo collections matching Postgres tables in kahoot_db (same names).
// Shared local DB: kahoot (temporary — split later per service).

db = db.getSiblingDB('kahoot');

const collections = [
  // identity
  'permissions',
  'roles',
  'role_permissions',
  'users',
  'user_images',
  // organization
  'organizations',
  'organization_members',
  'organization_departments',
  'organization_jobs',
  'organization_statuses',
  'organization_member_statuses',
  // quiz
  'quizzes',
  'categories',
  'quiz_categories',
  'questions',
  'answer_options',
  'question_assets',
  // gameplay
  'game_sessions',
  'session_players',
  'session_questions',
  'session_answer_options',
  'player_answers'
];

const existing = db.getCollectionNames();
collections.forEach((name) => {
  if (!existing.includes(name)) {
    db.createCollection(name);
    print('created collection: ' + name);
  } else {
    print('collection already exists: ' + name);
  }
});

print('Mongo kahoot collections ready (' + collections.length + ' = Postgres tables)');
