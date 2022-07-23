package cnd.trelloDuPauvre.perso.controller;


import cnd.trelloDuPauvre.perso.Exceptions.EntityNotFoundException;
import cnd.trelloDuPauvre.perso.model.Project;
import cnd.trelloDuPauvre.perso.service.ProjectService;
import cnd.trelloDuPauvre.perso.utils.TestUtil;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    private TestUtil testUtil;


    @Test
    void can_call_getAllProject() throws Exception {
        //given
        Project project1 = new Project(
                "name",
                true,
                LocalDateTime.now(),
                LocalDateTime.now(),
                "#fff"
        );
        Project project2 = new Project(
                "name",
                true,
                LocalDateTime.now(),
                LocalDateTime.now(),
                "#fff"
        );
        ArrayList<Project> projects = new ArrayList<>();
        projects.add(project1);
        projects.add(project2);

        // When
        when(projectService.getAllProjects()).thenReturn(projects);

        //Then
        mockMvc.perform(get("/api/v1/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is(project1.getName())));


        verify(projectService, times(1)).getAllProjects();
    }

    @Test
    void can_call_getProjectById_with_validId() throws Exception {
        //given
        int id = new Random().nextInt(100);
        Project project = new Project(
                "name",
                true,
                LocalDateTime.now(),
                LocalDateTime.now(),
                "#fff"
        );

        //when
        when(projectService.getProjectById(any(int.class))).thenReturn(project);

        //then
        mockMvc.perform(get("/api/v1/projects/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(project.getName())));

        ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(projectService, times(1)).getProjectById(idCaptor.capture());

        int capturedId = idCaptor.getValue();
        assertThat(capturedId).isEqualTo(id);
    }

    @Test
    void getProjectById_with_notExistingId_shouldThrow_NotFoundException() throws Exception {
        //given
        int id = new Random().nextInt(100);
        String errorMessage = "Project with id " + id + " not found";

        //when
        when(projectService.getProjectById(any(int.class))).thenThrow(new EntityNotFoundException(errorMessage));

        //then
        mockMvc.perform(get("/api/v1/projects/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(errorMessage)));
    }

    @Test
    void can_call_createProject() throws Exception {
        //given
        Project project = new Project(
                "name",
                true,
                LocalDateTime.now(),
                LocalDateTime.now(),
                "#fff"
        );

        //when
        when(projectService.createProject(any(Project.class))).thenReturn(project);

        //then
        mockMvc.perform(post("/api/v1/projects")
                        .content(testUtil.asJsonString(project))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(project.getName())))
                .andExpect(jsonPath("$.projectId").exists());


        verify(projectService, times(1)).createProject(any(Project.class));
    }

    @Test
    void can_call_updateProject_with_validId() throws Exception {
        //given
        Project project = new Project(
                "name",
                true,
                LocalDateTime.now(),
                LocalDateTime.now(),
                "#fff"
        );
        int id = new Random().nextInt(100);

        //when
        when(projectService.getProjectById(any(int.class))).thenReturn(project);
        when(projectService.updateProject(any(int.class), any(Project.class))).thenReturn(project);

        //then
        mockMvc.perform(put("/api/v1/projects/" + id)
                    .content(testUtil.asJsonString(project))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(project.getName())));


        ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(projectService, times(1)).updateProject(idCaptor.capture(), any(Project.class));

        int capturedId = idCaptor.getValue();

        assertThat(capturedId).isEqualTo(id);
    }

    @Test
    void updateProject_with_notExistingId_shouldThrow_NotFoundException() throws Exception {
        //given
        int id = new Random().nextInt(100);
        String errorMessage = "Project with id " + id + " not found";
        Project project = new Project(
                "name",
                true,
                LocalDateTime.now(),
                LocalDateTime.now(),
                "#fff"
        );

        //when
        when(projectService.updateProject(any(int.class), any(Project.class))).thenThrow(new EntityNotFoundException(errorMessage));

        //then
        mockMvc.perform(put("/api/v1/projects/" + id)
                        .content(testUtil.asJsonString(project))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(errorMessage)));
    }

    @Test
    void can_call_updateProjectFavorite_with_validId() throws Exception {
        //given
        Boolean isFavorite = true;
        Project project = new Project(
                "name",
                isFavorite,
                LocalDateTime.now(),
                LocalDateTime.now(),
                "#fff"
        );
        int id = new Random().nextInt(100);

        //when
        when(projectService.getProjectById(any(int.class))).thenReturn(project);
        when(projectService.updateProjectFavorite(any(int.class), any(Boolean.class))).thenReturn(project);

        //then
        mockMvc.perform(put("/api/v1/projects/" + id + "/favorite?isFavorite=" + isFavorite)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isFavorite", is(project.getIsFavorite())));


        ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(projectService, times(1)).updateProjectFavorite(idCaptor.capture(), any(Boolean.class));

        int capturedId = idCaptor.getValue();

        assertThat(capturedId).isEqualTo(id);
    }

    @Test
    void updateProjectFavorite_with_notExistingId_shouldThrow_NotFoundException() throws Exception {
        //given
        Boolean isFavorite = true;
        int id = new Random().nextInt(100);
        String errorMessage = "Project with id " + id + " not found";


        //when
        when(projectService.updateProjectFavorite(any(int.class), any(Boolean.class))).thenThrow(new EntityNotFoundException(errorMessage));

        //then
        mockMvc.perform(put("/api/v1/projects/" + id + "/favorite?isFavorite=" + isFavorite)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(errorMessage)));
    }

    @Test
    void can_call_deleteProject_with_validId_should_return_true() throws Exception {
        //given
        int id = new Random().nextInt(100);

        //when
        when(projectService.deleteProject(any(int.class))).thenReturn(true);

        //then
        mockMvc.perform(delete("/api/v1/projects/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(true)));

        ArgumentCaptor<Integer> idCaptorDeleteBy = ArgumentCaptor.forClass(Integer.class);
        verify(projectService, times(1)).deleteProject(idCaptorDeleteBy.capture());

        int capturedDeleteById = idCaptorDeleteBy.getValue();
        assertThat(capturedDeleteById).isEqualTo(id);
    }

    @Test
    void deleteProject_with_notExistingId_should_return_false() throws Exception {
        //given
        int id = new Random().nextInt(100);

        //when
        when(projectService.deleteProject(any(int.class))).thenReturn(false);

        //then
        mockMvc.perform(delete("/api/v1/projects/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(false)));

        ArgumentCaptor<Integer> idCaptorDeleteBy = ArgumentCaptor.forClass(Integer.class);
        verify(projectService, times(1)).deleteProject(idCaptorDeleteBy.capture());

        int capturedDeleteById = idCaptorDeleteBy.getValue();
        assertThat(capturedDeleteById).isEqualTo(id);
    }

}