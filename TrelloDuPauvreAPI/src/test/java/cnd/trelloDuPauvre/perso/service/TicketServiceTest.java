package cnd.trelloDuPauvre.perso.service;

import cnd.trelloDuPauvre.perso.Exceptions.EntityNotFoundException;
import cnd.trelloDuPauvre.perso.model.*;
import cnd.trelloDuPauvre.perso.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private StoryRepository storyRepository;

    @Mock
    private NatureRepository natureRepository;

    @Mock
    private StatusRepository statusRepository;

    TicketService underTest;

    @BeforeEach
    void setUp() {
        underTest = new TicketService(ticketRepository, projectRepository, storyRepository, natureRepository, statusRepository);
    }

    @Test
    void can_call_getAllTicket() {
        //given
        Ticket ticket1 = new Ticket(
                "name",
                "description",
                new Random().nextInt(100),
                LocalDate.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        Ticket ticket2 = new Ticket(
                "name",
                "description",
                new Random().nextInt(100),
                LocalDate.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        ArrayList<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket1);
        tickets.add(ticket2);

        // When
        when(ticketRepository.findAll()).thenReturn(tickets);
        List<Ticket> result = underTest.getAllTickets();

        //Then
        verify(ticketRepository).findAll();
        assertThat(result).isEqualTo(tickets);
    }

    @Test
    void can_call_getTicketById_with_validId() {
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
        when(ticketRepository.findById(any(int.class))).thenReturn(Optional.of(ticket));
        Ticket result = underTest.getTicketById(id);

        //then
        ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(ticketRepository).findById(idCaptor.capture());

        int capturedId = idCaptor.getValue();
        assertThat(capturedId).isEqualTo(id);
        assertThat(result).isEqualTo(ticket);
    }

    @Test
    void getTicketById_with_notExistingId_shouldThrow_NotFoundException() {
        //given
        int id = new Random().nextInt(100);

        //when
        //then
        assertThatThrownBy(() -> underTest.getTicketById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Ticket with id " + id + " not found");
    }

    @Test
    void can_call_createTicket() {
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
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
        when(projectRepository.findById(any(int.class))).thenReturn(Optional.of(project));
        when(statusRepository.findById(any(int.class))).thenReturn(Optional.of(status));
        when(natureRepository.findById(any(int.class))).thenReturn(Optional.of(nature));
        when(storyRepository.findById(any(int.class))).thenReturn(Optional.of(story));
        Ticket result = underTest.createTicket(ticket);

        //then
        ArgumentCaptor<Integer> projectIdCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> natureIdCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> statusIdCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> storyIdCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Ticket> ticketArgumentCaptor = ArgumentCaptor.forClass(Ticket.class);

        verify(projectRepository).findById(projectIdCaptor.capture());
        verify(storyRepository).findById(storyIdCaptor.capture());
        verify(natureRepository).findById(natureIdCaptor.capture());
        verify(statusRepository).findById(statusIdCaptor.capture());

        verify(ticketRepository).save(ticketArgumentCaptor.capture());

        int capturedStoryId = storyIdCaptor.getValue();
        int capturedNatureId = natureIdCaptor.getValue();
        int capturedStatusId = statusIdCaptor.getValue();
        int capturedProjectId = projectIdCaptor.getValue();

        Ticket capturedTicket = ticketArgumentCaptor.getValue();

        assertThat(capturedStoryId).isEqualTo(storyId);
        assertThat(capturedNatureId).isEqualTo(natureId);
        assertThat(capturedProjectId).isEqualTo(projectId);
        assertThat(capturedStatusId).isEqualTo(statusId);

        assertThat(capturedTicket).isEqualTo(ticket);
        assertThat(result).isEqualTo(ticket);
    }


    @Test
    void createTicket_with_notExistingProjectId_shouldThrow_NotFoundException() {
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
        //then
        assertThatThrownBy(() -> underTest.createTicket(ticket))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Project with id " + projectId + " not found");
    }

    @Test
    void createTicket_with_notExistingStoryId_shouldThrow_NotFoundException() {
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
        when(projectRepository.findById(any(int.class))).thenReturn(Optional.of(project));

        //then
        assertThatThrownBy(() -> underTest.createTicket(ticket))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Story with id " + storyId + " not found");
    }

    @Test
    void createTicket_with_notExistingNatureId_shouldThrow_NotFoundException() {
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
        when(projectRepository.findById(any(int.class))).thenReturn(Optional.of(project));
        when(storyRepository.findById(any(int.class))).thenReturn(Optional.of(story));

        //when
        //then
        assertThatThrownBy(() -> underTest.createTicket(ticket))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Nature with id " + natureId + " not found");
    }

    @Test
    void createTicket_with_notExistingStatusId_shouldThrow_NotFoundException() {
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
        when(projectRepository.findById(any(int.class))).thenReturn(Optional.of(project));
        when(storyRepository.findById(any(int.class))).thenReturn(Optional.of(story));
        when(natureRepository.findById(any(int.class))).thenReturn(Optional.of(nature));

        //when
        //then
        assertThatThrownBy(() -> underTest.createTicket(ticket))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Status with id " + statusId + " not found");
    }


    @Test
    void can_call_updateTicket_with_validId() {
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
        int ticketId = new Random().nextInt(100);

        ticket.setStatus(status);
        ticket.setNature(nature);
        ticket.setProject(project);
        ticket.setStory(story);

        ticket.getStatus().setStatusId(statusId);
        ticket.getNature().setNatureId(natureId);
        ticket.getProject().setProjectId(projectId);
        ticket.getStory().setStoryId(storyId);

        //when
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
        when(ticketRepository.findById(any(int.class))).thenReturn(Optional.of(ticket));
        when(projectRepository.findById(any(int.class))).thenReturn(Optional.of(project));
        when(statusRepository.findById(any(int.class))).thenReturn(Optional.of(status));
        when(natureRepository.findById(any(int.class))).thenReturn(Optional.of(nature));
        when(storyRepository.findById(any(int.class))).thenReturn(Optional.of(story));
        Ticket result = underTest.updateTicket(ticket, ticketId);

        //then
        ArgumentCaptor<Integer> projectIdCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> natureIdCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> statusIdCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> storyIdCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> ticketIdCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Ticket> ticketArgumentCaptor = ArgumentCaptor.forClass(Ticket.class);

        verify(projectRepository).findById(projectIdCaptor.capture());
        verify(storyRepository).findById(storyIdCaptor.capture());
        verify(natureRepository).findById(natureIdCaptor.capture());
        verify(statusRepository).findById(statusIdCaptor.capture());
        verify(ticketRepository).findById(ticketIdCaptor.capture());

        verify(ticketRepository).save(ticketArgumentCaptor.capture());

        int capturedStoryId = storyIdCaptor.getValue();
        int capturedNatureId = natureIdCaptor.getValue();
        int capturedStatusId = statusIdCaptor.getValue();
        int capturedProjectId = projectIdCaptor.getValue();
        int captureTicketId = ticketIdCaptor.getValue();

        Ticket capturedTicket = ticketArgumentCaptor.getValue();

        assertThat(capturedStoryId).isEqualTo(storyId);
        assertThat(capturedNatureId).isEqualTo(natureId);
        assertThat(capturedProjectId).isEqualTo(projectId);
        assertThat(capturedStatusId).isEqualTo(statusId);
        assertThat(captureTicketId).isEqualTo(ticketId);

        assertThat(capturedTicket).isEqualTo(ticket);
        assertThat(result).isEqualTo(ticket);
    }

    @Test
    void updateTicket_with_notExistingId_shouldThrow_NotFoundException(){
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
        //then
        assertThatThrownBy(() -> underTest.updateTicket(ticket, id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Ticket with id " + id + " not found");
    }

    @Test
    void updateTicket_with_notExistingProjectId_shouldThrow_NotFoundException() {
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
        int id = new Random().nextInt(100);

        ticket.setStatus(status);
        ticket.setNature(nature);
        ticket.setProject(project);
        ticket.setStory(story);

        ticket.getStatus().setStatusId(statusId);
        ticket.getNature().setNatureId(natureId);
        ticket.getProject().setProjectId(projectId);
        ticket.getStory().setStoryId(storyId);

        //when
        when(ticketRepository.findById(any(int.class))).thenReturn(Optional.of(ticket));

        //then
        assertThatThrownBy(() -> underTest.updateTicket(ticket, id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Project with id " + projectId + " not found");
    }

    @Test
    void updateTicket_with_notExistingStoryId_shouldThrow_NotFoundException() {
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
        int id = new Random().nextInt(100);

        ticket.setStatus(status);
        ticket.setNature(nature);
        ticket.setProject(project);
        ticket.setStory(story);

        ticket.getStatus().setStatusId(statusId);
        ticket.getNature().setNatureId(natureId);
        ticket.getProject().setProjectId(projectId);
        ticket.getStory().setStoryId(storyId);

        //when
        when(ticketRepository.findById(any(int.class))).thenReturn(Optional.of(ticket));
        when(projectRepository.findById(any(int.class))).thenReturn(Optional.of(project));

        //then
        assertThatThrownBy(() -> underTest.updateTicket(ticket, id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Story with id " + storyId + " not found");
    }

    @Test
    void updateTicket_with_notExistingNatureId_shouldThrow_NotFoundException() {
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
        int id = new Random().nextInt(100);

        ticket.setStatus(status);
        ticket.setNature(nature);
        ticket.setProject(project);
        ticket.setStory(story);

        ticket.getStatus().setStatusId(statusId);
        ticket.getNature().setNatureId(natureId);
        ticket.getProject().setProjectId(projectId);
        ticket.getStory().setStoryId(storyId);

        //when
        when(projectRepository.findById(any(int.class))).thenReturn(Optional.of(project));
        when(storyRepository.findById(any(int.class))).thenReturn(Optional.of(story));
        when(ticketRepository.findById(any(int.class))).thenReturn(Optional.of(ticket));

        //when
        //then
        assertThatThrownBy(() -> underTest.updateTicket(ticket, id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Nature with id " + natureId + " not found");
    }

    @Test
    void updateTicket_with_notExistingStatusId_shouldThrow_NotFoundException() {
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
        int id = new Random().nextInt(100);

        ticket.setStatus(status);
        ticket.setNature(nature);
        ticket.setProject(project);
        ticket.setStory(story);

        ticket.getStatus().setStatusId(statusId);
        ticket.getNature().setNatureId(natureId);
        ticket.getProject().setProjectId(projectId);
        ticket.getStory().setStoryId(storyId);

        //when
        when(projectRepository.findById(any(int.class))).thenReturn(Optional.of(project));
        when(storyRepository.findById(any(int.class))).thenReturn(Optional.of(story));
        when(natureRepository.findById(any(int.class))).thenReturn(Optional.of(nature));
        when(ticketRepository.findById(any(int.class))).thenReturn(Optional.of(ticket));

        //when
        //then
        assertThatThrownBy(() -> underTest.updateTicket(ticket, id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Status with id " + statusId + " not found");
    }

    @Test
    void can_call_deleteProject_with_validId_should_return_true() {
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
        when(ticketRepository.findById(any(int.class))).thenReturn(Optional.of(ticket));
        Boolean result = underTest.deleteTicket(id);

        //then
        ArgumentCaptor<Integer> idCaptorFindBy = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> idCaptorDeleteBy = ArgumentCaptor.forClass(Integer.class);
        verify(ticketRepository).findById(idCaptorFindBy.capture());
        verify(ticketRepository).deleteById(idCaptorDeleteBy.capture());

        int capturedFindById = idCaptorFindBy.getValue();
        int capturedDeleteById = idCaptorDeleteBy.getValue();
        assertThat(capturedFindById).isEqualTo(id);
        assertThat(capturedDeleteById).isEqualTo(id);
        assertThat(result).isEqualTo(true);
    }

    @Test
    void deleteProject_with_notExistingId_should_return_false() {
        //given
        int id = new Random().nextInt(100);
        //when
        Boolean result = underTest.deleteTicket(id);
        //then
        assertThat(result).isEqualTo(false);
    }
}
