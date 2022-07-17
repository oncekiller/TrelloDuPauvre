package cnd.trelloDuPauvre.perso.repository;

import cnd.trelloDuPauvre.perso.model.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface WorkspaceRepository extends JpaRepository <Workspace, Integer>{

}
