package de.unirostock.swt23.montag2.elearnsys.repo;

import de.unirostock.swt23.montag2.elearnsys.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findById(Long id);

    List<User> findByUsernameStartingWith(String letter);
    List<User> findByFirstNameStartingWith(String letter);
    List<User> findByLastNameStartingWith(String letter);

    boolean existsByUsername(String username);
}
