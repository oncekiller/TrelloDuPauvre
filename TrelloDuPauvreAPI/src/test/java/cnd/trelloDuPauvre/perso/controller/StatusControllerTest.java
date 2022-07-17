package cnd.trelloDuPauvre.perso.controller;


import cnd.trelloDuPauvre.perso.Exceptions.EntityNotFoundException;
import cnd.trelloDuPauvre.perso.model.Status;
import cnd.trelloDuPauvre.perso.service.StatusService;
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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatusController.class)
class StatusControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private StatusService statusService;

    private TestUtil testUtil;


    @Test
    void can_call_getAllStatus() throws Exception {
        //given
        Status status1 = new Status("name1", "label1");
        Status status2 = new Status("name2", "label2");
        ArrayList<Status> statuss = new ArrayList<>();
        statuss.add(status1);
        statuss.add(status2);
        // When
        when(statusService.getAllStatus()).thenReturn(statuss);
        //Then
        mockMvc.perform(get("/api/v1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is(status1.getName())))
                .andExpect(jsonPath("$[1].label", is(status2.getLabel())));


        verify(statusService, times(1)).getAllStatus();
    }

    @Test
    void can_call_getStatusById_with_validId() throws Exception {
        //given
        int id = new Random().nextInt(100);
        Status status = new Status("status", "label");

        //when
        when(statusService.getStatusById(any(int.class))).thenReturn(status);

        //then
        mockMvc.perform(get("/api/v1/status/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(status.getName())))
                .andExpect(jsonPath("$.label", is(status.getLabel())));

        ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(statusService, times(1)).getStatusById(idCaptor.capture());

        int capturedId = idCaptor.getValue();
        assertThat(capturedId).isEqualTo(id);
    }

    @Test
    void getStatusById_with_notExistingId_shouldThrow_NotFoundException() throws Exception {
        //given
        int id = new Random().nextInt(100);
        String errorMessage = "Status with id " + id + " not found";
        //when
        when(statusService.getStatusById(any(int.class))).thenThrow(new EntityNotFoundException(errorMessage));
        //then
        mockMvc.perform(get("/api/v1/status/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(errorMessage)));
    }

    @Test
    void can_call_createStatus() throws Exception {
        //given
        Status status = new Status("name", "label");

        //when
        when(statusService.createStatus(any(Status.class))).thenReturn(status);

        //then
        mockMvc.perform(post("/api/v1/status")
                        .content(testUtil.asJsonString(status))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(status.getName())))
                .andExpect(jsonPath("$.label", is(status.getLabel())))
                .andExpect(jsonPath("$.statusId").exists());


        verify(statusService, times(1)).createStatus(any(Status.class));

    }

    @Test
    void can_call_updateStatus_with_validId() throws Exception {
        //given
        Status status = new Status("name", "label");
        int id = new Random().nextInt(100);
        //when
        when(statusService.getStatusById(any(int.class))).thenReturn(status);
        when(statusService.updateStatus(any(int.class), any(Status.class))).thenReturn(status);

        //then
        mockMvc.perform(put("/api/v1/status/" + id)
                    .content(testUtil.asJsonString(status))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(status.getName())))
                .andExpect(jsonPath("$.label", is(status.getLabel())));


        ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(statusService, times(1)).updateStatus(idCaptor.capture(), any(Status.class));

        int capturedId = idCaptor.getValue();

        assertThat(capturedId).isEqualTo(id);
    }

    @Test
    void updateStatus_with_notExistingId_shouldThrow_NotFoundException() throws Exception {
        //given
        int id = new Random().nextInt(100);
        String errorMessage = "Status with id " + id + " not found";
        Status status = new Status("name", "label");
        //when
        when(statusService.updateStatus(any(int.class), any(Status.class))).thenThrow(new EntityNotFoundException(errorMessage));
        //then
        mockMvc.perform(put("/api/v1/status/" + id)
                        .content(testUtil.asJsonString(status))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(errorMessage)));
    }

    @Test
    void can_call_deleteStatus_with_validId_should_return_true() throws Exception {
        //given
        int id = new Random().nextInt(100);

        //when
        when(statusService.deleteStatus(any(int.class))).thenReturn(true);

        //then
        mockMvc.perform(delete("/api/v1/status/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(true)));

        ArgumentCaptor<Integer> idCaptorDeleteBy = ArgumentCaptor.forClass(Integer.class);
        verify(statusService, times(1)).deleteStatus(idCaptorDeleteBy.capture());

        int capturedDeleteById = idCaptorDeleteBy.getValue();
        assertThat(capturedDeleteById).isEqualTo(id);
    }

    @Test
    void deleteStatus_with_notExistingId_should_return_false() throws Exception {
        //given
        int id = new Random().nextInt(100);

        //when
        when(statusService.deleteStatus(any(int.class))).thenReturn(false);

        //then
        mockMvc.perform(delete("/api/v1/status/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(false)));

        ArgumentCaptor<Integer> idCaptorDeleteBy = ArgumentCaptor.forClass(Integer.class);
        verify(statusService, times(1)).deleteStatus(idCaptorDeleteBy.capture());

        int capturedDeleteById = idCaptorDeleteBy.getValue();
        assertThat(capturedDeleteById).isEqualTo(id);
    }

}