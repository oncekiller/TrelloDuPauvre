package cnd.trelloDuPauvre.perso.repository;

import cnd.trelloDuPauvre.perso.model.Project;
import cnd.trelloDuPauvre.perso.model.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoryRepository extends JpaRepository<Story, Integer> {
    List<Story> findByProject(Project project);
}
