package com.example.javafxdemo.usecaseeditor.drawablecomponent.boxcomponent;

import com.example.javafxdemo.usecaseeditor.drawablecomponent.DrawableComponent;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class Akteur extends BoxComponent {
    // Default sizes for newly created components
    private static final int DEFAULT_ACTOR_HEIGHT = 100;
    private static final int DEFAULT_ACTOR_WIDTH = 50;

    /** Constructor */
    public Akteur() {
        super("Actor", 0, 0, DEFAULT_ACTOR_HEIGHT, DEFAULT_ACTOR_WIDTH);
    }

    /**
     * Constructor
     *
     * @param title    the title to write on the component
     * @param centerX  the x coordinate in the center of the object to draw
     * @param centerY  the y coordinate in the center of the object to draw
     * @param height   the height of the actor
     * @param width    the width of the actor
     */
    public Akteur(String title, double centerX, double centerY, double height, double width) {
        super(title, centerX, centerY, height, width);
    }

    @Override
    protected double getTitleYCoord() {
        final Text throwaway = new Text(title);
        throwaway.setTextAlignment(TextAlignment.CENTER);
        new Scene(new Group(throwaway));
        return centerY + (height / 2) + throwaway.getLayoutBounds().getHeight() + 20;
    }

    @Override
    public DrawableComponent createCopy() {
        return new Akteur(this.title, this.centerX, this.centerY, this.height, this.width);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Akteur that = (Akteur) o;
        return title.equals(that.title) && centerX == that.getCenterX() && centerY == that.centerY
                && height == that.height && width == that.width;
    }

    public void draw(GraphicsContext gc, Color color, int lineWidth) {
        // Draw actor shape
        double startX = centerX - (width / 2);
        double startY = centerY - (height / 2);
        double headDiameter = width / 2; // Adjusted for a smaller head
        double neckHeight = height * 0.1; // Adjusted for proper positioning of the body
        double bodyHeight = height * 0.6 - neckHeight;
        double legHeight = height * 0.3; // Adjusted for a longer leg
        double legWidth = width * 0.2;

        gc.setStroke(color);
        gc.setLineWidth(lineWidth);
        gc.setLineDashes(0);

        // Draw head
        double headStartX = centerX - (headDiameter / 2);
        double headStartY = startY;

        gc.strokeOval(headStartX, headStartY, headDiameter, headDiameter);

        // Draw body
        gc.strokeLine(centerX, startY + headDiameter, centerX, startY + headDiameter + neckHeight + bodyHeight);

        // Draw arms
        gc.strokeLine(centerX - headDiameter / 2, startY + headDiameter + neckHeight + bodyHeight * 0.4, centerX + headDiameter / 2, startY + headDiameter + neckHeight + bodyHeight * 0.4);

        // Draw legs
        gc.strokeLine(centerX, startY + headDiameter + neckHeight + bodyHeight, centerX - legWidth, startY + headDiameter + neckHeight + bodyHeight + legHeight);
        gc.strokeLine(centerX, startY + headDiameter + neckHeight + bodyHeight, centerX + legWidth, startY + headDiameter + neckHeight + bodyHeight + legHeight);


        // Draw text
        final Text throwaway = new Text(title);
        throwaway.setTextAlignment(TextAlignment.CENTER);
        throwaway.setFont(Font.font("Arial", 12));
        new Scene(new Group(throwaway));
        gc.setFill(Color.BLACK);
        gc.setTextBaseline(VPos.BASELINE);
        gc.fillText(title, centerX - (throwaway.getLayoutBounds().getWidth() / 2), getTitleYCoord());
    }
}
