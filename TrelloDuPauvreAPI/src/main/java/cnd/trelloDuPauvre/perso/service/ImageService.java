package cnd.trelloDuPauvre.perso.service;

import cnd.trelloDuPauvre.perso.Exceptions.EntityNotFoundException;
import cnd.trelloDuPauvre.perso.model.Image;
import cnd.trelloDuPauvre.perso.repository.ImageRepository;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;


    public Image getImageById(int imageId) {
        Optional<Image> image = imageRepository.findById(imageId);

        if (image.isPresent()){
            return image.get();
        }else {
            throw new EntityNotFoundException("Image with id " + imageId + " not found");
        }
    }


    public Image createImage(Image image) {
        return imageRepository.save(image);
    }

    public Boolean deleteImage(int imageId) {
        Optional<Image> deletedImage = imageRepository.findById(imageId);

        if(deletedImage.isPresent()) {
            imageRepository.deleteById(imageId);
            return true;
        }else{
            return false;
        }
    }

}
