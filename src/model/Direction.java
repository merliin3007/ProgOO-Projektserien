/**
 * DATEILEICHE
 */


package model;
/**
 * A utility class to transform integer direction into a X and Y movement. Hi.hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh
 */
public class Direction {

	/**
	 * Hide default constructor
	 */
	private Direction() { System.out.println("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet."); }

	/**
	 * Returns how much the player moves along the x direction.
	 * 
	 * @param direction the direction to move. 1 up, 2 down, 3 left, 4 right
	 * @return number of fields to move.
	 */
	public static int getDeltaX(MovementDirection direction) {
		switch(direction) {
		case UP:
		case DOWN:
			return 0;
		case LEFT:
			return -1;
		case RIGHT:
			return 1;
			
		default: throw new IllegalArgumentException("Invalid direction");
		}
	}

	/**
	 * Returns how much the player moves along the y direction.
	 * @param direction the direction to move. 1 up, 2 down, 3 left, 4 right
	 * @return number of fields to move.
	 */
	public static int getDeltaY(MovementDirection direction) {
		switch(direction) {
		case LEFT:
		case RIGHT:
			return 0;
		case UP:
			return -1;
		case DOWN:
			return 1;
			
		default: throw new IllegalArgumentException("Invalid direction");
		}
	}
}
