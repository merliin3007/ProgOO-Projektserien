package view;


import utility.Console;
import model.World;
import model.Enemy;

/**
 * A view that prints the current state of the world to the console upon every
 * update.
 */
public class ConsoleView implements View {

	boolean isEnabled = true;

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

	@Override
	public boolean getIsEnabled() { return this.isEnabled; }

	@Override
	public void setIsEnabled(boolean isEnabled) { this.isEnabled = isEnabled; }

}
