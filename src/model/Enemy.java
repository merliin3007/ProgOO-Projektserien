package model;

import java.util.Random;

import utility.Point2d;

public class Enemy {

    private Point2d enemyPos;
    private boolean isDrowned;

    public Enemy(int x, int y) {
        enemyPos = new Point2d(x, y);
        Random rnd = new Random();
        this.isDrowned = rnd.nextBoolean();
    }

    public void update(World world) {
        /* Move towards player */
        MovementDirection moveDirection = MovementDirection.NONE;
        Random rnd = new Random();
        switch (world.getDifficulty()) {
            case EASY:
                moveDirection = this.moveDummbot(world);
                break;

            case NORMAL:
                moveDirection = rnd.nextBoolean() 
                    ? this.moveDummbot(world) 
                    : world.enemyPathingTable.enemyMoveCompute(this.getLocation());
                break;
                
            case HARD:
                moveDirection = world.enemyPathingTable.enemyMoveCompute(this.getLocation());
                break;
        }
        this.getLocation().add(moveDirection.deltaX, moveDirection.deltaY);
    }

    public void updateFrame(World world, float deltaTime) {
        return;
    }

    public EnemyRenderState getRenderState() {
        return this.isDrowned ? EnemyRenderState.DROWNED : EnemyRenderState.ZOMBIE;
    }

    private MovementDirection moveDummbot(World world) {
        int distanceX = world.getPlayerX() - this.getPositionX();
        int distanceY = this.getPositionY() - world.getPlayerY();

        boolean moveTowardsPlayer = true;
        ///int[] newPosValues = new int[2];
        MovementDirection moveDirection = MovementDirection.NONE;
        boolean handleXFirst = Math.abs(distanceX) >= Math.abs(distanceY);
        for (int i = 0; i < 4; i++) {
            if (handleXFirst) {
                if (distanceX == 0) // already in same row, moving here is useless
                    continue;
                // handle x-value
                moveDirection = distanceX > 0 == moveTowardsPlayer ? MovementDirection.RIGHT : MovementDirection.LEFT;
                ///newPosValues[0] = this.getPositionX() + (distanceX > 0 == moveTowardsPlayer ? 1 : -1);
                ///newPosValues[1] = this.getPositionY();
            } else {
                if (distanceY == 0) // already in same row, moving here is useless
                    continue;
                // handle y-value
                moveDirection = distanceY > 0 == moveTowardsPlayer ? MovementDirection.UP : MovementDirection.DOWN;
                ///newPosValues[0] = this.getPositionX();
                ///newPosValues[1] = this.getPositionY() + (distanceY > 0 == moveTowardsPlayer ? -1 : 1);
            }
            int newPositionX = this.getPositionX() +  moveDirection.deltaX, newPositionY = this.getPositionY() + moveDirection.deltaY;
            if (world.canMoveToField(newPositionX, newPositionY) && 3 > world.collisionAroundField(newPositionX, newPositionY)) {
                // successful move
                break; 
            }
            handleXFirst = !handleXFirst; // toggle if move unsuccessful
            if (i == 1)  // tried moving in player direction, now try opposite
                moveTowardsPlayer = false;
        }

        return moveDirection;
    }

    /**
     * Gets the current location of the enemy as a point.
     * 
     * @return The current point as a direct reference.
     */
    public Point2d getLocation() {
        return this.enemyPos;
    }

    /**
     * Gets the x-coordinate of the enemy.
     * 
     * @return The x-coordinate of the enemy.
     */
    public int getPositionX() {
        return this.enemyPos.getX();
    }

    /**
     * Sets the x-coordinate of the enemy.
     * 
     * @param x The x-coordinate to set.
     */
    public void setPositionX(int x) {
        this.enemyPos.setX(x);
    }

    /**
     * Gets the y-coordinate of the enemy.
     *
     * @return The y-coordinate of the enemy.
     */
    public int getPositionY() {
        return this.enemyPos.getY();
    }

    /**
     * Sets the x-coordinate of the enemy.
     * 
     * @param y The y-coordinate to set.
     */
    public void setPositionY(int y) {
        this.enemyPos.setY(y);
    }
}