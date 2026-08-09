package kahoot.clabs.infrastructure.persistence.mongo.repository;

import jakarta.enterprise.context.ApplicationScoped;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import kahoot.clabs.infrastructure.persistence.mongo.document.GameSessionReadDocument;

@ApplicationScoped
public class GameSessionMongoRepository implements PanacheMongoRepository<GameSessionReadDocument> {
}
