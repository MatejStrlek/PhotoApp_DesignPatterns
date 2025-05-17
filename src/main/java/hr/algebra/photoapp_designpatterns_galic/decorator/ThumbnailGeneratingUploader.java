package hr.algebra.photoapp_designpatterns_galic.decorator;

import hr.algebra.photoapp_designpatterns_galic.dto.PhotoUploadDTO;
import hr.algebra.photoapp_designpatterns_galic.model.Photo;
import hr.algebra.photoapp_designpatterns_galic.model.User;
import hr.algebra.photoapp_designpatterns_galic.repository.PhotoRepository;
import hr.algebra.photoapp_designpatterns_galic.repository.UserRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Paths;

@Component
@Primary
public class ThumbnailGeneratingUploader extends PhotoUploadDecorator{
    private final PhotoRepository photoRepository;
    private final UserRepository userRepository;
    private final ThumbnailGenerator thumbnailGenerator;

    public ThumbnailGeneratingUploader(PhotoUploadComponent delegate,
                                       PhotoRepository photoRepository,
                                       UserRepository userRepository,
                                       ThumbnailGenerator thumbnailGenerator) {
        super(delegate);
        this.photoRepository = photoRepository;
        this.userRepository = userRepository;
        this.thumbnailGenerator = thumbnailGenerator;
    }

    @Override
    public void uploadPhoto(PhotoUploadDTO dto, String email) throws IOException {
        super.uploadPhoto(dto, email);
        generateThumbnail(dto, email);
    }

    private void generateThumbnail(PhotoUploadDTO dto, String email) throws IOException {
        try {
            BufferedImage originalImage = ImageIO.read(dto.getImage().getInputStream());
            String thumbnailPath = thumbnailGenerator.generateThumbnail(originalImage, dto.getImage().getOriginalFilename());

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            Photo photo = photoRepository.findTopByAuthorOrderByUploadTimeDesc(user)
                    .orElseThrow(() -> new IllegalArgumentException("Photo not found"));

            photo.setThumbnailFileName(Paths.get(thumbnailPath).getFileName().toString());

            photoRepository.save(photo);
        } catch (IOException e) {
            throw new IOException("Thumbnail generation failed", e);
        }
    }
}