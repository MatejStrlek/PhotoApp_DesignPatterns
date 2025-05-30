package hr.algebra.photoapp_designpatterns_galic.service;

import hr.algebra.photoapp_designpatterns_galic.dto.PhotoUploadDTO;
import hr.algebra.photoapp_designpatterns_galic.model.ImageFormat;
import hr.algebra.photoapp_designpatterns_galic.model.Photo;
import hr.algebra.photoapp_designpatterns_galic.model.User;
import hr.algebra.photoapp_designpatterns_galic.repository.PhotoRepository;
import hr.algebra.photoapp_designpatterns_galic.decorator.PhotoUploadComponent;
import hr.algebra.photoapp_designpatterns_galic.repository.UserRepository;
import hr.algebra.photoapp_designpatterns_galic.strategy.image_processing.FormatConversionStrategy;
import hr.algebra.photoapp_designpatterns_galic.strategy.image_processing.ImageProcessingContext;
import hr.algebra.photoapp_designpatterns_galic.strategy.image_processing.ResizeStrategy;
import hr.algebra.photoapp_designpatterns_galic.strategy.image_storage.ImageStorageStrategy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PhotoUploadService implements PhotoUploadComponent {
    private final PhotoRepository photoRepository;
    private final UserRepository userRepository;
    private final ConsumptionService consumptionService;
    private final ImageStorageStrategy imageStorageStrategy;

    public PhotoUploadService(PhotoRepository photoRepository, UserRepository userRepository, ConsumptionService consumptionService, ImageStorageStrategy imageStorageStrategy) {
        this.photoRepository = photoRepository;
        this.userRepository = userRepository;
        this.consumptionService = consumptionService;
        this.imageStorageStrategy = imageStorageStrategy;
    }

    @Override
    public void uploadPhoto(PhotoUploadDTO dto, String email) throws IOException {
        User user = getUserByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        MultipartFile uploadedFile = dto.getImage();
        byte[] originalBytes = uploadedFile.getBytes();
        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(originalBytes));

        if (originalImage == null) {
            throw new IllegalArgumentException("Invalid image file.");
        }

        double originalSizeMB = calculateSizeInMB(originalBytes);
        consumptionService.recordUpload(originalSizeMB);

        String originalExtension = getExtension(uploadedFile.getOriginalFilename());
        String originalFileName = UUID.randomUUID() + originalExtension;
        String originalPath = imageStorageStrategy.storeImage(originalBytes, originalFileName);

        BufferedImage processedImage = processImage(originalImage, dto);
        byte[] processedBytes = encodeImage(processedImage, dto.getImageFormat());

        String processedFileName = UUID.randomUUID() + "." + dto.getImageFormat().name().toLowerCase();
        String processedPath = imageStorageStrategy.storeImage(processedBytes, processedFileName);

        Photo photo = createPhotoMetadata(originalPath, originalBytes,
                processedPath, processedBytes,
                dto, processedImage, user);

        photoRepository.save(photo);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private BufferedImage processImage(BufferedImage original, PhotoUploadDTO dto) {
        ImageProcessingContext context = new ImageProcessingContext();

        if (dto.getResizeWidth() != null && dto.getResizeHeight() != null) {
            context.addStrategy(new ResizeStrategy(dto.getResizeWidth(), dto.getResizeHeight()));
        }

        FormatConversionStrategy formatStrategy =
                new FormatConversionStrategy(dto.getImageFormat().name().toLowerCase());
        context.addStrategy(formatStrategy);

        return context.applyStrategies(original);
    }

    private byte[] encodeImage(BufferedImage image, ImageFormat format) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, format.getFormatName(), baos);
            return baos.toByteArray();
        }
    }

    private double calculateSizeInMB(byte[] imageBytes) {
        return (double) imageBytes.length / (1024 * 1024);
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new IllegalArgumentException("Invalid filename format.");
        }
        return filename.substring(filename.lastIndexOf('.')).toLowerCase();
    }

    private Photo createPhotoMetadata(String originalPath, byte[] originalSizeBytes,
                                      String processedPath, byte[] processedSizeBytes,
                                      PhotoUploadDTO dto, BufferedImage image,
                                      User author) {
        Photo photo = new Photo();

        photo.setOriginalFilePath(originalPath);
        photo.setOriginalFileName(originalPath.substring(originalPath.lastIndexOf('/') + 1));
        photo.setOriginalFileSizeMb(calculateSizeInMB(originalSizeBytes));

        photo.setProcessedFilePath(processedPath);
        photo.setProcessedFileName(processedPath.substring(processedPath.lastIndexOf('/') + 1));
        photo.setProcessedFileSizeMb(calculateSizeInMB(processedSizeBytes));

        photo.setFileType(dto.getImageFormat().name());
        photo.setWidth(image.getWidth());
        photo.setHeight(image.getHeight());
        photo.setDescription(dto.getDescription());
        photo.setHashtags(dto.getHashtags());
        photo.setUploadTime(LocalDateTime.now());
        photo.setAuthor(author);

        return photo;
    }
}