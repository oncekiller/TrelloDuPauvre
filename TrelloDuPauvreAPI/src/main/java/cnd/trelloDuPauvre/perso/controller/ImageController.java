package cnd.trelloDuPauvre.perso.controller;

import cnd.trelloDuPauvre.perso.model.Image;
import cnd.trelloDuPauvre.perso.service.ImageService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Controller
@AllArgsConstructor
@RequestMapping("/api/v1")
public class ImageController {
    @Autowired
    private ImageService imageService;

    @GetMapping("/images/{imageId}")
    public ResponseEntity<Object> getImageById(@PathVariable(name = "imageId", required = true) int imageId) {
        Image image = imageService.getImageById(imageId);
        return  new ResponseEntity<>(image,  new HttpHeaders(), HttpStatus.OK);
    }

    @PostMapping("/images")
    public ResponseEntity<Object> createImage(@RequestBody MultipartFile file) {
        Image image = Image.buildImage(file);
        Image createdImage = imageService.createImage(image);
        return new ResponseEntity<>(createdImage, new HttpHeaders(), HttpStatus.CREATED);
    }

    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<Object> deleteImage(@PathVariable(name = "imageId") int imageId) {
        Boolean isImageDeleted = imageService.deleteImage(imageId);
        return new ResponseEntity<>(isImageDeleted, new HttpHeaders(), HttpStatus.OK);
    }

}
