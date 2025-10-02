//package com.example.javafxdemo.controller;
//
//import javafx.event.ActionEvent;
//import javafx.scene.control.Label;
//import javafx.scene.control.PasswordField;
//import javafx.scene.control.TextField;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//
//import java.io.IOException;
//
//public class RegisterControllerTest {
//    private RegisterController registerController;
//
//    @Mock
//    private TextField usernameField;
//    @Mock
//    private Label errorMessageLabel;
//    @Mock
//    private TextField firstNameField;
//    @Mock
//    private TextField lastNameField;
//    @Mock
//    private PasswordField passwordField;
//    @Mock
//    private Label errorTextField;
//    @Mock
//    private PasswordField confirmPasswordField;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.initMocks(this);
//        registerController = new RegisterController();
//        registerController.usernameField = usernameField;
//        registerController.errorMessageLabel = errorMessageLabel;
//        registerController.firstNameField = firstNameField;
//        registerController.lastNameField = lastNameField;
//        registerController.passwordField = passwordField;
//        registerController.errorTextField = errorTextField;
//        registerController.confirmPasswordField = confirmPasswordField;
//    }
//
//    @Test
//    public void testRegisterTheUser_SuccessfulRegistration() throws IOException {
//        // Mock form input values
//        String username = "testuser";
//        String firstName = "John";
//        String lastName = "Doe";
//        String password = "password";
//        String confirmPassword = "password";
//
//        // Set up mockito stubs
//        Mockito.when(usernameField.getText()).thenReturn(username);
//        Mockito.when(firstNameField.getText()).thenReturn(firstName);
//        Mockito.when(lastNameField.getText()).thenReturn(lastName);
//        Mockito.when(passwordField.getText()).thenReturn(password);
//        Mockito.when(confirmPasswordField.getText()).thenReturn(confirmPassword);
//
//        // Invoke the method under test
//        ActionEvent event = Mockito.mock(ActionEvent.class);
//        registerController.registerTheUser(event);
//
//        // Verify the expected behavior
//        // TODO: Add your verification logic here based on the behavior of successful registration
//        // For example, you can assert that the scene is switched to the AufgabenPage
//    }
//
//    @Test
//    public void testRegisterTheUser_PasswordMismatch() throws IOException {
//        // Mock form input values with password mismatch
//        String username = "testuser";
//        String firstName = "John";
//        String lastName = "Doe";
//        String password = "password";
//        String confirmPassword = "differentpassword";
//
//        // Set up mockito stubs
//        Mockito.when(usernameField.getText()).thenReturn(username);
//        Mockito.when(firstNameField.getText()).thenReturn(firstName);
//        Mockito.when(lastNameField.getText()).thenReturn(lastName);
//        Mockito.when(passwordField.getText()).thenReturn(password);
//        Mockito.when(confirmPasswordField.getText()).thenReturn(confirmPassword);
//
//        // Invoke the method under test
//        ActionEvent event = Mockito.mock(ActionEvent.class);
//        registerController.registerTheUser(event);
//
//        // Verify the expected behavior
//        // TODO: Add your verification logic here based on the behavior of password mismatch
//        // For example, you can assert that the errorTextField displays the correct error message
//    }
//
//    // Add more test cases as needed to cover different scenarios
//
//}
