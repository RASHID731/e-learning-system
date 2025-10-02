package com.example.javafxdemo.controller.canvasstate;

import com.example.javafxdemo.controller.CanvasContentManagementController;
import com.example.javafxdemo.usecaseeditor.drawablecomponent.boxcomponent.BoxComponent;
import javafx.scene.input.MouseEvent;

/**
 * Represents the state of resizing a component on the canvas.
 * Subclass of CanvasState.
 */
public class ResizeComponentState extends CanvasState {
    // The component to resize
    private BoxComponent componentToResize;
    // The direction in which to resize the component
    private ResizeDirection resizeDirection;

    private static final double MINIMUM_COMPONENT_SIZE = 5;

    /**
     * Constructor for ResizeComponentState.
     *
     * @param canvasContentManagementController The controller for the main window that uses this state.
     * @param componentToResize                  The component to resize on the canvas.
     * @param resizeDirection                    The direction in which to resize the component.
     */
    public ResizeComponentState(CanvasContentManagementController canvasContentManagementController,
                                BoxComponent componentToResize, ResizeDirection resizeDirection) {
        super(canvasContentManagementController);
        this.componentToResize = componentToResize;
        this.resizeDirection = resizeDirection;
    }

    /**
     * Performs necessary operations when exiting the ResizeComponentState.
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
     * Resizes the component based on the specified resize direction and mouse coordinates.
     * Draws a preview of the resizing.
     *
     * @param mouseEvent The MouseEvent triggered by the mouse drag.
     */
    @Override
    public void mouseDraggedHandler(MouseEvent mouseEvent) {
        // Set new size based on the specified direction
        double oldCenterX = componentToResize.getCenterX();
        double oldCenterY = componentToResize.getCenterY();
        double leftEdge = componentToResize.getCenterX() - (componentToResize.getWidth() / 2);
        double topEdge = componentToResize.getCenterY() - (componentToResize.getHeight() / 2);
        double rightEdge = componentToResize.getCenterX() + (componentToResize.getWidth() / 2);
        double bottomEdge = componentToResize.getCenterY() + (componentToResize.getHeight() / 2);

        switch (resizeDirection) {
            case LEFT:
                resizeHorizontalLeft(rightEdge, mouseEvent.getX());
                break;
            case RIGHT:
                resizeHorizontalRight(leftEdge, mouseEvent.getX());
                break;
            case TOP:
                resizeVerticalUp(bottomEdge, mouseEvent.getY());
                break;
            case BOTTOM:
                resizeVerticalDown(topEdge, mouseEvent.getY());
                break;
            case TOP_LEFT:
                resizeHorizontalLeft(rightEdge, mouseEvent.getX());
                resizeVerticalUp(bottomEdge, mouseEvent.getY());
                break;
            case TOP_RIGHT:
                resizeHorizontalRight(leftEdge, mouseEvent.getX());
                resizeVerticalUp(bottomEdge, mouseEvent.getY());
                break;
            case BOTTOM_LEFT:
                resizeHorizontalLeft(rightEdge, mouseEvent.getX());
                resizeVerticalDown(topEdge, mouseEvent.getY());
                break;
            case BOTTOM_RIGHT:
                resizeHorizontalRight(leftEdge, mouseEvent.getX());
                resizeVerticalDown(topEdge, mouseEvent.getY());
                break;
        }

        // Set minimum size for the component
        if (componentToResize.getHeight() <= MINIMUM_COMPONENT_SIZE) {
            componentToResize.setHeight(MINIMUM_COMPONENT_SIZE);
            componentToResize.setCenterY(oldCenterY);
        }
        if (componentToResize.getWidth() <= MINIMUM_COMPONENT_SIZE) {
            componentToResize.setWidth(MINIMUM_COMPONENT_SIZE);
            componentToResize.setCenterX(oldCenterX);
        }

        // Draw a preview of the resizing
        canvasContentManagementController.getCanvasDrawController().drawPreviewComponent(componentToResize);
    }

    /**
     * Handles the mouse released event.
     * Draws the final resized component and redraws the canvas.
     * Exits the state.
     *
     * @param mouseEvent The MouseEvent triggered by the mouse release.
     */
    @Override
    public void mouseReleasedHandler(MouseEvent mouseEvent) {
        // Draw the final resized component
        canvasContentManagementController.getCanvasDrawController().drawFinalComponent(componentToResize);
        canvasContentManagementController.getCanvasDrawController().redrawCanvas();
        exitState();
    }

    /**
     * Resizes the selected component horizontally on the left side.
     *
     * @param oppositeEdge The x coordinate of the right edge of the box to resize.
     * @param xClickCoord  The x coordinate of the mouse to resize with.
     */
    private void resizeHorizontalLeft(double oppositeEdge, double xClickCoord) {
        componentToResize.setWidth(oppositeEdge - xClickCoord);
        componentToResize.setCenterX(xClickCoord + (componentToResize.getWidth() / 2));
    }

    /**
     * Resizes the selected component horizontally on the right side.
     *
     * @param oppositeEdge The x coordinate of the left edge of the box to resize.
     * @param xClickCoord  The x coordinate of the mouse to resize with.
     */
    private void resizeHorizontalRight(double oppositeEdge, double xClickCoord) {
        componentToResize.setWidth(xClickCoord - oppositeEdge);
        componentToResize.setCenterX(xClickCoord - (componentToResize.getWidth() / 2));
    }

    /**
     * Resizes the selected component vertically on the top side.
     *
     * @param oppositeEdge The y coordinate of the bottom edge of the box to resize.
     * @param yClickCoord  The y coordinate of the mouse to resize with.
     */
    private void resizeVerticalUp(double oppositeEdge, double yClickCoord) {
        componentToResize.setHeight(oppositeEdge - yClickCoord);
        componentToResize.setCenterY(yClickCoord + (componentToResize.getHeight() / 2));
    }

    /**
     * Resizes the selected component vertically on the bottom side.
     *
     * @param oppositeEdge The y coordinate of the top edge of the box to resize.
     * @param yClickCoord  The y coordinate of the mouse to resize with.
     */
    private void resizeVerticalDown(double oppositeEdge, double yClickCoord) {
        componentToResize.setHeight(yClickCoord - oppositeEdge);
        componentToResize.setCenterY(yClickCoord - (componentToResize.getHeight() / 2));
    }
}