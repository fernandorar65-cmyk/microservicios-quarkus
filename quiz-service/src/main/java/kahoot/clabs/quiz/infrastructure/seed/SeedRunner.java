package kahoot.clabs.quiz.infrastructure.seed;

import java.util.Comparator;
import java.util.List;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@ApplicationScoped
public class SeedRunner {

    private static final Logger LOG = Logger.getLogger(SeedRunner.class);

    private final boolean enabled;
    private final Instance<DataSeeder> seeders;

    @Inject
    public SeedRunner(
            @ConfigProperty(name = "app.seed.enabled", defaultValue = "false") boolean enabled,
            Instance<DataSeeder> seeders) {
        this.enabled = enabled;
        this.seeders = seeders;
    }

    @Transactional
    void onStart(@Observes StartupEvent event) {
        if (!enabled) {
            LOG.info("Data seeders disabled (app.seed.enabled=false)");
            return;
        }
        List<DataSeeder> ordered = seeders.stream()
                .sorted(Comparator.comparingInt(DataSeeder::order))
                .toList();
        if (ordered.isEmpty()) {
            LOG.debug("No DataSeeder beans registered");
            return;
        }
        LOG.infof("Running %d data seeder(s)", ordered.size());
        for (DataSeeder seeder : ordered) {
            LOG.infof("Seeding: %s (order=%d)", seeder.name(), seeder.order());
            seeder.seed();
        }
        LOG.info("Data seeders finished");
    }
}
