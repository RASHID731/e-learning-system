package com.example.javafxdemo;

import com.example.javafxdemo.model.AufgabeStatus;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.util.Locale;

public class GUIApplication extends Application {
    public static final String URL = "localhost";
    public static int maxNumber;
    public static AufgabeStatus aufgabeStatus;
    private ConfigurableApplicationContext applicationContext;

    public static String loggedInUsername;
    public static Long loggedInId;
    public static Long aufgabeId;
    public static String aufgabeName;
    public static String aufgabeAuthor;
    public static Long gruppeId;
    public static Long gruppeIdBearbeiten;
    public static Long userIdBearbeiten;

    public static int gruppeNr;
    private static Scene scene;

    @Override
    public void init() {
        applicationContext = new SpringApplicationBuilder(JavaFxDemoApplication.class).run();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Template/login.fxml"));
        Parent root = loader.load();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("E-Learning-System");
        Locale.setDefault(Locale.GERMANY);
/*
        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
*/
    }

    @Override
    public void stop() {
        applicationContext.close();
        Platform.exit();
    }
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GUIApplication.class.getResource("/Template/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

}
