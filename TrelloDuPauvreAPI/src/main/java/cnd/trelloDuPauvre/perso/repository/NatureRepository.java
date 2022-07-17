package cnd.trelloDuPauvre.perso.repository;

import cnd.trelloDuPauvre.perso.model.Nature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NatureRepository extends JpaRepository<Nature, Integer> {
}
