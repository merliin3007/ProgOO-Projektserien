package view;

/* Project */
import utility.Console;
import utility.Point2d;
import model.World;
import model.Enemy;

/**
 * A view that prints the current state of the world to the console upon every
 * update.
 */
public class ConsoleView implements View {

	/** Whehter this view is enabled or not */
	boolean isEnabled = true;

	/**
	 * Update the view.-
	 * 
	 * @world The world is ours.
	 */
	@Override
	public void update(World world) {
		if (!isEnabled) {
			return;
		}

		Console.clear();
		
		// The player's position
		int playerX = world.getPlayerX();
		int playerY = world.getPlayerY();

		for (int row = 0; row < world.getHeight(); row++) {
			for (int col = 0; col < world.getWidth(); col++) {
				boolean printEnemy = false;
				for (Enemy enemy : world.getEnemies()) {
					if (enemy.getPositionX() == col && enemy.getPositionY() == row) {
						printEnemy = true;
					}
				}
				// If the player is here, print #, otherwise print .
				if (printEnemy) {
					System.out.print('!');
				} else if (row == playerY && col == playerX) {
					System.out.print("#");
				} else if (world.getField(col, row)) {
					System.out.print("O");
				} else {
					System.out.print(".");
				}
			}

			// A newline after every row
			System.out.println();
		}

		// A newline between every update
		System.out.println();
	}

	/**
	 * Gets whether this view is enabled or not.
	 * 
	 * @return Whether this view is enabled or not.
	 */
	@Override
	public boolean getIsEnabled() { return this.isEnabled; }

	/**
	 * Sets whether this view is enabled or not.
	 * 
	 * @return Wheter this view is enabled or not.
	 */
	@Override
	public void setIsEnabled(boolean isEnabled) { this.isEnabled = isEnabled; }

	/** 
	 * Updates the camera.
	 * As the camera for the ConsoleView doesn' change position, zoom or 
	 * anything else, this method does nothing.
	 * 
	 * @param world The world of which the camera will not have changed.
	 * @param deltaTime The time since the last call to this method.
	 */
	@Override
	public void updateCamera(World world, float deltaTime) {
		return;
	}

	/**
	 * Gets called whenever the level of the world has changed.
	 * 
	 * @param world The world of which the level has changed.
	 */
	@Override
	public void onLevelChanged(World world) {
		return;
	}

	/**
	 * Why do u care? Its obviously doing nothing. But very effecitve.
	 */
	@Override
	public void spawnExplosion(Point2d position, float size) {
		return;
	}

	/**
	 * Why do u care? Its obviously doing nothing. But very effecitve.
	 */
	@Override
	public void triggerEnvironmentEvent(EnvironmentEvent event) { 
		return;
	}

}
