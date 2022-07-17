package cnd.trelloDuPauvre.perso.controller;


import cnd.trelloDuPauvre.perso.Exceptions.EntityNotFoundException;
import cnd.trelloDuPauvre.perso.model.CheckListItem;
import cnd.trelloDuPauvre.perso.model.Ticket;
import cnd.trelloDuPauvre.perso.service.CheckListItemService;
import cnd.trelloDuPauvre.perso.utils.TestUtil;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CheckListItemController.class)
class CheckListItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CheckListItemService checkListItemService;

    private TestUtil testUtil;


    @Test
    void can_call_getAllCheckListItem() throws Exception {
        //given
        CheckListItem checkListItem1 = new CheckListItem("label1", true);
        CheckListItem checkListItem2 = new CheckListItem("label2", false);
        ArrayList<CheckListItem> checkListItems = new ArrayList<>();
        checkListItems.add(checkListItem1);
        checkListItems.add(checkListItem2);

        // When
        when(checkListItemService.getAllCheckListItems()).thenReturn(checkListItems);

        //Then
        mockMvc.perform(get("/api/v1/checkListItems"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].label", is(checkListItem1.getLabel())))
                .andExpect(jsonPath("$[1].checked", is(checkListItem2.getChecked())));


        verify(checkListItemService, times(1)).getAllCheckListItems();
    }

    @Test
    void can_call_getCheckListItemById_with_validId() throws Exception {
        //given
        int id = new Random().nextInt(100);
        CheckListItem checkListItem = new CheckListItem("label", true);

        //when
        when(checkListItemService.getCheckListItemById(any(int.class))).thenReturn(checkListItem);

        //then
        mockMvc.perform(get("/api/v1/checkListItems/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label", is(checkListItem.getLabel())))
                .andExpect(jsonPath("$.checked", is(checkListItem.getChecked())));

        ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(checkListItemService, times(1)).getCheckListItemById(idCaptor.capture());

        int capturedId = idCaptor.getValue();
        assertThat(capturedId).isEqualTo(id);
    }

    @Test
    void getCheckListItemById_with_notExistingId_shouldThrow_NotFoundException() throws Exception {
        //given
        int id = new Random().nextInt(100);
        String errorMessage = "CheckListItem with id " + id + " not found";

        //when
        when(checkListItemService.getCheckListItemById(any(int.class))).thenThrow(new EntityNotFoundException(errorMessage));

        //then
        mockMvc.perform(get("/api/v1/checkListItems/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(errorMessage)));
    }

    @Test
    void can_call_createCheckListItem() throws Exception {
        //given
        Ticket ticket = new Ticket(
                "name",
                "description",
                new Random().nextInt(100),
                LocalDate.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        CheckListItem checkListItem = new CheckListItem("label", true);
        checkListItem.setTicket(ticket);
        //when
        when(checkListItemService.createCheckListItem(any(CheckListItem.class))).thenReturn(checkListItem);

        //then
        mockMvc.perform(post("/api/v1/checkListItems")
                        .content(checkListItem.toStringForTest())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.label", is(checkListItem.getLabel())))
                .andExpect(jsonPath("$.checked", is(checkListItem.getChecked())))
                .andExpect(jsonPath("$.checkListItemId").exists());


        verify(checkListItemService, times(1)).createCheckListItem(any(CheckListItem.class));

    }

    @Test
    void can_call_updateCheckListItem_with_validId() throws Exception {
        //given
        CheckListItem checkListItem = new CheckListItem("label", true);
        Ticket ticket = new Ticket(
                "name",
                "description",
                new Random().nextInt(100),
                LocalDate.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        checkListItem.setTicket(ticket);
        int id = new Random().nextInt(100);
        //when
        when(checkListItemService.getCheckListItemById(any(int.class))).thenReturn(checkListItem);
        when(checkListItemService.updateCheckListItem(any(int.class), any(CheckListItem.class))).thenReturn(checkListItem);

        //then
        mockMvc.perform(put("/api/v1/checkListItems/" + id)
                    .content(checkListItem.toStringForTest())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label", is(checkListItem.getLabel())))
                .andExpect(jsonPath("$.checked", is(checkListItem.getChecked())));


        ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(checkListItemService, times(1)).updateCheckListItem(idCaptor.capture(), any(CheckListItem.class));

        int capturedId = idCaptor.getValue();

        assertThat(capturedId).isEqualTo(id);
    }

    @Test
    void updateCheckListItem_with_notExistingId_shouldThrow_NotFoundException() throws Exception {
        //given
        int id = new Random().nextInt(100);
        String errorMessage = "CheckListItem with id " + id + " not found";
        CheckListItem checkListItem = new CheckListItem("label", true);
        Ticket ticket = new Ticket(
                "name",
                "description",
                new Random().nextInt(100),
                LocalDate.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        checkListItem.setTicket(ticket);

        //when
        when(checkListItemService.updateCheckListItem(any(int.class), any(CheckListItem.class))).thenThrow(new EntityNotFoundException(errorMessage));
        //then
        mockMvc.perform(put("/api/v1/checkListItems/" + id)
                        .content(checkListItem.toStringForTest())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(errorMessage)));
    }

    @Test
    void can_call_deleteCheckListItem_with_validId_should_return_true() throws Exception {
        //given
        int id = new Random().nextInt(100);

        //when
        when(checkListItemService.deleteCheckListItem(any(int.class))).thenReturn(true);

        //then
        mockMvc.perform(delete("/api/v1/checkListItems/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(true)));

        ArgumentCaptor<Integer> idCaptorDeleteBy = ArgumentCaptor.forClass(Integer.class);
        verify(checkListItemService, times(1)).deleteCheckListItem(idCaptorDeleteBy.capture());

        int capturedDeleteById = idCaptorDeleteBy.getValue();
        assertThat(capturedDeleteById).isEqualTo(id);
    }

    @Test
    void deleteCheckListItem_with_notExistingId_should_return_false() throws Exception {
        //given
        int id = new Random().nextInt(100);

        //when
        when(checkListItemService.deleteCheckListItem(any(int.class))).thenReturn(false);

        //then
        mockMvc.perform(delete("/api/v1/checkListItems/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(false)));

        ArgumentCaptor<Integer> idCaptorDeleteBy = ArgumentCaptor.forClass(Integer.class);
        verify(checkListItemService, times(1)).deleteCheckListItem(idCaptorDeleteBy.capture());

        int capturedDeleteById = idCaptorDeleteBy.getValue();
        assertThat(capturedDeleteById).isEqualTo(id);
    }

}