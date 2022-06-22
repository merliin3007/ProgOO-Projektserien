package view;

import java.awt.Graphics;

import utility.Point2d;

/**
 * This Class represents a drawable Object.
 */
public abstract class RenderObject {

    /* The position of the object */
    private Point2d position;
    /* The lighting of the object */
    private Lighting lighting;

    /**
     * Draws the RenderObject to a GraphicView.
     * 
     * @param g the Graphics to draw on
     * @param view the parent-view of the RenderObject
     */
    public abstract void draw(Graphics g, GraphicView view);

    /**
     * Gets the position of the object.
     * 
     * @return the position.
     */
    public Point2d getPosition() {
        return this.position;
    }

    /**
     * Sets the position of the object.
     * 
     * @param position the new position.
     */
    public void setPosition(Point2d position) {
        this.position = position;
    }

    /**
     * Gets the lighing of the object.
     * 
     * @return the lighting.
     */
    public Lighting getLighting() {
        return this.lighting;
    }

    /**
     * Sets the lighting of the objectz.
     * 
     * @param lighting the lighting.
     */
    public void setLighting(Lighting lighting) {
        this.lighting = lighting;
    }
}
