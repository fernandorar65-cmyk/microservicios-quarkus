// Optional Mongo demo markers for shared kahoot DB.
// Read models are normally projected by the Quarkus apps; this only ensures the DB exists.

db = db.getSiblingDB('kahoot');
db.createCollection('seed_meta');
db.seed_meta.updateOne(
  { _id: 'local-demo' },
  {
    $set: {
      note: 'Shared local Mongo for all services (temporary). Split later per service.',
      seededAt: new Date()
    }
  },
  { upsert: true }
);
print('Mongo kahoot ready');
