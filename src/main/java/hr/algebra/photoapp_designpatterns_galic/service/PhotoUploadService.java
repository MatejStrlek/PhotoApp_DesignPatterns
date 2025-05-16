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
        User user = getUserByEmail(email);
        BufferedImage originalImage = readOriginalImage(photoUploadDTO);
        BufferedImage processedImage = processImage(originalImage, photoUploadDTO);

        byte[] imageBytes = encodeImage(processedImage, photoUploadDTO.getImageFormat());
        double imageSizeMB = calculateSizeInMB(imageBytes);
        consumptionService.recordUpload(imageSizeMB);

        Path savedPath = saveImageFile(imageBytes, photoUploadDTO.getImageFormat());
        Photo photo = createPhotoMetadata(savedPath, photoUploadDTO, processedImage, imageSizeMB, user);
        photoRepository.save(photo);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private BufferedImage readOriginalImage(PhotoUploadDTO dto) throws IOException {
        BufferedImage image = ImageIO.read(dto.getImage().getInputStream());
        if (image == null) {
            throw new IllegalArgumentException("Invalid image format.");
        }
        return image;
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
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, format.getFormatName(), baos);
        return baos.toByteArray();
    }

    private double calculateSizeInMB(byte[] imageBytes) {
        return (double) imageBytes.length / (1024 * 1024);
    }

    private Path saveImageFile(byte[] imageBytes, ImageFormat format) throws IOException {
        Path uploadDir = Paths.get(uploadPath);

        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        String fileName = UUID.randomUUID() + "." + format.name().toLowerCase();
        Path destination = uploadDir.resolve(fileName);
        Files.write(destination, imageBytes);

        return destination;
    }

    private Photo createPhotoMetadata(Path path,
                                      PhotoUploadDTO dto,
                                      BufferedImage image,
                                      double sizeMB,
                                      User author) {
        Photo photo = new Photo();
        photo.setFileName(path.getFileName().toString());
        photo.setFilePath(path.toString());
        photo.setFileType(dto.getImageFormat().name());
        photo.setWidth(image.getWidth());
        photo.setHeight(image.getHeight());
        photo.setFileSizeMB(sizeMB);
        photo.setDescription(dto.getDescription());
        photo.setHashtags(dto.getHashtags());
        photo.setUploadTime(LocalDateTime.now());
        photo.setAuthor(author);

        return photo;
    }
}