package model;

import java.util.Iterator;
import java.util.Random;

/* Project */
import utility.Point2d;

public class Enemy {

    private Point2d enemyPos;
    private boolean isDrowned;
    private boolean isAlive = true;

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
            /* Difficulty Easy -> the bot should be stupid */
            case EASY:
                moveDirection = this.moveDummbot(world);
                break;

            /* Difficulty Normal -> sometimes the bot should be stupid, sometimes not */
            case NORMAL:
                moveDirection = rnd.nextBoolean() ? this.moveDummbot(world) : this.moveBigBrainBot(world);
                break;

            /* Difficulty Hard -> the bot is more intelligent than you */
            case HARD:
                moveDirection = this.moveBigBrainBot(world);
                break;
        }
        // Check for possible collision with already moved enemies.
        // Iterate over already modified enemies
        Iterator<String> it = world.getEnemyPositions().iterator();
        // The point the enemy would move to, in case of no collision.
        String newPoint = new Point2d(this.getPositionX() + moveDirection.deltaX, this.getPositionY() + moveDirection.deltaY).toString();
        while (it.hasNext()) {
            // get next enemy to check.
            String handledPoint = it.next();
            // check for equality with simple string comparison.
            if (handledPoint.equals(newPoint)){
                // In case of collision, do not move.
                moveDirection = MovementDirection.NONE;
                break;
            }
        }
        this.getLocation().add(moveDirection.deltaX, moveDirection.deltaY);
    }

    public void updateFrame(World world, float deltaTime) {
        return;
    }

    public EnemyRenderState getRenderState() {
        return this.isDrowned ? EnemyRenderState.DROWNED : EnemyRenderState.ZOMBIE;
    }

    private MovementDirection moveBigBrainBot(World world) {
        return world.enemyPathingTable.enemyMoveCompute(this.getLocation());
    }

    /**
     * It just moves.
     * 
     * @param world The world to move on.
     * @return A movement-direction for the enemy to go to.
     */
    private MovementDirection moveDummbot(World world) {
        int distanceX = world.getPlayerX() - this.getPositionX();
        int distanceY = world.getPlayerY() - this.getPositionY();
        boolean handleXFirst = Math.abs(distanceX) >= Math.abs(distanceY);
        boolean distanceYnegative = distanceY < 0, distanceXnegative = distanceX < 0;
        MovementDirection[] priorityMoves = new MovementDirection[4];
        for (int i = 0; i < priorityMoves.length; i++) {
            if (handleXFirst) {
                if (distanceXnegative) {
                    priorityMoves[i] = MovementDirection.LEFT;
                } else {
                    priorityMoves[i] = MovementDirection.RIGHT;
                }
                distanceXnegative = !distanceXnegative;
            } else {
                if (distanceYnegative) {
                    priorityMoves[i] = MovementDirection.UP;
                } else {
                    priorityMoves[i] = MovementDirection.DOWN;
                }
                distanceYnegative = !distanceYnegative;
            }
            handleXFirst = !handleXFirst;
        }
        MovementDirection finalDirection = MovementDirection.NONE;
        for (MovementDirection checkDirection : priorityMoves) {
            int newPositionX = this.getPositionX() + checkDirection.deltaX;
            int newPositionY = this.getPositionY() + checkDirection.deltaY;
            if (world.canMoveToField(newPositionX, newPositionY)) {
                finalDirection = checkDirection;
                if (3 > world.collisionAroundField(newPositionX, newPositionY)) {
                    break;
                }
            }
        }

        return finalDirection;
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

    /**
     * Sets the life of the current enemy.
     * 
     * @param newLife The life to grant.
     */
    public void setIsAlive(boolean newLife){
        this.isAlive = newLife;
    }

    /**
     * Returns whether the given enemy is alive.
     * 
     * @return True if alive, false otherwise.
     */
    public boolean getIsAlive(){
        return this.isAlive;
    }
}