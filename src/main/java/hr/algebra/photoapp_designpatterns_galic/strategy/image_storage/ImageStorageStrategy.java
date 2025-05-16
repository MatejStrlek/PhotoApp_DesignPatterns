package hr.algebra.photoapp_designpatterns_galic.strategy.image_storage;

import java.io.IOException;
import java.nio.file.Path;

public interface ImageStorageStrategy {
    Path storeImage(byte[] imageBytes, String fileName) throws IOException;
}