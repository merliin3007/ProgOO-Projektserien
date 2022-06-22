package model;

import utility.Point2d;

public class Enemy {
    
    private Point2d enemyPos;

    public Enemy(int x, int y) {
        enemyPos = new Point2d(x, y);
    }

    /**
     * Gets the current location of the enemy as a point.
     * 
     * @return The current point as a direct reference.
     */
    public Point2d getLocation(){
        return this.enemyPos;
    }
    /**
     * Gets the x-coordinate of the enemy.
     * 
     * @return The x-coordinate of the enemy.
     */
    public int getPositionX() { return this.enemyPos.getX(); }

    /**
     * Sets the x-coordinate of the enemy.
     * 
     * @param x The x-coordinate to set.
     */
    public void setPositionX(int x) { this.enemyPos.setX(x); }

   /**
    * Gets the y-coordinate of the enemy.
    *
    * @return The y-coordinate of the enemy.
    */
    public int getPositionY() { return this.enemyPos.getY(); }

    /**
     * Sets the x-coordinate of the enemy.
     * 
     * @param y The y-coordinate to set.
     */
    public void setPositionY(int y) { this.enemyPos.setY(y); }
}