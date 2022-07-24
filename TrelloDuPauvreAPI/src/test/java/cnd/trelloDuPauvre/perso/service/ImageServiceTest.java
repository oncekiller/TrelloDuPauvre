package cnd.trelloDuPauvre.perso.service;

import cnd.trelloDuPauvre.perso.Exceptions.EntityNotFoundException;
import cnd.trelloDuPauvre.perso.model.Image;
import cnd.trelloDuPauvre.perso.repository.ImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    ImageRepository imageRepository;
    ImageService underTest;

    @BeforeEach
    void setUp() {
        underTest = new ImageService(imageRepository);
    }


    @Test
    void can_call_getImageById_with_validId() {
        //given
        int id = new Random().nextInt(100);
        Image image = new Image(
                "fileName",
                "fileType",
                new Random().nextLong(),
                new byte[new Random().nextInt(100)]
        );
        //when
        when(imageRepository.findById(any(int.class))).thenReturn(Optional.of(image));
        Image result = underTest.getImageById(id);
        //then
        ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(imageRepository).findById(idCaptor.capture());

        int capturedId = idCaptor.getValue();
        assertThat(capturedId).isEqualTo(id);
        assertThat(result).isEqualTo(image);
    }

    @Test
    void getImageById_with_notExistingId_shouldThrow_NotFoundException() {
        //given
        int id = new Random().nextInt(100);
        //when
        //then
        assertThatThrownBy(() -> underTest.getImageById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Image with id " + id + " not found");
    }

    @Test
    void can_call_createImage() {
        //given
        Image image = new Image(
                "fileName",
                "fileType",
                new Random().nextLong(),
                new byte[new Random().nextInt(100)]
        );
        //when
        when(imageRepository.save(any(Image.class))).thenReturn(image);
        Image result = underTest.createImage(image);
        //then
        ArgumentCaptor<Image> imageCaptor = ArgumentCaptor.forClass(Image.class);
        verify(imageRepository).save(imageCaptor.capture());

        Image capturedImage = imageCaptor.getValue();
        assertThat(capturedImage).isEqualTo(image);
        assertThat(result).isEqualTo(image);
    }

    @Test
    void can_call_deleteImage_with_validId_should_return_true() {
        //given
        int id = new Random().nextInt(100);
        Image image = new Image(
                "fileName",
                "fileType",
                new Random().nextLong(),
                new byte[new Random().nextInt(100)]
        );
        //when
        when(imageRepository.findById(any(int.class))).thenReturn(Optional.of(image));
        Boolean result = underTest.deleteImage(id);
        //then
        ArgumentCaptor<Integer> idCaptorFindBy = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> idCaptorDeleteBy = ArgumentCaptor.forClass(Integer.class);
        verify(imageRepository).findById(idCaptorFindBy.capture());
        verify(imageRepository).deleteById(idCaptorDeleteBy.capture());

        int capturedFindById = idCaptorFindBy.getValue();
        int capturedDeleteById = idCaptorDeleteBy.getValue();
        assertThat(capturedFindById).isEqualTo(id);
        assertThat(capturedDeleteById).isEqualTo(id);
        assertThat(result).isEqualTo(true);
    }

    @Test
    void deleteImage_with_notExistingId_should_return_false() {
        //given
        int id = new Random().nextInt(100);
        //when
        Boolean result = underTest.deleteImage(id);
        //then
        assertThat(result).isEqualTo(false);
    }
}