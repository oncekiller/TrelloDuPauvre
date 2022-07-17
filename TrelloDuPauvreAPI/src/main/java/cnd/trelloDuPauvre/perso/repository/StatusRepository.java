package cnd.trelloDuPauvre.perso.repository;


import cnd.trelloDuPauvre.perso.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusRepository extends JpaRepository<Status, Integer> {
}
