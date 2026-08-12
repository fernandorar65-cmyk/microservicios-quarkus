package kahoot.clabs.application.port.blob;

public interface AssetsStoragePort {

    String upload(String objectKey, byte[] content, String contentType);
}
