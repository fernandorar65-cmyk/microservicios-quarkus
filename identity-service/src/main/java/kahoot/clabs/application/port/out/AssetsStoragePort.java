package kahoot.clabs.application.port.out;

/**
 * Technology-agnostic port for object/blob storage uploads.
 */
public interface AssetsStoragePort {

    String upload(String objectKey, byte[] content, String contentType);
}
