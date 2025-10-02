package com.example.javafxdemo.controller;

import com.example.javafxdemo.controller.canvasstate.CanvasState;
import com.example.javafxdemo.controller.canvasstate.SelectComponentState;
import com.example.javafxdemo.usecaseeditor.CanvasContentStateStack;
import com.example.javafxdemo.usecaseeditor.drawablecomponent.DrawableComponent;
import com.example.javafxdemo.usecaseeditor.drawablecomponent.boxcomponent.BoxComponent;
import com.example.javafxdemo.usecaseeditor.drawablecomponent.boxcomponent.CanvasSystem;
import javafx.scene.canvas.Canvas;
import lombok.Getter;
import lombok.Setter;
import org.javatuples.Pair;

import java.util.ArrayList;

/**
 * Controller class for managing the content of the canvas.
 */
public class CanvasContentManagementController {
    // The components drawn on the canvas
    @Getter @Setter
    private ArrayList<DrawableComponent> drawnComponents;

    // The DrawableComponent currently highlighted
    @Getter @Setter
    private DrawableComponent highlightedComponent;

    // A stack to allow actions to be undone
    @Getter
    private CanvasContentStateStack drawnComponentStateStack;

    // The state of the canvas
    @Setter @Getter
    public CanvasState currentCanvasState;

    // The canvas to be drawn on
    @Getter
    private Canvas canvas;

    // The controller to perform draw commands on the canvas
    @Getter
    private CanvasDrawController canvasDrawController;

    // Listener to keep track of any added components to save the file
    public Runnable componentAddedListener;

    // Listener to keep track of any deleted components to save the file
    private Runnable componentDeletedListener;

    // Listener to keep track of editing to stop polling
    public Runnable startEditingListener;

    // Listener to keep track of exiting from editing to start polling
    public Runnable stopEditingListener;

    // A Boolean to keep track of any highlighted component\
    public boolean clickComponent;

    /**
     * Constructor for the CanvasContentManagementController class.
     *
     * @param canvas The canvas to be managed.
     */
    public CanvasContentManagementController(Canvas canvas) {
        this.canvas = canvas;
        drawnComponents = new ArrayList<>();
        highlightedComponent = null;
        drawnComponentStateStack = new CanvasContentStateStack();
        canvasDrawController = new CanvasDrawController(this);

        // Set the starting state for the canvas
        currentCanvasState = new SelectComponentState(this);
    }

    /**
     * Sets the listener to be triggered when a component is added.
     *
     * @param listener The listener to set.
     */
    public void setComponentAddedListener(Runnable listener) {
        this.componentAddedListener = listener;
    }

    /**
     * Sets the listener to be triggered when a component is deleted.
     *
     * @param listener The listener to set.
     */
    public void setComponentDeletedListener(Runnable listener) {
        this.componentDeletedListener = listener;
    }

    /**
     * Sets the listener to be triggered when the canvas is being edited.
     *
     * @param listener The listener to set.
     */
    public void setStartEditingListener(Runnable listener) {
        this.startEditingListener = listener;
    }

    /**
     * Sets the listener to be triggered when the canvas is done being edited.
     *
     * @param listener The listener to set.
     */
    public void setStopEditingListener(Runnable listener) {
        this.stopEditingListener = listener;
    }

    /**
     * Adds a component to the list of components in the diagram.
     *
     * @param newComponent The component to be added.
     */
    public void addComponent(DrawableComponent newComponent){
        drawnComponents.add(newComponent);
    }

    /**
     * Removes a component from the list of components in the diagram.
     *
     * @param componentToRemove The component to be removed.
     */
    public void removeComponent(DrawableComponent componentToRemove){
        drawnComponents.remove(componentToRemove);

        if (componentDeletedListener != null) {
            componentDeletedListener.run();
        }
    }

    /**
     * Undo the last change made to the canvas drawing.
     */
    public void undoLastCanvasChange(){
        drawnComponents = drawnComponentStateStack.undoLastCanvasChange(drawnComponents);
        canvasDrawController.redrawCanvas();

        // Save the changes
        if (componentAddedListener != null) {
            componentAddedListener.run();
        }
    }

    /**
     * Determines what component the passed coordinates belong to and finds a point on the edge of
     * that component that is closest to the passed coordinates.
     *
     * @param clickX The x coordinate of the point to check.
     * @param clickY The y coordinate of the point to check.
     * @return The closest point on the edge of the component that the passed coordinates lies inside.
     */
    public Pair<Double, Double> findClosestPointOnComponentEdge(double clickX, double clickY){
        for(DrawableComponent component: drawnComponents){
            if(component.checkPointInBounds(clickX, clickY) && component instanceof BoxComponent boxComponent && !(component instanceof CanvasSystem)) {
                double topY = boxComponent.getCenterY() - (boxComponent.getHeight() / 2);
                double bottomY = topY + boxComponent.getHeight();
                double leftX = boxComponent.getCenterX() - (boxComponent.getWidth() / 2);
                double rightX = leftX + boxComponent.getWidth();

                if (checkIfFirstPairClosest(clickX, leftX, rightX, clickY, topY, bottomY)) {
                    // The click is closest to the left edge
                    return new Pair<>(leftX, clickY);
                } else if (checkIfFirstPairClosest(clickX, rightX, leftX, clickY, topY, bottomY)) {
                    // The click is closest to the right edge
                    return new Pair<>(rightX, clickY);
                } else if (checkIfFirstPairClosest(clickY, topY, bottomY, clickX, leftX, rightX)) {
                    // The click is closest to the top edge
                    return new Pair<>(clickX, topY);
                } else {
                    // The click is closest to the bottom edge
                    return new Pair<>(clickX, bottomY);
                }
            }
        }
        return new Pair<>(clickX, clickY);
    }

    /**
     * Compares each combination of the 6 values passed to check if the first
     * two values are more similar than any other combination of the values.
     *
     * @param d1 The first value.
     * @param d2 The second value.
     * @param d3 The third value.
     * @param d4 The fourth value.
     * @param d5 The fifth value.
     * @param d6 The sixth value.
     * @return True if the first two values are closer to each other than any other combination of the passed values.
     */
    private boolean checkIfFirstPairClosest(
            double d1, double d2, double d3, double d4, double  d5, double d6){
        return (checkIfFirstCoordinateCloser(d1, d2, d1, d3) &&
                checkIfFirstCoordinateCloser(d1, d2, d4, d5) &&
                checkIfFirstCoordinateCloser(d1, d2, d4, d6));
    }

    /**
     * Checks if the first two numbers provided are closer than the second two numbers provided.
     *
     * @param source The first number used in the first comparison.
     * @param first The second number used in the first comparison.
     * @param source2 The first number used in the second comparison.
     * @param second The second number used in the second comparison.
     * @return True if source and first are closer together than source2 and second, otherwise false.
     */
    private boolean checkIfFirstCoordinateCloser(double source, double first, double source2, double second){
        return Math.abs(source - first) < Math.abs(source2 - second);
    }
}