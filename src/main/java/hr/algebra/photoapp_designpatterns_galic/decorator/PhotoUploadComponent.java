package hr.algebra.photoapp_designpatterns_galic.decorator;

import hr.algebra.photoapp_designpatterns_galic.dto.PhotoUploadDTO;

import java.io.IOException;

public interface PhotoUploadComponent {
    void uploadPhoto(PhotoUploadDTO dto, String email) throws IOException;
}