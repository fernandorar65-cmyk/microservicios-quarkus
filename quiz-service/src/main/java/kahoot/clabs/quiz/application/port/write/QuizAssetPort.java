package kahoot.clabs.quiz.application.port.write;

public interface QuizAssetPort {

    String upload(String objectKey, byte[] content, String contentType);
}
