package model;
/**
 * A utility class to transform integer direction into a X and Y movement.
 */
public class Direction {

	/**
	 * Hide default constructor
	 */
	private Direction() {}

	/**
	 * Returns how much the player moves along the x direction.
	 * 
	 * @param direction the direction to move. 1 up, 2 down, 3 left, 4 right
	 * @return number of fields to move.
	 */
	public static int getDeltaX(int direction) {
		switch(direction) {
		case 1:
		case 2:
			return 0;
		case 3:
			return -1;
		case 4:
			return 1;
			
		default: throw new IllegalArgumentException("Invalid direction");
		}
	}

	/**
	 * Returns how much the player moves along the y direction.
	 * @param direction the direction to move. 1 up, 2 down, 3 left, 4 right
	 * @return number of fields to move.
	 */
	public static int getDeltaY(int direction) {
		switch(direction) {
		case 3:
		case 4:
			return 0;
		case 1:
			return -1;
		case 2:
			return 1;
			
		default: throw new IllegalArgumentException("Invalid direction");
		}
	}
}
