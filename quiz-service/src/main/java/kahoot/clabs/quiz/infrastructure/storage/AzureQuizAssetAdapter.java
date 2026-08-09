package kahoot.clabs.quiz.infrastructure.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.options.BlobParallelUploadOptions;
import io.quarkus.arc.lookup.LookupIfProperty;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import kahoot.clabs.quiz.domain.shared.DomainException;
import kahoot.clabs.quiz.application.port.out.QuizAssetPort;

@ApplicationScoped
@LookupIfProperty(name = "app.storage", stringValue = "azure")
public class AzureQuizAssetAdapter implements QuizAssetPort {

    private final BlobServiceClient blobServiceClient;
    private final Optional<String> container;
    private final Optional<String> accountName;
    private final Optional<String> publicBaseUrl;

    @Inject
    public AzureQuizAssetAdapter(
            @ConfigProperty(name = "storage.azure.connection-string") Optional<String> connectionString,
            @ConfigProperty(name = "storage.azure.container") Optional<String> container,
            @ConfigProperty(name = "storage.azure.account-name") Optional<String> accountName,
            @ConfigProperty(name = "storage.azure.public-base-url") Optional<String> publicBaseUrl) {
        String conn = connectionString.filter(value -> !value.isBlank()).orElse(null);
        if (conn == null) {
            throw new IllegalStateException(
                    "storage.azure.connection-string is required when app.storage=azure");
        }
        this.blobServiceClient = new BlobServiceClientBuilder().connectionString(conn).buildClient();
        this.container = container;
        this.accountName = accountName;
        this.publicBaseUrl = publicBaseUrl;
    }

    @Override
    public String upload(String objectKey, byte[] content, String contentType) {
        String containerName = container.filter(value -> !value.isBlank()).orElse(null);
        if (containerName == null) {
            throw new DomainException("Azure blob container is not configured");
        }
        if (content == null || content.length == 0) {
            throw new DomainException("Cannot upload empty content to Azure Blob Storage");
        }

        BlobClient blobClient = blobServiceClient
                .getBlobContainerClient(containerName)
                .getBlobClient(objectKey);

        BlobHttpHeaders headers = new BlobHttpHeaders().setContentType(contentType);
        Map<String, String> metadata = new HashMap<>();
        metadata.put("uploadedBy", "kahoot-clabs");

        blobClient.uploadWithResponse(
                new BlobParallelUploadOptions(BinaryData.fromBytes(content))
                        .setHeaders(headers)
                        .setMetadata(metadata),
                null,
                null);

        return publicUrl(objectKey, containerName);
    }

    private String publicUrl(String objectKey, String containerName) {
        if (publicBaseUrl.isPresent() && !publicBaseUrl.get().isBlank()) {
            return publicBaseUrl.get().replaceAll("/+$", "") + "/" + objectKey;
        }
        String account = accountName.filter(value -> !value.isBlank()).orElse(null);
        if (account == null) {
            throw new DomainException(
                    "Configure storage.azure.account-name or storage.azure.public-base-url to build blob URLs");
        }
        return "https://%s.blob.core.windows.net/%s/%s".formatted(account, containerName, objectKey);
    }
}
