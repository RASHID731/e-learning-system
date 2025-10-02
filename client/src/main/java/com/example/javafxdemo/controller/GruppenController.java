package com.example.javafxdemo.controller;

import com.example.javafxdemo.GUIApplication;
import com.example.javafxdemo.model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Controller class for managing the Gruppen view.
 */

public class GruppenController {

    @FXML
    private TableView<Gruppe> gruppenTable;

    @FXML
    public Label currentAufgabe;

    private final SceneController sceneController = new SceneController();
    @FXML
    private Label loggedInUserLabel;

    @FXML
    private Button mitgliederButton;
    @FXML
    public Button einschreibenButton;
    @FXML
    public Button freieEinschreibungButton;

    @FXML
    private Button benotenButton;

    /**
     * Initializes the user interface,
     * sets up event handlers, performs network requests,
     * and populates the table with data.
     *
     * @throws IOException
     */
    @FXML
    public void initialize() throws IOException {
        String name = GUIApplication.aufgabeName;
        currentAufgabe.setText( (name.length() > 10 ? name.substring(0,10) : name) + " > Gruppen");

        if (!Objects.equals(GUIApplication.loggedInUsername, GUIApplication.aufgabeAuthor)) {
            mitgliederButton.setVisible(false);
            freieEinschreibungButton.setVisible(false);
            mitgliederButton.setManaged(false);
            freieEinschreibungButton.setManaged(false);
            einschreibenButton.setVisible(true);
            GUIApplication.userIdBearbeiten = GUIApplication.loggedInId;
            benotenButton.setVisible(false);
        } else {
            mitgliederButton.setVisible(true);
            freieEinschreibungButton.setVisible(true);
            einschreibenButton.setVisible(false);
            einschreibenButton.setManaged(false);
        }

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
        Boolean freieEinschreibungErlaubt = aufgabe.getFreieEinschreibung();

        if (!freieEinschreibungErlaubt) {
            freieEinschreibungButton.setText("Freie Einschreibung einschalten");
            einschreibenButton.setManaged(false);
        }

        connection.disconnect();

        TableColumn<Gruppe, Long> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> new SimpleObjectProperty(cellData.getValue().getId()));
        idColumn.setVisible(false);

        TableColumn<Gruppe, Integer> numberColumn = new TableColumn<>("Number");
        numberColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getNumber()));

        TableColumn<Gruppe, String> anzahlColumn = new TableColumn<>("bearbeitbar / insgesamt");
        anzahlColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAnzahl() + " / " + cellData.getValue().getMaxAnzahl()));

        TableColumn<Gruppe, String> mitgliederColumn = new TableColumn<>("Mitglieder");
        mitgliederColumn.setCellValueFactory(cellData -> {
            List<String> firstNames = cellData.getValue().getMitglieder().stream()
                    .map(User::getFirstName)
                    .collect(Collectors.toList());
            String joinedNames = firstNames.stream().collect(Collectors.joining(", "));
            return new SimpleStringProperty(joinedNames);
        });

        TableColumn<Gruppe, String> noteColumn = new TableColumn<>("Note");
        noteColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNote()));

        TableColumn<Gruppe, AbgabeStatus> statusColumn = new TableColumn<>("Abgabestatus");
        statusColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getStatus()));

        gruppenTable.setRowFactory(tv -> {
            TableRow<Gruppe> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (! row.isEmpty()) ) {
                    Gruppe rowData = row.getItem();
                    mitgliederButton.setDisable(false);
                    System.out.println("GruppenId:" + rowData.getId());
                    GUIApplication.gruppeIdBearbeiten = rowData.getId();
                    GUIApplication.gruppeNr = rowData.getNumber();
                    einschreibenButton.setDisable(rowData.getAnzahl() >= rowData.getMaxAnzahl() || Objects.equals(GUIApplication.gruppeId, GUIApplication.gruppeIdBearbeiten));
                    benotenButton.setDisable(rowData.getStatus() != AbgabeStatus.ABGEGEBEN);
                }
            });
            return row ;
        });


        gruppenTable.getColumns().addAll(idColumn, numberColumn, anzahlColumn, mitgliederColumn, noteColumn, statusColumn);
        gruppenTable.setItems(getGruppen());
    }

    /**
     * performs a network request to retrieve a list of Gruppe objects from server,
     * deserializes the response into a list, and converts it into an ObservableList
     *
     * @return ObservableList containing the deserialized Gruppe objects.
     * @throws IOException
     */
    public ObservableList<Gruppe> getGruppen() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // GET Request to find the author with the username, and pull the object from the database
        URL url = new URL("http://" + GUIApplication.URL + ":8080/aufgabe/" + GUIApplication.aufgabeId + "/gruppen/all");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        // Read the response from the input stream
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        List<Gruppe> list = objectMapper.readValue(reader, new TypeReference<>() {});

        ObservableList<Gruppe> gruppen = FXCollections.observableArrayList();
        for (Gruppe gruppe : list) {
            gruppen.add(new Gruppe(gruppe.getId(),
                    gruppe.getNumber(),
                    gruppe.getAnzahl(),
                    gruppe.getMaxAnzahl(),
                    gruppe.getMitglieder(),
                    gruppe.getNote(),
                    gruppe.getStatus()));
        }

        return gruppen;
    }

    /**
     * Navigates back to the "Aufgabe" page.
     *
     * @param event
     * @throws IOException
     */
    public void back(ActionEvent event) throws IOException {
        sceneController.switchtoAufgabePage(event);
    }

    /**
     * Navigates to the "Mitglieder" page.
     *
     * @param event
     * @throws IOException
     */
    @FXML
    public void showMitglieder(ActionEvent event) throws IOException {
        sceneController.switchtoMitgliederPage(event);
    }

    /**
     * Handles the process of enrolling a user in a group.
     * It makes an HTTP request to the server, handles different response codes,
     * and provides appropriate feedback to the user based on the outcome of the request.
     *
     * @param event
     * @throws IOException
     */
    public void einschreiben(ActionEvent event) throws IOException {
        System.out.println(GUIApplication.gruppeIdBearbeiten);
        System.out.println(GUIApplication.userIdBearbeiten);
        URL url = new URL("http://" + GUIApplication.URL + ":8080/gruppe/" + GUIApplication.gruppeIdBearbeiten + "/add/user/" + GUIApplication.userIdBearbeiten);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");

        // Check the response code
        int responseCode = connection.getResponseCode();
        connection.disconnect();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            GUIApplication.userIdBearbeiten = null;
            GUIApplication.gruppeId = GUIApplication.gruppeIdBearbeiten;
            GUIApplication.gruppeIdBearbeiten = null;
            sceneController.switchtoGruppenPage(event);
            System.out.println(connection.getResponseCode());
        }
        else if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
            var chgrAlert = new Alert(Alert.AlertType.CONFIRMATION);
            var l = new Label("Sie sind bereits in einer anderen Gruppe eingeschrieben.\n" +
                    "Wollen Sie versuchen die Gruppe zu wechseln?");
            l.setWrapText(true);
            chgrAlert.getDialogPane().setContent(l);
            chgrAlert.getButtonTypes().set(1,ButtonType.YES);
            chgrAlert.getButtonTypes().set(0,ButtonType.NO);
            var changeGroup = chgrAlert.showAndWait();
            changeGroup.ifPresent(cyk -> {
                switch (cyk.getButtonData()) {
                    case NO -> System.out.println(cyk.getButtonData());
                    case YES -> {
                        try {
                            var s = "http://" + GUIApplication.URL + ":8080/gruppe/" + GUIApplication.gruppeId + "/changeto/" + GUIApplication.gruppeIdBearbeiten + "/user/" + GUIApplication.userIdBearbeiten;
                            System.out.println(s);
                            var urll = new URL(s);
                            var con = (HttpURLConnection) urll.openConnection();
                            con.setRequestMethod("POST");
                            con.setRequestProperty("Content-Type", "application/json");
                            var resp = con.getResponseCode();
                            switch (resp) {
                                case HttpURLConnection.HTTP_OK -> {
                                    GUIApplication.gruppeId = GUIApplication.gruppeIdBearbeiten;
                                    System.out.println("Wechsel erfolgreich.");
                                }
                                case HttpURLConnection.HTTP_CONFLICT -> new Alert(Alert.AlertType.ERROR, "Gruppe ist voll.").show();
                                case HttpURLConnection.HTTP_NOT_ACCEPTABLE -> new Alert(Alert.AlertType.ERROR, "Die Gruppe hat schon abgegeben.").show();
                                case HttpURLConnection.HTTP_FORBIDDEN -> new Alert(Alert.AlertType.ERROR, "Ihre Gruppe hat schon abgegeben.").show();
                                default -> {
                                    new Alert(Alert.AlertType.ERROR, "Wechsel nicht möglich.").show();
                                    System.out.println(resp);
                                }
                            }
                            neuladen(event);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
        }
        else {
            // User login failed
            System.out.println("Adding did not work");
            System.out.println(connection.getResponseCode());
            new Alert(Alert.AlertType.ERROR,"Einschreibung nicht möglich.").show();
        }

    }

    /**
     * Reloads the scene, to fetch new data from server.
     *
     * @param event
     * @throws IOException
     */
    public void neuladen(ActionEvent event) throws IOException {
        sceneController.switchtoGruppenPage(event);
    }

    /**
     * Handles the process of assigning a grade to a group.
     * It displays a dialog to the user to enter the grade,
     * sends a POST request to the server with the grade,
     * handles the response, and reloads the page.
     *
     * @param event
     */
    public void benoten(ActionEvent event) {
        var dialog = new TextInputDialog();
        dialog.setTitle("Benoten");
        dialog.setHeaderText("Gruppe "+ GUIApplication.gruppeNr +" Benoten");
        dialog.setContentText("Note:");
        var benotung = dialog.showAndWait();
        benotung.ifPresent(note -> {
            try {
                var url = new URL("http://" + GUIApplication.URL + ":8080/gruppe/" + GUIApplication.gruppeIdBearbeiten + "/note/" + note);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");

                // Check the response code
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    System.out.println("Note aktualisert: "+responseCode);
                } else {
                    // User login failed
                    System.out.println("Note not updated");
                    System.out.println(responseCode);
                    new Alert(Alert.AlertType.ERROR,"Note konnte nicht aktualisiert werden.").show();
                }

                connection.disconnect();
                neuladen(event);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
    }

    /**
     * Handles the process of toggling the "freie Einschreibung" feature for an assignment.
     * It sends a POST request to the server,
     * handles the response and updates the button text to reflect the toggled state.
     *
     * @param event
     * @throws IOException
     */
    public void freiEinschreiben(ActionEvent event) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        URL url = new URL("http://" + GUIApplication.URL + ":8080/aufgabe/" + GUIApplication.aufgabeId + "/abgabe/toggle");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");

        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        // Check the response code
        if (responseCode == HttpURLConnection.HTTP_OK) {
            System.out.println("Freie Einschreibung geändert: "+ responseCode);
        } else {
            // User login failed
            System.out.println("FAIL");
            System.out.println(responseCode);
            new Alert(Alert.AlertType.ERROR,"Freie Einschreibung konnte nicht geändert werden.").show();
        }

        connection.disconnect();
        switch (freieEinschreibungButton.getText()) {
            case "Freie Einschreibung ausschalten" -> freieEinschreibungButton.setText("Freie Einschreibung einschalten");
            case "Freie Einschreibung einschalten" -> freieEinschreibungButton.setText("Freie Einschreibung ausschalten");
        }
    }
}
