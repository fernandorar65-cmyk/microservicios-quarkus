package kahoot.clabs.quiz.infrastructure.seed;

public interface DataSeeder {

    default int order() {
        return 100;
    }

    String name();

    void seed();
}
