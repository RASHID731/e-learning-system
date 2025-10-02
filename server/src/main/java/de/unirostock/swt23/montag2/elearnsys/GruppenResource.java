package de.unirostock.swt23.montag2.elearnsys;

import de.unirostock.swt23.montag2.elearnsys.model.AbgabeStatus;
import de.unirostock.swt23.montag2.elearnsys.model.Aufgabe;
import de.unirostock.swt23.montag2.elearnsys.model.Gruppe;
import de.unirostock.swt23.montag2.elearnsys.model.User;
import de.unirostock.swt23.montag2.elearnsys.service.AufgabeService;
import de.unirostock.swt23.montag2.elearnsys.service.GruppenService;
import de.unirostock.swt23.montag2.elearnsys.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/gruppe")
public class GruppenResource {
    private final GruppenService gruppenService;
    private final UserService userService;
    private final AufgabeService aufgabeService;

    @Autowired
    public GruppenResource(GruppenService gruppenService, UserService userService, AufgabeService aufgabeService) {
        this.gruppenService = gruppenService;
        this.userService = userService;
        this.aufgabeService = aufgabeService;
    }

    @GetMapping("/users/all/{gruppeId}")
    public ResponseEntity<List<User>> getAllUsers(@PathVariable Long gruppeId) {
        Gruppe gruppe = gruppenService.findById(gruppeId);
        List<User> list = gruppe.getMitglieder();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping("/{gruppeId}/save-drawn-components")
    public ResponseEntity<String> addNewUseCase(@PathVariable Long gruppeId, @RequestBody String loesung) {
        Gruppe gruppe = gruppenService.findById(gruppeId);
        gruppe.setLoesung(loesung);
        gruppenService.addGruppe(gruppe);
        return new ResponseEntity<>(loesung, HttpStatus.CREATED);
    }

    @GetMapping("/{gruppeId}/get-drawn-components")
    public ResponseEntity<String> getComponents(@PathVariable Long gruppeId) {
        Gruppe gruppe = gruppenService.findById(gruppeId);
        String loesung = gruppe.getLoesung();
        return new ResponseEntity<>(loesung, HttpStatus.OK);
    }

    @PostMapping("/{gruppeId}/add/user/{userId}")
    public ResponseEntity<Void> addUserToGroup(@PathVariable Long gruppeId, @PathVariable Long userId) {
        Gruppe gruppe = gruppenService.findById(gruppeId);
        User user = userService.findById(userId);
        List<User> mitglieder = gruppe.getMitglieder();
        Aufgabe aufgabe = gruppe.getAufgabe();
        List<Gruppe> gruppen = aufgabe.getGruppen();
        for (Gruppe aufgabegruppe : gruppen) {
            List<User> aufgabeusers = aufgabegruppe.getMitglieder();
            for (User aufgabeuser : aufgabeusers) {
                if (Objects.equals(aufgabeuser.getId(), user.getId())) {
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            }
        }
        if (gruppe.getAnzahl() < gruppe.getMaxAnzahl()) {
            mitglieder.add(user);
            userService.saveUser(user);
            gruppe.setAnzahl(gruppe.getAnzahl() + 1);
            gruppenService.addGruppe(gruppe);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @PostMapping("/{gruppeId1}/changeto/{gruppeId2}/user/{userId}")
    public ResponseEntity<Void> changeGroup(@PathVariable long gruppeId1, @PathVariable long gruppeId2, @PathVariable long userId) {
        var g1 = gruppenService.findById(gruppeId1);
        var g2 = gruppenService.findById(gruppeId2);
        var u = userService.findById(userId);
        if (g2.getStatus() == AbgabeStatus.NICHT_ABGEGEBEN && g1.getStatus() == AbgabeStatus.NICHT_ABGEGEBEN) {
            var m1 = g1.getMitglieder();
            var m2 = g2.getMitglieder();
            m1.remove(u);
            m2.add(u);
            g1.setMitglieder(m1);
            g2.setMitglieder(m2);
            g2.setAnzahl(g2.getAnzahl() + 1);
            g1.setAnzahl(g1.getAnzahl() - 1);
            gruppenService.addGruppe(g1);
            gruppenService.addGruppe(g2);
            userService.saveUser(u);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else if (g2.getStatus() == AbgabeStatus.ABGEGEBEN)
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        else if (g1.getStatus() == AbgabeStatus.ABGEGEBEN)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @PostMapping("/{gruppeId}/abgabe/toggle")
    public ResponseEntity<Void> changeGruppeAbgabeStatus(@PathVariable Long gruppeId) {
        var gruppe = gruppenService.findById(gruppeId);
        switch (gruppe.getStatus()) {
            case ABGEGEBEN -> gruppe.setStatus(AbgabeStatus.NICHT_ABGEGEBEN);
            case NICHT_ABGEGEBEN -> gruppe.setStatus(AbgabeStatus.ABGEGEBEN);
        }
        gruppenService.addGruppe(gruppe);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{gruppeId}/delete/user/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId, @PathVariable Long gruppeId) {
        Gruppe gruppe = gruppenService.findById(gruppeId);
        List<User> users = gruppe.getMitglieder();
        users.removeIf(user -> Objects.equals(user.getId(), userId));
        gruppe.setMitglieder(users);
        gruppe.setAnzahl(gruppe.getAnzahl() - 1);
        gruppenService.addGruppe(gruppe);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/aufgabe/find/{aufgabeId}/user/find/{userId}")
    public ResponseEntity<Gruppe> findGruppe(@PathVariable Long aufgabeId, @PathVariable Long userId) {
        Aufgabe aufgabe = aufgabeService.findById(aufgabeId);
        List<Gruppe> gruppen = aufgabe.getGruppen();
        for (Gruppe gruppe : gruppen) {
            List<User> users = gruppe.getMitglieder();
            for (User user : users) {
                if (Objects.equals(user.getId(), userId)) {
                    return new ResponseEntity<>(gruppe, HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{gruppeId}/note/{note}")
    public ResponseEntity<Void> postNote(@PathVariable String note, @PathVariable long gruppeId) {
        var gruppe = gruppenService.findById(gruppeId);
        gruppe.setNote(note);
        gruppenService.addGruppe(gruppe);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
