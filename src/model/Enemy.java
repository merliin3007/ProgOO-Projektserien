package model;

public class Enemy {
    private int positionX;
    private int positionY;

    private float health; // TODO: implement health

    private float damage;

    public Enemy(int x, int y) {
        this.positionX = x;
        this.positionY = y;
    }

    /**
     * Getters / Setters
     */

    public int getPositionX() { return this.positionX; }

    public void setPositionX(int x) { this.positionX = x; }
   
    public int getPositionY() { return this.positionY; }

    public void setPositionY(int y) { this.positionY = y; }
}