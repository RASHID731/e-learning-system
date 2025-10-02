package com.example.javafxdemo.controller;

import com.example.javafxdemo.GUIApplication;
import com.example.javafxdemo.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller class for managing the Login view.
 */
@Controller
public class LoginController implements Initializable {
    @FXML
    private TextField usernameField;
    @FXML
    private Label errorMessageLabel;

    @FXML
    private PasswordField passwordField;

    @FXML
    public Button loginButton;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    SceneController sceneController = new SceneController();
    private URL LoginURL;

    /**
     * Navigates back to the "Register" page.
     *
     * @param event
     * @throws IOException
     */
    public void register(ActionEvent event) throws IOException {
        sceneController.switchtoRegisterPage(event);
    }

    /**
     * Handles the logging in of a user. It sends a POST request to
     * the server with the provided username and password,
     * handles the response, and switches the scene to the "Aufgaben" page
     * if the login is successful.
     *
     * @param event
     * @throws IOException
     */
    public void login(ActionEvent event) throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        URL url = new URL("http://" + GUIApplication.URL + ":8080/user/login/" + username + "/" + password);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");

        // Check the response code
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            //check the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            User user = objectMapper.readValue(reader, User.class);
            GUIApplication.loggedInUsername = username;
            GUIApplication.loggedInId = user.getId();
            sceneController.switchtoAufgabenPage(event);
            System.out.println(connection.getResponseCode());
        } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            // User login failed
            errorMessageLabel.setManaged(true);
            errorMessageLabel.setText("Falsches Passwort. Bitte versuchen Sie es erneut.");
            System.out.println("Login did not work");
            System.out.println(connection.getResponseCode());
        } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
            // User login failed
            errorMessageLabel.setManaged(true);
            errorMessageLabel.setText("Bitte geben Sie Username und Password ein.");
            System.out.println("Login did not work");
            System.out.println(connection.getResponseCode());
        } else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST || responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
            errorMessageLabel.setManaged(true);
            errorMessageLabel.setText("Die Angaben sind falsch.");
            System.out.println(connection.getResponseCode());
            // Hier können Sie das Textfeld anzeigen oder eine entsprechende Benachrichtigung anzeigen.
        }else {
            // User login failed
            System.out.println("Login did not work");
            System.out.println(connection.getResponseCode());
        }

        connection.disconnect();
    }

    /**
     * Hides the error message label at the start.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        errorMessageLabel.setManaged(false);
    }

    public void setLoginURL(URL url) {
        this.LoginURL = url;
    }

    public Label getErrorMessageLabel() {
        return  errorMessageLabel;
    }
}

