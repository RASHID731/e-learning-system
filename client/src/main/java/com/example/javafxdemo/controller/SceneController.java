package com.example.javafxdemo.controller;

import com.example.javafxdemo.GUIApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Class for handling scene switching.
 */
public class SceneController {

    public void switchtoLoginPage(ActionEvent event) throws IOException {
        GUIApplication.setRoot("login");
    }

    public void switchtoRegisterPage(ActionEvent event) throws IOException {
        GUIApplication.setRoot("register");
    }

    public void switchtoAufgabenPage(ActionEvent event) throws IOException {
        GUIApplication.setRoot("aufgaben");
    }

    public void switchtoNewAufgabePage(ActionEvent event) throws IOException {
        GUIApplication.setRoot("newaufgabe");
    }

    public void switchtoAufgabePage(ActionEvent event) throws IOException {
        GUIApplication.setRoot("aufgabepage");
    }

    public void switchtoUseCaseEditor(ActionEvent event) throws IOException {
        Stage editorWindow = new Stage();
        editorWindow.setTitle("Editor");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Template/usecaseeditor.fxml"));
        var s = new Scene(loader.load(), 1200, 700);
        editorWindow.setScene(s);
        editorWindow.initModality(Modality.APPLICATION_MODAL);
        editorWindow.show();

        UseCaseController ucc = loader.getController();
        s.setOnKeyPressed(keyEvent -> ucc.canvasContentManagementController.currentCanvasState.keyStrokeHandler(keyEvent));

        editorWindow.setOnCloseRequest(e -> ucc.stopPolling());
    }

    public void switchtoGruppenPage(ActionEvent event) throws IOException {
        GUIApplication.setRoot("gruppen");
    }

    public void switchtoMitgliederPage(ActionEvent event) throws IOException {
        GUIApplication.setRoot("mitglieder");
    }

    public void switchtoSuchePage(ActionEvent event) throws IOException {
        GUIApplication.setRoot("suche");
    }
}
