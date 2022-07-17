package cnd.trelloDuPauvre.perso.controller;

import cnd.trelloDuPauvre.perso.model.Project;
import cnd.trelloDuPauvre.perso.service.ProjectService;
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
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @GetMapping("/projects")
    public ResponseEntity<Object> getAllProjects() {
            List<Project> projects = projectService.getAllProjects();
            return  new ResponseEntity<>(projects,  new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<Object> getProjectById(@PathVariable(name = "projectId", required = true) int projectId) {
        Project project = projectService.getProjectById(projectId);
        return  new ResponseEntity<>(project,  new HttpHeaders(), HttpStatus.OK);
    }

    @PostMapping("/projects")
    public ResponseEntity<Object> createProject(@RequestBody @Valid Project project) {
        Project createdProject = projectService.createProject(project);
        return new ResponseEntity<>(createdProject, new HttpHeaders(), HttpStatus.CREATED);
    }

    @PutMapping("/projects/{projectId}")
    public ResponseEntity<Object> updateProject(@PathVariable(name = "projectId") int projectId, @RequestBody @Valid Project project) {
        Project updatedProject = projectService.updateProject(projectId, project);
        return new ResponseEntity<>(updatedProject, new HttpHeaders(), HttpStatus.OK);
    }

    @PutMapping("/projects/{projectId}/favorite")
    public ResponseEntity<Object> updateProjectFavorite(@PathVariable(name = "projectId") int projectId,
                                                        @RequestParam(name = "isFavorite", required = true) Boolean isFavorite) {
        Project updatedProject = projectService.updateProjectFavorite(projectId, isFavorite);
        return new ResponseEntity<>(updatedProject, new HttpHeaders(), HttpStatus.OK);
    }

    @DeleteMapping("/projects/{projectId}")
    public ResponseEntity<Object> deleteProject(@PathVariable(name = "projectId") int projectId) {
        Boolean isProjectDeleted = projectService.deleteProject(projectId);
        return new ResponseEntity<>(isProjectDeleted, new HttpHeaders(), HttpStatus.OK);
    }

}
