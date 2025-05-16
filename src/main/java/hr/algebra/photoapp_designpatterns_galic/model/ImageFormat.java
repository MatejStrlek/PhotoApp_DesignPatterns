package hr.algebra.photoapp_designpatterns_galic.model;

import lombok.Getter;

import java.awt.image.BufferedImage;
import java.util.Arrays;

@Getter
public enum ImageFormat {
    PNG("png", BufferedImage.TYPE_INT_ARGB),
    JPG("jpg", BufferedImage.TYPE_INT_RGB);

    private final String formatName;
    private final int bufferedImageType;

    ImageFormat(String formatName, int bufferedImageType) {
        this.formatName = formatName;
        this.bufferedImageType = bufferedImageType;
    }

    public static ImageFormat fromString(String format) {
        return Arrays.stream(values())
                .filter(imageFormat -> imageFormat.formatName.equalsIgnoreCase(format))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported format: " + format));
    }
}