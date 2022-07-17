package cnd.trelloDuPauvre.perso.controller;

import cnd.trelloDuPauvre.perso.model.Nature;
import cnd.trelloDuPauvre.perso.service.NatureService;
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
public class NatureController {

    @Autowired
    private NatureService natureService;

    @GetMapping("/natures")
    public ResponseEntity<Object> getAllNatures() {
        List<Nature> natures = natureService.getAllNatures();
        return  new ResponseEntity<>(natures,  new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/natures/{natureId}")
    public ResponseEntity<Object> getNatureById(@PathVariable(name = "natureId", required = true) int natureId) {
        Nature nature = natureService.getNatureById(natureId);
        return  new ResponseEntity<>(nature,  new HttpHeaders(), HttpStatus.OK);
    }

    @PostMapping("/natures")
    public ResponseEntity<Object> createNature(@RequestBody @Valid Nature nature) {
        Nature createdNature = natureService.createNature(nature);
        return new ResponseEntity<>(createdNature, new HttpHeaders(), HttpStatus.CREATED);
    }

    @PutMapping("/natures/{natureId}")
    public ResponseEntity<Object> updateNature(@PathVariable(name = "natureId") int natureId, @RequestBody @Valid Nature nature) {
        Nature updatedNature = natureService.updateNature(natureId, nature);
        return new ResponseEntity<>(updatedNature, new HttpHeaders(), HttpStatus.OK);
    }

    @DeleteMapping("/natures/{natureId}")
    public ResponseEntity<Object> deleteNature(@PathVariable(name = "natureId") int natureId) {
        Boolean isNatureDeleted = natureService.deleteNature(natureId);
        return new ResponseEntity<>(isNatureDeleted, new HttpHeaders(), HttpStatus.OK);
    }
}
