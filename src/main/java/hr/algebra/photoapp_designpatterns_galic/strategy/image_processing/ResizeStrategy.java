package hr.algebra.photoapp_designpatterns_galic.strategy.image_processing;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ResizeStrategy implements ImageProcessingStrategy {
    private final int newWidth;
    private final int newHeight;

    public ResizeStrategy(int newWidth, int newHeight) {
        this.newWidth = newWidth;
        this.newHeight = newHeight;
    }

    @Override
    public BufferedImage process(BufferedImage inputImage) {
        Image tmpImage = inputImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, inputImage.getType());
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(tmpImage, 0, 0, null);
        g2d.dispose();
        return resizedImage;
    }
}