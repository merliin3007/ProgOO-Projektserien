package utility;

import java.security.cert.PolicyNode;
import java.util.Random;

public class Point2d {
    private int x;
    private int y;

    public Point2d(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Point2d RandomPoint2d(int maxX ,int maxY) {
        Random rnd = new Random();
        return new Point2d(rnd.nextInt(maxX), rnd.nextInt(maxY));
    }

    public void addX(int x) { this.x += x; }

    public void addY(int y) { this.y += y; }

    public int getX() { return this.x; }

    public void setX(int x) { this.x = x; }

    public int getY() { return this.y; }

    public void setY(int y) { this.y = y; }

    @Override
    public String toString() {
        return String.format("(%d, %d)", this.x, this.y);
    }
}
