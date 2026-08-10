package kahoot.clabs.application.port.blob;

/**
 * Technology-agnostic port for object/blob storage uploads.
 */
public interface AssetsStoragePort {

    String upload(String objectKey, byte[] content, String contentType);
}
