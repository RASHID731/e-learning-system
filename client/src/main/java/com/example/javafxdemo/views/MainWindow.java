package com.example.javafxdemo.views;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainWindow extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                MainWindow.class.getResource("/de/unirostock/usecaseeditor/main/mainlayout.fxml"));
        Scene scene = new Scene(loader.load());

        stage.setTitle("Kollaborativer Editor");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) { launch(args); }
}
