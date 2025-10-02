package de.unirostock.swt23.montag2.elearnsys;

import de.unirostock.swt23.montag2.elearnsys.DTO.AufgabeDTO;
import de.unirostock.swt23.montag2.elearnsys.model.AbgabeStatus;
import de.unirostock.swt23.montag2.elearnsys.model.*;
import de.unirostock.swt23.montag2.elearnsys.service.AufgabeService;
import de.unirostock.swt23.montag2.elearnsys.service.GruppenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/aufgabe")
public class AufgabeResource {
    private final AufgabeService aufgabeService;
    private final GruppenService gruppenService;

    @Autowired
    public AufgabeResource(AufgabeService aufgabeService, GruppenService gruppenService) {
        this.aufgabeService = aufgabeService;
        this.gruppenService = gruppenService;
    }

    @PostMapping("/add")
    public ResponseEntity<Aufgabe> addNewAufgabe(@RequestBody AufgabeDTO aufgabeDTO) {
        Aufgabe aufgabe = new Aufgabe();
        aufgabe.setName(aufgabeDTO.getName());
        aufgabe.setAuthor(aufgabeDTO.getAuthor());
        aufgabe.setId(aufgabeDTO.getId());
        aufgabe.setStartDate(aufgabeDTO.getStartDate());
        aufgabe.setEndDate(aufgabeDTO.getEndDate());
        aufgabe.setStatus(aufgabeDTO.getStatus());
        aufgabe.setAufgabenStellung(aufgabeDTO.getAufgabenStellung());
        aufgabe.setGroupCount(aufgabeDTO.getGroupCount());
        aufgabe.setMaxNumber(aufgabeDTO.getMaxNumber());
        aufgabe.setFreieEinschreibung(aufgabeDTO.getFreieEinschreibung());

        List<Gruppe> list = aufgabeDTO.getGruppen();

        if (list == null) {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }

        for (Gruppe gruppe : list) {
            gruppe.setAufgabe(aufgabe);
        }
        aufgabe.setGruppen(list);

        Aufgabe newAufgabe = aufgabeService.addAufgabe(aufgabe);

        for (Gruppe gruppe : list) {
            gruppenService.addGruppe(gruppe);
        }
        return new ResponseEntity<>(newAufgabe, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Aufgabe>> getAllAufgaben() {
        List<Aufgabe> list = aufgabeService.allAufgaben();
        for (var a : list) {
            gruppenService.updateStatus(a);
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/{id}/gruppen/all")
    public ResponseEntity<List<Gruppe>> getAllGruppen(@PathVariable Long id) {
        Aufgabe aufgabe = aufgabeService.findById(id);
        List<Gruppe> list = aufgabe.getGruppen();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<Aufgabe> findTheAufgabe(@PathVariable Long id) {
        Aufgabe foundAufgabe = aufgabeService.findById(id);
        return new ResponseEntity<>(foundAufgabe, HttpStatus.OK);
    }

    @PostMapping("/{aufgabeId}/abgabe/toggle")
    public ResponseEntity<Void> changeAufgabeFreieEinschreibung(@PathVariable Long aufgabeId) {
        var aufgabe = aufgabeService.findById(aufgabeId);
        aufgabe.setFreieEinschreibung(!aufgabe.getFreieEinschreibung());
        aufgabeService.updateAufgabe(aufgabe);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
