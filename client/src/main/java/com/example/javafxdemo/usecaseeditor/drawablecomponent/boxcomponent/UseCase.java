package com.example.javafxdemo.usecaseeditor.drawablecomponent.boxcomponent;

import com.example.javafxdemo.usecaseeditor.drawablecomponent.DrawableComponent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**Represents a class box with a single section for the class name*/
public class UseCase extends BoxComponent {
    //default sizes for newly created components
    private static final int DEFAULT_SINGLE_SECTION_BOX_HEIGHT = 50;
    private static final int DEFAULT_SINGLE_SECTION_BOX_WIDTH = 100;

    /**constructor*/
    public UseCase(){
        super("Use-Case",0, 0, DEFAULT_SINGLE_SECTION_BOX_HEIGHT, DEFAULT_SINGLE_SECTION_BOX_WIDTH);
    }

    /**
     * Constructor
     *
     * @param title the title to write on the component
     * @param centerX the x coordinate in the center of the object to draw
     * @param centerY the y coordinate in the center of the object to draw
     * @param height the height of the box
     * @param width  the width of the box
     */
    public UseCase(String title, double centerX, double centerY, double height, double width){
        super(title, centerX, centerY, height, width);
    }

    public void draw(GraphicsContext gc, Color color, int lineWidth) {
        //draw outside box
        double startX = centerX - (width / 2);
        double startY = centerY - (height / 2);
        gc.setStroke(color);
        gc.setLineWidth(lineWidth);
        gc.setLineDashes(0);
        gc.strokeOval(startX, startY, width, height);

        final Text throwaway = new Text(title);
        new Scene(new Group(throwaway));
        gc.setFill(Color.BLACK);
        gc.fillText(title,centerX - (throwaway.getLayoutBounds().getWidth()/2), getTitleYCoord());
    }

    @Override
    protected double getTitleYCoord(){
        final Text throwaway = new Text(title);
        new Scene(new Group(throwaway));
        return centerY + (throwaway.getLayoutBounds().getHeight()/4);
    }

    @Override
    public DrawableComponent createCopy() {
        return new UseCase(this.title, this.centerX, this.centerY, this.height, this.width);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UseCase that = (UseCase) o;
        return title.equals(that.title) && centerX == that.getCenterX() && centerY == that.centerY
                && height == that.height && width == that.width;
    }
}
