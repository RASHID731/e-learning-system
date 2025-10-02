package de.unirostock.swt23.montag2.elearnsys.service;

import de.unirostock.swt23.montag2.elearnsys.exception.AufgabeNotFoundException;
import de.unirostock.swt23.montag2.elearnsys.exception.RegistrationException;
import de.unirostock.swt23.montag2.elearnsys.model.Aufgabe;
import de.unirostock.swt23.montag2.elearnsys.repo.AufgabeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AufgabeService {
    private final AufgabeRepository aufgabeRepository;

    @Autowired
    public AufgabeService(AufgabeRepository aufgabeRepository) {
        this.aufgabeRepository = aufgabeRepository;
    }

    public Aufgabe addAufgabe(Aufgabe aufgabe) {
        // Check if the username or email is already taken
        if (aufgabeRepository.existsByName(aufgabe.getName())) {
            throw new RegistrationException("Name is already taken");
        }

        return aufgabeRepository.save(aufgabe);
    }

    public Aufgabe updateAufgabe(Aufgabe aufgabe) {
        return aufgabeRepository.save(aufgabe);
    }

    public List<Aufgabe> allAufgaben() {
        return aufgabeRepository.findAll();
    }

    public Aufgabe findById(Long id) {
        Optional<Aufgabe> aufgabe = aufgabeRepository.findById(id);
        Aufgabe foundAufgabe = null;
        if (aufgabe.isPresent()) {
            foundAufgabe = aufgabe.get();
        } else {
            throw new AufgabeNotFoundException("Aufgabe not found");
        }
        return foundAufgabe;
    }
}