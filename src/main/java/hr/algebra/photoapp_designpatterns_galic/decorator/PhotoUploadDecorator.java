package hr.algebra.photoapp_designpatterns_galic.decorator;

import hr.algebra.photoapp_designpatterns_galic.dto.PhotoUploadDTO;

import java.io.IOException;

public abstract class PhotoUploadDecorator implements PhotoUploadComponent {
    protected final PhotoUploadComponent delegate;

    protected PhotoUploadDecorator(PhotoUploadComponent delegate) {
        this.delegate = delegate;
    }

    @Override
    public void uploadPhoto(PhotoUploadDTO dto, String email) throws IOException {
        delegate.uploadPhoto(dto, email);
    }
}