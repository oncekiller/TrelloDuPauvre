package cnd.trelloDuPauvre.perso.controller;


import cnd.trelloDuPauvre.perso.Exceptions.EntityNotFoundException;
import cnd.trelloDuPauvre.perso.model.Status;
import cnd.trelloDuPauvre.perso.model.Workspace;
import cnd.trelloDuPauvre.perso.service.WorkspaceService;
import cnd.trelloDuPauvre.perso.utils.TestUtil;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

@WebMvcTest(WorkspaceController.class)
class WorkspaceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkspaceService workspaceService;

    private TestUtil testUtil;


    @Test
    void can_call_getAllWorkspace() throws Exception {
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
        when(workspaceService.getAllWorkspaces()).thenReturn(workspaces);

        //Then
        mockMvc.perform(get("/api/v1/workspaces"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is(workspace1.getName())))
                .andExpect(jsonPath("$[1].iconName", is(workspace2.getIconName())));


        verify(workspaceService, times(1)).getAllWorkspaces();
    }

    @Test
    void can_call_getWorkspaceById_with_validId() throws Exception {
        //given
        int id = new Random().nextInt(100);
        Workspace workspace = new Workspace(
                "name",
                "iconName",
                LocalDateTime.now()
        );

        //when
        when(workspaceService.getWorkspaceById(any(int.class))).thenReturn(workspace);

        //then
        mockMvc.perform(get("/api/v1/workspaces/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(workspace.getName())))
                .andExpect(jsonPath("$.iconName", is(workspace.getIconName())))
                .andExpect(jsonPath("$.creationDate", is(workspace.getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))));

        ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(workspaceService, times(1)).getWorkspaceById(idCaptor.capture());

        int capturedId = idCaptor.getValue();
        assertThat(capturedId).isEqualTo(id);
    }

    @Test
    void getWorkspaceById_with_notExistingId_shouldThrow_NotFoundException() throws Exception {
        //given
        int id = new Random().nextInt(100);
        String errorMessage = "Workspace with id " + id + " not found";

        //when
        when(workspaceService.getWorkspaceById(any(int.class))).thenThrow(new EntityNotFoundException(errorMessage));

        //then
        mockMvc.perform(get("/api/v1/workspaces/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(errorMessage)));
    }

    @Test
    void can_call_createWorkspace() throws Exception {
        //given
        Workspace workspace = new Workspace(
                "name",
                "iconName",
                LocalDateTime.now()
        );

        //when
        when(workspaceService.createWorkspace(any(Workspace.class))).thenReturn(workspace);

        //then
        mockMvc.perform(post("/api/v1/workspaces")
                        .content(testUtil.asJsonString(workspace))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(workspace.getName())))
                .andExpect(jsonPath("$.iconName", is(workspace.getIconName())))
                .andExpect(jsonPath("$.creationDate", is(workspace.getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))))
                .andExpect(jsonPath("$.workspaceId").exists());


        verify(workspaceService, times(1)).createWorkspace(any(Workspace.class));

    }

    @Test
    void can_call_updateWorkspace_with_validId() throws Exception {
        //given
        Workspace workspace = new Workspace(
                "name",
                "iconName",
                LocalDateTime.now()
        );
        int id = new Random().nextInt(100);
        //when
        when(workspaceService.getWorkspaceById(any(int.class))).thenReturn(workspace);
        when(workspaceService.updateWorkspace(any(int.class), any(Workspace.class))).thenReturn(workspace);

        //then
        mockMvc.perform(put("/api/v1/workspaces/" + id)
                    .content(testUtil.asJsonString(workspace))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(workspace.getName())))
                .andExpect(jsonPath("$.iconName", is(workspace.getIconName())))
                .andExpect(jsonPath("$.creationDate", is(workspace.getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))));


        ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(workspaceService, times(1)).updateWorkspace(idCaptor.capture(), any(Workspace.class));

        int capturedId = idCaptor.getValue();

        assertThat(capturedId).isEqualTo(id);
    }

    @Test
    void updateWorkspace_with_notExistingId_shouldThrow_NotFoundException() throws Exception {
        //given
        int id = new Random().nextInt(100);
        String errorMessage = "Workspace with id " + id + " not found";
        Workspace workspace = new Workspace(
                "name",
                "iconName",
                LocalDateTime.now()
        );
        //when
        when(workspaceService.updateWorkspace(any(int.class), any(Workspace.class))).thenThrow(new EntityNotFoundException(errorMessage));
        //then
        mockMvc.perform(put("/api/v1/workspaces/" + id)
                        .content(testUtil.asJsonString(workspace))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(errorMessage)));
    }

    @Test
    void can_call_deleteWorkspace_with_validId_should_return_true() throws Exception {
        //given
        int id = new Random().nextInt(100);

        //when
        when(workspaceService.deleteWorkspace(any(int.class))).thenReturn(true);

        //then
        mockMvc.perform(delete("/api/v1/workspaces/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(true)));

        ArgumentCaptor<Integer> idCaptorDeleteBy = ArgumentCaptor.forClass(Integer.class);
        verify(workspaceService, times(1)).deleteWorkspace(idCaptorDeleteBy.capture());

        int capturedDeleteById = idCaptorDeleteBy.getValue();
        assertThat(capturedDeleteById).isEqualTo(id);
    }

    @Test
    void deleteWorkspace_with_notExistingId_should_return_false() throws Exception {
        //given
        int id = new Random().nextInt(100);

        //when
        when(workspaceService.deleteWorkspace(any(int.class))).thenReturn(false);

        //then
        mockMvc.perform(delete("/api/v1/workspaces/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(false)));

        ArgumentCaptor<Integer> idCaptorDeleteBy = ArgumentCaptor.forClass(Integer.class);
        verify(workspaceService, times(1)).deleteWorkspace(idCaptorDeleteBy.capture());

        int capturedDeleteById = idCaptorDeleteBy.getValue();
        assertThat(capturedDeleteById).isEqualTo(id);
    }

}