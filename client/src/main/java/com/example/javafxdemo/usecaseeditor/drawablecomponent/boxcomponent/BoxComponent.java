package com.example.javafxdemo.usecaseeditor.drawablecomponent.boxcomponent;

import com.example.javafxdemo.usecaseeditor.drawablecomponent.DrawableComponent;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**Represents DrawableComponents that are box-shaped*/
@Setter @Getter
public abstract class BoxComponent extends DrawableComponent {
    //the width of the component
    @JsonProperty("width")
    protected double width;
    //the height of the component
    protected double height;
    //the x coordinate of the center of the component
    protected double centerX;
    //the y coordinate of the center of the component
    protected double centerY;
    //the title to be written on the component
    protected String title;
    //the text field used to edit the title of the component
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    protected TextField titleTextField;

    /**
     * Constructor
     *
     * @param title the title to write on the component
     * @param centerX the x coordinate in the center of the object to draw
     * @param centerY the y coordinate in the center of the object to draw
     * @param height the height of the object to draw
     * @param width the width of the object to draw
     */

    protected BoxComponent(String title, double centerX, double centerY, double height, double width) {
        this.title = title;
        this.centerX = centerX;
        this.centerY = centerY;
        this.height = height;
        this.width = width;
    }

    @Override
    public VBox fetchUpdateContentsDialog(){
        VBox vbox = new VBox();
        HBox hbox = new HBox();
        Label titleLabel = new Label("Title: ");
        titleTextField = new TextField(title);
        hbox.getChildren().add(titleLabel);
        hbox.getChildren().add(titleTextField);
        vbox.getChildren().add(hbox);
        return vbox;
    }

    @Override
    public void updateContents() { title = titleTextField.getText(); }

    public boolean checkPointInBounds(double x, double y) {
        double startX = centerX - (width / 2);
        double startY = centerY - (height / 2);

        return startX < x && startY < y && startX + width > x && startY + height > y;
    }

    /**
     * fetch the y coordinate to draw the component's title
     *
     * @return the y coordinate to draw the component's title
     */
    protected abstract double getTitleYCoord();
}
