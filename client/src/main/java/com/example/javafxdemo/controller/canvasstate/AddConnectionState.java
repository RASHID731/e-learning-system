package com.example.javafxdemo.controller.canvasstate;

import com.example.javafxdemo.controller.CanvasContentManagementController;
import com.example.javafxdemo.usecaseeditor.drawablecomponent.Connection;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.javatuples.Pair;

/**
 * Represents the state of adding a connection to the canvas.
 * Subclass of CanvasState.
 */
public class AddConnectionState extends CanvasState {
    // The new connection to draw on the canvas
    Connection newConnection;

    /**
     * Constructor for AddConnectionState.
     *
     * @param canvasContentManagementController The controller for the main window using this state.
     * @param newConnection                      The new connection to draw on the canvas.
     */
    public AddConnectionState(CanvasContentManagementController canvasContentManagementController, Connection newConnection) {
        super(canvasContentManagementController);
        this.newConnection = newConnection;
        newConnection.setStart(canvasContentManagementController.findClosestPointOnComponentEdge(
                newConnection.getStart().getValue0(), newConnection.getStart().getValue1()));
    }

    /**
     * Performs necessary operations when exiting the AddConnectionState.
     * Sets the current canvas state to SelectComponentState.
     * Executes the componentAddedListener and stopEditingListener if they are not null.
     * Resets clickComponent, highlightedComponent, and redraws the canvas.
     */
    @Override
    public void exitState() {
        canvasContentManagementController.setCurrentCanvasState(new SelectComponentState(canvasContentManagementController));

        if (canvasContentManagementController.componentAddedListener != null) {
            canvasContentManagementController.componentAddedListener.run();
        }
        if (canvasContentManagementController.stopEditingListener != null) {
            canvasContentManagementController.stopEditingListener.run();
        }

        canvasContentManagementController.clickComponent = false;
        canvasContentManagementController.setHighlightedComponent(null);
        canvasContentManagementController.getCanvasDrawController().redrawCanvas();
    }

    /**
     * Performs necessary operations when entering the AddConnectionState.
     * Sets the highlightedComponent to null.
     */
    @Override
    public void enterState() {
        super.enterState();
        canvasContentManagementController.setHighlightedComponent(null);
    }

    /**
     * Handles the mouse press event.
     * If the right mouse button is pressed, it indicates canceling the new connection addition, and the state is exited.
     * If the left mouse button is pressed, it sets the end point of the connection to the closest point on the component edge
     * based on the mouse coordinates, draws the final connection on the canvas, adds the connection to the canvas,
     * and exits the state.
     *
     * @param mouseEvent The MouseEvent triggered by the mouse press.
     */
    @Override
    public void mousePressedHandler(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.SECONDARY) {
            // A right click indicates canceling the new connection addition
            exitState();
        } else if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            // Draw the component on the canvas
            newConnection.setEnd(canvasContentManagementController.findClosestPointOnComponentEdge(
                    mouseEvent.getX(), mouseEvent.getY()));
            canvasContentManagementController.getCanvasDrawController().drawFinalComponent(newConnection);
            canvasContentManagementController.addComponent(newConnection);
            exitState();
        }
    }

    /**
     * Handles the mouse move event.
     * Draws a preview of where the connection will be drawn on the canvas based on the mouse coordinates.
     *
     * @param mouseEvent The MouseEvent triggered by the mouse move.
     */
    @Override
    public void mouseMoveHandler(MouseEvent mouseEvent) {
        // Draw a preview of where the connection will be drawn on the canvas
        newConnection.setEnd(new Pair<>(mouseEvent.getX(), mouseEvent.getY()));
        canvasContentManagementController.getCanvasDrawController().drawPreviewComponent(newConnection);
    }
}
