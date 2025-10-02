package com.example.javafxdemo.controller;

import com.example.javafxdemo.GUIApplication;
import com.example.javafxdemo.model.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import org.springframework.stereotype.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Controller class for managing the Suche view.
 */
@Controller
public class SucheController {
    private final SceneController sceneController = new SceneController();
    @FXML
    private Label loggedInUserLabel;
    @FXML
    private TextField sucheTextField;
    @FXML
    private Button sucheButton;
    @FXML
    private Button addButton;
    @FXML
    private TableView sucheErgebnisseTable;
    @FXML
    private Label titleLabel;

    /**
     * Navigates back to Mitglieder page.
     * @param event
     * @throws IOException
     */
    public void back(ActionEvent event) throws IOException {
        sceneController.switchtoMitgliederPage(event);
    }

    /**
     * Retrieves the search results by calling the getUsers method
     * and updates the table view to display the results.
     *
     * @param actionEvent
     * @throws IOException
     */
    @FXML
    public void suchen(ActionEvent actionEvent) throws IOException {
//        sucheErgebnisseTable.getColumns().clear();
        sucheErgebnisseTable.setItems(getUsers());
    }

    /**
     * Adds the selected user to a group by making a POST request to the server
     * and handles the response accordingly.
     *
     * @param actionEvent
     * @throws IOException
     */
    @FXML
    public void addSelected(ActionEvent actionEvent) throws IOException {
        URL url = new URL("http://" + GUIApplication.URL + ":8080/gruppe/" + GUIApplication.gruppeIdBearbeiten + "/add/user/" + GUIApplication.userIdBearbeiten);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");

        // Check the response code
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            GUIApplication.userIdBearbeiten = null;
            GUIApplication.gruppeIdBearbeiten = null;
            sceneController.switchtoGruppenPage(actionEvent);
            System.out.println(connection.getResponseCode());
        } else {
            // User login failed
            System.out.println("Adding did not work");
            System.out.println(connection.getResponseCode());
            new Alert(Alert.AlertType.ERROR,"Dieser Nutzer ist schon in einer anderen Gruppe.").show();
        }

        connection.disconnect();
    }

    /**
     * Retrieves a list of users based on a search query
     * by making a GET request to the server and parsing the response into
     * a list of User objects.
     *
     * @return ObservalbleList of User objects based on a search query
     * @throws IOException
     */
    public ObservableList<User> getUsers() throws IOException {
        String searchQuery = sucheTextField.getText(); // Get the search query from the text field
        ObjectMapper objectMapper = new ObjectMapper();

        // GET Request to find the author with the username, and pull the object from the database
        URL url = new URL("http://" + GUIApplication.URL + ":8080/user/search/" + searchQuery);
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
        System.out.println(users);

        return users;
    }

    /**
     * Sets up the table columns, populates the table with users,
     * handles user selection, focuses on the search field,
     * and handles search requests triggered by the Enter key.
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

        sucheErgebnisseTable.getColumns().addAll(idColumn, usernameColumn, firstColumn, lastColumn);
        sucheErgebnisseTable.setItems(getAllUsers());

        sucheErgebnisseTable.setRowFactory(tv -> {
            TableRow<User> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (! row.isEmpty()) ) {
                    User rowData = row.getItem();
                    addButton.setDisable(false);
                    System.out.println(rowData.getId());
                    GUIApplication.userIdBearbeiten = rowData.getId();
                }
            });
            return row;
        });



        Platform.runLater(() -> sucheTextField.requestFocus());

        // Confirm search with Enter
        sucheTextField.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER){
                try {
                    suchen(null);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        String name = GUIApplication.aufgabeName;
        titleLabel.setText((name.length() > 10 ? name.substring(0,10) : name) +" > Hinzufügen zu G"+GUIApplication.gruppeNr);
    }

    /**
     * Fetches all users from the database,
     * converts them into an observable list,
     * and returns the list.
     *
     * @return ObservalbleList of all users.
     * @throws IOException
     */
    private ObservableList getAllUsers() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        // GET Request to find the author with the username, and pull the object from the database
        URL url = new URL("http://" + GUIApplication.URL + ":8080/user/all");
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
