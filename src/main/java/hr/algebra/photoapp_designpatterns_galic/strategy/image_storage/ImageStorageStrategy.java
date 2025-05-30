package hr.algebra.photoapp_designpatterns_galic.strategy.image_storage;

import java.io.IOException;

public interface ImageStorageStrategy {
    String storeImage(byte[] imageBytes, String fileName) throws IOException;
}