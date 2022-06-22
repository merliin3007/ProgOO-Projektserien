package model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import utility.Point2d;

public class EnemyPathTable {

    // the value to initialize the array with (since 0 is a bit dumb, you know...)
    int INITVALUE = -1;
    int[][] shortestPathToPlayer;
    Point2d lastPlayerPos;

    EnemyPathTable(World world) {
        this.shortestPathToPlayer = new int[world.getHeight()][world.getWidth()];
        // initialize the map with an invalid value
        fillPathMap();
        // insert collisions into it
        insertCollisionObjects(world.getObstacleMap());
        // compute the map once initially
        computeInitialMap(world.getStartLocation());
        this.lastPlayerPos = world.getStartCopy();
    }

    /**
     * Inserts all the map-collsions into the map so they are not calculated while
     * using the map
     * 
     * @param collisionMap The collision-map of the world to consider.
     */
    private void insertCollisionObjects(boolean[][] collisionMap) {
        for (int y = 0; y < collisionMap.length; y++) {
            for (int x = 0; x < collisionMap[y].length; x++) {
                if (collisionMap[y][x]) {
                    this.shortestPathToPlayer[y][x] = Integer.MAX_VALUE;
                }
            }
        }
    }

    /**
     * Computes the player-distance-map based upon the given playerPosition.
     * 
     * @param playerPos The player-position used to compute the distance map.
     */
    private void computeInitialMap(Point2d playerPos) {
        Queue<Point2d> queue = new LinkedList<Point2d>();
        queue.add(playerPos);
        while (!queue.isEmpty()) {
            Point2d curPoint2d = queue.remove();
            int potentialNewValue = getMinSurroundingValue(curPoint2d) + 1, actualValue = getValue(curPoint2d);
            if (potentialNewValue < actualValue || actualValue == INITVALUE) {
                setValue(curPoint2d, potentialNewValue);
                for (MovementDirection direction : MovementDirection.values()) {
                    if (direction == MovementDirection.NONE) {
                        continue;
                    }
                    int newX = curPoint2d.getX() + direction.deltaX, newY = curPoint2d.getY() + direction.deltaY;
                    if (fieldNeedsToBeCalculated(newX, newY)
                            && (getValue(newX, newY) == INITVALUE || getValue(curPoint2d) + 1 < getValue(newX, newY))) {
                        queue.add(new Point2d(newX, newY));
                    }
                }
            }
        }
    }

    /**
     * Updates the path map based upon player movement.
     * 
     * @param movedDirection The direction the player went to.
     */
    public void updatePlayerPosOnEnemyMap(MovementDirection movedDirection) {
        // if no update - do not update
        if (movedDirection == MovementDirection.NONE) {
            return;
        }
        // update relevant values
        increaseValue(this.lastPlayerPos);
        this.lastPlayerPos.add(movedDirection.deltaX, movedDirection.deltaY);
        decreaseValue(this.lastPlayerPos);
        // handle other values
        Queue<Point2d> queue = new LinkedList<Point2d>();
        // update surrounding
        for (MovementDirection direction : MovementDirection.values()) {
            if (direction == MovementDirection.NONE) {
                continue;
            }
            int newX = this.lastPlayerPos.getX() + direction.deltaX, newY = this.lastPlayerPos.getY() + direction.deltaY;
            if (fieldNeedsToBeCalculated(newX, newY)) {
                queue.add(new Point2d(newX, newY));
            }
        }
        // normal update
        while (!queue.isEmpty()) {
            Point2d curPoint2d = queue.remove();
            // TODO check logic
            int potentialNewValue = getMinSurroundingValue(curPoint2d) + 1, actualValue = getValue(curPoint2d);
            if (potentialNewValue != actualValue && actualValue != 0) {
                setValue(curPoint2d, potentialNewValue);
                for (MovementDirection direction : MovementDirection.values()) {
                    if (direction == MovementDirection.NONE) {
                        continue;
                    }
                    int newX = curPoint2d.getX() + direction.deltaX, newY = curPoint2d.getY() + direction.deltaY;
                    if (fieldNeedsToBeCalculated(newX, newY) && getValue(curPoint2d) + 1 < getValue(newX, newY)) {
                        queue.add(new Point2d(newX, newY));
                    }
                }
            }
        }
    }

    /**
     * Compute the next move for an enemy based on their given position.
     * 
     * @param enemyPos The positon of the enemy
     * @return The direction to go to next, given as an Enum
     */
    public MovementDirection enemyMoveCompute(Point2d enemyPos) {
        MovementDirection enemyMove = MovementDirection.NONE;
        int min = Integer.MAX_VALUE;
        for (MovementDirection potentialDirection : MovementDirection.values()) {
            if (fieldNeedsToBeCalculated(enemyPos.getX() + potentialDirection.deltaX,
                    enemyPos.getY() + potentialDirection.deltaY)) {
                int currentValue = getValue(enemyPos.getX() + potentialDirection.deltaX,
                        enemyPos.getY() + potentialDirection.deltaY);
                if (currentValue < min && currentValue != INITVALUE) {
                    min = currentValue;
                    enemyMove = potentialDirection;
                }
            }
        }
        return enemyMove;
    }

    /**
     * Gets the minimal value of the given field
     * 
     * @param currentPos The field to check
     * @return The minimal value around that field.
     */
    private int getMinSurroundingValue(Point2d currentPos) {
        int min = Integer.MAX_VALUE;
        for (MovementDirection direction : MovementDirection.values()) {
            // if (direction == MovementDirection.NONE){
            // continue;
            // }
            if (fieldNeedsToBeCalculated(currentPos.getX() + direction.deltaX, currentPos.getY() + direction.deltaY)) {
                int currentValue = getValue(currentPos.getX() + direction.deltaX, currentPos.getY() + direction.deltaY);
                if (currentValue < min && currentValue != INITVALUE) {
                    min = currentValue;
                }
            }
        }
        return min != Integer.MAX_VALUE ? min : INITVALUE; // special case for the first initialization.
    }

    /**
     * Determines whether a field needs to be calculated by the algorithm.
     * 
     * @param x The x-coordinate of the field
     * @param y The y-coordinate of the field
     * @return True if it needs to be considered, false otherwise.
     */
    private boolean fieldNeedsToBeCalculated(int x, int y) {
        if (!inField(x, y)) {
            return false;
        }
        return getValue(x, y) != Integer.MAX_VALUE;
    }

    /**
     * Determines whether a field needs to be calculated by the algorithm.
     * 
     * @param pos The field to check.
     * @return True if it needs to be handled, false otherwise.
     */
    private boolean fieldNeedsToBeCalculated(Point2d pos) {
        return fieldNeedsToBeCalculated(pos.getX(), pos.getY());
    }

    /**
     * Checks whether the given point is in the table
     * 
     * @param pos The position to check
     * @return True if in the field, false otherwise.
     */
    private boolean inField(Point2d pos) {
        return inField(pos.getX(), pos.getY());
    }

    /**
     * Checks whether the given point is in the table
     * 
     * @param x The x-coordinate of the position to check
     * @param y The y-coordinate of the position to check
     * @return True if in the field, false otherwise.
     */
    private boolean inField(int x, int y) {
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
    }

    /**
     * Fills the map with the value -1 for later computations.
     */
    private void fillPathMap() {
        for (int row[] : this.shortestPathToPlayer) {
            Arrays.fill(row, INITVALUE);
        }
    }

    /**
     * Sets the value of the given field in the distance map.
     * 
     * @param pos   The position of the value to set.
     * @param value The value to set.
     */
    private void setValue(Point2d pos, int value) {
        this.shortestPathToPlayer[pos.getY()][pos.getX()] = value;
    }

    /**
     * Increases the value of the given field in the distance map by 1.
     * 
     * @param pos The position of the value to increase.
     */
    private void increaseValue(Point2d pos) {
        this.shortestPathToPlayer[pos.getY()][pos.getX()]++;
    }

    /**
     * Decreases the value of the given field in the distance map by 1.
     * 
     * @param pos The position of the value to decrease.
     */
    private void decreaseValue(Point2d pos) {
        this.shortestPathToPlayer[pos.getY()][pos.getX()]--;
    }

    /**
     * Decreases the value of the given field in the distance map by 1.
     * 
     * @param x The x-coordinate of the value to decrease.
     * @param y The y-coordinate of the value to decrease.
     */
    private void decreaseValue(int x, int y) {
        this.shortestPathToPlayer[y][x]--;
    }

    /**
     * Gets the value of the given field in the distance map.
     * 
     * @param pos The position of the value to get.
     * @return The value.
     */
    private int getValue(Point2d pos) {
        return getValue(pos.getX(), pos.getY());
    }

    /**
     * Gets the value of the given field in the distance map.
     * 
     * @param x The x - position of the value to get.
     * @param y The y - position of the value to get.
     * @return The value.
     */
    private int getValue(int x, int y) {
        return this.shortestPathToPlayer[y][x];
    }

    /**
     * Gets the height of the table.
     * 
     * @return The height of the table.
     */
    private int getHeight() {
        return this.shortestPathToPlayer.length;
    }

    /**
     * Gets the width of the table.
     * 
     * @return The width of the table.
     */
    private int getWidth() {
        return this.shortestPathToPlayer[0].length;
    }

    /**
     * Prints the pathtable
     */
    public void print() {
        for (int[] row : this.shortestPathToPlayer) {
            System.out.println(Arrays.toString(row));
        }
    }
}
