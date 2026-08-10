package kahoot.clabs.infrastructure.persistence.mongo.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import kahoot.clabs.application.port.read.GameSessionReadPort;
import kahoot.clabs.application.readmodel.GameSessionReadModel;
import kahoot.clabs.infrastructure.persistence.mongo.mapper.GameSessionReadMapper;
import kahoot.clabs.infrastructure.persistence.mongo.repository.GameSessionMongoRepository;

@ApplicationScoped
public class GameSessionMongoReadAdapter implements GameSessionReadPort {

    @Inject
    GameSessionMongoRepository repository;

    @Inject
    GameSessionReadMapper mapper;

    @Override
    public Optional<GameSessionReadModel> findById(UUID id) {
        return repository.find("_id", id).firstResultOptional().map(mapper::toReadModel);
    }

    @Override
    public List<GameSessionReadModel> search(UUID organizationId, Collection<String> statuses, UUID quizId) {
        StringBuilder query = new StringBuilder("organizationId = ?1");
        List<Object> params = new ArrayList<>();
        params.add(organizationId);
        int index = 2;

        if (quizId != null) {
            query.append(" and quizId = ?").append(index++);
            params.add(quizId);
        }
        if (statuses != null && !statuses.isEmpty()) {
            query.append(" and status in ?").append(index++);
            params.add(statuses);
        }
        query.append(" order by createdAt desc");

        return repository.list(query.toString(), params.toArray()).stream()
                .map(mapper::toReadModel)
                .map(this::toSummary)
                .toList();
    }

    private GameSessionReadModel toSummary(GameSessionReadModel model) {
        model.setPlayers(List.of());
        model.setQuestions(List.of());
        model.setPlayerAnswers(List.of());
        return model;
    }
}
