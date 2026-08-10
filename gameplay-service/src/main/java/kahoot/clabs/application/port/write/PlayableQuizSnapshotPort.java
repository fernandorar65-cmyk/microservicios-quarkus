package kahoot.clabs.application.port.write;

import kahoot.clabs.application.snapshot.PublishedQuizSnapshot;

/*Local write/read of published-quiz snapshots owned by gameplay.*/
public interface PlayableQuizSnapshotPort {

    void upsert(PublishedQuizSnapshot snapshot);
}
