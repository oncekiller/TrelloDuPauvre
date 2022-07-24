package cnd.trelloDuPauvre.perso.service;

import cnd.trelloDuPauvre.perso.Exceptions.EntityNotFoundException;
import cnd.trelloDuPauvre.perso.model.Workspace;
import cnd.trelloDuPauvre.perso.repository.WorkspaceRepository;
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
class WorkspaceServiceTest {

    @Mock
    WorkspaceRepository workspaceRepository;
    WorkspaceService underTest;

    @BeforeEach
    void setUp() {
        underTest = new WorkspaceService(workspaceRepository);
    }

    @Test
    void can_call_getAllWorkspaces() {
        //given
        Workspace workspace1 = new Workspace(
                "name1",
                "iconName1",
                LocalDateTime.now()
        );
        Workspace workspace2 = new Workspace(
                "name2",
                "iconName2",
                LocalDateTime.now()
        );
        ArrayList<Workspace> workspaces = new ArrayList<>();
        workspaces.add(workspace1);
        workspaces.add(workspace2);
        // When
        when(workspaceRepository.findAll()).thenReturn(workspaces);
        List<Workspace> result = underTest.getAllWorkspaces();
        //Then
        verify(workspaceRepository).findAll();
        assertThat(result).isEqualTo(workspaces);
    }

    @Test
    void can_call_getWorkspaceById_with_validId() {
        //given
        int id = new Random().nextInt(100);
        Workspace workspace = new Workspace(
                "name",
                "iconName",
                LocalDateTime.now()
        );
        //when
        when(workspaceRepository.findById(any(int.class))).thenReturn(Optional.of(workspace));
        Workspace result = underTest.getWorkspaceById(id);
        //then
        ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(workspaceRepository).findById(idCaptor.capture());

        int capturedId = idCaptor.getValue();
        assertThat(capturedId).isEqualTo(id);
        assertThat(result).isEqualTo(workspace);
    }

    @Test
    void getWorkspaceById_with_notExistingId_shouldThrow_NotFoundException() {
        //given
        int id = new Random().nextInt(100);
        //when
        //then
        assertThatThrownBy(() -> underTest.getWorkspaceById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Workspace with id " + id + " not found");
    }

    @Test
    void can_call_createWorkspace() {
        //given
        Workspace workspace = new Workspace(
                "name",
                "iconName",
                LocalDateTime.now()
        );
        //when
        when(workspaceRepository.save(any(Workspace.class))).thenReturn(workspace);
        Workspace result = underTest.createWorkspace(workspace);
        //then
        ArgumentCaptor<Workspace> workspaceCaptor = ArgumentCaptor.forClass(Workspace.class);
        verify(workspaceRepository).save(workspaceCaptor.capture());

        Workspace capturedWorkspace = workspaceCaptor.getValue();
        assertThat(capturedWorkspace).isEqualTo(workspace);
        assertThat(result).isEqualTo(workspace);
    }

    @Test
    void can_call_updateWorkspace_with_validId() {
        //given
        Workspace workspace = new Workspace(
                "name",
                "iconName",
                LocalDateTime.now()
        );
        int id = new Random().nextInt(100);
        //when
        when(workspaceRepository.findById(any(int.class))).thenReturn(Optional.of(workspace));
        when(workspaceRepository.save(any(Workspace.class))).thenReturn(workspace);
        Workspace result = underTest.updateWorkspace(id, workspace);
        //then
        ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Workspace> workspaceCaptor = ArgumentCaptor.forClass(Workspace.class);
        verify(workspaceRepository).findById(idCaptor.capture());
        verify(workspaceRepository).save(workspaceCaptor.capture());

        int capturedId = idCaptor.getValue();
        Workspace capturedWorkspace = workspaceCaptor.getValue();
        assertThat(capturedId).isEqualTo(id);
        assertThat(capturedWorkspace).isEqualTo(workspace);
        assertThat(result).isEqualTo(workspace);
    }

    @Test
    void updateWorkspace_with_notExistingId_shouldThrow_NotFoundException(){
        //given
        int id = new Random().nextInt(100);
        Workspace workspace = new Workspace(
                "name",
                "iconName",
                LocalDateTime.now()
        );
        //when
        //then
        assertThatThrownBy(() -> underTest.updateWorkspace(id, workspace))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Workspace with id " + id + " not found");
    }

    @Test
    void can_call_deleteWorkspace_with_validId_should_return_true() {
        //given
        int id = new Random().nextInt(100);
        Workspace workspace = new Workspace(
                "name",
                "iconName",
                LocalDateTime.now()
        );
        //when
        when(workspaceRepository.findById(any(int.class))).thenReturn(Optional.of(workspace));
        Boolean result = underTest.deleteWorkspace(id);
        //then
        ArgumentCaptor<Integer> idCaptorFindBy = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> idCaptorDeleteBy = ArgumentCaptor.forClass(Integer.class);
        verify(workspaceRepository).findById(idCaptorFindBy.capture());
        verify(workspaceRepository).deleteById(idCaptorDeleteBy.capture());

        int capturedFindById = idCaptorFindBy.getValue();
        int capturedDeleteById = idCaptorDeleteBy.getValue();
        assertThat(capturedFindById).isEqualTo(id);
        assertThat(capturedDeleteById).isEqualTo(id);
        assertThat(result).isEqualTo(true);
    }

    @Test
    void deleteWorkspace_with_notExistingId_should_return_false() {
        //given
        int id = new Random().nextInt(100);
        //when
        Boolean result = underTest.deleteWorkspace(id);
        //then
        assertThat(result).isEqualTo(false);
    }
}