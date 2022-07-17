package cnd.trelloDuPauvre.perso.service;

import cnd.trelloDuPauvre.perso.Exceptions.EntityNotFoundException;
import cnd.trelloDuPauvre.perso.model.Project;
import cnd.trelloDuPauvre.perso.repository.ProjectRepository;
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
public class ProjectServiceTest {
    @Mock
    ProjectRepository projectRepository;
    ProjectService underTest;

    @BeforeEach
    void setUp() {
        underTest = new ProjectService(projectRepository);
    }

    @Test
    void can_call_getAllProject() {
        //given
        Project project1 = new Project("name");
        Project project2 = new Project("name");
        ArrayList<Project> projects = new ArrayList<>();
        projects.add(project1);
        projects.add(project2);
        // When
        when(projectRepository.findAll()).thenReturn(projects);
        List<Project> result = underTest.getAllProjects();
        //Then
        verify(projectRepository).findAll();
        assertThat(result).isEqualTo(projects);
    }

    @Test
    void can_call_getProjectById_with_validId() {
        //given
        Project project = new Project("name");
        int id = new Random().nextInt(100);
        //when
        when(projectRepository.findById(any(int.class))).thenReturn(Optional.of(project));
        Project result = underTest.getProjectById(id);
        //then
        ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(projectRepository).findById(idCaptor.capture());

        int capturedId = idCaptor.getValue();
        assertThat(capturedId).isEqualTo(id);
        assertThat(result).isEqualTo(project);
    }

    @Test
    void getProjectById_with_notExistingId_shouldThrow_NotFoundException() {
        //given
        int id = new Random().nextInt(100);
        //when
        //then
        assertThatThrownBy(() -> underTest.getProjectById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Project with id " + id + " not found");
    }

    @Test
    void can_call_createProject() {
        //given
        Project project = new Project("name");
        //when
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        Project result = underTest.createProject(project);
        //then
        ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(projectCaptor.capture());

        Project capturedProject = projectCaptor.getValue();
        assertThat(capturedProject).isEqualTo(project);
        assertThat(result).isEqualTo(project);
    }

    @Test
    void can_call_updateProject_with_validId() {
        //given
        Project project = new Project("name");
        int id = new Random().nextInt(100);
        //when
        when(projectRepository.findById(any(int.class))).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        Project result = underTest.updateProject(id, project);
        //then
        ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).findById(idCaptor.capture());
        verify(projectRepository).save(projectCaptor.capture());

        int capturedId = idCaptor.getValue();
        Project capturedProject = projectCaptor.getValue();
        assertThat(capturedId).isEqualTo(id);
        assertThat(capturedProject).isEqualTo(project);
        assertThat(result).isEqualTo(project);
    }

    @Test
    void updateProject_with_notExistingId_shouldThrow_NotFoundException(){
        //given
        int id = new Random().nextInt(100);
        Project project = new Project("name");
        //when
        //then
        assertThatThrownBy(() -> underTest.updateProject(id, project))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Project with id " + id + " not found");
    }

    @Test
    void can_call_deleteProject_with_validId_should_return_true() {
        //given
        int id = new Random().nextInt(100);
        Project project = new Project("name");
        //when
        when(projectRepository.findById(any(int.class))).thenReturn(Optional.of(project));
        Boolean result = underTest.deleteProject(id);
        //then
        ArgumentCaptor<Integer> idCaptorFindBy = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> idCaptorDeleteBy = ArgumentCaptor.forClass(Integer.class);
        verify(projectRepository).findById(idCaptorFindBy.capture());
        verify(projectRepository).deleteById(idCaptorDeleteBy.capture());

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
        Boolean result = underTest.deleteProject(id);
        //then
        assertThat(result).isEqualTo(false);
    }
}
