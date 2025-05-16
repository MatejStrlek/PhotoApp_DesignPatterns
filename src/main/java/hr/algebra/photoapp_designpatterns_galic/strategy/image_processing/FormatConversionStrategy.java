package hr.algebra.photoapp_designpatterns_galic.strategy.image_processing;

import hr.algebra.photoapp_designpatterns_galic.model.ImageFormat;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.awt.image.BufferedImage;

@Getter
@Setter
public class FormatConversionStrategy implements ImageProcessingStrategy {
    private final ImageFormat format;

    public FormatConversionStrategy(String formatString) {
        this.format = ImageFormat.fromString(formatString);
    }

    @Override
    public BufferedImage process(BufferedImage inputImage) {
        BufferedImage outputImage = new BufferedImage(
                inputImage.getWidth(),
                inputImage.getHeight(),
                format.getBufferedImageType()
        );

        Graphics2D g = outputImage.createGraphics();
        g.drawImage(inputImage, 0, 0, null);
        g.dispose();

        return outputImage;
    }
}