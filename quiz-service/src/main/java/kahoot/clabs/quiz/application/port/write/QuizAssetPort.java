package kahoot.clabs.quiz.application.port.write;

/**
 * Port for uploading quiz-related media assets.
 */
public interface QuizAssetPort {

    String upload(String objectKey, byte[] content, String contentType);
}
