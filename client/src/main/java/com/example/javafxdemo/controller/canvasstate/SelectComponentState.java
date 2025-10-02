package com.example.javafxdemo.controller.canvasstate;

import com.example.javafxdemo.controller.CanvasContentManagementController;
import com.example.javafxdemo.usecaseeditor.drawablecomponent.Connection;
import com.example.javafxdemo.usecaseeditor.drawablecomponent.DrawableComponent;
import com.example.javafxdemo.usecaseeditor.drawablecomponent.boxcomponent.BoxComponent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * Handles the selecting and deselecting of components drawn on the canvas to perform actions on.
 */
public class SelectComponentState extends CanvasState {
    // Sets how close to the edge a mouse drag must be to trigger component resizing
    private static final double COMPONENT_EDGE_THRESHOLD = 15;

    /**
     * Constructor.
     *
     * @param canvasContentManagementController The controller for the main window using this state.
     */
    public SelectComponentState(CanvasContentManagementController canvasContentManagementController) {
        super(canvasContentManagementController);
    }

    /**
     * Handles the mouse pressed event.
     *
     * @param mouseEvent The MouseEvent object containing information about the event.
     */
    @Override
    public void mousePressedHandler(MouseEvent mouseEvent) {
        // Get the position of the mouse click
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();

        // Flag to indicate if a component was clicked
        boolean isComponentClicked = false;
        DrawableComponent clickedComponent = null;

        // Check if click location is in the bounds of a component
        for (DrawableComponent component : canvasContentManagementController.getDrawnComponents()) {
            if (component.checkPointInBounds(x, y)) {
                isComponentClicked = true;
                canvasContentManagementController.clickComponent = true;
                clickedComponent = component;
                System.out.println(clickedComponent);
                break;
            }
        }

        System.out.println(isComponentClicked);

        // If a component is clicked, take action; if not, ignore
        if (canvasContentManagementController.clickComponent) {
            if (isComponentClicked) {
                System.out.println("component clicked");
                if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                    if (mouseEvent.getClickCount() == 1) { // Single click
                        // Highlight/unhighlight the clicked component
                        // Stop polling during component editing
                        if (canvasContentManagementController.startEditingListener != null) {
                            canvasContentManagementController.startEditingListener.run();
                        }
                        canvasContentManagementController.setHighlightedComponent(clickedComponent);
                        canvasContentManagementController.getCanvasDrawController().redrawCanvas();
                    } else if (mouseEvent.getClickCount() == 2) { // Double click
                        // Edit the contents of the component that was double clicked
                        // Stop polling during component editing
                        if (canvasContentManagementController.startEditingListener != null) {
                            canvasContentManagementController.startEditingListener.run();
                        }
                        canvasContentManagementController.setCurrentCanvasState(
                                new EditComponentContentsState(canvasContentManagementController, clickedComponent));
                    }
                } else if (mouseEvent.getButton() == MouseButton.SECONDARY) { // Right click
                    // Draw a connection between two points
                    // Stop polling during component editing
                    if (canvasContentManagementController.startEditingListener != null) {
                        canvasContentManagementController.startEditingListener.run();
                    }
                    canvasContentManagementController.setCurrentCanvasState(
                            new AddConnectionState(canvasContentManagementController,
                                    new Connection(mouseEvent.getX(), mouseEvent.getY())));
                }
            } else {
                System.out.println("component not clicked");
                // No component was clicked, handle background click event
                if (canvasContentManagementController.stopEditingListener != null) {
                    canvasContentManagementController.stopEditingListener.run();
                }
                canvasContentManagementController.clickComponent = false;
                canvasContentManagementController.setHighlightedComponent(null);
                canvasContentManagementController.getCanvasDrawController().redrawCanvas();
            }
        }
    }

    /**
     * Handles the drag detected event.
     *
     * @param dragEvent The MouseEvent object containing information about the event.
     */
    @Override
    public void dragDetectedHandler(MouseEvent dragEvent) {
        if (canvasContentManagementController.getHighlightedComponent() instanceof BoxComponent) {
            // Stop polling during component editing
            if (canvasContentManagementController.startEditingListener != null) {
                canvasContentManagementController.startEditingListener.run();
            }
            // Drag on the highlighted component
            BoxComponent componentToDrag = (BoxComponent) canvasContentManagementController.getHighlightedComponent();
            if (componentToDrag != null) {
                /* We need to check if the drag is on the edge of the component (to resize),
                 * or in the center (to move)
                 */
                double x = dragEvent.getX();
                double y = dragEvent.getY();
                double leftEdge = componentToDrag.getCenterX() - (componentToDrag.getWidth() / 2);
                double topEdge = componentToDrag.getCenterY() - (componentToDrag.getHeight() / 2);
                double rightEdge = componentToDrag.getCenterX() + (componentToDrag.getWidth() / 2);
                double bottomEdge = componentToDrag.getCenterY() + (componentToDrag.getHeight() / 2);

                if (checkCloseToEdge(topEdge, y) && checkCloseToEdge(leftEdge, x)) { // Resize from the top left
                    canvasContentManagementController.setCurrentCanvasState(new ResizeComponentState(
                            canvasContentManagementController, componentToDrag, ResizeDirection.TOP_LEFT));
                } else if (checkCloseToEdge(topEdge, y) && checkCloseToEdge(rightEdge, x)) { // Resize from the top right
                    canvasContentManagementController.setCurrentCanvasState(new ResizeComponentState(
                            canvasContentManagementController, componentToDrag, ResizeDirection.TOP_RIGHT));
                } else if (checkCloseToEdge(bottomEdge, y) && checkCloseToEdge(leftEdge, x)) { // Resize from the bottom left
                    canvasContentManagementController.setCurrentCanvasState(new ResizeComponentState(
                            canvasContentManagementController, componentToDrag, ResizeDirection.BOTTOM_LEFT));
                } else if (checkCloseToEdge(bottomEdge, y) && checkCloseToEdge(rightEdge, x)) { // Resize from the bottom right
                    canvasContentManagementController.setCurrentCanvasState(new ResizeComponentState(
                            canvasContentManagementController, componentToDrag, ResizeDirection.BOTTOM_RIGHT));
                } else if (checkCloseToEdge(rightEdge, x)) { // Resize from the right edge
                    canvasContentManagementController.setCurrentCanvasState(new ResizeComponentState(
                            canvasContentManagementController, componentToDrag, ResizeDirection.RIGHT));
                } else if (checkCloseToEdge(bottomEdge, y)) { // Resize from the bottom edge
                    canvasContentManagementController.setCurrentCanvasState(new ResizeComponentState(
                            canvasContentManagementController, componentToDrag, ResizeDirection.BOTTOM));
                } else if (checkCloseToEdge(topEdge, y)) { // Resize from the top edge
                    canvasContentManagementController.setCurrentCanvasState(new ResizeComponentState(
                            canvasContentManagementController, componentToDrag, ResizeDirection.TOP));
                } else if (checkCloseToEdge(leftEdge, x)) { // Resize from the left edge
                    canvasContentManagementController.setCurrentCanvasState(new ResizeComponentState(
                            canvasContentManagementController, componentToDrag, ResizeDirection.LEFT));
                } else { // If no edge checks pass, we are repositioning the component rather than resizing
                    canvasContentManagementController.setCurrentCanvasState(new MoveComponentState(
                            canvasContentManagementController, componentToDrag));
                }
            }
        }
    }

    /**
     * Checks if the location the drag event started is near the highlighted component's edge.
     *
     * @param edgeCoordinate   The coordinate of the edge to check against.
     * @param mouseCoordinate  The coordinate of the mouse where the drag event started from.
     * @return True if the drag action started near the specified edge, false otherwise.
     */
    private boolean checkCloseToEdge(double edgeCoordinate, double mouseCoordinate) {
        return mouseCoordinate < (edgeCoordinate + COMPONENT_EDGE_THRESHOLD) &&
                mouseCoordinate > (edgeCoordinate - COMPONENT_EDGE_THRESHOLD);
    }

    /**
     * Handles the key stroke event.
     *
     * @param keyEvent The KeyEvent object containing information about the event.
     */
    @Override
    public void keyStrokeHandler(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.DELETE || keyEvent.getCode() == KeyCode.BACK_SPACE) {
            // Delete the selected component
            DrawableComponent componentToDelete = canvasContentManagementController.getHighlightedComponent();
            if (componentToDelete != null) {
                canvasContentManagementController.removeComponent(componentToDelete);
                if (canvasContentManagementController.stopEditingListener != null) {
                    canvasContentManagementController.stopEditingListener.run();
                }
                canvasContentManagementController.clickComponent = false;
                canvasContentManagementController.setHighlightedComponent(null);
                canvasContentManagementController.getCanvasDrawController().redrawCanvas();
            }
        } else if (keyEvent.getCode() == KeyCode.Z) {
            // Undo the last change made to the canvas
            canvasContentManagementController.undoLastCanvasChange();
        }
    }
}
