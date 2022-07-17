package cnd.trelloDuPauvre.perso.controller;


import cnd.trelloDuPauvre.perso.Exceptions.EntityNotFoundException;
import cnd.trelloDuPauvre.perso.model.Nature;
import cnd.trelloDuPauvre.perso.model.Status;
import cnd.trelloDuPauvre.perso.service.NatureService;
import cnd.trelloDuPauvre.perso.utils.TestUtil;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NatureController.class)
class NatureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NatureService natureService;

    private TestUtil testUtil;


    @Test
    void can_call_getAllNature() throws Exception {
        //given
        Nature nature1 = new Nature("name1", "label1");
        Nature nature2 = new Nature("name2", "label2");
        ArrayList<Nature> natures = new ArrayList<>();
        natures.add(nature1);
        natures.add(nature2);

        // When
        when(natureService.getAllNatures()).thenReturn(natures);

        //Then
        mockMvc.perform(get("/api/v1/natures"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is(nature1.getName())))
                .andExpect(jsonPath("$[1].label", is(nature2.getLabel())));


        verify(natureService, times(1)).getAllNatures();
    }

    @Test
    void can_call_getNatureById_with_validId() throws Exception {
        //given
        int id = new Random().nextInt(100);
        Nature nature = new Nature("name", "label");

        //when
        when(natureService.getNatureById(any(int.class))).thenReturn(nature);

        //then
        mockMvc.perform(get("/api/v1/natures/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(nature.getName())))
                .andExpect(jsonPath("$.label", is(nature.getLabel())));

        ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(natureService, times(1)).getNatureById(idCaptor.capture());

        int capturedId = idCaptor.getValue();
        assertThat(capturedId).isEqualTo(id);
    }

    @Test
    void getNatureById_with_notExistingId_shouldThrow_NotFoundException() throws Exception {
        //given
        int id = new Random().nextInt(100);
        String errorMessage = "Nature with id " + id + " not found";

        //when
        when(natureService.getNatureById(any(int.class))).thenThrow(new EntityNotFoundException(errorMessage));

        //then
        mockMvc.perform(get("/api/v1/natures/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(errorMessage)));
    }

    @Test
    void can_call_createNature() throws Exception {
        //given
        Nature nature = new Nature("name", "label");

        //when
        when(natureService.createNature(any(Nature.class))).thenReturn(nature);

        //then
        mockMvc.perform(post("/api/v1/natures")
                        .content(testUtil.asJsonString(nature))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(nature.getName())))
                .andExpect(jsonPath("$.label", is(nature.getLabel())))
                .andExpect(jsonPath("$.natureId").exists());


        verify(natureService, times(1)).createNature(any(Nature.class));

    }

    @Test
    void can_call_updateNature_with_validId() throws Exception {
        //given
        Nature nature = new Nature("name", "label");
        int id = new Random().nextInt(100);
        //when
        when(natureService.getNatureById(any(int.class))).thenReturn(nature);
        when(natureService.updateNature(any(int.class), any(Nature.class))).thenReturn(nature);

        //then
        mockMvc.perform(put("/api/v1/natures/" + id)
                    .content(testUtil.asJsonString(nature))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(nature.getName())))
                .andExpect(jsonPath("$.label", is(nature.getLabel())));


        ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(natureService, times(1)).updateNature(idCaptor.capture(), any(Nature.class));

        int capturedId = idCaptor.getValue();

        assertThat(capturedId).isEqualTo(id);
    }

    @Test
    void updateNature_with_notExistingId_shouldThrow_NotFoundException() throws Exception {
        //given
        int id = new Random().nextInt(100);
        String errorMessage = "Nature with id " + id + " not found";
        Status status = new Status("name", "label");
        //when
        when(natureService.updateNature(any(int.class), any(Nature.class))).thenThrow(new EntityNotFoundException(errorMessage));
        //then
        mockMvc.perform(put("/api/v1/natures/" + id)
                        .content(testUtil.asJsonString(status))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(errorMessage)));
    }

    @Test
    void can_call_deleteNature_with_validId_should_return_true() throws Exception {
        //given
        int id = new Random().nextInt(100);

        //when
        when(natureService.deleteNature(any(int.class))).thenReturn(true);

        //then
        mockMvc.perform(delete("/api/v1/natures/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(true)));

        ArgumentCaptor<Integer> idCaptorDeleteBy = ArgumentCaptor.forClass(Integer.class);
        verify(natureService, times(1)).deleteNature(idCaptorDeleteBy.capture());

        int capturedDeleteById = idCaptorDeleteBy.getValue();
        assertThat(capturedDeleteById).isEqualTo(id);
    }

    @Test
    void deleteNature_with_notExistingId_should_return_false() throws Exception {
        //given
        int id = new Random().nextInt(100);

        //when
        when(natureService.deleteNature(any(int.class))).thenReturn(false);

        //then
        mockMvc.perform(delete("/api/v1/natures/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(false)));

        ArgumentCaptor<Integer> idCaptorDeleteBy = ArgumentCaptor.forClass(Integer.class);
        verify(natureService, times(1)).deleteNature(idCaptorDeleteBy.capture());

        int capturedDeleteById = idCaptorDeleteBy.getValue();
        assertThat(capturedDeleteById).isEqualTo(id);
    }

}