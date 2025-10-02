package de.unirostock.swt23.montag2.elearnsys.service;

import de.unirostock.swt23.montag2.elearnsys.exception.AufgabeNotFoundException;
import de.unirostock.swt23.montag2.elearnsys.model.AbgabeStatus;
import de.unirostock.swt23.montag2.elearnsys.model.Aufgabe;
import de.unirostock.swt23.montag2.elearnsys.model.AufgabeStatus;
import de.unirostock.swt23.montag2.elearnsys.model.Gruppe;
import de.unirostock.swt23.montag2.elearnsys.repo.GruppenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class GruppenService {
    private final GruppenRepository gruppenRepository;

    @Autowired
    public GruppenService(GruppenRepository gruppenRepository) {
        this.gruppenRepository = gruppenRepository;
    }

    public Gruppe addGruppe(Gruppe gruppe) {
        return gruppenRepository.save(gruppe);
    }

    public Gruppe findById(Long id) {
        Optional<Gruppe> gruppe = gruppenRepository.findById(id);
        Gruppe foundGruppe = null;
        if (gruppe.isPresent()) {
            foundGruppe = gruppe.get();
        } else {
            throw new AufgabeNotFoundException("Gruppe not found");
        }
        return foundGruppe;
    }
    public void updateStatus(Aufgabe a) {
        LocalDate now = LocalDate.now();

        if (now.isBefore(a.getStartDate())) {
            a.setStatus(AufgabeStatus.BEFORE);
        } else if (now.isAfter(a.getEndDate())) {
            for (var g : a.getGruppen()) {
                if (!Objects.equals(g.getLoesung(), "")) {
                    g.setStatus(AbgabeStatus.ABGEGEBEN);
                    gruppenRepository.save(g);
                }
            }
            a.setStatus(AufgabeStatus.AFTER);
        } else {
            a.setStatus(AufgabeStatus.BETWEEN);
        }
    }

}
