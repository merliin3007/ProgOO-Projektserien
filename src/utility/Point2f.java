package utility;

import java.util.Random;

public class Point2f {
    /** The x-axis coordinate. */
    private float x;
    /** The y-axis coordinate. */
    private float y;

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
    public static Point2f RandomPoint2f(float maxX ,float maxY) {
        Random rnd = new Random();
        return new Point2f(rnd.nextFloat(maxX), rnd.nextFloat(maxY));
    }

    public void addX(float x) { this.x += x; }

    public void addY(float y) { this.y += y; }

    public float getX() { return this.x; }

    public void setX(float x) { this.x = x; }

    public float getY() { return this.y; }

    public void setY(float y) { this.y = y; }

    @Override
    public String toString() {
        return String.format("(%f, %f)", this.x, this.y);
    }
}
