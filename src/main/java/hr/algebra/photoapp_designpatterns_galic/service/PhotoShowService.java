package hr.algebra.photoapp_designpatterns_galic.service;

import hr.algebra.photoapp_designpatterns_galic.model.Photo;
import hr.algebra.photoapp_designpatterns_galic.model.User;
import hr.algebra.photoapp_designpatterns_galic.repository.PhotoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PhotoShowService {
    private final PhotoRepository photoRepository;

    public PhotoShowService(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }

    public List<Photo> findLast10AllPhotos() {
        return photoRepository.findTop10ByOrderByUploadTimeDesc();
    }

    public List<Photo> findPhotosByAuthor(User author) {
        return photoRepository.findByAuthor(author);
    }

    public Optional<Photo> findPhotoById(Long id) {
        return photoRepository.findById(id);
    }
}