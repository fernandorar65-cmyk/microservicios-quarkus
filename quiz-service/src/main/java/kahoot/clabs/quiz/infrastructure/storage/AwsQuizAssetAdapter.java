package kahoot.clabs.quiz.infrastructure.storage;

import java.util.Map;
import java.util.Optional;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import kahoot.clabs.quiz.domain.shared.DomainException;
import kahoot.clabs.quiz.application.port.out.QuizAssetPort;

public class AwsQuizAssetAdapter implements QuizAssetPort {

    private final S3Client s3Client;
    private final Optional<String> bucket;
    private final String region;
    private final Optional<String> publicBaseUrl;

    public AwsQuizAssetAdapter(
            Optional<String> bucket,
            String region,
            Optional<String> publicBaseUrl) {
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

    public void close() {
        s3Client.close();
    }

    private String publicUrl(String objectKey, String bucketName) {
        if (publicBaseUrl.isPresent() && !publicBaseUrl.get().isBlank()) {
            return publicBaseUrl.get().replaceAll("/+$", "") + "/" + objectKey;
        }
        return "https://%s.s3.%s.amazonaws.com/%s".formatted(bucketName, region, objectKey);
    }
}
