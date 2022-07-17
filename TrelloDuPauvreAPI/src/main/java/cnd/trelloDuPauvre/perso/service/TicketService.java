package cnd.trelloDuPauvre.perso.service;

import cnd.trelloDuPauvre.perso.Exceptions.EntityNotFoundException;
import cnd.trelloDuPauvre.perso.model.*;
import cnd.trelloDuPauvre.perso.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@AllArgsConstructor
public class TicketService {
    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private StoryRepository storyRepository;

    @Autowired
    private NatureRepository natureRepository;

    @Autowired
    private StatusRepository statusRepository;

    public List<Ticket> getAllTickets() {
        List<Ticket> tickets = ticketRepository.findAll();
        return  tickets;
    }

    public Ticket getTicketById(int ticketId) {
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);

        if (ticket.isPresent()){
            return ticket.get();
        }else {
            throw new EntityNotFoundException("Ticket with id " + ticketId + " not found");
        }
    }

    public List<Ticket> getTicketsByStoryId(int storyId){
        Optional<Story> story = storyRepository.findById(storyId);
        if (story.isPresent()){
            return ticketRepository.findByStory(story.get());
        }else {
            throw new EntityNotFoundException("Story with id " + storyId + " not found");
        }
    }

    public List<Object> getTicketsByProjectIdGroupByStory(int projectId) throws ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<Object> result = new ArrayList<>();
        Optional<Project> project = projectRepository.findById(projectId);
        if(project.isPresent()){
            List<Story> projectStories = storyRepository.findByProject(project.get());
            for (Story story: projectStories) {
                List<Ticket> storyTickets = ticketRepository.findByStory(story);
                TreeMap<String, Object> subResult = new TreeMap<String, Object>();
                subResult.put("storyId", story.getStoryId());
                subResult.put("name", story.getName());
                subResult.put("frontIndex", story.getFrontIndex());
                subResult.put("creationDate", formatter.format(story.getCreationDate()));
                subResult.put("projectId", story.getProject().getProjectId());
                subResult.put("tickets", storyTickets);
                result.add(subResult);
            }
        }
        return  result;
    }

    public Ticket createTicket (Ticket ticket) {
        int projectId = ticket.getProject().getProjectId();
        Optional<Project> project = projectRepository.findById(projectId);

        int storyId = ticket.getStory().getStoryId();
        Optional<Story> story = storyRepository.findById(storyId);

        int natureId = ticket.getNature().getNatureId();
        Optional<Nature> nature = natureRepository.findById(natureId);

        int statusId = ticket.getStatus().getStatusId();
        Optional<Status> status = statusRepository.findById(statusId);

        //Check if project of the created ticket exist
        if(project.isPresent()){
            //Check if story of the created ticket exist
            if(story.isPresent()) {
                //Check if nature of the created ticket exist
                if(nature.isPresent()) {
                    //Check if status of the created ticket exist
                    if(status.isPresent()) {
                        ticket.setStory(story.get());
                        ticket.setProject(project.get());
                        ticket.setNature(nature.get());
                        ticket.setStatus(status.get());
                        return ticketRepository.save(ticket);
                    }else {
                        throw new EntityNotFoundException("Status with id " + statusId + " not found");
                    }
                }else {
                    throw new EntityNotFoundException("Nature with id " + natureId + " not found");
                }
            }else {
                throw new EntityNotFoundException("Story with id " + storyId + " not found");
            }
        }else {
            throw new EntityNotFoundException("Project with id " + projectId + " not found");
        }
    }

    public Ticket updateTicket (Ticket ticket, int ticketId) {
        Optional<Ticket> updatedTicket = ticketRepository.findById(ticketId);

        //Check if the updated ticket exist
        if(updatedTicket.isPresent()) {
            int projectId = ticket.getProject().getProjectId();
            Optional<Project> project = projectRepository.findById(projectId);

            int storyId = ticket.getStory().getStoryId();
            Optional<Story> story = storyRepository.findById(storyId);

            int natureId = ticket.getNature().getNatureId();
            Optional<Nature> nature = natureRepository.findById(natureId);

            int statusId = ticket.getStatus().getStatusId();
            Optional<Status> status = statusRepository.findById(statusId);

            //Check if project of the updated ticket exist
            if(project.isPresent()){
                //Check if story of the updated ticket exist
                if(story.isPresent()) {
                    //Check if nature of the updated ticket exist
                    if(nature.isPresent()) {
                        //Check if status of the updated ticket exist
                        if(status.isPresent()) {
                            Ticket newTicket = updatedTicket.get();
                            newTicket.setName(ticket.getName());
                            newTicket.setDescription(ticket.getDescription());
                            newTicket.setPriority(ticket.getPriority());
                            newTicket.setProject(project.get());
                            newTicket.setStory(story.get());
                            newTicket.setCreationDate(ticket.getCreationDate());
                            newTicket.setModificationDate(ticket.getModificationDate());
                            newTicket.setDeadLine(ticket.getDeadLine());
                            newTicket.setNature(nature.get());
                            newTicket.setStatus(status.get());
                            newTicket.setFrontIndex(ticket.getFrontIndex());

                            return ticketRepository.save(newTicket);
                        }else {
                            throw new EntityNotFoundException("Status with id " + statusId + " not found");
                        }
                    }else {
                        throw new EntityNotFoundException("Nature with id " + natureId + " not found");
                    }
                }else {
                    throw new EntityNotFoundException("Story with id " + storyId + " not found");
                }
            }else {
                throw new EntityNotFoundException("Project with id " + projectId + " not found");
            }
        }else {
            throw new EntityNotFoundException("Ticket with id " + ticketId + " not found");
        }
    }

    public List<Ticket> updateMultipleTickets(List<Ticket> tickets){
        ArrayList<Ticket> updatedTickets = new ArrayList<Ticket>();
        for (Ticket ticket : tickets) {
            int ticketId = ticket.getTicketId();
            Optional<Ticket> updatedTicket = ticketRepository.findById(ticketId);

            //Check if the updated ticket exist
            if(updatedTicket.isPresent()) {
                int projectId = ticket.getProject().getProjectId();
                Optional<Project> project = projectRepository.findById(projectId);

                int storyId = ticket.getStory().getStoryId();
                Optional<Story> story = storyRepository.findById(storyId);

                int natureId = ticket.getNature().getNatureId();
                Optional<Nature> nature = natureRepository.findById(natureId);

                int statusId = ticket.getStatus().getStatusId();
                Optional<Status> status = statusRepository.findById(statusId);

                //Check if project of the updated ticket exist
                if(project.isPresent()){
                    //Check if story of the updated ticket exist
                    if(story.isPresent()) {
                        //Check if nature of the updated ticket exist
                        if(nature.isPresent()) {
                            //Check if status of the updated ticket exist
                            if(status.isPresent()) {
                                Ticket newTicket = updatedTicket.get();
                                newTicket.setName(ticket.getName());
                                newTicket.setDescription(ticket.getDescription());
                                newTicket.setPriority(ticket.getPriority());
                                newTicket.setProject(project.get());
                                newTicket.setStory(story.get());
                                newTicket.setCreationDate(ticket.getCreationDate());
                                newTicket.setModificationDate(ticket.getModificationDate());
                                newTicket.setDeadLine(ticket.getDeadLine());
                                newTicket.setNature(nature.get());
                                newTicket.setStatus(status.get());
                                newTicket.setFrontIndex(ticket.getFrontIndex());

                                updatedTickets.add(newTicket);
                            }else {
                                throw new EntityNotFoundException("Status with id " + statusId + " not found");
                            }
                        }else {
                            throw new EntityNotFoundException("Nature with id " + natureId + " not found");
                        }
                    }else {
                        throw new EntityNotFoundException("Story with id " + storyId + " not found");
                    }
                }else {
                    throw new EntityNotFoundException("Project with id " + projectId + " not found");
                }
            }else {
                throw new EntityNotFoundException("Ticket with id " + ticketId + " not found");
            }

        }
        return ticketRepository.saveAll(updatedTickets);
    }
    public Boolean deleteTicket(int ticketId) {
        Optional<Ticket> deletedTicket = ticketRepository.findById(ticketId);

        if(deletedTicket.isPresent()) {
            ticketRepository.deleteById(ticketId);
            return true;
        }else{
            return false;
        }
    }
}
