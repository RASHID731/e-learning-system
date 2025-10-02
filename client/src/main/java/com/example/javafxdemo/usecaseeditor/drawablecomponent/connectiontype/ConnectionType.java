package com.example.javafxdemo.usecaseeditor.drawablecomponent.connectiontype;

import com.example.javafxdemo.usecaseeditor.drawablecomponent.connectiontype.connectionhead.Arrow;
import com.example.javafxdemo.usecaseeditor.drawablecomponent.connectiontype.connectionhead.ConnectionHead;
import javafx.scene.canvas.GraphicsContext;
import lombok.Getter;
import org.javatuples.Pair;

public enum ConnectionType {
    Ausloeser(null, LineStyle.SOLID_LINE),
    Beziehung(new Arrow(), LineStyle.DASHED_LINE);

    //the strategy for drawing the arrow head
    private final ConnectionHead connectionHead;
    //the space to be set in between line dashes (0 if line is solid)
    @Getter
    private final int dashedLineGap;

    private static class LineStyle{
        private static final int DASHED_LINE = 5;
        private static final int SOLID_LINE = 0;
    }

    /**
     * Constructor
     *
     * @param connectionHead the strategy for drawing the arrow head
     */
    ConnectionType(ConnectionHead connectionHead, int dashedLineGap){
        this.connectionHead = connectionHead;
        this.dashedLineGap = dashedLineGap;
    }

    /**
     * draws the head on the given GraphicsContext with the specified color and line thickness
     *
     * @param gc the GraphicsContext of the canvas to draw on

     */
    public void drawHead(GraphicsContext gc, Pair<Double, Double> lastPoint, Pair<Double, Double> secondLast) {
        if(connectionHead != null){
            connectionHead.drawHead(gc, lastPoint, secondLast);
        }
    }
}
