package com.example.javafxdemo.controller;

import com.example.javafxdemo.GUIApplication;
import com.example.javafxdemo.model.Aufgabe;
import com.example.javafxdemo.model.AufgabeStatus;
import com.example.javafxdemo.model.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import org.springframework.stereotype.Controller;

import javafx.fxml.FXML;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller class for managing the Aufgaben view.
 */
@Controller
public class AufgabenController {

    @FXML
    private TableView<Aufgabe> aufgabenTable;

    @FXML
    private Button reloadButton;

    @FXML
    private Button aufgabeLaden;

    @FXML
    private Label loggedInUserLabel;

    private final SceneController sceneController = new SceneController();

    /**
     * Handles the logout action.
     *
     * @param event The ActionEvent object containing information about the event.
     * @throws IOException If an I/O error occurs.
     */
    public void logout(ActionEvent event) throws IOException {
        GUIApplication.loggedInUsername = null;
        sceneController.switchtoLoginPage(event);
    }

    /**
     * Handles the creation of a new Aufgabe.
     *
     * @param event The ActionEvent object containing information about the event.
     * @throws IOException If an I/O error occurs.
     */
    public void newaufgabe(ActionEvent event) throws IOException {
        sceneController.switchtoNewAufgabePage(event);
    }

    /**
     * Initializes the controller.
     * Sends GET Request to find the author with the username, and pull the object from the database
     * Changes the text of label to show the name of logged in person
     * Gets all the Aufgaben with getAufgaben() and populates the data in the tableview
     * Sets up a listener on the table to save data on a any particular for further use, such as AufgabePageView
     *
     * @throws IOException If an I/O error occurs.
     */
    @FXML
    private void initialize() throws IOException {
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
        User loggedInUser = objectMapper.readValue(reader, User.class);
        loggedInUserLabel.setText("eingeloggt als:\n" + loggedInUser.getFirstName() + " " + loggedInUser.getLastName());

        TableColumn<Aufgabe, Long> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> new SimpleObjectProperty(cellData.getValue().getId()));
        idColumn.setVisible(false);

        TableColumn<Aufgabe, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        TableColumn<Aufgabe, LocalDate> startDateColumn = new TableColumn<>("Startdatum");
        startDateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getStartDate()));

        TableColumn<Aufgabe, LocalDate> endDateColumn = new TableColumn<>("Endedatum");
        endDateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getEndDate()));

        TableColumn<Aufgabe, AufgabeStatus> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getStatus()));

        TableColumn<Aufgabe, String> authorColumn = new TableColumn<>("Autor");
        authorColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getAuthor().getName()));

        TableColumn<Aufgabe, Integer> groupCountColumn = new TableColumn<>("Gruppen");
        groupCountColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getGroupCount()));

        aufgabeLaden.setDisable(true);

        aufgabenTable.setRowFactory(tv -> {
            TableRow<Aufgabe> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (! row.isEmpty()) ) {
                    Aufgabe rowData = row.getItem();
                    aufgabeLaden.setDisable(false);
                    GUIApplication.aufgabeId = rowData.getId();
                    GUIApplication.aufgabeName = rowData.getName();
                    GUIApplication.aufgabeAuthor = rowData.getAuthor().getUsername();
                    GUIApplication.aufgabeStatus = rowData.getStatus();
                }
            });
            return row ;
        });

        aufgabenTable.getColumns().addAll(idColumn, nameColumn, startDateColumn, endDateColumn, statusColumn, authorColumn, groupCountColumn);
        aufgabenTable.setItems(getAufgaben());
    }

    /**
     * Retrieves a list of Aufgaben.
     *
     * @return The ObservableList of Aufgabe objects.
     * @throws IOException If an I/O error occurs.
     */
    public ObservableList<Aufgabe> getAufgaben() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // GET Request to find the author with the username, and pull the object from the database
        URL url = new URL("http://" + GUIApplication.URL + ":8080/aufgabe/all");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        // Read the response from the input stream
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        List<Aufgabe> list = objectMapper.readValue(reader, new TypeReference<>() {});

        ObservableList<Aufgabe> aufgaben = FXCollections.observableArrayList();
        for (Aufgabe aufgabe : list) {
            aufgaben.add(new Aufgabe(aufgabe.getId(),
                    aufgabe.getName(),
                    aufgabe.getStartDate(),
                    aufgabe.getEndDate(),
                    aufgabe.getAuthor(),
                    aufgabe.getGroupCount(),
                    aufgabe.getStatus()));
        }

        return aufgaben;

    }

    /**
     * Handles the aufgabepage action.
     *
     * @param event The ActionEvent object containing information about the event.
     * @throws IOException If an I/O error occurs.
     */
    public void aufgabepage(ActionEvent event) throws IOException {
        sceneController.switchtoAufgabePage(event);
    }
}
