package cnd.trelloDuPauvre.perso.service;

import cnd.trelloDuPauvre.perso.Exceptions.EntityNotFoundException;
import cnd.trelloDuPauvre.perso.model.Status;
import cnd.trelloDuPauvre.perso.repository.StatusRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class StatusService {
    @Autowired
    private StatusRepository statusRepository;

    public List<Status> getAllStatus() {
        List<Status> status = statusRepository.findAll();
        return status;
    }

    public Status getStatusById(int statusId){
        Optional<Status> status = statusRepository.findById(statusId);

        if (status.isPresent()){
            return status.get();
        }else {
            throw new EntityNotFoundException("Status with id " + statusId + " not found");
        }
    }

    public Status createStatus(Status status) {
        return  statusRepository.save(status);
    }

    public Status updateStatus(int statusId, Status status){
        Optional<Status> updatedStatus = statusRepository.findById(statusId);

        if (updatedStatus.isPresent()) {
            Status newStatus = updatedStatus.get();
            newStatus.setName(status.getName());
            newStatus.setLabel(status.getLabel());

            return statusRepository.save(newStatus);
        }else {
            throw new EntityNotFoundException("Status with id " + statusId + " not found");
        }
    }

    public Boolean deleteStatus(int statusId) {
        Optional<Status> deletedStatus = statusRepository.findById(statusId);

        if(deletedStatus.isPresent()) {
            statusRepository.deleteById(statusId);
            return true;
        }else{
            return false;
        }
    }
}
