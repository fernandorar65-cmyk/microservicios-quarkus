package kahoot.clabs.application.port;

public interface AssetsStoragePort {

    String upload(String objectKey, byte[] content, String contentType);
}
