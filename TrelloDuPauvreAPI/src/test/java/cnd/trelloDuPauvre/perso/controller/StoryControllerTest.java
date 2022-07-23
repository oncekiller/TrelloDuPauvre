package cnd.trelloDuPauvre.perso.controller;


import cnd.trelloDuPauvre.perso.Exceptions.EntityNotFoundException;
import cnd.trelloDuPauvre.perso.model.Project;
import cnd.trelloDuPauvre.perso.model.Story;
import cnd.trelloDuPauvre.perso.service.StoryService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StoryController.class)
class StoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StoryService storyService;

    private TestUtil testUtil;


    @Test
    void can_call_getAllStory() throws Exception {
        //given
        Story story1 = new Story("name1", LocalDateTime.now());
        Story story2 = new Story("name2", LocalDateTime.now());
        ArrayList<Story> stories = new ArrayList<>();
        stories.add(story1);
        stories.add(story2);

        // When
        when(storyService.getAllStories()).thenReturn(stories);

        //Then
        mockMvc.perform(get("/api/v1/stories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is(story1.getName())));


        verify(storyService, times(1)).getAllStories();
    }

    @Test
    void can_call_getStoryById_with_validId() throws Exception {
        //given
        int id = new Random().nextInt(100);
        Story story = new Story("name", LocalDateTime.now());

        //when
        when(storyService.getStoryById(any(int.class))).thenReturn(story);

        //then
        mockMvc.perform(get("/api/v1/stories/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(story.getName())));

        ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(storyService, times(1)).getStoryById(idCaptor.capture());

        int capturedId = idCaptor.getValue();
        assertThat(capturedId).isEqualTo(id);
    }

    @Test
    void getStoryById_with_notExistingId_shouldThrow_NotFoundException() throws Exception {
        //given
        int id = new Random().nextInt(100);
        String errorMessage = "Story with id " + id + " not found";

        //when
        when(storyService.getStoryById(any(int.class))).thenThrow(new EntityNotFoundException(errorMessage));

        //then
        mockMvc.perform(get("/api/v1/stories/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(errorMessage)));
    }

    @Test
    void can_call_createStory() throws Exception {
        //given
        Story story = new Story("name", LocalDateTime.now());
        Project project = new Project(
                "name",
                true,
                LocalDateTime.now(),
                LocalDateTime.now(),
                "#fff"
        );
        story.setProject(project);

        //when
        when(storyService.createStory(any(Story.class))).thenReturn(story);

        //then
        mockMvc.perform(post("/api/v1/stories")
                        .content(testUtil.asJsonString(story))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(story.getName())))
                .andExpect(jsonPath("$.storyId").exists());


        verify(storyService, times(1)).createStory(any(Story.class));

    }

    @Test
    void can_call_updateStory_with_validId() throws Exception {
        //given
        Story story = new Story("name", LocalDateTime.now());
        Project project = new Project(
                "name",
                true,
                LocalDateTime.now(),
                LocalDateTime.now(),
                "#fff"
        );
        story.setProject(project);
        int id = new Random().nextInt(100);
        //when
        when(storyService.getStoryById(any(int.class))).thenReturn(story);
        when(storyService.updateStory(any(int.class), any(Story.class))).thenReturn(story);

        //then
        mockMvc.perform(put("/api/v1/stories/" + id)
                    .content(testUtil.asJsonString(story))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(story.getName())));


        ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(storyService, times(1)).updateStory(idCaptor.capture(), any(Story.class));

        int capturedId = idCaptor.getValue();

        assertThat(capturedId).isEqualTo(id);
    }

    @Test
    void updateStory_with_notExistingId_shouldThrow_NotFoundException() throws Exception {
        //given
        int id = new Random().nextInt(100);
        String errorMessage = "Story with id " + id + " not found";
        Story story = new Story("name", LocalDateTime.now());
        Project project = new Project(
                "name",
                true,
                LocalDateTime.now(),
                LocalDateTime.now(),
                "#fff"
        );
        story.setProject(project);
        //when
        when(storyService.updateStory(any(int.class), any(Story.class))).thenThrow(new EntityNotFoundException(errorMessage));
        //then
        mockMvc.perform(put("/api/v1/stories/" + id)
                        .content(testUtil.asJsonString(story))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(errorMessage)));
    }

    @Test
    void can_call_deleteStory_with_validId_should_return_true() throws Exception {
        //given
        int id = new Random().nextInt(100);

        //when
        when(storyService.deleteStory(any(int.class))).thenReturn(true);

        //then
        mockMvc.perform(delete("/api/v1/stories/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(true)));

        ArgumentCaptor<Integer> idCaptorDeleteBy = ArgumentCaptor.forClass(Integer.class);
        verify(storyService, times(1)).deleteStory(idCaptorDeleteBy.capture());

        int capturedDeleteById = idCaptorDeleteBy.getValue();
        assertThat(capturedDeleteById).isEqualTo(id);
    }

    @Test
    void deleteStory_with_notExistingId_should_return_false() throws Exception {
        //given
        int id = new Random().nextInt(100);

        //when
        when(storyService.deleteStory(any(int.class))).thenReturn(false);

        //then
        mockMvc.perform(delete("/api/v1/stories/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(false)));

        ArgumentCaptor<Integer> idCaptorDeleteBy = ArgumentCaptor.forClass(Integer.class);
        verify(storyService, times(1)).deleteStory(idCaptorDeleteBy.capture());

        int capturedDeleteById = idCaptorDeleteBy.getValue();
        assertThat(capturedDeleteById).isEqualTo(id);
    }

}