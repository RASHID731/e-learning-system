package com.example.javafxdemo.controller;

import com.example.javafxdemo.GUIApplication;
import com.example.javafxdemo.model.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Controller class for managing the Mitglieder view.
 */
@Controller
public class MitgliederController {
    private final SceneController sceneController = new SceneController();
    @FXML
    private Label loggedInUserLabel;
    @FXML
    private TableView mitgliederTable;
    @FXML
    private Button deleteButton;
    @FXML
    private Button addButton;
    @FXML
    private Label titleTextField;

    /**
     * Navigates to the "Suche" page.
     *
     * @param event
     * @throws IOException
     */
    @FXML
    public void addStudent(ActionEvent event) throws IOException {
        sceneController.switchtoSuchePage(event);
    }

    /**
     * Performs a delete operation on the selected student within a group by
     * sending an HTTP DELETE request to the server. It handles the response and takes
     * appropriate actions accordingly.
     *
     * @param event
     * @throws IOException
     */
    @FXML
    public void deleteStudent(ActionEvent event) throws IOException {
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        alert.show();
        System.out.println(GUIApplication.gruppeIdBearbeiten);
        System.out.println(GUIApplication.userIdBearbeiten);
        URL url = new URL("http://" + GUIApplication.URL + ":8080/gruppe/" + GUIApplication.gruppeIdBearbeiten + "/delete/user/" + GUIApplication.userIdBearbeiten);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Content-Type", "application/json");

        // Check the response code
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            GUIApplication.userIdBearbeiten = null;
            sceneController.switchtoMitgliederPage(event);
            System.out.println(connection.getResponseCode());
        } else {
            // User login failed
            System.out.println("DELETE did not work");
            System.out.println(connection.getResponseCode());
        }

        connection.disconnect();
    }

    /**
     * Navigates back to the "Gruppen" page.
     *
     * @param event
     * @throws IOException
     */
    public void back(ActionEvent event) throws IOException {
        GUIApplication.gruppeIdBearbeiten = null;
        sceneController.switchtoGruppenPage(event);
    }

    /**
     * Sets up the table columns, defines the behavior of row selection,
     * retrieves user data, populates the table with users,
     * and sets the initial state of UI components based on the data
     * and settings in GUIApplication.
     *
     * @throws IOException
     */
    @FXML
    public void initialize() throws IOException {
        TableColumn<User, Long> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> new SimpleObjectProperty(cellData.getValue().getId()));
        idColumn.setVisible(false);

        TableColumn<User, String> usernameColumn = new TableColumn<>("Nutzerkürzel");
        usernameColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getUsername()));

        TableColumn<User, String> firstColumn = new TableColumn<>("Vorname");
        firstColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getFirstName()));

        TableColumn<User, String> lastColumn = new TableColumn<>("Nachname");
        lastColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getLastName()));

        mitgliederTable.setRowFactory(tv -> {
            TableRow<User> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (! row.isEmpty()) ) {
                    User rowData = row.getItem();
                    System.out.println("UserId: " + rowData.getId());
                    GUIApplication.userIdBearbeiten = rowData.getId();
                    deleteButton.setDisable(false);
                }
            });
            return row ;
        });


        mitgliederTable.getColumns().addAll(idColumn, usernameColumn, firstColumn, lastColumn);
        var users = getUsers();
        mitgliederTable.setItems(users);

        addButton.setDisable(users.size() >= GUIApplication.maxNumber);

        String name = GUIApplication.aufgabeName;
        titleTextField.setText((name.length() > 10 ? name.substring(0,10) : name) + " > G" + GUIApplication.gruppeNr + " > Mitglieder");
    }

    /**
     * Performs a GET request to retrieve user data,
     * deserializes the JSON response into a list of User objects,
     * converts it into an ObservableList, and returns the result
     * @return ObservableList of User objects
     * @throws IOException
     */
    public ObservableList<User> getUsers() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        // GET Request to find the author with the username, and pull the object from the database
        URL url = new URL("http://" + GUIApplication.URL + ":8080/gruppe/users/all/" + GUIApplication.gruppeIdBearbeiten);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        // Read the response from the input stream
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        List<User> list = objectMapper.readValue(reader, new TypeReference<>() {});

        System.out.println(list);

        ObservableList<User> users = FXCollections.observableArrayList();
        for (User user : list) {
            users.add(new User(user.getId(),
                    user.getUsername(),
                    user.getFirstName(),
                    user.getLastName()));
        }

        return users;
    }

}
