package de.unirostock.swt23.montag2.elearnsys.service;

import de.unirostock.swt23.montag2.elearnsys.exception.RegistrationException;
import de.unirostock.swt23.montag2.elearnsys.exception.UserNotFoundException;
import de.unirostock.swt23.montag2.elearnsys.model.User;
import de.unirostock.swt23.montag2.elearnsys.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(User user) {
        // Check if the username or email is already taken
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RegistrationException("Username is already taken");
        }

        return userRepository.save(user);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public Boolean loginUser(String username, String password) {
        // Find the user by username and test the password
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            User loginUser = user.get();
            String loginPassword = loginUser.getPassword();
            boolean isPasswordCorrect = loginPassword.equals(password);
            return isPasswordCorrect;
        } else {
            return false;
        }
    }

    public User findByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        User foundUser = null;
        if (user.isPresent()) {
            foundUser = user.get();
        } else {
            throw new UserNotFoundException("User not found");
        }
        return foundUser;
    }

    public User findById(Long id) {
        Optional<User> user = userRepository.findById(id);
        User foundUser = null;
        if (user.isPresent()) {
            foundUser = user.get();
        } else {
            throw new UserNotFoundException("User not found");
        }
        return foundUser;
    }

    public List<User> searchForUsers(String letter) {
        var l1 = userRepository.findByUsernameStartingWith(letter);
        var l2 = userRepository.findByFirstNameStartingWith(letter);
        var l3 = userRepository.findByLastNameStartingWith(letter);
        var l12 = Stream.concat(l1.stream(), l2.stream()).toList();
        var l123 = Stream.concat(l12.stream(), l3.stream()).toList();
        return l123.stream().distinct().collect(Collectors.toList());
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }
}
