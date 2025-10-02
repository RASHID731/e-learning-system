package com.example.javafxdemo.controller.canvasstate;

import com.example.javafxdemo.controller.CanvasContentManagementController;
import com.example.javafxdemo.usecaseeditor.drawablecomponent.boxcomponent.BoxComponent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * Represents the state of adding a component to the canvas.
 * Subclass of CanvasState.
 */
public class AddComponentState extends CanvasState {
    // The component to add onto the canvas drawing
    protected BoxComponent newComponent;

    /**
     * Constructor for AddComponentState.
     *
     * @param canvasContentManagementController The controller for the main window using this state.
     * @param newComponent                      The new component to draw on the canvas.
     */
    public AddComponentState(CanvasContentManagementController canvasContentManagementController, BoxComponent newComponent) {
        super(canvasContentManagementController);
        this.newComponent = newComponent;
    }

    /**
     * Performs necessary operations when exiting the AddComponentState.
     * Sets the current canvas state to SelectComponentState.
     * Executes the componentAddedListener if it is not null.
     */
    @Override
    public void exitState() {
        canvasContentManagementController.setCurrentCanvasState(new SelectComponentState(canvasContentManagementController));

        if (canvasContentManagementController.componentAddedListener != null) {
            canvasContentManagementController.componentAddedListener.run();
        }
    }

    /**
     * Performs necessary operations when entering the AddComponentState.
     * Sets the highlightedComponent to null.
     */
    @Override
    public void enterState() {
        super.enterState();
        canvasContentManagementController.setHighlightedComponent(null);
    }

    /**
     * Handles the mouse press event.
     * If the right mouse button is pressed, it indicates canceling the new component addition, and the state is exited.
     * If the left mouse button is pressed, it draws the component on the canvas at the mouse coordinates,
     * adds the component to the canvas, and exits the state.
     *
     * @param mouseEvent The MouseEvent triggered by the mouse press.
     */
    @Override
    public void mousePressedHandler(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.SECONDARY) {
            // A right click indicates canceling the new component addition
            exitState();
        } else if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            // Draw the component on the canvas
            newComponent.setCenterX(mouseEvent.getX());
            newComponent.setCenterY(mouseEvent.getY());
            canvasContentManagementController.getCanvasDrawController().drawFinalComponent(newComponent);
            canvasContentManagementController.addComponent(newComponent);
            exitState();
        }
    }

    /**
     * Handles the mouse move event.
     * Draws a preview of where the component will be drawn on the canvas at the mouse coordinates.
     *
     * @param mouseEvent The MouseEvent triggered by the mouse move.
     */
    @Override
    public void mouseMoveHandler(MouseEvent mouseEvent) {
        // Draw a preview of where the component will be drawn on the canvas
        newComponent.setCenterX(mouseEvent.getX());
        newComponent.setCenterY(mouseEvent.getY());
        canvasContentManagementController.getCanvasDrawController().drawPreviewComponent(newComponent);
    }
}
