package kahoot.clabs.quiz.infrastructure.persistence.mongo.repository;

import java.util.UUID;

import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import kahoot.clabs.quiz.infrastructure.persistence.mongo.document.QuizReadDocument;

@ApplicationScoped
public class QuizMongoRepository implements PanacheMongoRepositoryBase<QuizReadDocument, UUID> {}
