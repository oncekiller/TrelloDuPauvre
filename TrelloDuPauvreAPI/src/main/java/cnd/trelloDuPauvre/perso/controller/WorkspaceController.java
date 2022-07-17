package cnd.trelloDuPauvre.perso.controller;



import cnd.trelloDuPauvre.perso.model.Workspace;
import cnd.trelloDuPauvre.perso.service.WorkspaceService;
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
public class WorkspaceController {

    @Autowired
    private WorkspaceService workspaceService;

    @GetMapping("/workspaces")
    public ResponseEntity<Object> getAllWorkspaces() {
            List<Workspace> workspaces = workspaceService.getAllWorkspaces();
            return  new ResponseEntity<>(workspaces,  new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/workspaces/{workspaceId}")
    public ResponseEntity<Object> getWorkspaceById(@PathVariable(name = "workspaceId", required = true) int workspaceId) {
        Workspace workspace = workspaceService.getWorkspaceById(workspaceId);
        return  new ResponseEntity<>(workspace,  new HttpHeaders(), HttpStatus.OK);
    }

    @PostMapping("/workspaces")
    public ResponseEntity<Object> createWorkspace(@RequestBody @Valid Workspace workspace) {
        Workspace createdWorkspace = workspaceService.createWorkspace(workspace);
        return new ResponseEntity<>(createdWorkspace, new HttpHeaders(), HttpStatus.CREATED);
    }

    @PutMapping("/workspaces/{workspaceId}")
    public ResponseEntity<Object> updateWorkspace(@PathVariable(name = "workspaceId") int workspaceId, @RequestBody @Valid Workspace workspace) {
        Workspace updatedWorkspace = workspaceService.updateWorkspace(workspaceId, workspace);
        return new ResponseEntity<>(updatedWorkspace, new HttpHeaders(), HttpStatus.OK);
    }

    @DeleteMapping("/workspaces/{workspaceId}")
    public ResponseEntity<Object> deleteWorkspace(@PathVariable(name = "workspaceId") int workspaceId) {
        Boolean isWorkspaceDeleted = workspaceService.deleteWorkspace(workspaceId);
        return new ResponseEntity<>(isWorkspaceDeleted, new HttpHeaders(), HttpStatus.OK);
    }

}
