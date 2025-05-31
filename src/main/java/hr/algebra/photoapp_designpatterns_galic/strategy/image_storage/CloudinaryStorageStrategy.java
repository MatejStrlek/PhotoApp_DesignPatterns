package hr.algebra.photoapp_designpatterns_galic.strategy.image_storage;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
@Profile("cloudinary")
public class CloudinaryStorageStrategy implements ImageStorageStrategy {
    private final Cloudinary cloudinary;

    public CloudinaryStorageStrategy(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret
    ) {
        this.cloudinary = new Cloudinary(
                Map.of(
                        "cloud_name", cloudName,
                        "api_key", apiKey,
                        "api_secret", apiSecret
                )
        );
    }

    @Override
    public String storeImage(byte[] imageBytes, String fileName) throws IOException {
        String fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));

        try {
            Map uploadResult = cloudinary.uploader().upload(
                    imageBytes,
                    Map.of(
                            "public_id", fileNameWithoutExtension,
                            "resource_type", "image"
                    ));
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new IOException(e);
        }
    }
}