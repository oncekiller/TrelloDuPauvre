package cnd.trelloDuPauvre.perso.repository;

import cnd.trelloDuPauvre.perso.model.Story;
import cnd.trelloDuPauvre.perso.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository <Ticket, Integer>{
    List<Ticket> findByStory(Story story);
}
