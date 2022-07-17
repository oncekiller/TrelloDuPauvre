package cnd.trelloDuPauvre.perso.service;

import cnd.trelloDuPauvre.perso.Exceptions.EntityNotFoundException;
import cnd.trelloDuPauvre.perso.model.CheckListItem;
import cnd.trelloDuPauvre.perso.model.Ticket;
import cnd.trelloDuPauvre.perso.repository.CheckListItemRepository;
import cnd.trelloDuPauvre.perso.repository.TicketRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CheckListItemService {
    @Autowired
    private CheckListItemRepository checkListItemRepository;

    @Autowired
    private TicketRepository ticketRepository;


    public List<CheckListItem> getAllCheckListItems() {
        List<CheckListItem> checkListItems = checkListItemRepository.findAll();
        return checkListItems;
    }

    public CheckListItem getCheckListItemById(int checkListItemId){
        Optional<CheckListItem> checkListItem = checkListItemRepository.findById(checkListItemId);

        if (checkListItem.isPresent()){
            return checkListItem.get();
        }else {
            throw new EntityNotFoundException("CheckListItem with id " + checkListItemId + " not found");
        }
    }

    public CheckListItem createCheckListItem(CheckListItem checkListItem) {
        int ticketId = checkListItem.getTicket().getTicketId();
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);

        //Check if ticket of the created checkListItem exist
        if (ticket.isPresent()){
            checkListItem.setTicket(ticket.get());
            return  checkListItemRepository.save(checkListItem);
        } else {
            throw new EntityNotFoundException("Ticket with id " + ticketId + " not found");
        }
    }

    public CheckListItem updateCheckListItem(int checkListItemId, CheckListItem checkListItem){

        Optional<CheckListItem> updatedCheckListItem = checkListItemRepository.findById(checkListItemId);

        //Check if updated checkListItem exist
        if (updatedCheckListItem.isPresent()) {
            int ticketId = checkListItem.getTicket().getTicketId();
            Optional<Ticket> ticket = ticketRepository.findById(ticketId);

            //Check if ticket of the updated checkListItem exist
            if (ticket.isPresent()){
                CheckListItem newCheckListItem = updatedCheckListItem.get();
                newCheckListItem.setChecked(checkListItem.getChecked());
                newCheckListItem.setLabel(checkListItem.getLabel());
                newCheckListItem.setTicket(ticket.get());

                return checkListItemRepository.save(newCheckListItem);
            } else {
                throw new EntityNotFoundException("Ticket with id " + ticketId + " not found");
            }
        }else {
            throw new EntityNotFoundException("CheckListItem with id " + checkListItemId + " not found");
        }
    }

    public Boolean deleteCheckListItem(int checkListItemId) {
        Optional<CheckListItem> deletedCheckListItem = checkListItemRepository.findById(checkListItemId);

        if(deletedCheckListItem.isPresent()) {
            checkListItemRepository.deleteById(checkListItemId);
            return true;
        }else{
            return false;
        }
    }
}
