package cnd.trelloDuPauvre.perso.controller;

import cnd.trelloDuPauvre.perso.model.Status;
import cnd.trelloDuPauvre.perso.service.StatusService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("api/v1")
public class StatusController {

    @Autowired
    private StatusService statusService;

    @GetMapping("/status")
    public ResponseEntity<Object> getAllStatus() {
        List<Status> status = statusService.getAllStatus();
        return  new ResponseEntity<>(status,  new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/status/{statusId}")
    public ResponseEntity<Object> getStatusById(@PathVariable(name = "statusId", required = true) int statusId) {
        Status status = statusService.getStatusById(statusId);
        return  new ResponseEntity<>(status,  new HttpHeaders(), HttpStatus.OK);
    }

    @PostMapping("/status")
    public ResponseEntity<Object> createStatus(@RequestBody @Valid Status status) {
        Status createdStatus = statusService.createStatus(status);
        return new ResponseEntity<>(createdStatus, new HttpHeaders(), HttpStatus.CREATED);
    }

    @PutMapping("/status/{statusId}")
    public ResponseEntity<Object> updateStatus(@PathVariable(name = "statusId") int statusId, @RequestBody @Valid Status status) {
        Status updatedStatus = statusService.updateStatus(statusId, status);
        return new ResponseEntity<>(updatedStatus, new HttpHeaders(), HttpStatus.OK);
    }

    @DeleteMapping("/status/{statusId}")
    public ResponseEntity<Object> deleteStatus(@PathVariable(name = "statusId") int statusId) {
        Boolean isStatusDeleted = statusService.deleteStatus(statusId);
        return new ResponseEntity<>(isStatusDeleted, new HttpHeaders(), HttpStatus.OK);
    }
}
