package utility;

/**
 * A point
 * with four integers
 */
public class Point2d {

    private int xpos;
    private int ypos;
    private int startXpos;
    private int startYpos;
    private int pathLength;

    /**
     * @param x the x coordinate
     * @param y the y coordinate
     * @param startX the x start coordinate
     * @param startY the y start coordinate
     * @param pathLength the length of se path
     */
    public Point2d(int x, int y, int startX, int startY, int pathLength) {
        this.xpos = x;
        this.ypos = y;
        this.startXpos = startX;
        this.startYpos = startY;
        this.pathLength = pathLength;
    }

    public void setPosX(int x) {
        this.xpos = x;
    }

    public void setPosY(int y) {
        this.ypos = y;
    }
    
    public void setPosStartX(int x) {
        this.startXpos = x;
    }

    public void setPosStartY(int y) {
        this.startYpos = y;
    }

    public void setPathLength(int l) {
        this.pathLength = l;
    }

    public int getPosX() {
        return this.xpos;
    }

    public int getPosY() {
        return this.ypos;
    }
    public int getPosStartX() {
        return this.startXpos;
    }

    public int getPosStartY() {
        return this.startYpos;
    }
    @Override
    public String toString() {
        return "{ "  + this.getPosX() + ", " + this.getPosY() + ", " + this.getPosStartX() + ", " + this.getPosStartY() + " }";
    }

    public int getPathLength() {
        return this.pathLength;
    }
}
