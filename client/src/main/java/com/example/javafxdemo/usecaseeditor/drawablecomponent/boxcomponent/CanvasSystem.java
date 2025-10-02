package com.example.javafxdemo.usecaseeditor.drawablecomponent.boxcomponent;

import com.example.javafxdemo.usecaseeditor.drawablecomponent.DrawableComponent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class CanvasSystem extends BoxComponent {
    private static final int DEFAULT_RECTANGLE_WIDTH = 300;
    private static final int DEFAULT_RECTANGLE_HEIGHT = 400;

    public CanvasSystem() {
        super("System", 0, 0, DEFAULT_RECTANGLE_HEIGHT, DEFAULT_RECTANGLE_WIDTH);
    }

    public CanvasSystem(String title, double centerX, double centerY, double height, double width) {
        super(title, centerX, centerY, height, width);
    }

    @Override
    protected double getTitleYCoord() {
        final Text throwaway = new Text(title);
        new Scene(new Group(throwaway));
        return centerY + throwaway.getLayoutBounds().getHeight();
    }

    @Override
    public DrawableComponent createCopy() {
        return new CanvasSystem(title, centerX, centerY, height, width);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CanvasSystem that = (CanvasSystem) o;
        return title.equals(that.title) && centerX == that.getCenterX() && centerY == that.centerY
                && height == that.height && width == that.width;
    }

    @Override
    public void draw(GraphicsContext gc, Color color, int lineWidth) {
        double startX = centerX - (width / 2);
        double startY = centerY - (height / 2);

        gc.setStroke(color);
        gc.setLineWidth(lineWidth);
        gc.setLineDashes(0);

        gc.strokeRect(startX, startY, width, height);

        final Text throwaway = new Text(title);
        new Scene(new Group(throwaway));
        gc.setFill(Color.BLACK);
        gc.fillText(title, startX + width - throwaway.getLayoutBounds().getWidth(), startY + throwaway.getLayoutBounds().getHeight());
    }
}

