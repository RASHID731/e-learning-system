package com.example.javafxdemo.controller;

import com.example.javafxdemo.GUIApplication;
import com.example.javafxdemo.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

/**
 * Controller class for managing the AufgabePage view.
 */
public class AufgabePageController {

    public TextArea aufgabenBeschreibung;
    public Text Name;
    public Text Start;
    public Text Ende;
    public Text Anzahl;
    public Text MaxAnzahl;
    public Button aufgabeBearbeitenButton;
    private final SceneController sceneController = new SceneController();
    @FXML
    private Button abgebenButton;

    /**
     * Handles the back action to AufgabenPage.
     *
     * @param event The ActionEvent object containing information about the event.
     * @throws IOException If an I/O error occurs.
     */
    public void back(ActionEvent event) throws IOException {
        GUIApplication.aufgabeId = null;
        GUIApplication.aufgabeName = null;
        GUIApplication.aufgabeAuthor = null;
        sceneController.switchtoAufgabenPage(event);
    }

    /**
     * Initializes the controller.
     * GET Request to find the aufgabe with the aufgabeId
     * GET Request to determine the group to which the current logged-in user belongs to, if not then disable gruppe bearbeiten button
     * Populate the data of aufgabe object in the fxml file
     *
     * @throws IOException If an I/O error occurs.
     */
    @FXML
    public void initialize() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // GET Request to find the author with the username, and pull the object from the database
        URL url = new URL("http://" + GUIApplication.URL + ":8080/aufgabe/find/" + GUIApplication.aufgabeId);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        // Read the response from the input stream
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        Aufgabe aufgabe = objectMapper.readValue(reader, Aufgabe.class);
        System.out.println(aufgabe.getName());

        connection.disconnect();

        // GET Request to determine the group to which the current logged-in user belongs to, if not then disable aufgabe bearbeiten button
        URL url1 = new URL("http://" + GUIApplication.URL + ":8080/gruppe/aufgabe/find/" + GUIApplication.aufgabeId + "/user/find/" + GUIApplication.loggedInId);
        HttpURLConnection connection1 = (HttpURLConnection) url1.openConnection();
        connection1.setRequestMethod("GET");

        int responseCode1 = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode1);

        // Read the response from the input stream
        BufferedReader reader1 = new BufferedReader(new InputStreamReader(connection1.getInputStream()));

        try {
            Gruppe gruppe = objectMapper.readValue(reader1, Gruppe.class);
            GUIApplication.gruppeId = gruppe.getId();
            System.out.println("Gruppe Id:" + gruppe.getId());

            if (gruppe.getStatus() == AbgabeStatus.ABGEGEBEN) {
                aufgabeBearbeitenButton.setDisable(true);
                if (!Objects.equals(gruppe.getNote(), "-")) {
                    abgebenButton.setDisable(true);
                }
            }

            switch (gruppe.getStatus()) {
                case ABGEGEBEN -> abgebenButton.setText("Abgabe zurücksetzen");
                case NICHT_ABGEGEBEN -> abgebenButton.setText("Abgeben");
            }

        } catch (IOException e) {
            aufgabeBearbeitenButton.setDisable(true);
            abgebenButton.setDisable(true);
        }

        if (GUIApplication.aufgabeStatus != AufgabeStatus.BETWEEN) {
            aufgabeBearbeitenButton.setDisable(true);
            abgebenButton.setDisable(true);
        }

        connection1.disconnect();

        aufgabenBeschreibung.setText(aufgabe.getAufgabenStellung());
        Name.setText(aufgabe.getName());
        Start.setText(aufgabe.getStartDate().toString());
        Ende.setText(aufgabe.getEndDate().toString());
        Anzahl.setText(String.valueOf(aufgabe.getGroupCount()));
        MaxAnzahl.setText(String.valueOf(aufgabe.getMaxNumber()));

        GUIApplication.maxNumber = aufgabe.getMaxNumber();
    }

    /**
     * Opens the Use Case Editor.
     *
     * @param event The ActionEvent object containing information about the event.
     * @throws IOException If an I/O error occurs.
     */
    public void openEditor(ActionEvent event) throws IOException {
        sceneController.switchtoUseCaseEditor(event);
    }

    /**
     * Opens the GruppenPage.
     *
     * @param event The ActionEvent object containing information about the event.
     * @throws IOException If an I/O error occurs.
     */
    public void openGruppen(ActionEvent event) throws IOException {
        sceneController.switchtoGruppenPage(event);
    }

    /**
     * Handles the abgeben action with POST Request to Server.
     * Switches the text name of the button
     *
     * @param actionEvent The ActionEvent object containing information about the event.
     * @throws IOException If an I/O error occurs.
     */
    @FXML
    public void abgeben(ActionEvent actionEvent) throws IOException {
        URL url = new URL("http://" + GUIApplication.URL + ":8080/gruppe/" + GUIApplication.gruppeId + "/abgabe/toggle");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");

        // Check the response code
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            System.out.println("Abgabe aktualisert: "+responseCode);
        } else {
            // User login failed
            System.out.println("Abgabestatus not updated");
            System.out.println(responseCode);
            new Alert(Alert.AlertType.ERROR,"Abgabestatus konnte nicht aktualisiert werden.").show();
        }

        connection.disconnect();
        switch (abgebenButton.getText()) {
            case "Abgeben" -> {
                abgebenButton.setText("Abgabe zurücksetzen");
                aufgabeBearbeitenButton.setDisable(true);
            }
            case "Abgabe zurücksetzen" -> {
                abgebenButton.setText("Abgeben");
                aufgabeBearbeitenButton.setDisable(false);
            }
        }

    }
}
