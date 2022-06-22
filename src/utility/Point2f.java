package utility;

import java.util.Random;

/**
 * A class to store a two-dimensional point with float values.
 */
public class Point2f {
    /** The x-axis coordinate. */
    private float x;
    /** The y-axis coordinate. */
    private float y;

    /**
     * Initializes the two-dimensional point with the given vaulues.
     * 
     * @param x The x-axis coordinate.
     * @param y The y-axis coordinate.
     */
    public Point2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Generates a new random point.
     * 
     * @param maxX the maximum value for the x-coordinate.
     * @param maxY the maximum value for the y-coordinate.
     * 
     * @return a new random point.
     */
    public static Point2f RandomPoint2f(float maxX, float maxY) {
        Random rnd = new Random();
        return new Point2f(rnd.nextFloat(maxX), rnd.nextFloat(maxY));
    }

    /** 
     * Adds a value onto the points x-axis coordinate
     * 
     * @param x the value to add to the x-axis coordinate
     */
    public void addX(float x) {
        this.x += x;
    }

    /** 
     * Adds a value onto the points y-axis coordinate
     * 
     * @param y the value to add to the y-axis coordinate
     */
    public void addY(float y) {
        this.y += y;
    }

    /** 
     * Gets the x-axis coordinate. 
     *
     * @return float the x-axis coordinate
     */
    public float getX() {
        return this.x;
    }

    /**
     * Sets the value of the x-position of the point.
     * 
     * @param x The x-value to set
     */
    public void setX(float x) {
        this.x = x;
    }

    /** 
     * Gets the y-value of the float.
     * 
     * @return The y-value
     */
    public float getY() {
        return this.y;
    }
    
    /**
     * Sets the value of the y-position of the point.
     * 
     * @param y The y-value to set
     */
    public void setY(float y) {
        this.y = y;
    }

    /** 
     * Returns the values humanly readable
     * 
     * @return The values seperated by a comma, humanly readable.
     */
    @Override
    public String toString() {
        return String.format("(%f, %f)", this.x, this.y);
    }
}
