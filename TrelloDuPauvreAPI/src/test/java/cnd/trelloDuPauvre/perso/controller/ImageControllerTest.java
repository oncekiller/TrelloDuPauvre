package cnd.trelloDuPauvre.perso.controller;


import cnd.trelloDuPauvre.perso.Exceptions.EntityNotFoundException;
import cnd.trelloDuPauvre.perso.model.Image;
import cnd.trelloDuPauvre.perso.service.ImageService;
import cnd.trelloDuPauvre.perso.utils.TestUtil;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.multipart.MultipartFile;

import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ImageController.class)
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImageService imageService;

    private TestUtil testUtil;


    @Test
    void can_call_getImageById_with_validId() throws Exception {
        //given
        int id = new Random().nextInt(100);
        byte[] bytes = new byte[new Random().nextInt(100)];
        Image image = new Image(
                "fileName",
                "fileType",
                new Random().nextLong(),
                bytes
        );

        //when
        when(imageService.getImageById(any(int.class))).thenReturn(image);

        //then
        mockMvc.perform(get("/api/v1/images/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName", is(image.getFileName())))
                .andExpect(jsonPath("$.fileType", is(image.getFileType())))
                .andExpect(jsonPath("$.size", is(image.getSize())));

        ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(imageService, times(1)).getImageById(idCaptor.capture());

        int capturedId = idCaptor.getValue();
        assertThat(capturedId).isEqualTo(id);
    }

    @Test
    void getImageById_with_notExistingId_shouldThrow_NotFoundException() throws Exception {
        //given
        int id = new Random().nextInt(100);
        String errorMessage = "Image with id " + id + " not found";

        //when
        when(imageService.getImageById(any(int.class))).thenThrow(new EntityNotFoundException(errorMessage));

        //then
        mockMvc.perform(get("/api/v1/images/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(errorMessage)));
    }

    @Test
    void can_call_createImage() throws Exception {
        //given
        Image image = new Image(
                "fileName",
                "fileType",
                new Random().nextLong(),
                new byte[new Random().nextInt(100)]
        );
        MockMultipartFile  file = new MockMultipartFile ("foo", "foo.txt", MediaType.TEXT_PLAIN_VALUE, "Hello World".getBytes());
        MockedStatic<Image> imageBuilder = Mockito.mockStatic(Image.class);
        //when
        when(imageService.createImage(any(Image.class))).thenReturn(image);
        imageBuilder.when(() -> Image.buildImage(any(MultipartFile.class))).thenReturn(image);
        //then
        mockMvc.perform(multipart("/api/v1/images")
                        .file(file))
                .andExpect(status().isCreated());
    }

    @Test
    void can_call_deleteImage_with_validId_should_return_true() throws Exception {
        //given
        int id = new Random().nextInt(100);

        //when
        when(imageService.deleteImage(any(int.class))).thenReturn(true);

        //then
        mockMvc.perform(delete("/api/v1/images/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(true)));

        ArgumentCaptor<Integer> idCaptorDeleteBy = ArgumentCaptor.forClass(Integer.class);
        verify(imageService, times(1)).deleteImage(idCaptorDeleteBy.capture());

        int capturedDeleteById = idCaptorDeleteBy.getValue();
        assertThat(capturedDeleteById).isEqualTo(id);
    }

    @Test
    void deleteImage_with_notExistingId_should_return_false() throws Exception {
        //given
        int id = new Random().nextInt(100);

        //when
        when(imageService.deleteImage(any(int.class))).thenReturn(false);

        //then
        mockMvc.perform(delete("/api/v1/images/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(false)));

        ArgumentCaptor<Integer> idCaptorDeleteBy = ArgumentCaptor.forClass(Integer.class);
        verify(imageService, times(1)).deleteImage(idCaptorDeleteBy.capture());

        int capturedDeleteById = idCaptorDeleteBy.getValue();
        assertThat(capturedDeleteById).isEqualTo(id);
    }

}