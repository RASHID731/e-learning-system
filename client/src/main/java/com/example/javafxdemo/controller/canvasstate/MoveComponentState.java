package com.example.javafxdemo.controller.canvasstate;

import com.example.javafxdemo.controller.CanvasContentManagementController;
import com.example.javafxdemo.usecaseeditor.drawablecomponent.boxcomponent.BoxComponent;
import javafx.scene.input.MouseEvent;

/**
 * Represents the state of repositioning a component on the canvas.
 * Subclass of CanvasState.
 */
public class MoveComponentState extends CanvasState {
    // The component to reposition on the canvas
    BoxComponent componentToDrag;

    /**
     * Constructor for MoveComponentState.
     *
     * @param canvasContentManagementController The controller for the main window that uses this state.
     * @param componentToDrag                   The component to move on the canvas.
     */
    public MoveComponentState(CanvasContentManagementController canvasContentManagementController, BoxComponent componentToDrag) {
        super(canvasContentManagementController);
        this.componentToDrag = componentToDrag;
    }

    /**
     * Performs necessary operations when exiting the MoveComponentState.
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
     * Handles the mouse dragged event.
     * Draws a preview of where the component will be moved on the canvas based on the mouse coordinates.
     *
     * @param mouseEvent The MouseEvent triggered by the mouse drag.
     */
    @Override
    public void mouseDraggedHandler(MouseEvent mouseEvent) {
        // Draw a preview of where the component will now be positioned
        componentToDrag.setCenterX(mouseEvent.getX());
        componentToDrag.setCenterY(mouseEvent.getY());
        canvasContentManagementController.getCanvasDrawController().drawPreviewComponent(componentToDrag);
    }

    /**
     * Handles the mouse released event.
     * Redraws the component in its new canvas position based on the mouse coordinates and exits the state.
     *
     * @param mouseEvent The MouseEvent triggered by the mouse release.
     */
    @Override
    public void mouseReleasedHandler(MouseEvent mouseEvent) {
        // Redraw the component in its new canvas position
        componentToDrag.setCenterX(mouseEvent.getX());
        componentToDrag.setCenterY(mouseEvent.getY());
        canvasContentManagementController.getCanvasDrawController().drawFinalComponent(componentToDrag);
        exitState();
    }
}
