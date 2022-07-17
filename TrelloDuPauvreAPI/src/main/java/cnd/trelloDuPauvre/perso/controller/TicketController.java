package cnd.trelloDuPauvre.perso.controller;


import cnd.trelloDuPauvre.perso.model.Story;
import cnd.trelloDuPauvre.perso.model.Ticket;
import cnd.trelloDuPauvre.perso.service.TicketService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class TicketController {
    @Autowired
    private TicketService ticketService;

    @GetMapping("/tickets")
    public ResponseEntity<Object> getAllTickets(){
        List<Ticket> tickets = ticketService.getAllTickets();
        return new ResponseEntity<>(tickets, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/tickets/{ticketId}")
    public ResponseEntity<Object> getTicketById(@PathVariable(value = "ticketId", required = true) int ticketId) {
        Ticket ticket = ticketService.getTicketById(ticketId);

        return new ResponseEntity<>(ticket, new HttpHeaders(),  HttpStatus.OK);
    }
    @GetMapping("/story/{storyId}/tickets")
    public ResponseEntity<List<Ticket>> getTicketsByStoryId (@PathVariable(value = "storyId", required = true) int storyId) {
        List<Ticket> tickets = ticketService.getTicketsByStoryId(storyId);
        return  new ResponseEntity<>(tickets,  new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/project/{projectId}/tickets")
    public ResponseEntity<List<Object>> getTicketsByProjectIdGroupByStory (@PathVariable(value = "projectId", required = true) int projectId) throws ParseException {
        List<Object> tickets = ticketService.getTicketsByProjectIdGroupByStory(projectId);
        return  new ResponseEntity<>(tickets,  new HttpHeaders(), HttpStatus.OK);
    }

    @PostMapping("/tickets")
    public ResponseEntity<Object> postTicket(@RequestBody @Valid Ticket ticket) {
        Ticket createdTicket = ticketService.createTicket(ticket);
        return new ResponseEntity<>(createdTicket, new HttpHeaders(), HttpStatus.CREATED);
    }

    @PutMapping("/tickets/{ticketId}")
    public ResponseEntity<Object> updateTicket(@PathVariable(name = "ticketId", required = true) int ticketId, @RequestBody @Valid Ticket ticket) {
        Ticket updatedTicket = ticketService.updateTicket(ticket, ticketId);
        return new ResponseEntity<>(updatedTicket, new HttpHeaders(), HttpStatus.OK);
    }

    @PutMapping("/tickets/multiple")
    public ResponseEntity<Object> updateMultipleTickets(@RequestBody @Valid List<Ticket> tickets) {
        List<Ticket> updatedTickets = ticketService.updateMultipleTickets(tickets);
        return new ResponseEntity<>(updatedTickets, new HttpHeaders(), HttpStatus.OK);
    }

    @DeleteMapping("tickets/{ticketId}")
    public ResponseEntity<Object> deleteTicket(@PathVariable(name = "ticketId", required = true) int ticketId) {
        Boolean isTicketDeleted = ticketService.deleteTicket(ticketId);
        return new ResponseEntity<>(isTicketDeleted, new HttpHeaders(), HttpStatus.OK);
    }
}
