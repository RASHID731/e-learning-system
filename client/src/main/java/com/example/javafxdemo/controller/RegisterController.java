package com.example.javafxdemo.controller;

import com.example.javafxdemo.GUIApplication;
import com.example.javafxdemo.model.User;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

/**
 * Controller class for managing the Register view.
 */
public class RegisterController implements Initializable {

    @FXML
    public TextField usernameField;
    @FXML
    public Label errorMessageLabel;
    @FXML
    public TextField firstNameField;

    @FXML
    public TextField lastNameField;

    @FXML
    public PasswordField passwordField;

    @FXML
    public PasswordField confirmPasswordField;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final SceneController sceneController = new SceneController();

    /**
     * Handles the registration of a new user by sending
     * a POST request to the server, validating the inputs,
     * and handling different response scenarios.
     *
     * @param event
     * @throws IOException
     */
    public void registerTheUser(ActionEvent event) throws IOException {
        String username = usernameField.getText();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION,"Bitte alles ausfüllen.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        // Validate form inputs
        if (!password.equals(confirmPassword)) {
            // Handle password mismatch error
            errorMessageLabel.setManaged(true);
            errorMessageLabel.setText("Das wiederholte Passwort stimmt nicht überein.");
            return;
        }

        // Create a new user entity
        User user = new User(username, firstName, lastName, password);

        // Save the user in the database
        // Convert user object to JSON string
        String userJson = objectMapper.writeValueAsString(user);

        // Set up the connection
        URL url = new URL("http://" + GUIApplication.URL + ":8080/user/add");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Send the request
        try (OutputStream outputStream = connection.getOutputStream()) {
            byte[] input = userJson.getBytes(StandardCharsets.UTF_8);
            outputStream.write(input, 0, input.length);
        }

        // Check the response code
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_CREATED) {
            // Handle the successful registration
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            User newUser = objectMapper.readValue(reader, User.class);
            GUIApplication.loggedInUsername = username;
            GUIApplication.loggedInId = newUser.getId();
            sceneController.switchtoAufgabenPage(event);
        } else if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
            // Handle the case where the username is already taken
            errorMessageLabel.setManaged(true);
            errorMessageLabel.setText("Der Benutzername ist bereits vergeben.");
            //System.out.println("Der Benutzername ist bereits vergeben.");
            // Hier können Sie das Textfeld anzeigen oder eine entsprechende Benachrichtigung anzeigen.
        } else {
            // Handle the error case
            System.out.println(responseCode);
        }

        connection.disconnect();
    }

    /**
     * Navigates back to the "Login" page.
     *
     * @param event
     * @throws IOException
     */
    public void back(ActionEvent event) throws IOException {
        sceneController.switchtoLoginPage(event);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        errorMessageLabel.setManaged(false);
    }
}

