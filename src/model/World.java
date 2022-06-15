package model;

import java.util.ArrayList;
import java.util.Random;

import view.View;
import model.Enemy;

/**
 * The world is our model. It saves the bare minimum of information required to
 * accurately reflect the state of the game. Note how this does not know
 * anything about graphics.
 */
public class World {

	public static final int DIR_RIGHT = 3;
	public static final int DIR_LEFT = 2;
	public static final int DIR_DOWN = 1;
	public static final int DIR_UP = 0;
	/** The world's width. */
	private final int width;
	/** The world's height. */
	private final int height;

	private int level = 1;
	
	/** The player's x position in the world. */
	private int playerX = 0;
	/** The player's y position in the world. */
	private int playerY = 0;

	private ArrayList<Enemy> enemies;

	/* The X and Y position of the start field. */
	private int startX = 0;
	private int startY = 0;

	/* The x and y position of the finish field. */
	private int finishX = 0;
	private int finishY = 0;

	/** Set of views registered to be notified of world updates. */
	private final ArrayList<View> views = new ArrayList<>();

	/** creates a map of obstacles for the labyrinth, where "true" is an obstacle */
	private boolean[][] obstacleMap;
	private byte[][] playerTailMap;
	/**
	 * Creates a new world with the given size.
	 */
	public World(int width, int height) {
		// Normally, we would check the arguments for proper values
		this.width = width;
		this.height = height;
		this.obstacleMap = new boolean[width][height];
		this.playerTailMap = new byte[width][height];

		this.enemies = new ArrayList<Enemy>();
		
		/* This is some ugly temporary stuff dude */
		/*
		for (int i = 0; i < width; ++i) {
			for (int j = 0; j < height; ++j) {
				if(((i == 0 || i == width - 1)&& j % 2 == 0 )||((j == 0 || j == height - 1)&& i % 2 == 0 ) )
					this.obstacleMap[i][j] = true;
			}
		}*/

		//if (true) return;
		/*
		Random rnd = new Random();
		for (int i = 0; i < this.getWidth() * this.getHeight() * this.level; ++i) {
			for (int x = rnd.nextInt(this.getWidth() - 1), y = rnd.nextInt(this.getHeight() - 1);;) {
				MovementDirection dir = MovementDirection.UP;
				switch(rnd.nextInt(4)) {
				case 0: dir = MovementDirection.UP; break;
				case 1: dir = MovementDirection.DOWN; break;
				case 2: dir = MovementDirection.LEFT; break;
				case 3: dir = MovementDirection.RIGHT; break;
				}
				x += dir.deltaX;
				y += dir.deltaY;
				if (x < 0 || x >= this.getWidth() || y < 0 || y >= this.getHeight()) {
					break;
				} else if (this.getField(x + dir.deltaX, y + dir.deltaX) || this.getField(x + dir.deltaX * 2, y + dir.deltaY * 2)) {
					break;
				} else if (false) {
					break;
				} else {
					this.setObstacleInField(x + dir.deltaX, y + dir.deltaY);
				}
			}
		}*/
	}

	public void timerTick() {
		// TODO: was mitm timer machen, weils geht
	}

	// Getters and Setters

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getPlayerX() {
		return playerX;
	}
	// TODO remove
	public void setPlayerX(int playerX) {
		/*
		 * border logic in controller
		 */
		//playerX = Math.max(0, playerX);
		//playerX = Math.min(getWidth() - 1, playerX);
		this.playerX = playerX;
		
		updateViews();
	}

	public int getPlayerY() {
		return playerY;
	}
	// TODO remove
	public void setPlayerY(int playerY) {
		/*
		 * border logic in controller
		 */
		//playerY = Math.max(0, playerY);
		//playerY = Math.min(getHeight() - 1, playerY);
		this.playerY = playerY;
		
		updateViews();
	}

	/**
	 * returns the x-coordinate of the start-field
	 * @return The x-value.
	*/
	public int getStartX() { return this.startX; }
	
	/**
	 * Sets the x-coordinate of the start-field
	 * @param x The new x of the start-field
	 */
	public void setStartX(int x) { this.startX = x; }

	/**
	 * returns the y-coordinate of the start-field
	 * @return The y-value.
	*/
	public int getStartY() { return this.startY; }
	
	/**
	 * Sets the y-coordinate of the start-field
	 * @param y The new y of the start-field
	 */
	public void setStartY(int y) { this.startY = y; }

	/**
	 * Sets the x-coordinate of the finish-field
	 * @param x The new x of the finish-field
	 */
	public void setFinishX(int x) { this.finishX = x; }
	
	/**
	 * Sets the y-coordinate of the finish-field
	 * @param y The new y of the finish-field
	 */
	public void setFinishY(int y) { this.finishY = y; }
	
	/**
	 * returns the x-coordinate of the finish-field
	 * @return The x-value.
	*/
	public int getFinishX() { return this.finishX; }

	/**
	 * returns the y-coordinate of the finish-field
	 * @return The y-value.
	*/
	public int getFinishY() { return this.finishY; }

	public ArrayList<Enemy> getEnemies() { return this.enemies; }

	public boolean[][] getObstacleMap() { return this.obstacleMap; }

	/**
	 * returns whether the checked field has a collision object in it
	 * @param xPos	The x-position to check
	 * @param yPos	The y-position to check
	 * @return true in case of a 
	 */
	public boolean getField(int xPos, int yPos) {
		return (xPos < 0 || xPos >= this.getWidth() || yPos < 0 || yPos >= this.getHeight()) ? true : this.obstacleMap[yPos][xPos];
	}
	///////////////////////////////////////////////////////////////////////////
	// Player Management
	void setObstacleInField(int xPos, int yPos){
		if (xPos >= 0 && xPos < this.obstacleMap.length && yPos >= 0 && yPos < this.obstacleMap[0].length)
			this.obstacleMap[xPos][yPos] = true;
		else
			System.out.println("Error: Tried setting obstacle outside map");
	}
	/**
	 * Moves the player along the given direction.
	 * 
	 * @param direction where to move. 1 up, 2 down, 3, left, 4 right
	 */
	public void movePlayer(MovementDirection direction) {	
		// The direction tells us exactly how much we need to move along
		// every direction
		setPlayerX(getPlayerX() + direction.deltaX);
		setPlayerY(getPlayerY() + direction.deltaY);
	}

	/**
	 * Sets a new position for the player entity
	 * @param xPos	The x-coordinate
	 * @param yPos	The y-coordinate
	 */
	public void setPlayerLocation(int xPos, int yPos) {
		setPlayerX(xPos);
		setPlayerY(yPos);
	}

	///////////////////////////////////////////////////////////////////////////
	// View Management

	/**
	 * Adds the given view of the world and updates it once. Once registered through
	 * this method, the view will receive updates whenever the world changes.
	 * 
	 * @param view the view to be registered.
	 */
	public void registerView(View view) {
		views.add(view);
		view.update(this);
	}

	/**
	 * Updates all views by calling their {@link View#update(World)} methods.
	 */
	private void updateViews() {
		for (int i = 0; i < views.size(); i++) {
			views.get(i).update(this);
		}
	}

}
