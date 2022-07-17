package cnd.trelloDuPauvre.perso.controller;

import cnd.trelloDuPauvre.perso.model.CheckListItem;
import cnd.trelloDuPauvre.perso.service.CheckListItemService;
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
public class CheckListItemController {

    @Autowired
    private CheckListItemService checkListItemService;

    @GetMapping("/checkListItems")
    public ResponseEntity<Object> getAllCheckListItems() {
        List<CheckListItem> checkListItems = checkListItemService.getAllCheckListItems();
        return  new ResponseEntity<>(checkListItems,  new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/checkListItems/{checkListItemId}")
    public ResponseEntity<Object> getCheckListItemById(@PathVariable(name = "checkListItemId", required = true) int checkListItemId) {
        CheckListItem checkListItem = checkListItemService.getCheckListItemById(checkListItemId);
        return  new ResponseEntity<>(checkListItem,  new HttpHeaders(), HttpStatus.OK);
    }

    @PostMapping("/checkListItems")
    public ResponseEntity<Object> createCheckListItem(@RequestBody @Valid CheckListItem checkListItem) {
        CheckListItem createdCheckListItem = checkListItemService.createCheckListItem(checkListItem);
        return new ResponseEntity<>(createdCheckListItem, new HttpHeaders(), HttpStatus.CREATED);
    }

    @PutMapping("/checkListItems/{checkListItemId}")
    public ResponseEntity<Object> updateCheckListItem(@PathVariable(name = "checkListItemId") int checkListItemId, @RequestBody @Valid CheckListItem checkListItem) {
        CheckListItem updatedCheckListItem = checkListItemService.updateCheckListItem(checkListItemId, checkListItem);
        return new ResponseEntity<>(updatedCheckListItem, new HttpHeaders(), HttpStatus.OK);
    }

    @DeleteMapping("/checkListItems/{checkListItemId}")
    public ResponseEntity<Object> deleteCheckListItem(@PathVariable(name = "checkListItemId") int checkListItemId) {
        Boolean isCheckListItemDeleted = checkListItemService.deleteCheckListItem(checkListItemId);
        return new ResponseEntity<>(isCheckListItemDeleted, new HttpHeaders(), HttpStatus.OK);
    }
}
