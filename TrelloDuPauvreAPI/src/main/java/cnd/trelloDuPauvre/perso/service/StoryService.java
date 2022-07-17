package cnd.trelloDuPauvre.perso.service;

import cnd.trelloDuPauvre.perso.Exceptions.EntityNotFoundException;
import cnd.trelloDuPauvre.perso.model.Project;
import cnd.trelloDuPauvre.perso.model.Story;
import cnd.trelloDuPauvre.perso.repository.ProjectRepository;
import cnd.trelloDuPauvre.perso.repository.StoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class StoryService {
    @Autowired
    private StoryRepository storyRepository;

    @Autowired
    private ProjectRepository projectRepository;

    public List<Story> getAllStories() {
        List<Story> stories = storyRepository.findAll();
        return stories;
    }

    public Story getStoryById(int storyId){
        Optional<Story> story = storyRepository.findById(storyId);

        if (story.isPresent()){
            return story.get();
        }else {
            throw new EntityNotFoundException("Story with id " + storyId + " not found");
        }
    }

    public List<Story> getStoriesByProjectId(int projectId){
        Optional<Project> project = projectRepository.findById(projectId);

        if (project.isPresent()){
            return storyRepository.findByProject(project.get());
        }else {
            throw new EntityNotFoundException("Project with id " + projectId + " not found");
        }
    }

    public Story createStory(Story story) {
        int projectId = story.getProject().getProjectId();
        Optional<Project> project = projectRepository.findById(projectId);

        //Check if project of the created story exist
        if(project.isPresent()){
            story.setProject(project.get());
            return  storyRepository.save(story);
        }else {
            throw new EntityNotFoundException("Project with id " + projectId + " not found");
        }
    }

    public Story updateStory(int storyId, Story story){
        Optional<Story> updatedStory = storyRepository.findById(storyId);

        //Check if updated story exist
        if (updatedStory.isPresent()) {
            int projectId = story.getProject().getProjectId();
            Optional<Project> project = projectRepository.findById(projectId);

            //Check if project of the new story exist
            if(project.isPresent()){
                Story newStory = updatedStory.get();
                newStory.setName(story.getName());
                newStory.setCreationDate(story.getCreationDate());
                newStory.setFrontIndex(story.getFrontIndex());
                newStory.setProject(project.get());

                return storyRepository.save(newStory);
            }else {
                throw new EntityNotFoundException("Project with id " + projectId + " not found");
            }
        }else {
            throw new EntityNotFoundException("Story with id " + storyId + " not found");
        }
    }

    public List<Story> updateMultipleStories(List<Story> stories){
        ArrayList<Story> updatedStories = new ArrayList<Story>();
        for (Story story : stories) {
            int storyId = story.getStoryId();
            Optional<Story> updatedStory = storyRepository.findById(storyId);

            //Check if updated story exist
            if (updatedStory.isPresent()) {
                int projectId = story.getProject().getProjectId();
                Optional<Project> project = projectRepository.findById(projectId);

                //Check if project of the new story exist
                if(project.isPresent()){
                    Story newStory = updatedStory.get();
                    newStory.setName(story.getName());
                    newStory.setCreationDate(story.getCreationDate());
                    newStory.setFrontIndex(story.getFrontIndex());
                    newStory.setProject(project.get());

                    updatedStories.add(newStory);
                }else {
                    throw new EntityNotFoundException("Project with id " + projectId + " not found");
                }
            }else {
                throw new EntityNotFoundException("Story with id " + storyId + " not found");
            }
        }
        return storyRepository.saveAll(updatedStories);
    }

    public Story moveStory(int storyId, int projectId, int frontIndex){
        Optional<Story> updatedStory = storyRepository.findById(storyId);

        //Check if updated story exist
        if (updatedStory.isPresent()) {
            Optional<Project> project = projectRepository.findById(projectId);

            //Check if project of the new story exist
            if(project.isPresent()){
                Story newStory = updatedStory.get();
                newStory.setFrontIndex(frontIndex);
                newStory.setProject(project.get());

                return storyRepository.save(newStory);
            }else {
                throw new EntityNotFoundException("Project with id " + projectId + " not found");
            }
        }else {
            throw new EntityNotFoundException("Story with id " + storyId + " not found");
        }
    }

    public Boolean deleteStory(int storyId) {
        Optional<Story> deletedStory = storyRepository.findById(storyId);

        if(deletedStory.isPresent()) {
            storyRepository.deleteById(storyId);
            return true;
        }else{
            return false;
        }
    }
}
