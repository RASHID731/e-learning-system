package de.unirostock.swt23.montag2.elearnsys.repo;

import de.unirostock.swt23.montag2.elearnsys.model.Gruppe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GruppenRepository extends JpaRepository<Gruppe, Long> {
    @Override
    Optional<Gruppe> findById(Long id);
}
