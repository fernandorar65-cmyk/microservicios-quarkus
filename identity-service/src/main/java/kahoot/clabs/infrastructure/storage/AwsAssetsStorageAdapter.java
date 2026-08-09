package kahoot.clabs.infrastructure.storage;

import java.util.Map;
import java.util.Optional;

import io.quarkus.arc.lookup.LookupIfProperty;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import kahoot.clabs.domain.shared.DomainException;
import kahoot.clabs.application.port.out.AssetsStoragePort;

@ApplicationScoped
@LookupIfProperty(name = "app.storage", stringValue = "aws", lookupIfMissing = true)
public class AwsAssetsStorageAdapter implements AssetsStoragePort {

    private final S3Client s3Client;
    private final Optional<String> bucket;
    private final String region;
    private final Optional<String> publicBaseUrl;

    @Inject
    public AwsAssetsStorageAdapter(
            @ConfigProperty(name = "storage.s3.bucket") Optional<String> bucket,
            @ConfigProperty(name = "storage.s3.region", defaultValue = "us-east-2") String region,
            @ConfigProperty(name = "storage.s3.public-base-url") Optional<String> publicBaseUrl) {
        this.bucket = bucket;
        this.region = region;
        this.publicBaseUrl = publicBaseUrl;
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .build();
    }

    @Override
    public String upload(String objectKey, byte[] content, String contentType) {
        String bucketName = bucket.filter(value -> !value.isBlank()).orElse(null);
        if (bucketName == null) {
            throw new DomainException("S3 bucket is not configured");
        }
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .contentType(contentType)
                        .metadata(Map.of("uploaded-by", "kahoot-clabs"))
                        .build(),
                RequestBody.fromBytes(content));
        return publicUrl(objectKey, bucketName);
    }

    private String publicUrl(String objectKey, String bucketName) {
        if (publicBaseUrl.isPresent() && !publicBaseUrl.get().isBlank()) {
            return publicBaseUrl.get().replaceAll("/+$", "") + "/" + objectKey;
        }
        return "https://%s.s3.%s.amazonaws.com/%s".formatted(bucketName, region, objectKey);
    }
}
