package model;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/* Project */
import utility.Point2d;

public class EnemyPathTable {

    int[][] shortestPathToPlayer;
    // used to know whether the given field has received an update in the current calculation already
    boolean[][] distanceUpdated;

    EnemyPathTable(World world) {
        int ySize = world.getHeight(), xSize = world.getWidth();
        this.shortestPathToPlayer = new int[ySize][xSize];
        this.distanceUpdated = new boolean[ySize][xSize];
        // insert collisions into it
        insertCollisionObjects(world.getObstacleMap());
        // compute the map once initially
        computeMap(world.getStartLocation());
    }
    
    /**
     * Inserts all the map-collisions into the map, so they are not calculated while
     * using the map
     *
     * @param collisionMap The collision-map of the world to consider.
     */
    public void removeCollisionObjects(boolean[][] collisionMap) {
        for (int y = 0; y < collisionMap.length; y++) {
            for (int x = 0; x < collisionMap[y].length; x++) {
                if (!collisionMap[y][x] && getValue(x, y) == Integer.MAX_VALUE) {
                    this.shortestPathToPlayer[y][x] = 0;
                }
            }
        }
    }

    /**
     * Inserts all the map-collisions into the map, so they are not calculated while
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
    public void computeMap(Point2d playerPos) {
        // reset updates
        resetDistanceUpdates();
        // start algorithm
        Queue<Point2d> queue = new LinkedList<Point2d>();
        queue.add(playerPos);
        while (!queue.isEmpty()) {
            Point2d curPoint2d = queue.remove();
            int potentialNewValue = getMinSurroundingValue(curPoint2d) + 1, actualValue = getValue(curPoint2d);
            if (!isUpdated(curPoint2d) || potentialNewValue < actualValue) {
                setValue(curPoint2d, potentialNewValue);
                registerUpdate(curPoint2d);
                for (MovementDirection direction : MovementDirection.values()) {
                    if (direction == MovementDirection.NONE) {
                        continue;
                    }
                    int newX = curPoint2d.getX() + direction.deltaX, newY = curPoint2d.getY() + direction.deltaY;
                    if (fieldNeedsToBeCalculated(newX, newY)
                            && (!isUpdated(newX, newY) || getValue(curPoint2d) + 1 < getValue(newX, newY))) {
                        queue.add(new Point2d(newX, newY));
                    }
                }
            }
        }
    }

    /**
     * Compute the next move for an enemy based on their given position.
     *
     * @param enemyPos The position of the enemy
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
                if (currentValue < min) {
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
            // do not handle the given position itself
            if (direction == MovementDirection.NONE) {
                continue;
            }
            if (fieldNeedsToBeCalculated(currentPos.getX() + direction.deltaX, currentPos.getY() + direction.deltaY)) {
                // check whether the field has a usable value
                if (isUpdated(currentPos.getX() + direction.deltaX, currentPos.getY() + direction.deltaY)) {
                    int currentValue = getValue(currentPos.getX() + direction.deltaX, currentPos.getY() + direction.deltaY);
                    if (currentValue < min) {
                        min = currentValue;
                    }
                }
            }
        }
        return min != Integer.MAX_VALUE ? min : -1; // special case for the first initialization.
    }

    /**
     * Determines whether a field needs to be calculated by the algorithm and is not a collision field on the map.
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
     * Checks whether the given position was updated during this computation
     *
     * @param pos The position, given as a point, to check.
     * @return True if it was updated already, false otherwise
     */
    private boolean isUpdated(Point2d pos) {
        return isUpdated(pos.getX(), pos.getY());
    }

    /**
     * Checks whether the given position was updated during this computation
     *
     * @param x The x - position to check
     * @param y The y - position to check
     * @return True if it was updated already, false otherwise
     */
    private boolean isUpdated(int x, int y) {
        return this.distanceUpdated[y][x];
    }

    /**
     * Registers an update for a given position
     *
     * @param pos The position to register the update at.
     */
    private void registerUpdate(Point2d pos) {
        // updates are collectively deleted, so just a setting method is required
        this.distanceUpdated[pos.getY()][pos.getX()] = true;
    }

    /**
     * Resets the array checking updates.
     */
    private void resetDistanceUpdates() {
        for (boolean[] row : this.distanceUpdated) {
            Arrays.fill(row, false);
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
}
