package hr.algebra.photoapp_designpatterns_galic.decorator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class ThumbnailGenerator {
    @Value("${upload.thumbnail.path:uploads/thumbnails}")
    private String thumbnailDirectory;

    public String generateThumbnail(BufferedImage originalImage, String originalFileName) throws IOException {
        int targetWidth = 300;
        double aspectRatio = (double) originalImage.getWidth() / originalImage.getHeight();
        int targetHeight = (int) (targetWidth / aspectRatio);

        BufferedImage thumbnailImage = new BufferedImage(targetWidth, targetHeight, originalImage.getType());
        Graphics2D g = thumbnailImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g.dispose();

        String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        String thumbnailFileName = originalFileName.replace("." + extension, "_thumbnail." + extension);

        Path dir = Paths.get(thumbnailDirectory);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        Path thumbnailPath = dir.resolve(thumbnailFileName);
        ImageIO.write(thumbnailImage, extension, thumbnailPath.toFile());

        return thumbnailPath.toString();
    }
}