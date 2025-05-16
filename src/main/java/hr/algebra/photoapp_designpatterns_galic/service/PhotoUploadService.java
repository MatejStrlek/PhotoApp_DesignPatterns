package hr.algebra.photoapp_designpatterns_galic.service;

import hr.algebra.photoapp_designpatterns_galic.dto.PhotoUploadDTO;
import hr.algebra.photoapp_designpatterns_galic.model.ImageFormat;
import hr.algebra.photoapp_designpatterns_galic.model.Photo;
import hr.algebra.photoapp_designpatterns_galic.model.User;
import hr.algebra.photoapp_designpatterns_galic.repository.PhotoRepository;
import hr.algebra.photoapp_designpatterns_galic.repository.UserRepository;
import hr.algebra.photoapp_designpatterns_galic.strategy.image_processing.FormatConversionStrategy;
import hr.algebra.photoapp_designpatterns_galic.strategy.image_processing.ImageProcessingContext;
import hr.algebra.photoapp_designpatterns_galic.strategy.image_processing.ResizeStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
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

        String originalExtension = getExtension(uploadedFile.getOriginalFilename());
        String originalFileName = UUID.randomUUID() + originalExtension;
        Path originalPath = saveImageFile(originalBytes, originalFileName);

        BufferedImage processedImage = processImage(originalImage, dto);
        byte[] processedBytes = encodeImage(processedImage, dto.getImageFormat());

        double originalSizeMB = calculateSizeInMB(originalBytes);
        consumptionService.recordUpload(originalSizeMB);

        String processedFileName = UUID.randomUUID() + "." + dto.getImageFormat().name().toLowerCase();
        Path processedPath = saveImageFile(processedBytes, processedFileName);

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

    private Path saveImageFile(byte[] imageBytes, String fileName) throws IOException {
        Path uploadDir = Paths.get(uploadPath);

        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        Path destination = uploadDir.resolve(fileName);
        Files.write(destination, imageBytes);
        return destination;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new IllegalArgumentException("Invalid filename format.");
        }
        return filename.substring(filename.lastIndexOf('.')).toLowerCase();
    }

    private Photo createPhotoMetadata(Path originalPath, byte[] originalSizeBytes,
                                      Path processedPath, byte[] processedSizeBytes,
                                      PhotoUploadDTO dto, BufferedImage image,
                                      User author) {

        Photo photo = new Photo();
        photo.setOriginalFilePath(originalPath.toString());
        photo.setOriginalFileName(originalPath.getFileName().toString());
        photo.setOriginalFileSizeMb(calculateSizeInMB(originalSizeBytes));

        photo.setProcessedFilePath(processedPath.toString());
        photo.setProcessedFileName(processedPath.getFileName().toString());
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