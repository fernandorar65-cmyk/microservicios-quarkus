package kahoot.clabs.application.port.write;

import kahoot.clabs.application.snapshot.PublishedQuizSnapshot;

public interface PlayableQuizSnapshotPort {

    void upsert(PublishedQuizSnapshot snapshot);
}
