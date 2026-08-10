package kahoot.clabs.quiz.infrastructure.persistence.mongo.adapter;

import java.time.Instant;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import kahoot.clabs.quiz.application.port.write.CategoryProjectionPort;
import kahoot.clabs.quiz.application.readmodel.CategoryReadModel;
import kahoot.clabs.quiz.infrastructure.persistence.mongo.document.CategoryReadDocument;
import kahoot.clabs.quiz.infrastructure.persistence.mongo.repository.CategoryMongoRepository;

@ApplicationScoped
public class CategoryMongoProjectionAdapter implements CategoryProjectionPort {

    private final CategoryMongoRepository categoryMongoRepository;

    @Inject
    public CategoryMongoProjectionAdapter(CategoryMongoRepository categoryMongoRepository) {
        this.categoryMongoRepository = categoryMongoRepository;
    }

    @Override
    public void save(CategoryReadModel readModel) {
        CategoryReadDocument existing = categoryMongoRepository.findById(readModel.getId());
        CategoryReadDocument document = toDocument(readModel, existing);
        categoryMongoRepository.persistOrUpdate(document);
    }

    @Override
    public void deleteById(java.util.UUID id) {
        categoryMongoRepository.deleteById(id);
    }

    private CategoryReadDocument toDocument(CategoryReadModel readModel, CategoryReadDocument existing) {
        CategoryReadDocument document = existing != null ? existing : new CategoryReadDocument();
        document.setId(readModel.getId());
        document.setOrganizationId(readModel.getOrganizationId());
        document.setName(readModel.getName());
        document.setDescription(readModel.getDescription());
        document.setColor(readModel.getColor());
        document.setIcon(readModel.getIcon());
        document.setQuizCount(existing != null ? existing.getQuizCount() : readModel.getQuizCount());
        document.setCreatedAt(readModel.getCreatedAt() != null
                ? readModel.getCreatedAt()
                : existing != null ? existing.getCreatedAt() : Instant.now());
        document.setUpdatedAt(readModel.getUpdatedAt() != null ? readModel.getUpdatedAt() : Instant.now());
        return document;
    }
}
