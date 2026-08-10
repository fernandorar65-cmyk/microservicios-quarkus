package kahoot.clabs.infrastructure.storage;

import java.util.Optional;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import kahoot.clabs.application.port.AssetsStoragePort;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class ConfigurableAssetsStorageAdapter implements AssetsStoragePort {

    private final AssetsStoragePort delegate;
    private final AwsAssetsStorageAdapter awsDelegate;

    @Inject
    public ConfigurableAssetsStorageAdapter(
            @ConfigProperty(name = "app.storage", defaultValue = "aws") String storageMode,
            @ConfigProperty(name = "storage.s3.bucket") Optional<String> s3Bucket,
            @ConfigProperty(name = "storage.s3.region", defaultValue = "us-east-2") String s3Region,
            @ConfigProperty(name = "storage.s3.public-base-url") Optional<String> s3PublicBaseUrl,
            @ConfigProperty(name = "storage.azure.connection-string") Optional<String> azureConnectionString,
            @ConfigProperty(name = "storage.azure.container") Optional<String> azureContainer,
            @ConfigProperty(name = "storage.azure.account-name") Optional<String> azureAccountName,
            @ConfigProperty(name = "storage.azure.public-base-url") Optional<String> azurePublicBaseUrl) {
        String mode = storageMode == null ? "aws" : storageMode.trim().toLowerCase();
        if ("azure".equals(mode)) {
            this.awsDelegate = null;
            this.delegate = new AzureAssetsStorageAdapter(
                    azureConnectionString, azureContainer, azureAccountName, azurePublicBaseUrl);
        } else {
            this.awsDelegate = new AwsAssetsStorageAdapter(s3Bucket, s3Region, s3PublicBaseUrl);
            this.delegate = this.awsDelegate;
        }
    }

    @Override
    public String upload(String objectKey, byte[] content, String contentType) {
        return delegate.upload(objectKey, content, contentType);
    }

    @PreDestroy
    void shutdown() {
        if (awsDelegate != null) {
            awsDelegate.close();
        }
    }
}
