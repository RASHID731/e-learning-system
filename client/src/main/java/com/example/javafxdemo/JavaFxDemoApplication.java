package com.example.javafxdemo;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class JavaFxDemoApplication {

    public static void main(String[] args) {
        Application.launch(GUIApplication.class, args);
    }

}
