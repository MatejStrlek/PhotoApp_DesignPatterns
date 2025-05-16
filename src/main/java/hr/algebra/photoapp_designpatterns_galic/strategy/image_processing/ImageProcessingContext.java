package hr.algebra.photoapp_designpatterns_galic.strategy.image_processing;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ImageProcessingContext {
    private final List<ImageProcessingStrategy> imageProcessingStrategies = new ArrayList<>();

    public void addStrategy(ImageProcessingStrategy strategy) {
        imageProcessingStrategies.add(strategy);
    }

    public BufferedImage applyStrategies(BufferedImage inputImage) {
        BufferedImage processedImage = inputImage;
        for (ImageProcessingStrategy strategy : imageProcessingStrategies) {
            processedImage = strategy.process(processedImage);
        }
        return processedImage;
    }
}