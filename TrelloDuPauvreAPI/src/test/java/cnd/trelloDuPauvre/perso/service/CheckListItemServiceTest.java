package cnd.trelloDuPauvre.perso.service;

import cnd.trelloDuPauvre.perso.Exceptions.EntityNotFoundException;
import cnd.trelloDuPauvre.perso.model.CheckListItem;
import cnd.trelloDuPauvre.perso.model.Ticket;
import cnd.trelloDuPauvre.perso.repository.CheckListItemRepository;
import cnd.trelloDuPauvre.perso.repository.TicketRepository;
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
public class CheckListItemServiceTest {
    @Mock
    CheckListItemRepository checkListItemRepository;

    @Mock
    TicketRepository ticketRepository;
    CheckListItemService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CheckListItemService(checkListItemRepository, ticketRepository);
    }

    @Test
    void can_call_getAllCheckListItemService() {
        //given
        CheckListItem checkListItem1 = new CheckListItem("label", true);
        CheckListItem checkListItem2 = new CheckListItem("label", true);
        ArrayList<CheckListItem> checkListItems = new ArrayList<>();
        checkListItems.add(checkListItem1);
        checkListItems.add(checkListItem2);
        // When
        when(checkListItemRepository.findAll()).thenReturn(checkListItems);
        List<CheckListItem> result = underTest.getAllCheckListItems();
        //Then
        verify(checkListItemRepository).findAll();
        assertThat(result).isEqualTo(checkListItems);
    }

    @Test
    void can_call_getCheckListItemById_with_validId() {
        //given
        int id = new Random().nextInt(100);
        CheckListItem checkListItem = new CheckListItem("label", true);
        //when
        when(checkListItemRepository.findById(any(int.class))).thenReturn(Optional.of(checkListItem));
        CheckListItem result = underTest.getCheckListItemById(id);
        //then
        ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(checkListItemRepository).findById(idCaptor.capture());

        int capturedId = idCaptor.getValue();
        assertThat(capturedId).isEqualTo(id);
        assertThat(result).isEqualTo(checkListItem);
    }

    @Test
    void getCheckListItemById_with_notExistingId_shouldThrow_NotFoundException() {
        //given
        int id = new Random().nextInt(100);
        //when
        //then
        assertThatThrownBy(() -> underTest.getCheckListItemById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("CheckListItem with id " + id + " not found");
    }

    @Test
    void can_call_createCheckListItem() {
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

        int ticketId = new Random().nextInt(100);
        checkListItem.getTicket().setTicketId(ticketId);


        //when
        when(ticketRepository.findById(any(int.class))).thenReturn(Optional.of(ticket));
        when(checkListItemRepository.save(any(CheckListItem.class))).thenReturn(checkListItem);
        CheckListItem result = underTest.createCheckListItem(checkListItem);
        //then
        ArgumentCaptor<CheckListItem> checkListItemCaptor = ArgumentCaptor.forClass(CheckListItem.class);
        ArgumentCaptor<Integer> ticketIdCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(checkListItemRepository).save(checkListItemCaptor.capture());
        verify(ticketRepository).findById(ticketIdCaptor.capture());

        CheckListItem capturedCheckListItem = checkListItemCaptor.getValue();
        int capturedTicketId = ticketIdCaptor.getValue();
        assertThat(capturedCheckListItem).isEqualTo(checkListItem);
        assertThat(capturedTicketId).isEqualTo(ticketId);
        assertThat(result).isEqualTo(checkListItem);
    }

    @Test
    void createCheckListItem_with_notExistingTicketId_shouldThrow_NotFoundException() {
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

        int ticketId = new Random().nextInt(100);
        checkListItem.getTicket().setTicketId(ticketId);


        //when
        //then
        assertThatThrownBy(() -> underTest.createCheckListItem(checkListItem))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Ticket with id " + ticketId + " not found");
    }

    @Test
    void can_call_updateCheckListItem_with_validId() {
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

        int ticketId = new Random().nextInt(100);
        int checkListItemId = new Random().nextInt(100);
        checkListItem.getTicket().setTicketId(ticketId);
        //when
        when(ticketRepository.findById(any(int.class))).thenReturn(Optional.of(ticket));
        when(checkListItemRepository.findById(any(int.class))).thenReturn(Optional.of(checkListItem));
        when(checkListItemRepository.save(any(CheckListItem.class))).thenReturn(checkListItem);
        CheckListItem result = underTest.updateCheckListItem(checkListItemId, checkListItem);
        //then
        ArgumentCaptor<Integer> ticketIdCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> checkListItemIdCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<CheckListItem> checkListItemCaptor = ArgumentCaptor.forClass(CheckListItem.class);
        verify(ticketRepository).findById(ticketIdCaptor.capture());
        verify(checkListItemRepository).findById(checkListItemIdCaptor.capture());
        verify(checkListItemRepository).save(checkListItemCaptor.capture());

        int capturedTicketId = ticketIdCaptor.getValue();
        int capturedCheckListItemId = checkListItemIdCaptor.getValue();
        CheckListItem capturedCheckListItem = checkListItemCaptor.getValue();

        assertThat(capturedTicketId).isEqualTo(ticketId);
        assertThat(capturedCheckListItemId).isEqualTo(checkListItemId);
        assertThat(capturedCheckListItem).isEqualTo(checkListItem);
        assertThat(result).isEqualTo(checkListItem);
    }

    @Test
    void updateCheckListItem_with_notExistingId_shouldThrow_NotFoundException(){
        //given
        int id = new Random().nextInt(100);
        CheckListItem checkListItem = new CheckListItem("label", true);
        //when
        //then
        assertThatThrownBy(() -> underTest.updateCheckListItem(id, checkListItem))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("CheckListItem with id " + id + " not found");
    }

    @Test
    void updateCheckListItem_with_notExistingTicketId_shouldThrow_NotFoundException(){
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

        int ticketId = new Random().nextInt(100);
        int checkListItemId = new Random().nextInt(100);
        checkListItem.getTicket().setTicketId(ticketId);
        //when
        when(checkListItemRepository.findById(any(int.class))).thenReturn(Optional.of(checkListItem));
        //then
        assertThatThrownBy(() -> underTest.updateCheckListItem(checkListItemId, checkListItem))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Ticket with id " + ticketId + " not found");
    }

    @Test
    void can_call_deleteCheckListItem_with_validId_should_return_true() {
        //given
        int id = new Random().nextInt(100);
        CheckListItem checkListItem = new CheckListItem("label", true);
        //when
        when(checkListItemRepository.findById(any(int.class))).thenReturn(Optional.of(checkListItem));
        Boolean result = underTest.deleteCheckListItem(id);
        //then
        ArgumentCaptor<Integer> idCaptorFindBy = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> idCaptorDeleteBy = ArgumentCaptor.forClass(Integer.class);
        verify(checkListItemRepository).findById(idCaptorFindBy.capture());
        verify(checkListItemRepository).deleteById(idCaptorDeleteBy.capture());

        int capturedFindById = idCaptorFindBy.getValue();
        int capturedDeleteById = idCaptorDeleteBy.getValue();
        assertThat(capturedFindById).isEqualTo(id);
        assertThat(capturedDeleteById).isEqualTo(id);
        assertThat(result).isEqualTo(true);
    }

    @Test
    void deleteCheckListItem_with_notExistingId_should_return_false() {
        //given
        int id = new Random().nextInt(100);
        //when
        Boolean result = underTest.deleteCheckListItem(id);
        //then
        assertThat(result).isEqualTo(false);
    }
}
