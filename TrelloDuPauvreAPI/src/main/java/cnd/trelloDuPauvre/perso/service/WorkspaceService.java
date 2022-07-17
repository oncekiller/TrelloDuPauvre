package cnd.trelloDuPauvre.perso.service;

import cnd.trelloDuPauvre.perso.Exceptions.EntityNotFoundException;
import cnd.trelloDuPauvre.perso.model.Workspace;
import cnd.trelloDuPauvre.perso.repository.WorkspaceRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class WorkspaceService {
    @Autowired
    private WorkspaceRepository workspaceRepository;

    public List<Workspace> getAllWorkspaces() {
        List<Workspace> workspaces = workspaceRepository.findAll();
        return workspaces;
    }

    public Workspace getWorkspaceById(int workspaceId){
        Optional<Workspace> workspace = workspaceRepository.findById(workspaceId);

        if (workspace.isPresent()){
            return workspace.get();
        }else {
            throw new EntityNotFoundException("Workspace with id " + workspaceId + " not found");
        }
    }

    public Workspace createWorkspace(Workspace workspace) {
        return  workspaceRepository.save(workspace);
    }

    public Workspace updateWorkspace(int workspaceId, Workspace workspace){
        Optional<Workspace> updatedWorkspace = workspaceRepository.findById(workspaceId);

        if (updatedWorkspace.isPresent()) {
            Workspace newWorkspace = updatedWorkspace.get();
            newWorkspace.setName(workspace.getName());
            newWorkspace.setIconName(workspace.getIconName());
            newWorkspace.setCreationDate(workspace.getCreationDate());

            return workspaceRepository.save(newWorkspace);
        }else {
            throw new EntityNotFoundException("Workspace with id " + workspaceId + " not found");
        }
    }

    public Boolean deleteWorkspace(int workspaceId) {
        Optional<Workspace> deletedWorkspace = workspaceRepository.findById(workspaceId);
        if(deletedWorkspace.isPresent()) {
            workspaceRepository.deleteById(workspaceId);
            return true;
        }else{
            return false;
        }
    }

}
