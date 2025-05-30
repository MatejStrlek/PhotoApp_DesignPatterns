package hr.algebra.photoapp_designpatterns_galic.strategy.image_storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Profile("local")
public class LocalImageStorageStrategy implements ImageStorageStrategy {
    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public String storeImage(byte[] imageBytes, String fileName) throws IOException {
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        Path destination = uploadDir.resolve(fileName);
        Files.write(destination, imageBytes);
        return destination.toString();
    }
}