package kahoot.clabs.infrastructure.persistence.mongo.adapter;

import java.util.Optional;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import kahoot.clabs.application.port.out.read.GameSessionReadPort;
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
}
