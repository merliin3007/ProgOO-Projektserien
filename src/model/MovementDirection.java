package model;
/**
 * Enum used to provide X and Y movement for given direction
 */
public enum MovementDirection {
    NONE(0, 0),
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0);

    public final int deltaX;
    public final int deltaY;

    private MovementDirection(int deltaX, int deltaY) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }
}