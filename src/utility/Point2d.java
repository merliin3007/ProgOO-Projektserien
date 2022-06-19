package utility;

import java.security.cert.PolicyNode;
import java.util.Random;

/**
 * A class that represents a 2d integer point.
 */
public class Point2d {
    /** The x-axis position. */
    private int x;
    /** The y-axis position. */
    private int y;

    /**
     * Initializes a two-dimensional point with the provided values.
     * 
     * @param x The x-coordinate to set.
     * @param y The y-coordinate to set.
     */
    public Point2d(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Creates a Copy of this point.
     * 
     * @return the Copy.
     */
    public Point2d copy() {
        return new Point2d(this.getX(), this.getY());
    }

    /**
     * Generates a new random point.
     * 
     * @param maxX the maximum value for the x-coordinate.
     * @param maxY the maximum value for the y-coordinate.
     * 
     * @return a new random point.
     */
    public static Point2d RandomPoint2d(int maxX, int maxY) {
        Random rnd = new Random();
        return new Point2d(rnd.nextInt(maxX), rnd.nextInt(maxY));
    }

    /**
     * Checks whether the given point is with in a square of a given size around another point.
     * 
     * @param otherPoint the other point.
     * @param range the size of the square.
     * 
     * @return whether this point is in the range of the other point or not.
     */
    public boolean inRangeOf(Point2d otherPoint, int range) {
        range = Math.abs(range);
        for (int xOffset = -range; xOffset <= range; xOffset++) {
            for (int yOffset = -range; yOffset <= range; yOffset++) {
                if (getX() + xOffset == otherPoint.getX() || getY() + yOffset == otherPoint.getY())
                    return true;
            }
        }
        return false;
    }

    /**
     * Checks whether two points are equal
     * @param point1    The first point to check
     * @param point2    The second point to check
     * @return  True in case both points are equal, false otherwies
     */
    public static boolean equalPoints(Point2d point1, Point2d point2){
        return point1.getX() == point2.getX() && point1.getY() == point2.getY();
    }

    /**
     * Adds an integer to the x-coordinate.
     * 
     * @param x the value to add to the x-coordinate.
     */
    public void addX(int x) {
        this.x += x;
    }

    /**
     * Adds an integer to the y-coordinate.
     * 
     * @param y the value to add to the y-coordinate.
     */
    public void addY(int y) {
        this.y += y;
    }
    /**
     * Gets the x-coordinate of the two-dimensional point. 
     * 
     * @return The x-coordinate-value
     */
    public int getX() {
        return this.x;
    }
    /**
     * Sets the x-value of the two-dimensional point.
     * 
     * @param x The x-value to set.
     */
    public void setX(int x) {
        this.x = x;
    }
    /**
     * Gets the y-coordinate of the two-dimensional point. 
     * 
     * @return The y-coordinate-value
     */
    public int getY() {
        return this.y;
    }

    /**
     * Sets the y-value of the two-dimensional point.
     * 
     * @param y The y-value to set.
     */
    public void setY(int y) {
        this.y = y;
    }
    
    @Override
    public String toString() {
        return String.format("(%d, %d)", this.x, this.y);
    }
}
