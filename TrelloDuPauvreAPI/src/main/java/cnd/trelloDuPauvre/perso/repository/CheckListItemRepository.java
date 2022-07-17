package cnd.trelloDuPauvre.perso.repository;

import cnd.trelloDuPauvre.perso.model.CheckListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckListItemRepository extends JpaRepository<CheckListItem, Integer> {
}
