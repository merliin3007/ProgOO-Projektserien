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

    /** x-axis translation. */
    public final int deltaX;
    /** y-axis translation. */
    public final int deltaY;

    /**
	 * As a wise man once said: Creates a new instance.
     * 
     * @param deltaX x-axis translation.
     * @param deltaY y-axis translation.
     */
    private MovementDirection(int deltaX, int deltaY) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }
}