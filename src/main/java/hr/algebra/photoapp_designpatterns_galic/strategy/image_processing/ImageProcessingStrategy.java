package hr.algebra.photoapp_designpatterns_galic.strategy.image_processing;

import java.awt.image.BufferedImage;

public interface ImageProcessingStrategy {
    BufferedImage process(BufferedImage inputImage);
}