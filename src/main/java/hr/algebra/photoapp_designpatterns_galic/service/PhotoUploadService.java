package hr.algebra.photoapp_designpatterns_galic.service;

import hr.algebra.photoapp_designpatterns_galic.dto.PhotoUploadDTO;
import hr.algebra.photoapp_designpatterns_galic.model.Photo;
import hr.algebra.photoapp_designpatterns_galic.repository.PhotoRepository;
import hr.algebra.photoapp_designpatterns_galic.repository.UserRepository;
import hr.algebra.photoapp_designpatterns_galic.strategy.image_processing.FormatConversionStrategy;
import hr.algebra.photoapp_designpatterns_galic.strategy.image_processing.ImageProcessingContext;
import hr.algebra.photoapp_designpatterns_galic.strategy.image_processing.ResizeStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PhotoUploadService {
    private final PhotoRepository photoRepository;
    private final UserRepository userRepository;
    private final ConsumptionService consumptionService;

    @Value("${upload.path}")
    private String uploadPath;

    public PhotoUploadService(PhotoRepository photoRepository, UserRepository userRepository, ConsumptionService consumptionService) {
        this.photoRepository = photoRepository;
        this.userRepository = userRepository;
        this.consumptionService = consumptionService;
    }

    public void uploadPhoto(PhotoUploadDTO photoUploadDTO, String email) throws IOException {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found."));

        BufferedImage originalImage = ImageIO.read(photoUploadDTO.getImage().getInputStream());
        if (originalImage == null) {
            throw new IllegalArgumentException("Invalid image format.");
        }

        ImageProcessingContext imageProcessingContext = new ImageProcessingContext();

        if (photoUploadDTO.getResizeWidth() != null && photoUploadDTO.getResizeHeight() != null) {
            imageProcessingContext.addStrategy(new ResizeStrategy(
                    photoUploadDTO.getResizeWidth(),
                    photoUploadDTO.getResizeHeight()));
        }

        FormatConversionStrategy formatConversionStrategy =
                new FormatConversionStrategy(photoUploadDTO.getImageFormat().name().toLowerCase());
        imageProcessingContext.addStrategy(formatConversionStrategy);

        BufferedImage processedImage = imageProcessingContext.applyStrategies(originalImage);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(processedImage, formatConversionStrategy.getFormat().name().toLowerCase(), baos);

        byte[] imageBytes = baos.toByteArray();
        baos.close();

        double imageSizeMB = (double) imageBytes.length / (1024 * 1024);
        consumptionService.recordUpload(imageSizeMB);

        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        String fileName = UUID.randomUUID() + "." + formatConversionStrategy.getFormat();
        Path destinationPath = uploadDir.resolve(fileName);
        Files.write(destinationPath, imageBytes);

        var photo = new Photo();
        photo.setFileName(fileName);
        photo.setFilePath(destinationPath.toString());
        photo.setFileType(photoUploadDTO.getImageFormat().name());
        photo.setWidth(processedImage.getWidth());
        photo.setHeight(processedImage.getHeight());
        photo.setFileSizeMB(imageSizeMB);
        photo.setDescription(photoUploadDTO.getDescription());
        photo.setHashtags(photoUploadDTO.getHashtags());
        photo.setUploadTime(LocalDateTime.now());
        photo.setAuthor(user);

        photoRepository.save(photo);
    }
}