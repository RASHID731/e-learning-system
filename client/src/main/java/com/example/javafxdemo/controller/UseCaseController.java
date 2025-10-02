package com.example.javafxdemo.controller;

import com.example.javafxdemo.controller.canvasstate.AddComponentState;
import com.example.javafxdemo.usecaseeditor.drawablecomponent.boxcomponent.Akteur;
import com.example.javafxdemo.usecaseeditor.drawablecomponent.boxcomponent.CanvasSystem;
import com.example.javafxdemo.usecaseeditor.drawablecomponent.boxcomponent.UseCase;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Controller class for managing the UseCaseEditor view.
 */
public class UseCaseController {
    @FXML
    public BorderPane root;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Button hilfeButton;

    @FXML
    private Canvas canvas;

    //the controller for the Canvas contents
    public CanvasContentManagementController canvasContentManagementController;

    private final SceneController sceneController = new SceneController();
    //the controller for file operations
    private FileController fileController;

    //the maximum dimension for the canvas drawing
    private static final double CANVAS_MAX_SIZE = 4000;
    private static final double CANVAS_SIZE_INCREASE = 100;

    private static final int POLLING_INTERVAL = 200; // 0.2 second
    private Timer pollingTimer;

    private PollTask pollingTask;

    /**
     * Sets up various listeners and event handlers for managing the canvas and its contents,
     * handles resizing of the canvas, loads initial contents,
     * and handles editing events by starting or stopping polling.
     */
    @FXML
    private void initialize() {
        //initialize the canvas content management controller
        canvasContentManagementController = new CanvasContentManagementController(canvas);
        fileController = new FileController(canvasContentManagementController);

        //set a listener to redraw the canvas when the window is resized
        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) ->
                canvasContentManagementController.getCanvasDrawController().redrawCanvas();
                root.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
                    if (newScene != null) {
                        newScene.windowProperty().addListener((observableWindow, oldWindow, newWindow) -> {
                            if (root.getScene().getWindow() != null) {
                                root.getScene().getWindow().widthProperty().addListener(stageSizeListener);
                                root.getScene().getWindow().heightProperty().addListener(stageSizeListener);
                            }
                        });
                    }
                });


        //set canvas size of center pane
        canvasContentManagementController.getCanvasDrawController().issueDrawingCommand(() -> {
            canvas.heightProperty().setValue(scrollPane.heightProperty().getValue());
            canvas.widthProperty().setValue(scrollPane.widthProperty().getValue());
        });

        //set listeners to grow canvas size
        scrollPane.vvalueProperty().addListener((observableValue, number, t1) -> {
            if (t1.doubleValue() == scrollPane.getVmax() && canvas.heightProperty().getValue() < CANVAS_MAX_SIZE) {
                canvasContentManagementController.getCanvasDrawController().issueDrawingCommand(() ->
                        canvas.heightProperty().setValue(canvas.heightProperty().getValue() + CANVAS_SIZE_INCREASE));
            }
        });
        scrollPane.hvalueProperty().addListener((observableValue, number, t1) -> {
            if(t1.doubleValue() == scrollPane.getHmax() && canvas.widthProperty().getValue() < CANVAS_MAX_SIZE){
                canvasContentManagementController.getCanvasDrawController().issueDrawingCommand(() ->
                        canvas.widthProperty().setValue(canvas.widthProperty().getValue() + CANVAS_SIZE_INCREASE));
            }
        });

        loadCanvasContents();
        startPolling();

        // Set listeners for adding and deleting components
        canvasContentManagementController.setComponentAddedListener(() -> {
            // Component added, perform necessary actions
            fileController.saveDrawnComponents(canvasContentManagementController.getDrawnComponents());
        });

        canvasContentManagementController.setComponentDeletedListener(() -> {
            // Component deleted, perform necessary actions
            fileController.saveDrawnComponents(canvasContentManagementController.getDrawnComponents());
        });

        canvasContentManagementController.setStartEditingListener(() -> {
            System.out.println("polling stopped");
            stopPolling();
        });

        canvasContentManagementController.setStopEditingListener(() -> {
            System.out.println("polling started");
            startPolling();
        });
    }

    private void startPolling() {
        System.out.println("start polling");
        pollingTask = new PollTask();
        pollingTimer = new Timer();
        pollingTimer.schedule(pollingTask, 0, POLLING_INTERVAL);
    }

    public void stopPolling() {
        System.out.println("stop polling");
        if (pollingTimer != null) {
            pollingTask.setPollingAllowed(false);
            pollingTimer.cancel();
            pollingTimer = null;
        }
    }

    private class PollTask extends TimerTask {
        private boolean isPollingAllowed = true;

        public void setPollingAllowed(boolean isPollingAllowed) {
            this.isPollingAllowed = isPollingAllowed;
        }

        @Override
        public void run() {
            if (isPollingAllowed) {
                try {
                    loadCanvasContents();
                } catch (Exception e) {
                    // Handle any exceptions that occur during the request or response handling
                    e.printStackTrace();
                }
            }
        }
    }

    /**handler for adding a new SingleSectionClassBox to the canvas*/
    @FXML
    public void drawNewUseCase() {
        canvasContentManagementController.setCurrentCanvasState(
                new AddComponentState(canvasContentManagementController, new UseCase())
        );
    }

    public void drawNewActor() {
        canvasContentManagementController.setCurrentCanvasState(
                new AddComponentState(canvasContentManagementController, new Akteur())
        );
    }

    public void drawNewSystem() {
        canvasContentManagementController.setCurrentCanvasState(
                new AddComponentState(canvasContentManagementController, new CanvasSystem())
        );
    }

    /**handler for saving the current set of DrawableComponents on the canvas to a file*/
    @FXML
    public void saveCurrentCanvasContents() {
        fileController.saveDrawnComponents(canvasContentManagementController.getDrawnComponents());
    }

    /**handler for loading a new set of DrawableComponents onto the canvas*/
    @FXML
    public void loadCanvasContents(){
        canvasContentManagementController.setDrawnComponents(
                fileController.loadDrawnComponents());

        //after loading the file, we need to refresh the canvas drawing and reset the keystroke handler by resetting state
        canvasContentManagementController.getCanvasDrawController().redrawCanvas();
        canvasContentManagementController.getCurrentCanvasState().enterState();
    }

    @FXML
    public void back(ActionEvent event) throws IOException {
        stopPolling();
//        sceneController.switchtoAufgabePage(event);
        ( (Stage) root.getScene().getWindow()).close();
    }

    @FXML
    public void hilfe(ActionEvent event) {
        var alert = new Alert(Alert.AlertType.INFORMATION);
        var l = new Label("- Doppelklick: z.B. Namen bearbeiten\n- Rechtklick: Auslöser bzw. Beziehung zeichnen\n- DELETE-Knopf (auf der Tastatur): markiertes Element löschen\n- z-Knopf (Tastatur): letzte Aktion rückgängig machen");
        l.setWrapText(true);
        alert.getDialogPane().setContent(l);
        alert.setHeaderText("Anleitung");
        alert.setTitle("Anleitung");
        alert.show();
    }
}
