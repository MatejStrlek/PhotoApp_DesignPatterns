package hr.algebra.photoapp_designpatterns_galic.dto;

import hr.algebra.photoapp_designpatterns_galic.model.ImageFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class PhotoUploadDTO {
    @NotBlank(message = "File description cannot be blank")
    private String description;

    @NotEmpty(message = "At least one hashtag is required")
    private List<String> hashtags;

    @NotNull(message = "Image cannot be null")
    private MultipartFile image;

    @NotNull(message = "Image format cannot be null")
    private ImageFormat imageFormat;

    private Integer resizeWidth;
    private Integer resizeHeight;
}