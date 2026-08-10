package kahoot.clabs.infrastructure.persistence.mongo.mapper;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

import kahoot.clabs.application.snapshot.PublishedQuizSnapshot;
import kahoot.clabs.application.snapshot.PublishedQuizSnapshot.AnswerOptionSnapshot;
import kahoot.clabs.application.snapshot.PublishedQuizSnapshot.QuestionSnapshot;
import kahoot.clabs.infrastructure.persistence.mongo.document.PlayableAnswerOptionEmbed;
import kahoot.clabs.infrastructure.persistence.mongo.document.PlayableQuestionEmbed;
import kahoot.clabs.infrastructure.persistence.mongo.document.PlayableQuizSnapshotDocument;

public final class PlayableQuizSnapshotMapper {

    private PlayableQuizSnapshotMapper() {
    }

    public static PlayableQuizSnapshotDocument toDocument(PublishedQuizSnapshot snapshot) {
        PlayableQuizSnapshotDocument document = new PlayableQuizSnapshotDocument();
        document.setId(snapshot.quizId());
        document.setOrganizationId(snapshot.organizationId());
        document.setTitle(null);
        document.setQuestions(snapshot.questions().stream().map(PlayableQuizSnapshotMapper::toQuestionEmbed).toList());
        document.setUpdatedAt(Instant.now());
        return document;
    }

    public static PlayableQuizSnapshotDocument toDocument(
            PublishedQuizSnapshot snapshot, String title) {
        PlayableQuizSnapshotDocument document = toDocument(snapshot);
        document.setTitle(title);
        return document;
    }

    public static PublishedQuizSnapshot toSnapshot(PlayableQuizSnapshotDocument document) {
        List<QuestionSnapshot> questions = document.getQuestions().stream()
                .sorted(Comparator.comparingInt(PlayableQuestionEmbed::getOrderIndex))
                .map(PlayableQuizSnapshotMapper::toQuestionSnapshot)
                .toList();
        return new PublishedQuizSnapshot(document.getId(), document.getOrganizationId(), questions);
    }

    private static PlayableQuestionEmbed toQuestionEmbed(QuestionSnapshot question) {
        PlayableQuestionEmbed embed = new PlayableQuestionEmbed();
        embed.setId(question.id());
        embed.setOrderIndex(question.orderIndex());
        embed.setPoints(question.points());
        embed.setTimeLimitSeconds(question.timeLimitSeconds());
        embed.setTitle(question.title());
        embed.setDescription(question.description());
        embed.setType(question.type());
        embed.setOptions(question.options().stream().map(PlayableQuizSnapshotMapper::toOptionEmbed).toList());
        return embed;
    }

    private static PlayableAnswerOptionEmbed toOptionEmbed(AnswerOptionSnapshot option) {
        PlayableAnswerOptionEmbed embed = new PlayableAnswerOptionEmbed();
        embed.setId(option.id());
        embed.setText(option.text());
        embed.setCorrect(option.correct());
        embed.setOrderIndex(option.orderIndex());
        return embed;
    }

    private static QuestionSnapshot toQuestionSnapshot(PlayableQuestionEmbed embed) {
        List<AnswerOptionSnapshot> options = embed.getOptions().stream()
                .sorted(Comparator.comparingInt(PlayableAnswerOptionEmbed::getOrderIndex))
                .map(option -> new AnswerOptionSnapshot(
                        option.getId(),
                        option.getText(),
                        option.isCorrect(),
                        option.getOrderIndex()))
                .toList();
        return new QuestionSnapshot(
                embed.getId(),
                embed.getOrderIndex(),
                embed.getPoints(),
                embed.getTimeLimitSeconds(),
                embed.getTitle(),
                embed.getDescription(),
                embed.getType(),
                options);
    }
}
