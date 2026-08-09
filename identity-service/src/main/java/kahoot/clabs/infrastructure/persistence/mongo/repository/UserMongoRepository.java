package kahoot.clabs.infrastructure.persistence.mongo.repository;

import java.util.UUID;

import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import kahoot.clabs.infrastructure.persistence.mongo.document.UserReadDocument;

@ApplicationScoped
public class UserMongoRepository implements PanacheMongoRepositoryBase<UserReadDocument, UUID> {}
