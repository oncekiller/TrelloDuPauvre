package cnd.trelloDuPauvre.perso.controller;

import cnd.trelloDuPauvre.perso.model.Story;
import cnd.trelloDuPauvre.perso.service.StoryService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("api/v1")
public class StoryController {
    @Autowired
    private StoryService storyService;

    @GetMapping("/stories")
    public ResponseEntity<Object> getAllStories() {
        List<Story> stories = storyService.getAllStories();
        return  new ResponseEntity<>(stories,  new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/stories/{storyId}")
    public ResponseEntity<Object> getStoryById(@PathVariable(name = "storyId", required = true) int storyId) {
        Story story = storyService.getStoryById(storyId);
        return  new ResponseEntity<>(story,  new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/project/{projectId}/stories")
    public ResponseEntity<List<Story>> getStoriesByProjectId (@PathVariable(name = "projectId", required = true) int projectId) {
        List<Story> stories = storyService.getStoriesByProjectId(projectId);
        return  new ResponseEntity<>(stories,  new HttpHeaders(), HttpStatus.OK);
    }

    @PostMapping("/stories")
    public ResponseEntity<Object> createStory(@RequestBody @Valid Story story) {
        Story createdStory = storyService.createStory(story);
        return new ResponseEntity<>(createdStory, new HttpHeaders(), HttpStatus.CREATED);
    }

    @PutMapping("/stories/{storyId}")
    public ResponseEntity<Object> updateStory(@PathVariable(name = "storyId") int storyId, @RequestBody @Valid Story story) {
        Story updatedStory= storyService.updateStory(storyId, story);
        return new ResponseEntity<>(updatedStory, new HttpHeaders(), HttpStatus.OK);
    }

    @PutMapping("/stories/multiple")
    public ResponseEntity<Object> updateMultipleStories(@RequestBody @Valid List<Story> stories) {
        List<Story> updatedStories = storyService.updateMultipleStories(stories);
        return new ResponseEntity<>(updatedStories, new HttpHeaders(), HttpStatus.OK);
    }

    @PutMapping("/stories/{storyId}/move")
    public ResponseEntity<Object> moveStory(
            @PathVariable(name = "storyId") int storyId,
            @RequestParam(name= "projectId", required = true) int projectId,
            @RequestParam(name= "frontIndex", required = true) int frontIndex) {
        Story updatedStory= storyService.moveStory(storyId, projectId, frontIndex);
        return new ResponseEntity<>(updatedStory, new HttpHeaders(), HttpStatus.OK);
    }

    @DeleteMapping("/stories/{storyId}")
    public ResponseEntity<Object> deleteStory(@PathVariable(name = "storyId") int storyId) {
        Boolean isStoryDeleted = storyService.deleteStory(storyId);
        return new ResponseEntity<>(isStoryDeleted, new HttpHeaders(), HttpStatus.OK);
    }
}
