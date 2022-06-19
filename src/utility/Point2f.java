package utility;

import java.util.Random;

public class Point2f {
    private float x;
    private float y;

    public Point2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

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
