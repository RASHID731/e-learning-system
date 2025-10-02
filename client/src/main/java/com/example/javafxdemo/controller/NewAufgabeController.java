package com.example.javafxdemo.controller;

import com.example.javafxdemo.DTO.AufgabeDTO;
import com.example.javafxdemo.GUIApplication;
import com.example.javafxdemo.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller class for managing the NewAufgabe view.
 */
@Controller
public class NewAufgabeController {
    @FXML
    private TextArea aufgabenBeschreibungField;

    @FXML
    private TextField nameField;

    @FXML
    private DatePicker startField;

    @FXML
    private DatePicker endeField;

    @FXML
    private TextField anzahlField;

    @FXML
    private Label errorLabel;

    @FXML
    private TextField maxField;

    private final SceneController sceneController = new SceneController();

    /**
     * Initializes the form fields with default values,
     * such as the name, start and end dates, quantity, and maximum values.
     */
    public void initialize() {
        this.nameField.setText("Aufgabe " + System.currentTimeMillis() % 10);
        var now = LocalDate.now();
        this.startField.setValue(now);
        this.endeField.setValue(now.plusDays(1));
        this.anzahlField.setText(String.valueOf(3));
        this.maxField.setText(String.valueOf(2));
    }

    /**
     * Performs input validation, retrieves author information,
     * creates group objects, serializes the task object to JSON,
     * sends a POST request to add the task,
     * and handles the server response accordingly.
     *
     * @param event
     * @throws IOException
     */
    public void addAufgabe(ActionEvent event) throws IOException {
        String aufgabenBeschreibung = aufgabenBeschreibungField.getText();
        String name = nameField.getText();
        LocalDate start = startField.getValue();
        LocalDate ende = endeField.getValue();
        //int anzahl = Integer.parseInt(anzahlField.getText());
        //int max = Integer.parseInt(maxField.getText());

        if (name.isEmpty()) {
            errorLabel.setText("Aufgabe muss einen Namen haben.");
            errorLabel.setVisible(true);
            return;
        }

        if (start == null || ende == null) {
            errorLabel.setText("Bitte wähle ein Start- und Enddatum aus.");
            errorLabel.setVisible(true);
            return;
        }

        if (ende.isBefore(start)) {
            errorLabel.setText("Das Enddatum muss nach dem Startdatum liegen.");
            errorLabel.setVisible(true);
            return;
        }
        int anzahl;
        try {
            anzahl = Integer.parseInt(anzahlField.getText());
            if (anzahl <= 0) {
                errorLabel.setText("Anzahl der Gruppen muss positiv sein.");
                errorLabel.setVisible(true);
                return;
            }
        } catch (NumberFormatException e) {
            errorLabel.setText("Ungültige Eingabe für die Anzahl.");
            errorLabel.setVisible(true);
            return;
        }

        int max;
        try {
            max = Integer.parseInt(maxField.getText());
            if (max <= 0) {
                errorLabel.setText("Maximale Anzahl der Gruppen muss positiv sein.");
                errorLabel.setVisible(true);
                return;
            }
        } catch (NumberFormatException e) {
            errorLabel.setText("Ungültige Eingabe für die Maximalzahl.");
            errorLabel.setVisible(true);
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // GET Request to find the author with the username, and pull the object from the database
        URL url = new URL("http://" + GUIApplication.URL + ":8080/user/find/" + GUIApplication.loggedInUsername);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        // Read the response from the input stream
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        User author = objectMapper.readValue(reader, User.class);

        // POST Request to save Aufgabe in the database
        List<User> mitglieder = new ArrayList<>();
        Gruppe gruppe;
        List<Gruppe> gruppen = new ArrayList<>();

        for (int i = 0; i < anzahl; i++) {
            gruppe = new Gruppe(i+1, 0, max, mitglieder, "-", AbgabeStatus.NICHT_ABGEGEBEN, "-");
            gruppen.add(gruppe);
        }

        // set Aufgabestatus
        var status = compareDates(start, ende);

        // freieEinschreibung
        Boolean freieEinschreibung = true;

        AufgabeDTO aufgabe = new AufgabeDTO(name, start, ende, author, anzahl, max, aufgabenBeschreibung, status, gruppen, freieEinschreibung);

        String aufgabeJson = objectMapper.writeValueAsString(aufgabe);
        System.out.println(aufgabeJson);

        // Set up the connection
        url = new URL("http://" + GUIApplication.URL + ":8080/aufgabe/add");
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Send the request
        try (OutputStream outputStream = connection.getOutputStream()) {
            byte[] body = aufgabeJson.getBytes(StandardCharsets.UTF_8);
            outputStream.write(body, 0, body.length);
        }

        responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_CREATED) {
            sceneController.switchtoAufgabenPage(event);
        }
        else if (responseCode == 500) {
            new Alert(Alert.AlertType.ERROR,"Es gibt bereits eine Aufgabe mit diesem Namen.").show();
        }
        else {
            System.out.println(responseCode);
        }

        connection.disconnect();
    }

    /**
     * Navigates back to the "Aufgaben" page.
     *
     * @param event
     * @throws IOException
     */
    public void back(ActionEvent event) throws IOException {
        sceneController.switchtoAufgabenPage(event);
    }

    /**
     * Determines the status of a task based on the current date
     * in relation to its start and end dates.
     *
     * @param start start date
     * @param end end date
     * @return AufgabeStatus
     */
    public static AufgabeStatus compareDates(LocalDate start, LocalDate end) {
        LocalDate now = LocalDate.now();

        if (now.isBefore(start)) {
            return AufgabeStatus.BEFORE;
        } else if (now.isAfter(end)) {
            return AufgabeStatus.AFTER;
        } else {
            return AufgabeStatus.BETWEEN;
        }
    }
}
