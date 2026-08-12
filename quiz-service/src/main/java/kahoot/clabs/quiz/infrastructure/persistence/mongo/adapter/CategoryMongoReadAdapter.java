package kahoot.clabs.quiz.infrastructure.persistence.mongo.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import kahoot.clabs.quiz.application.port.read.CategoryReadPort;
import kahoot.clabs.quiz.application.readmodel.CategoryReadModel;
import kahoot.clabs.quiz.infrastructure.persistence.mongo.document.CategoryReadDocument;
import kahoot.clabs.quiz.infrastructure.persistence.mongo.repository.CategoryMongoRepository;

@ApplicationScoped
public class CategoryMongoReadAdapter implements CategoryReadPort {

    private final CategoryMongoRepository categoryMongoRepository;

    public CategoryMongoReadAdapter(CategoryMongoRepository categoryMongoRepository) {
        this.categoryMongoRepository = categoryMongoRepository;
    }

    @Override
    @Transactional(TxType.NOT_SUPPORTED)
    public Optional<CategoryReadModel> findById(UUID id) {
        return categoryMongoRepository.findByIdOptional(id).map(this::toReadModel);
    }

    @Override
    @Transactional(TxType.NOT_SUPPORTED)
    public List<CategoryReadModel> findByOrganization(UUID organizationId) {
        return categoryMongoRepository.list("organizationId", organizationId).stream()
                .map(this::toReadModel)
                .toList();
    }

    private CategoryReadModel toReadModel(CategoryReadDocument document) {
        CategoryReadModel readModel = new CategoryReadModel();
        readModel.setId(document.getId());
        readModel.setOrganizationId(document.getOrganizationId());
        readModel.setName(document.getName());
        readModel.setDescription(document.getDescription());
        readModel.setColor(document.getColor());
        readModel.setIcon(document.getIcon());
        readModel.setQuizCount(document.getQuizCount());
        readModel.setCreatedAt(document.getCreatedAt());
        readModel.setUpdatedAt(document.getUpdatedAt());
        return readModel;
    }
}
