package cnd.trelloDuPauvre.perso.service;

import cnd.trelloDuPauvre.perso.Exceptions.EntityNotFoundException;
import cnd.trelloDuPauvre.perso.model.Status;
import cnd.trelloDuPauvre.perso.repository.StatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class StatusServiceTest {

    @Mock
    StatusRepository statusRepository;
    StatusService underTest;

    @BeforeEach
    void setUp() {
        underTest = new StatusService(statusRepository);
    }

    @Test
    void can_call_getAllStatus() {
        //given
        Status status1 = new Status("name", "label");
        Status status2 = new Status("name", "label");
        ArrayList<Status> statuss = new ArrayList<>();
        statuss.add(status1);
        statuss.add(status2);
        // When
        when(statusRepository.findAll()).thenReturn(statuss);
        List<Status> result = underTest.getAllStatus();
        //Then
        verify(statusRepository).findAll();
        assertThat(result).isEqualTo(statuss);
    }

    @Test
    void can_call_getStatusById_with_validId() {
        //given
        int id = new Random().nextInt(100);
        Status status = new Status("status", "label");
        //when
        when(statusRepository.findById(any(int.class))).thenReturn(Optional.of(status));
        Status result = underTest.getStatusById(id);
        //then
        ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(statusRepository).findById(idCaptor.capture());

        int capturedId = idCaptor.getValue();
        assertThat(capturedId).isEqualTo(id);
        assertThat(result).isEqualTo(status);
    }

    @Test
    void getStatusById_with_notExistingId_shouldThrow_NotFoundException() {
        //given
        int id = new Random().nextInt(100);
        //when
        //then
        assertThatThrownBy(() -> underTest.getStatusById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Status with id " + id + " not found");
    }

    @Test
    void can_call_createStatus() {
        //given
        Status status = new Status("name", "label");
        //when
        when(statusRepository.save(any(Status.class))).thenReturn(status);
        Status result = underTest.createStatus(status);
        //then
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        verify(statusRepository).save(statusCaptor.capture());

        Status capturedStatus = statusCaptor.getValue();
        assertThat(capturedStatus).isEqualTo(status);
        assertThat(result).isEqualTo(status);
    }

    @Test
    void can_call_updateStatus_with_validId() {
        //given
        Status status = new Status("name", "label");
        int id = new Random().nextInt(100);
        //when
        when(statusRepository.findById(any(int.class))).thenReturn(Optional.of(status));
        when(statusRepository.save(any(Status.class))).thenReturn(status);
        Status result = underTest.updateStatus(id, status);
        //then
        ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        verify(statusRepository).findById(idCaptor.capture());
        verify(statusRepository).save(statusCaptor.capture());

        int capturedId = idCaptor.getValue();
        Status capturedStatus = statusCaptor.getValue();
        assertThat(capturedId).isEqualTo(id);
        assertThat(capturedStatus).isEqualTo(status);
        assertThat(result).isEqualTo(status);
    }

    @Test
    void updateStatus_with_notExistingId_shouldThrow_NotFoundException(){
        //given
        int id = new Random().nextInt(100);
        Status status = new Status("name", "label");
        //when
        //then
        assertThatThrownBy(() -> underTest.updateStatus(id, status))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Status with id " + id + " not found");
    }

    @Test
    void can_call_deleteStatus_with_validId_should_return_true() {
        //given
        int id = new Random().nextInt(100);
        Status status = new Status("status", "label");
        //when
        when(statusRepository.findById(any(int.class))).thenReturn(Optional.of(status));
        Boolean result = underTest.deleteStatus(id);
        //then
        ArgumentCaptor<Integer> idCaptorFindBy = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> idCaptorDeleteBy = ArgumentCaptor.forClass(Integer.class);
        verify(statusRepository).findById(idCaptorFindBy.capture());
        verify(statusRepository).deleteById(idCaptorDeleteBy.capture());

        int capturedFindById = idCaptorFindBy.getValue();
        int capturedDeleteById = idCaptorDeleteBy.getValue();
        assertThat(capturedFindById).isEqualTo(id);
        assertThat(capturedDeleteById).isEqualTo(id);
        assertThat(result).isEqualTo(true);
    }

    @Test
    void deleteStatus_with_notExistingId_should_return_false() {
        //given
        int id = new Random().nextInt(100);
        //when
        Boolean result = underTest.deleteStatus(id);
        //then
        assertThat(result).isEqualTo(false);
    }
}