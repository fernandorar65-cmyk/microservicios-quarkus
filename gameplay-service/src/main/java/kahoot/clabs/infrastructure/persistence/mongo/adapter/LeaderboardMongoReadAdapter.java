package kahoot.clabs.infrastructure.persistence.mongo.adapter;

import java.util.Optional;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import kahoot.clabs.application.port.read.LeaderboardReadPort;
import kahoot.clabs.application.readmodel.LeaderboardReadModel;
import kahoot.clabs.infrastructure.persistence.mongo.mapper.LeaderboardReadMapper;
import kahoot.clabs.infrastructure.persistence.mongo.repository.LeaderboardMongoRepository;

@ApplicationScoped
public class LeaderboardMongoReadAdapter implements LeaderboardReadPort {

    @Inject
    LeaderboardMongoRepository repository;

    @Inject
    LeaderboardReadMapper mapper;

    @Override
    public Optional<LeaderboardReadModel> findBySessionId(UUID sessionId) {
        return repository.find("sessionId", sessionId).firstResultOptional().map(mapper::toReadModel);
    }
}
