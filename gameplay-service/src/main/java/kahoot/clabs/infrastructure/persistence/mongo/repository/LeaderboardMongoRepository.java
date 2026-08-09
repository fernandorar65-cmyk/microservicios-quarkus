package kahoot.clabs.infrastructure.persistence.mongo.repository;

import jakarta.enterprise.context.ApplicationScoped;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import kahoot.clabs.infrastructure.persistence.mongo.document.LeaderboardReadDocument;

@ApplicationScoped
public class LeaderboardMongoRepository implements PanacheMongoRepository<LeaderboardReadDocument> {
}
