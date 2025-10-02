package de.unirostock.swt23.montag2.elearnsys.repo;

import de.unirostock.swt23.montag2.elearnsys.model.Aufgabe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AufgabeRepository extends JpaRepository<Aufgabe, Long> {
    Optional<Aufgabe> findById(Long id);

    boolean existsByName(String name);
}