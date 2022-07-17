package cnd.trelloDuPauvre.perso.service;

import cnd.trelloDuPauvre.perso.Exceptions.EntityNotFoundException;
import cnd.trelloDuPauvre.perso.model.Project;
import cnd.trelloDuPauvre.perso.model.Story;
import cnd.trelloDuPauvre.perso.repository.ProjectRepository;
import cnd.trelloDuPauvre.perso.repository.StoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StoryServiceTest {
    @Mock
    StoryRepository storyRepository;

    @Mock
    ProjectRepository projectRepository;
    StoryService underTest;

    @BeforeEach
    void setUp() {
        underTest = new StoryService(storyRepository, projectRepository);
    }

    @Test
    void can_call_getAllStory() {
        //given
        Story story1 = new Story("name");
        Story story2 = new Story("name");
        ArrayList<Story> stories = new ArrayList<>();
        stories.add(story1);
        stories.add(story2);
        // When
        when(storyRepository.findAll()).thenReturn(stories);
        List<Story> result = underTest.getAllStories();
        //Then
        verify(storyRepository).findAll();
        assertThat(result).isEqualTo(stories);
    }

    @Test
    void can_call_getStoryById_with_validId() {
        //given
        int id = new Random().nextInt(100);
        Story story = new Story("name");
        //when
        when(storyRepository.findById(any(int.class))).thenReturn(Optional.of(story));
        Story result = underTest.getStoryById(id);
        //then
        ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(storyRepository).findById(idCaptor.capture());

        int capturedId = idCaptor.getValue();
        assertThat(capturedId).isEqualTo(id);
        assertThat(result).isEqualTo(story);
    }

    @Test
    void getStoryById_with_notExistingId_shouldThrow_NotFoundException() {
        //given
        int id = new Random().nextInt(100);
        //when
        //then
        assertThatThrownBy(() -> underTest.getStoryById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Story with id " + id + " not found");
    }

    @Test
    void can_call_createStory() {
        //given
        Story story = new Story("name");
        Project project = new Project(
                "name"
        );
        story.setProject(project);

        int projectId = new Random().nextInt(100);
        story.getProject().setProjectId(projectId);


        //when
        when(projectRepository.findById(any(int.class))).thenReturn(Optional.of(project));
        when(storyRepository.save(any(Story.class))).thenReturn(story);
        Story result = underTest.createStory(story);
        //then
        ArgumentCaptor<Story> storyCaptor = ArgumentCaptor.forClass(Story.class);
        ArgumentCaptor<Integer> projectIdCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(storyRepository).save(storyCaptor.capture());
        verify(projectRepository).findById(projectIdCaptor.capture());

        Story capturedStory = storyCaptor.getValue();
        int capturedProjectId = projectIdCaptor.getValue();
        assertThat(capturedStory).isEqualTo(story);
        assertThat(capturedProjectId).isEqualTo(projectId);
        assertThat(result).isEqualTo(story);
    }

    @Test
    void createStory_with_notExistingProjectId_shouldThrow_NotFoundException() {
        //given
        Story story = new Story("name");
        Project project = new Project(
                "name"
        );
        story.setProject(project);

        int projectId = new Random().nextInt(100);
        story.getProject().setProjectId(projectId);

        //when
        //then
        assertThatThrownBy(() -> underTest.createStory(story))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Project with id " + projectId + " not found");
    }

    @Test
    void can_call_updateStory_with_validId() {
        //given
        Story story = new Story("name");
        Project project = new Project(
                "name"
        );
        story.setProject(project);

        int storyId = new Random().nextInt(100);
        int projectId = new Random().nextInt(100);
        story.getProject().setProjectId(projectId);

        //when
        when(projectRepository.findById(any(int.class))).thenReturn(Optional.of(project));
        when(storyRepository.findById(any(int.class))).thenReturn(Optional.of(story));
        when(storyRepository.save(any(Story.class))).thenReturn(story);
        Story result = underTest.updateStory(storyId, story);
        //then
        ArgumentCaptor<Integer> projectIdCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> storyIdCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Story> storyCaptor = ArgumentCaptor.forClass(Story.class);
        verify(projectRepository).findById(projectIdCaptor.capture());
        verify(storyRepository).findById(storyIdCaptor.capture());
        verify(storyRepository).save(storyCaptor.capture());

        int capturedProjectId = projectIdCaptor.getValue();
        int capturedStoryId = storyIdCaptor.getValue();
        Story capturedStory = storyCaptor.getValue();

        assertThat(capturedProjectId).isEqualTo(projectId);
        assertThat(capturedStoryId).isEqualTo(storyId);
        assertThat(capturedStory).isEqualTo(story);
        assertThat(result).isEqualTo(story);
    }

    @Test
    void updateStory_with_notExistingId_shouldThrow_NotFoundException(){
        //given
        int id = new Random().nextInt(100);
        Story story = new Story("name");
        //when
        //then
        assertThatThrownBy(() -> underTest.updateStory(id, story))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Story with id " + id + " not found");
    }

    @Test
    void updateStory_with_notExistingProjectId_shouldThrow_NotFoundException(){
        //given
        Story story = new Story("name");
        Project project = new Project(
                "name"
        );
        story.setProject(project);

        int projectId = new Random().nextInt(100);
        int storyId = new Random().nextInt(100);
        story.getProject().setProjectId(projectId);
        //when
        when(storyRepository.findById(any(int.class))).thenReturn(Optional.of(story));
        //then
        assertThatThrownBy(() -> underTest.updateStory(storyId, story))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Project with id " + projectId + " not found");
    }

    @Test
    void can_call_deleteStory_with_validId_should_return_true() {
        //given
        int id = new Random().nextInt(100);
        Story story = new Story("name");
        //when
        when(storyRepository.findById(any(int.class))).thenReturn(Optional.of(story));
        Boolean result = underTest.deleteStory(id);
        //then
        ArgumentCaptor<Integer> idCaptorFindBy = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> idCaptorDeleteBy = ArgumentCaptor.forClass(Integer.class);
        verify(storyRepository).findById(idCaptorFindBy.capture());
        verify(storyRepository).deleteById(idCaptorDeleteBy.capture());

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
        Boolean result = underTest.deleteStory(id);
        //then
        assertThat(result).isEqualTo(false);
    }
}
