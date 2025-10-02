package de.unirostock.swt23.montag2.elearnsys;

import de.unirostock.swt23.montag2.elearnsys.model.User;
import de.unirostock.swt23.montag2.elearnsys.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserResource {
    private final UserService userService;

    public UserResource(UserService userService) { this.userService = userService; }

    @PostMapping("/add")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User newUser = userService.registerUser(user);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @PostMapping("/login/{username}/{password}")
    public ResponseEntity<User> loginUser(@PathVariable String username, @PathVariable String password) {
        boolean isLoginSuccessful = userService.loginUser(username, password);
        User user = userService.findByUsername(username);
        if (isLoginSuccessful) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<User> findTheUser(@PathVariable String username) {
        User foundUser = userService.findByUsername(username);
        return new ResponseEntity<>(foundUser, HttpStatus.OK);
    }

    @GetMapping("/search/{letter}")
    public ResponseEntity<List<User>> searchForUsers(@PathVariable String letter) {
        List<User> list = userService.searchForUsers(letter);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> allUsers() {
        List<User> list = userService.getAll();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

}
