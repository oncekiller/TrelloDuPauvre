package cnd.trelloDuPauvre.perso.service;

import cnd.trelloDuPauvre.perso.Exceptions.EntityNotFoundException;
import cnd.trelloDuPauvre.perso.model.Image;
import cnd.trelloDuPauvre.perso.model.Project;
import cnd.trelloDuPauvre.perso.model.Workspace;
import cnd.trelloDuPauvre.perso.repository.ImageRepository;
import cnd.trelloDuPauvre.perso.repository.ProjectRepository;
import cnd.trelloDuPauvre.perso.repository.WorkspaceRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private WorkspaceRepository workspaceRepository;


    public List<Project> getAllProjects() {
        List<Project> projects = projectRepository.findAll();
        return projects;
    }

    public Project getProjectById(int projectId){
        Optional<Project> project = projectRepository.findById(projectId);

        if (project.isPresent()){
            return project.get();
        }else {
            throw new EntityNotFoundException("Project with id " + projectId + " not found");
        }
    }

    public Project createProject(Project project) {

        return  projectRepository.save(project);
    }

    public Project updateProject(int projectId, Project project){
        Optional<Project> updatedProject = projectRepository.findById(projectId);

        if (updatedProject.isPresent()) {
            Project newProject = updatedProject.get();
            newProject.setName(project.getName());
            newProject.setIsFavorite(project.getIsFavorite());
            newProject.setCreationDate(project.getCreationDate());
            newProject.setLastConsultationDate(project.getLastConsultationDate());
            newProject.setBgColor(project.getBgColor());
            newProject.setBgImage(null);
            newProject.setWorkspace(null);
            if(project.getWorkspace() != null){
                int workspaceId = project.getWorkspace().getWorkspaceId();
                Optional<Workspace> workspace = workspaceRepository.findById(workspaceId);

                //Check if the workspace of the new story exist
                if (workspace.isPresent()) {
                    newProject.setWorkspace(workspace.get());
                } else {
                    throw new EntityNotFoundException("Workspace with id " + workspaceId + " not found");
                }
            }
            if(project.getBgImage() != null){
                int imageId = project.getBgImage().getImageId();
                Optional<Image> image = imageRepository.findById(imageId);

                //Check if image of the new story exist
                if (image.isPresent()) {
                    newProject.setBgImage(image.get());
                } else {
                    throw new EntityNotFoundException("Image with id " + imageId + " not found");
                }
            }
            return projectRepository.save(newProject);
        }else {
            throw new EntityNotFoundException("Project with id " + projectId + " not found");
        }
    }

    public  Project updateProjectFavorite(int projectId, Boolean isFavorite){
        Optional<Project> updatedProject = projectRepository.findById(projectId);

        if (updatedProject.isPresent()) {
            Project newProject = updatedProject.get();
            newProject.setIsFavorite(isFavorite);
            return projectRepository.save(newProject);
        }else {
            throw new EntityNotFoundException("Project with id " + projectId + " not found");
        }
    }

    public Boolean deleteProject(int projectId) {
        Optional<Project> deletedProject = projectRepository.findById(projectId);

        if(deletedProject.isPresent()) {
            projectRepository.deleteById(projectId);
            return true;
        }else{
            return false;
        }
    }

}
