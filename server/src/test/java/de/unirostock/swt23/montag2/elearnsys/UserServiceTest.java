package de.unirostock.swt23.montag2.elearnsys;

import de.unirostock.swt23.montag2.elearnsys.exception.RegistrationException;
import de.unirostock.swt23.montag2.elearnsys.exception.UserNotFoundException;
import de.unirostock.swt23.montag2.elearnsys.model.User;
import de.unirostock.swt23.montag2.elearnsys.repo.UserRepository;
import de.unirostock.swt23.montag2.elearnsys.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void userRegistrationAndLoginTest() {
        // User registration
        User newUser = new User("un8822", "Rashid", "Gorkhmaz", "1111");
        when(userRepository.existsByUsername(newUser.getUsername())).thenReturn(false);
        when(userRepository.save(newUser)).thenReturn(newUser);

        User registeredUser = userService.registerUser(newUser);

        assertNotNull(registeredUser);
        assertEquals(newUser.getUsername(), registeredUser.getUsername());
        assertEquals(newUser.getFirstName(), registeredUser.getFirstName());
        assertEquals(newUser.getLastName(), registeredUser.getLastName());
        assertEquals(newUser.getPassword(), registeredUser.getPassword());

        verify(userRepository, times(1)).existsByUsername(newUser.getUsername());
        verify(userRepository, times(1)).save(newUser);

        // Modify the mock behavior for findByUsername to return the registered user
        when(userRepository.findByUsername(newUser.getUsername())).thenReturn(Optional.of(newUser));

        // User login with correct password
        boolean loginResult = userService.loginUser(newUser.getUsername(), newUser.getPassword());
        assertTrue(loginResult);

        // User login with incorrect password
        loginResult = userService.loginUser(newUser.getUsername(), "wrongpassword");
        assertFalse(loginResult);

        // User login with nonexistent user
        loginResult = userService.loginUser("nonexistentuser", "password");
        assertFalse(loginResult);
    }

    @Test(expected = RegistrationException.class)
    public void registerUserWithoutExistingUsernameTest() {
        User existingUser = new User("existinguser", "Existing", "User", "existingpassword");

        when(userRepository.existsByUsername(existingUser.getUsername())).thenReturn(true);

        userService.registerUser(existingUser);

        verify(userRepository, times(1)).existsByUsername(existingUser.getUsername());
        verify(userRepository, never()).save(existingUser);
    }

    @Test
    public void saveUserTest() {
        User user = new User("un8822", "Rashid", "Gorkhmaz", "1111");

        when(userRepository.save(user)).thenReturn(user);

        User savedUser = userService.saveUser(user);

        assertNotNull(savedUser);
        assertEquals(user.getUsername(), savedUser.getUsername());
        assertEquals(user.getFirstName(), savedUser.getFirstName());
        assertEquals(user.getLastName(), savedUser.getLastName());
        assertEquals(user.getPassword(), savedUser.getPassword());

        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void findByUsernameTest() {
        User existingUser = new User("un8822", "Rashid", "Gorkhmaz", "1111");
        existingUser.setId(1L); // Set the ID of the existing user

        when(userRepository.findByUsername("un8822")).thenReturn(Optional.of(existingUser));

        User user = userService.findByUsername("un8822");

        assertNotNull(user);
        assertEquals("un8822", user.getUsername());

        verify(userRepository, times(1)).findByUsername("un8822");
    }

    @Test(expected = UserNotFoundException.class)
    public void findByUsernameWithNonexistentUserTest() {
        userService.findByUsername("un8823");

        verify(userRepository, times(1)).findByUsername("un8823");
    }

    @Test
    public void findByIdTest() {
        User existingUser = new User("un8822", "Rashid", "Gorkhmaz", "1111");
        existingUser.setId(1L); // Set the ID of the existing user

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        User user = userService.findById(1L);

        assertNotNull(user);
        assertEquals(1L, (long) user.getId());

        verify(userRepository, times(1)).findById(1L);
    }

    @Test(expected = UserNotFoundException.class)
    public void findByIdWithNonexistentUserTest() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        userService.findById(2L);

        verify(userRepository, times(1)).findById(2L);
    }

    @Test
    public void searchForUsersTest() {
        User user1 = new User("un8822", "Rashid", "Gorkhmaz", "1111");
        user1.setId(1L); // Set the ID of the existing user

        User user2 = new User("un8823", "Vahid", "Gorkhmaz", "1111");
        user2.setId(1L); // Set the ID of the existing user

        List<User> searchResults = Arrays.asList(user1, user2);

        when(userRepository.findByUsernameStartingWith("u")).thenReturn(searchResults);
        when(userRepository.findByFirstNameStartingWith("u")).thenReturn(searchResults);
        when(userRepository.findByLastNameStartingWith("u")).thenReturn(searchResults);

        List<User> result = userService.searchForUsers("u");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(user1));
        assertTrue(result.contains(user2));

        verify(userRepository, times(1)).findByUsernameStartingWith("u");
        verify(userRepository, times(1)).findByFirstNameStartingWith("u");
        verify(userRepository, times(1)).findByLastNameStartingWith("u");
    }

    @Test
    public void getAllTest() {
        User user1 = new User("un8822", "Rashid", "Gorkhmaz", "1111");
        user1.setId(1L); // Set the ID of the existing user

        User user2 = new User("un8823", "Vahid", "Gorkhmaz", "1111");
        user2.setId(2L); // Set the ID of the existing user

        List<User> allUsers = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(allUsers);

        List<User> result = userService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(user1));
        assertTrue(result.contains(user2));

        verify(userRepository, times(1)).findAll();
    }
}
