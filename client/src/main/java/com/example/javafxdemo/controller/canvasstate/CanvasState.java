package com.example.javafxdemo.controller.canvasstate;

import com.example.javafxdemo.controller.CanvasContentManagementController;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Represents the state of the canvas and defines the actions to be performed for each event in the state.
 * This class is meant to be subclassed to implement specific state behavior.
 */
public abstract class CanvasState {
    // The controller for the main window
    protected CanvasContentManagementController canvasContentManagementController;
    // The canvas to draw on
    protected Canvas canvas;

    /**
     * Constructor for CanvasState.
     *
     * @param canvasContentManagementController The controller for the main window that uses this CanvasState object.
     */
    public CanvasState(CanvasContentManagementController canvasContentManagementController) {
        this.canvasContentManagementController = canvasContentManagementController;
        this.canvas = canvasContentManagementController.getCanvas();
        enterState();
    }

    /**
     * Sets the listeners for each event and performs necessary initialization for the state.
     */
    public void enterState() {
        canvas.setOnMousePressed(this::mousePressedHandler);
        canvas.setOnMouseMoved(this::mouseMoveHandler);
        canvas.setOnMouseReleased(this::mouseReleasedHandler);
        canvas.setOnMouseDragged(this::mouseDraggedHandler);
        canvas.setOnDragDetected(this::dragDetectedHandler);
        Stage.getWindows().stream()
                .filter(Window::isShowing)
                .findFirst()
                .ifPresent(currentWindow -> currentWindow.getScene().setOnKeyPressed(this::keyStrokeHandler));

        canvasContentManagementController.getDrawnComponentStateStack().updateStateStack(canvasContentManagementController.getDrawnComponents());
        canvasContentManagementController.getCanvasDrawController().redrawCanvas();
    }

    /**
     * Activities to be done before exiting the state and transitioning to the next state.
     * This method can be overridden by subclasses to perform state-specific cleanup.
     */
    public void exitState() {}

    /**
     * Handles the mouse pressed event.
     *
     * @param mouseEvent The MouseEvent triggered by the mouse press.
     */
    public void mousePressedHandler(MouseEvent mouseEvent) {}

    /**
     * Handles the mouse move event.
     *
     * @param mouseEvent The MouseEvent triggered by the mouse move.
     */
    public void mouseMoveHandler(MouseEvent mouseEvent) {}

    /**
     * Handles the mouse released event.
     *
     * @param mouseEvent The MouseEvent triggered by the mouse release.
     */
    public void mouseReleasedHandler(MouseEvent mouseEvent) {}

    /**
     * Handles the mouse dragged event.
     *
     * @param mouseEvent The MouseEvent triggered by the mouse drag.
     */
    public void mouseDraggedHandler(MouseEvent mouseEvent) {}

    /**
     * Handles the drag detected event.
     *
     * @param dragEvent The MouseEvent triggered by the drag detection.
     */
    public void dragDetectedHandler(MouseEvent dragEvent) {}

    /**
     * Handles the key stroke event.
     *
     * @param keyEvent The KeyEvent triggered by the key stroke.
     */
    public void keyStrokeHandler(KeyEvent keyEvent) {}
}