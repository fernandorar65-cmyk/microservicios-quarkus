package kahoot.clabs.quiz.domain.valueobject;

public final class QuizSettings {

    private final boolean randomQuestions;
    private final boolean randomAnswers;
    private final boolean showCorrectAnswer;
    private final boolean showRanking;
    private final boolean allowRetry;
    private final boolean showTimer;
    private final boolean musicEnabled;

    private QuizSettings(
            boolean randomQuestions,
            boolean randomAnswers,
            boolean showCorrectAnswer,
            boolean showRanking,
            boolean allowRetry,
            boolean showTimer,
            boolean musicEnabled) {
        this.randomQuestions = randomQuestions;
        this.randomAnswers = randomAnswers;
        this.showCorrectAnswer = showCorrectAnswer;
        this.showRanking = showRanking;
        this.allowRetry = allowRetry;
        this.showTimer = showTimer;
        this.musicEnabled = musicEnabled;
    }

    public static QuizSettings defaultSettings() {
        return new QuizSettings(false, false, true, true, false, true, false);
    }

    public static QuizSettings strictSettings() {
        return new QuizSettings(false, false, false, true, false, true, false);
    }

    public static QuizSettings of(
            boolean randomQuestions,
            boolean randomAnswers,
            boolean showCorrectAnswer,
            boolean showRanking,
            boolean allowRetry,
            boolean showTimer,
            boolean musicEnabled) {
        return new QuizSettings(
                randomQuestions,
                randomAnswers,
                showCorrectAnswer,
                showRanking,
                allowRetry,
                showTimer,
                musicEnabled);
    }

    public QuizSettings withRandomQuestions(boolean enabled) {
        return new QuizSettings(enabled, randomAnswers, showCorrectAnswer, showRanking, allowRetry, showTimer, musicEnabled);
    }

    public QuizSettings withRandomAnswers(boolean enabled) {
        return new QuizSettings(randomQuestions, enabled, showCorrectAnswer, showRanking, allowRetry, showTimer, musicEnabled);
    }

    public QuizSettings withShowCorrectAnswer(boolean enabled) {
        return new QuizSettings(randomQuestions, randomAnswers, enabled, showRanking, allowRetry, showTimer, musicEnabled);
    }

    public QuizSettings withShowRanking(boolean enabled) {
        return new QuizSettings(randomQuestions, randomAnswers, showCorrectAnswer, enabled, allowRetry, showTimer, musicEnabled);
    }

    public QuizSettings withAllowRetry(boolean enabled) {
        return new QuizSettings(randomQuestions, randomAnswers, showCorrectAnswer, showRanking, enabled, showTimer, musicEnabled);
    }

    public QuizSettings withShowTimer(boolean enabled) {
        return new QuizSettings(randomQuestions, randomAnswers, showCorrectAnswer, showRanking, allowRetry, enabled, musicEnabled);
    }

    public QuizSettings withMusicEnabled(boolean enabled) {
        return new QuizSettings(randomQuestions, randomAnswers, showCorrectAnswer, showRanking, allowRetry, showTimer, enabled);
    }

    public boolean isRandomQuestions() {
        return randomQuestions;
    }

    public boolean isRandomAnswers() {
        return randomAnswers;
    }

    public boolean isShowCorrectAnswer() {
        return showCorrectAnswer;
    }

    public boolean isShowRanking() {
        return showRanking;
    }

    public boolean isAllowRetry() {
        return allowRetry;
    }

    public boolean isShowTimer() {
        return showTimer;
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }
}
