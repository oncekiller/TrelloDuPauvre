package cnd.trelloDuPauvre.perso.controller;


import cnd.trelloDuPauvre.perso.Exceptions.EntityNotFoundException;
import cnd.trelloDuPauvre.perso.model.*;
import cnd.trelloDuPauvre.perso.service.TicketService;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TicketController.class)
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TicketService ticketService;

    private TestUtil testUtil;


    @Test
    void can_call_getAllTicket() throws Exception {
        //given
        Ticket ticket1 = new Ticket(
                "name1",
                "description1",
                new Random().nextInt(100),
                LocalDate.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        Ticket ticket2 = new Ticket(
                "name2",
                "description2",
                new Random().nextInt(100),
                LocalDate.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        ArrayList<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket1);
        tickets.add(ticket2);

        // When
        when(ticketService.getAllTickets()).thenReturn(tickets);

        //Then
        mockMvc.perform(get("/api/v1/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is(ticket1.getName())))
                .andExpect(jsonPath("$[0].description", is(ticket1.getDescription())))
                .andExpect(jsonPath("$[0].creationDate", is(ticket1.getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))))
                .andExpect(jsonPath("$[1].deadLine", is(ticket2.getDeadLine().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))))
                .andExpect(jsonPath("$[1].priority", is(ticket2.getPriority())))
                .andExpect(jsonPath("$[1].modificationDate", is(ticket2.getModificationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))));


        verify(ticketService, times(1)).getAllTickets();
    }

    @Test
    void can_call_getTicketById_with_validId() throws Exception {
        //given
        Ticket ticket = new Ticket(
                "name",
                "description",
                new Random().nextInt(100),
                LocalDate.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        int id = new Random().nextInt(100);

        //when
        when(ticketService.getTicketById(any(int.class))).thenReturn(ticket);

        //then
        mockMvc.perform(get("/api/v1/tickets/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(ticket.getName())))
                .andExpect(jsonPath("$.description", is(ticket.getDescription())))
                .andExpect(jsonPath("$.creationDate", is(ticket.getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))))
                .andExpect(jsonPath("$.deadLine", is(ticket.getDeadLine().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))))
                .andExpect(jsonPath("$.priority", is(ticket.getPriority())))
                .andExpect(jsonPath("$.modificationDate", is(ticket.getModificationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))));

        ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(ticketService, times(1)).getTicketById(idCaptor.capture());

        int capturedId = idCaptor.getValue();
        assertThat(capturedId).isEqualTo(id);
    }

    @Test
    void getTicketById_with_notExistingId_shouldThrow_NotFoundException() throws Exception {
        //given
        int id = new Random().nextInt(100);
        String errorMessage = "Ticket with id " + id + " not found";

        //when
        when(ticketService.getTicketById(any(int.class))).thenThrow(new EntityNotFoundException(errorMessage));

        //then
        mockMvc.perform(get("/api/v1/tickets/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(errorMessage)));
    }

    @Test
    void can_call_createTicket() throws Exception {
        //given
        Ticket ticket = new Ticket(
                "name",
                "description",
                new Random().nextInt(100),
                LocalDate.now(),
                LocalDateTime.parse("2014-12-22T10:15:30"),
                LocalDateTime.parse("2014-12-22T10:15:30")
        );
        Project project = new Project("name");
        Story story = new Story("name");
        Status status = new Status("name", "label");
        Nature nature = new Nature("name", "label");
        int storyId = new Random().nextInt(100);
        int projectId = new Random().nextInt(100);
        int statusId = new Random().nextInt(100);
        int natureId = new Random().nextInt(100);

        ticket.setStatus(status);
        ticket.setNature(nature);
        ticket.setProject(project);
        ticket.setStory(story);

        ticket.getStatus().setStatusId(statusId);
        ticket.getNature().setNatureId(natureId);
        ticket.getProject().setProjectId(projectId);
        ticket.getStory().setStoryId(storyId);

        //when
        when(ticketService.createTicket(any(Ticket.class))).thenReturn(ticket);

        //then
        mockMvc.perform(post("/api/v1/tickets")
                        .content(testUtil.asJsonString(ticket))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(ticket.getName())))
                .andExpect(jsonPath("$.description", is(ticket.getDescription())))
                .andExpect(jsonPath("$.creationDate", is(ticket.getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))))
                .andExpect(jsonPath("$.deadLine", is(ticket.getDeadLine().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))))
                .andExpect(jsonPath("$.priority", is(ticket.getPriority())))
                .andExpect(jsonPath("$.modificationDate", is(ticket.getModificationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))))
                .andExpect(jsonPath("$.ticketId").exists());


        verify(ticketService, times(1)).createTicket(any(Ticket.class));

    }

    @Test
    void can_call_updateTicket_with_validId() throws Exception {
        //given
        Ticket ticket = new Ticket(
                "name",
                "description",
                new Random().nextInt(100),
                LocalDate.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        Project project = new Project("name");
        Story story = new Story("name");
        Status status = new Status("name", "label");
        Nature nature = new Nature("name", "label");
        int storyId = new Random().nextInt(100);
        int projectId = new Random().nextInt(100);
        int statusId = new Random().nextInt(100);
        int natureId = new Random().nextInt(100);

        ticket.setStatus(status);
        ticket.setNature(nature);
        ticket.setProject(project);
        ticket.setStory(story);

        ticket.getStatus().setStatusId(statusId);
        ticket.getNature().setNatureId(natureId);
        ticket.getProject().setProjectId(projectId);
        ticket.getStory().setStoryId(storyId);

        int id = new Random().nextInt(100);

        //when
        when(ticketService.getTicketById(any(int.class))).thenReturn(ticket);
        when(ticketService.updateTicket( any(Ticket.class),any(int.class))).thenReturn(ticket);

        //then
        mockMvc.perform(put("/api/v1/tickets/" + id)
                    .content(testUtil.asJsonString(ticket))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(ticket.getName())))
                .andExpect(jsonPath("$.description", is(ticket.getDescription())))
                .andExpect(jsonPath("$.creationDate", is(ticket.getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))))
                .andExpect(jsonPath("$.deadLine", is(ticket.getDeadLine().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))))
                .andExpect(jsonPath("$.priority", is(ticket.getPriority())))
                .andExpect(jsonPath("$.modificationDate", is(ticket.getModificationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))));



        ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(ticketService, times(1)).updateTicket(any(Ticket.class),idCaptor.capture());

        int capturedId = idCaptor.getValue();

        assertThat(capturedId).isEqualTo(id);
    }

    @Test
    void updateTicket_with_notExistingId_shouldThrow_NotFoundException() throws Exception {
        //given
        int id = new Random().nextInt(100);
        String errorMessage = "Ticket with id " + id + " not found";
        //given
        Ticket ticket = new Ticket(
                "name",
                "description",
                new Random().nextInt(100),
                LocalDate.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        Project project = new Project("name");
        Story story = new Story("name");
        Status status = new Status("name", "label");
        Nature nature = new Nature("name", "label");
        int storyId = new Random().nextInt(100);
        int projectId = new Random().nextInt(100);
        int statusId = new Random().nextInt(100);
        int natureId = new Random().nextInt(100);

        ticket.setStatus(status);
        ticket.setNature(nature);
        ticket.setProject(project);
        ticket.setStory(story);

        ticket.getStatus().setStatusId(statusId);
        ticket.getNature().setNatureId(natureId);
        ticket.getProject().setProjectId(projectId);
        ticket.getStory().setStoryId(storyId);

        //when
        when(ticketService.updateTicket(any(Ticket.class),any(int.class))).thenThrow(new EntityNotFoundException(errorMessage));

        //then
        mockMvc.perform(put("/api/v1/tickets/" + id)
                        .content(testUtil.asJsonString(ticket))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(errorMessage)));
    }

    @Test
    void can_call_deleteTicket_with_validId_should_return_true() throws Exception {
        //given
        int id = new Random().nextInt(100);

        //when
        when(ticketService.deleteTicket(any(int.class))).thenReturn(true);

        //then
        mockMvc.perform(delete("/api/v1/tickets/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(true)));

        ArgumentCaptor<Integer> idCaptorDeleteBy = ArgumentCaptor.forClass(Integer.class);
        verify(ticketService, times(1)).deleteTicket(idCaptorDeleteBy.capture());

        int capturedDeleteById = idCaptorDeleteBy.getValue();
        assertThat(capturedDeleteById).isEqualTo(id);
    }

    @Test
    void deleteTicket_with_notExistingId_should_return_false() throws Exception {
        //given
        int id = new Random().nextInt(100);

        //when
        when(ticketService.deleteTicket(any(int.class))).thenReturn(false);

        //then
        mockMvc.perform(delete("/api/v1/tickets/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(false)));

        ArgumentCaptor<Integer> idCaptorDeleteBy = ArgumentCaptor.forClass(Integer.class);
        verify(ticketService, times(1)).deleteTicket(idCaptorDeleteBy.capture());

        int capturedDeleteById = idCaptorDeleteBy.getValue();
        assertThat(capturedDeleteById).isEqualTo(id);
    }

}