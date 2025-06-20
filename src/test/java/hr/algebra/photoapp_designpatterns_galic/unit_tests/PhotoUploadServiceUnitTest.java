package hr.algebra.photoapp_designpatterns_galic.unit_tests;

import hr.algebra.photoapp_designpatterns_galic.dto.PhotoUploadDTO;
import hr.algebra.photoapp_designpatterns_galic.model.ActionType;
import hr.algebra.photoapp_designpatterns_galic.model.ImageFormat;
import hr.algebra.photoapp_designpatterns_galic.model.Photo;
import hr.algebra.photoapp_designpatterns_galic.model.User;
import hr.algebra.photoapp_designpatterns_galic.repository.PhotoRepository;
import hr.algebra.photoapp_designpatterns_galic.repository.UserRepository;
import hr.algebra.photoapp_designpatterns_galic.service.AuditLoggerService;
import hr.algebra.photoapp_designpatterns_galic.service.ConsumptionService;
import hr.algebra.photoapp_designpatterns_galic.service.PhotoUploadService;
import hr.algebra.photoapp_designpatterns_galic.strategy.image_storage.ImageStorageStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PhotoUploadServiceUnitTest {
    @Mock
    private PhotoRepository photoRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ConsumptionService consumptionService;
    @Mock
    private ImageStorageStrategy imageStorageStrategy;
    @Mock
    private AuditLoggerService auditLoggerService;
    @InjectMocks
    private PhotoUploadService photoUploadService;
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(testUser));
    }

    @Test
    void shouldUploadPhotoSuccessfully() throws Exception {
        BufferedImage testImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(testImage, "jpg", baos);
        byte[] imageBytes = baos.toByteArray();

        MockMultipartFile mockFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", imageBytes);
        PhotoUploadDTO dto = new PhotoUploadDTO();
        dto.setImage(mockFile);
        dto.setImageFormat(ImageFormat.JPG);
        dto.setDescription("Test image");
        dto.setHashtags(List.of("test"));
        dto.setResizeWidth(10);
        dto.setResizeHeight(10);

        when(imageStorageStrategy.storeImage(any(), anyString()))
                .thenReturn("path/original.jpg")
                .thenReturn("path/processed.jpg");

        photoUploadService.uploadPhoto(dto, "test@example.com");

        verify(photoRepository).save(any(Photo.class));
        verify(consumptionService).recordUpload(anyDouble());
        verify(imageStorageStrategy, times(2)).storeImage(any(), anyString());
        verify(auditLoggerService).logAction(eq(testUser), eq(ActionType.UPLOAD), contains("Photo with ID"));
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findByEmail("invalid@example.com")).thenReturn(Optional.empty());

        PhotoUploadDTO dto = new PhotoUploadDTO();
        dto.setImageFormat(ImageFormat.JPG);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                photoUploadService.uploadPhoto(dto, "invalid@example.com"));

        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void shouldThrowForInvalidImage() {
        MockMultipartFile mockFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", new byte[0]);

        PhotoUploadDTO dto = new PhotoUploadDTO();
        dto.setImage(mockFile);
        dto.setImageFormat(ImageFormat.JPG);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                photoUploadService.uploadPhoto(dto, "test@example.com"));

        assertEquals("Invalid image file.", ex.getMessage());
    }
}