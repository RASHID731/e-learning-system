//package com.example.javafxdemo.controller;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//
//import java.io.IOException;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//public class LoginControllerTest {
//    private LoginController loginController;
//
//    @BeforeEach
//    public void setup() {
//        loginController = new LoginController();
//    }
//
//    @Test
//    public void testSuccessfulLogin() throws IOException {
//        // Mocking the HttpURLConnection to simulate a successful login response
//        HttpURLConnection connection = Mockito.mock(HttpURLConnection.class);
//        Mockito.when(connection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
//        Mockito.when(connection.getInputStream()).thenReturn(null);
//
//        // Mocking the URL and opening the connection
//        URL url = Mockito.mock(URL.class);
//        Mockito.when(url.openConnection()).thenReturn(connection);
//
//        // Set the mocked URL to the loginController
//        loginController.setLoginURL(url);
//
//        // Test the login() method
//        loginController.login(null);
//
//        // Assertions
//        // Verify that the expected methods were called
//        Mockito.verify(connection, Mockito.times(1)).setRequestMethod("POST");
//        Mockito.verify(connection, Mockito.times(1)).setRequestProperty("Content-Type", "application/json");
//        Mockito.verify(connection, Mockito.times(1)).getResponseCode();
//        Mockito.verify(connection, Mockito.times(1)).disconnect();
//
//        // Assert that the error message label is empty
//        Assertions.assertEquals("", loginController.getErrorMessageLabel().getText());
//    }
//
//    @Test
//    public void testFailedLogin_Unauthorized() throws IOException {
//        // Mocking the HttpURLConnection to simulate an unauthorized login response
//        HttpURLConnection connection = Mockito.mock(HttpURLConnection.class);
//        Mockito.when(connection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_UNAUTHORIZED);
//        Mockito.when(connection.getInputStream()).thenReturn(null);
//
//        // Mocking the URL and opening the connection
//        URL url = Mockito.mock(URL.class);
//        Mockito.when(url.openConnection()).thenReturn(connection);
//
//        // Set the mocked URL to the loginController
//        loginController.setLoginURL(url);
//
//        // Test the login() method
//        loginController.login(null);
//
//        // Assertions
//        // Verify that the expected methods were called
//        Mockito.verify(connection, Mockito.times(1)).setRequestMethod("POST");
//        Mockito.verify(connection, Mockito.times(1)).setRequestProperty("Content-Type", "application/json");
//        Mockito.verify(connection, Mockito.times(1)).getResponseCode();
//        Mockito.verify(connection, Mockito.times(1)).disconnect();
//
//        // Assert the error message label
//        Assertions.assertEquals("Falsches Passwort. Bitte versuchen Sie es erneut.", loginController.getErrorMessageLabel().getText());
//    }
//
//    @Test
//    public void testFailedLogin_NotFound() throws IOException {
//        // Mocking the HttpURLConnection to simulate a not found login response
//        HttpURLConnection connection = Mockito.mock(HttpURLConnection.class);
//        Mockito.when(connection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_NOT_FOUND);
//        Mockito.when(connection.getInputStream()).thenReturn(null);
//
//        // Mocking the URL and opening the connection
//        URL url = Mockito.mock(URL.class);
//        Mockito.when(url.openConnection()).thenReturn(connection);
//
//        // Set the mocked URL to the loginController
//        loginController.setLoginURL(url);
//
//        // Test the login() method
//        loginController.login(null);
//
//        // Assertions
//        // Verify that the expected methods were called
//        Mockito.verify(connection, Mockito.times(1)).setRequestMethod("POST");
//        Mockito.verify(connection, Mockito.times(1)).setRequestProperty("Content-Type", "application/json");
//        Mockito.verify(connection, Mockito.times(1)).getResponseCode();
//        Mockito.verify(connection, Mockito.times(1)).disconnect();
//
//        // Assert the error message label
//        Assertions.assertEquals("Bitte geben Sie Username und Password ein.", loginController.getErrorMessageLabel().getText());
//    }
//
//    // You can add more test methods for different scenarios (invalid URL, server error, etc.)
//}
