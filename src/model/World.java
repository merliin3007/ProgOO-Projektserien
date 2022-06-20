package model;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import view.View;
import utility.Point2d;

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
    /**
     * The world's width.
     */
    private final int width;
    /**
     * The world's height.
     */
    private final int height;

    private int level = 1;

    /**
     * The player's x position in the world.
     */
    private int playerX = 0;
    /**
     * The player's y position in the world.
     */
    private int playerY = 0;

	private ArrayList<Enemy> enemies;

    /* The X and Y position of the start field. */
    private int startX = 0;
    private int startY = 0;

    /* The x and y position of the finish field. */
    private int finishX = 9;
    private int finishY = 9;

    /**
     * Set of views registered to be notified of world updates.
     */
    private final ArrayList<View> views = new ArrayList<>();

    /**
     * creates a map of obstacles for the labyrinth, where "true" is an obstacle
     */
    private boolean[][] obstacleMap;
    private float[][] lightingMap;
    private float[][] playerDistanceLightingMap;
    private float globalBrightness = 1.0f;
    private ArrayList<Point2d> emptyFields;

    private WorldGenerator worldGenerator;
    private ArrayList<WorldGenerator> worldGenerators;

    /**
     * Creates a new world with the given height and width
     * 
     * @param width The width of the new world
     * @param height The height of the new world.
     */
    public World(int width, int height) {
        // Normally, we would check the arguments for proper values
        this.width = width;
        this.height = height;

        this.worldGenerators = new ArrayList<WorldGenerator>();
        this.worldGenerators.add(new PathWorldGenerator(this));
        this.worldGenerators.add(new CaveWorldGenerator(this));
        //this.worldGenerator = new PathWorldGenerator(this);

        this.resetWorld();
    }

    /**
     * Resets the world.
     */
    public void resetWorld() {
        this.obstacleMap = new boolean[height][width];
        this.lightingMap = new float[height][width];
        this.playerDistanceLightingMap = new float[height][width];
        this.emptyFields = new ArrayList<Point2d>();

        this.enemies = new ArrayList<Enemy>();

        Random rnd = new Random();
        this.worldGenerators.get(rnd.nextInt(this.worldGenerators.size())).generateWorld();
        this.generateLightingMap();
    }

    public void generatePlayerDistanceLightingMap() {
        for (int i = 0; i < this.width; ++i) {
            for (int j = 0; j < this.height; ++j) {
                float levelFactor = (float)(Math.log(this.level > 1 ? this.level / 2.f : 1.f) + 1.f);
                this.playerDistanceLightingMap[j][i] = 1.f / ((float)getDistance(i, j, this.playerX, this.playerY) * 0.1f * levelFactor);
            }
        }
    }

    /**
     * Generates the lighting-map.
     */
    public void generateLightingMap() {
        /* Set empty fields to brightness 1.0f and obstacle fields to -1.0f */
        for (int i = 0; i < this.width; ++i) {
            for (int j = 0; j < this.height; ++j) {
                if (this.obstacleMap[j][i]) {
                    this.lightingMap[j][i] = -1.0f;
                } else {
                    this.lightingMap[j][i] = 1.0f;
                }
            }
        }

        /* Push all empty field points onto a stack */
        Stack<Point2d> s = new Stack<Point2d>();
        for (Point2d p : this.emptyFields) {
            s.push(p);
        }

        int[][] directions = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, 1}, {1, -1}, {-1, -1}};
        while (s.size() != 0) {
            Point2d top = s.pop();
            /* Light this field if its not lit. */
            if (this.lightingMap[top.getY()][top.getX()] == -1.0f) {
                int numLightedNeighbors = 0;
                float lightingSum = 0.0f;
                /* Calculate Lighting for this field. */
                for (int[] dir : directions) {
                    int x = top.getX() + dir[0];
                    int y = top.getY() + dir[1];
                    if (x < 0 || x >= this.getWidth() || y < 0 || y >= this.getHeight()) {
                        continue;
                    }
                    float fieldVal = this.lightingMap[y][x];
                    if (fieldVal != -1.0f) {
                        lightingSum += fieldVal;
                        numLightedNeighbors++;
                    } else {
                        /* Push unlit neighbors to stack. */
                        s.push(new Point2d(x, y));
                    }
                }
                this.lightingMap[top.getY()][top.getX()] = (lightingSum / (float) numLightedNeighbors) * 0.95f;
            /* This field is already lit af. */
            } else {
                /* Push unlit neighbors to stack. */
                for (int[] dir : directions) {
                    int x = top.getX() + dir[0];
                    int y = top.getY() + dir[1];
                    if (x < 0 || x >= this.getWidth() || y < 0 || y >= this.getHeight()) {
                        continue;
                    }
                    float fieldVal = this.lightingMap[y][x];
                    if (fieldVal == -1.0f) {
                        s.push(new Point2d(x, y));
                    }
                }
            }
        }
    }

	private long lastTime = System.nanoTime();

    /**
     * Called every 16ms.
     */
	public void timerTick(float time) {
		// TODO: was mitm timer machen, weils geht

		long currentTime = System.nanoTime();
		//this.globalBrightness = 1.0f - ((float)Math.sin((double)time) + 1.0f) / 10.0f;
		//this.views.get(0).update(this);
    	float deltaTime = (currentTime - this.lastTime) / 1000000.f;
		lastTime = currentTime;

        this.generatePlayerDistanceLightingMap();
        this.views.get(0).updateCamera(this, deltaTime);
    }

    /**
     * TODO: aufräumen :D
     */
    /**
     * Returns the width of the labyrinth-world.
     * 
     * @return The width of the world.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of the labyrinth-world.
     * 
     * @return The height of the world.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the x-position of the player.
     * 
     * @return The x-position of the player
     */
    public int getPlayerX() {
        return playerX;
    }

    /**
     * Sets the x-position of the player
     * 
     * @param playerX The new x-position
     */
    public void setPlayerX(int playerX) {
        this.playerX = playerX;

        updateViews();
    }

    /**
     * Get the y-position of the player
     * 
     * @return The y-position of the player
     */
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
     * Checks whether the given point is within world boundaries
     * @param pos The two-dimensional point to check
     * @return True if within the field, false otherwise
     */
    public boolean isValidField(Point2d pos){
        return isValidField(pos.getX(), pos.getY());
    }

    /**
     * Checks whether the given coordinates are within world boundaries
     * @param xPos  The x-coordinate to check
     * @param yPos  The y-coordinate to check
     * @return True if within the field, false otherwise
     */
    public boolean isValidField(int xPos, int yPos) {
        return !(xPos >= this.getWidth() || xPos < 0 || yPos >= this.getHeight() || yPos < 0);
    }

    /**
     * Checks whether an entity can move to a given field.
     * @param xPos The x-position of the field to check.
     * @param yPos The y-position of the field to check.
     * @return True if within the world and no collision occurs, false otherwise.
     */
    public boolean canMoveToField(int xPos, int yPos) {
        return isValidField(xPos, yPos) && !this.getField(xPos, yPos);
    }

    /**
     * Calculates the collisions around a given field
     * @param xPos  The x-coordinate of the field to check
     * @param yPos  The y-coordinate of the field to check
     * @return The amount of fields with collisions around the given field, so a number inbetween 0 and 4.
     */
    public int collisionAroundField(int xPos, int yPos) {
        int sum = 0;
        for (MovementDirection move : MovementDirection.values()) {
            if (!this.canMoveToField(xPos + move.deltaX, yPos + move.deltaY))
                sum++;
        }
        return sum;
    }

    /**
     * returns the x-coordinate of the start-field
     *
     * @return The x-value.
     */
    public int getStartX() {
        return this.startX;
    }

    /**
     * Sets the x-coordinate of the start-field
     *
     * @param x The new x of the start-field
     */
    public void setStartX(int x) {
        this.startX = x;
    }

    /**
     * returns the y-coordinate of the start-field
     *
     * @return The y-value.
     */
    public int getStartY() {
        return this.startY;
    }

    /**
     * Sets the y-coordinate of the start-field
     *
     * @param y The new y of the start-field
     */
    public void setStartY(int y) {
        this.startY = y;
    }

    /**
     * Sets the start-point of the player
     *
     * @param start The start-point to set
     */
    public void setStart(Point2d start) {
        setStartX(start.getX());
        setStartY(start.getY());
    }

    /**
     * Sets the x-coordinate of the finish-field
     *
     * @param x The new x of the finish-field
     */
    public void setFinishX(int x) {
        this.finishX = x;
    }

    /**
     * Sets the y-coordinate of the finish-field
     *
     * @param y The new y of the finish-field
     */
    public void setFinishY(int y) {
        this.finishY = y;
    }

    /**
     * Sets the finish-point given as a point
     *
     * @param finish The finish-point
     */
    public void setFinish(Point2d finish) {
        setFinishX(finish.getX());
        setFinishY(finish.getY());
    }

    /**
     * returns the x-coordinate of the finish-field
     *
     * @return The x-value.
     */
    public int getFinishX() {
        return this.finishX;
    }

    /**
     * returns the y-coordinate of the finish-field
     *
     * @return The y-value.
     */
    public int getFinishY() {
        return this.finishY;
    }
    /**
     * Returns the reference to the Arraylist of two-dimensional points of fields without collision.
     * @return An Arraylist of the collision-free points.
     */
    public final ArrayList<Point2d> getEmptyFields() {
        return this.emptyFields;
    }
    /**
     * Returns the list of enemy-objects.
     * @return An ArrayList of enemies.
     */
    public ArrayList<Enemy> getEnemies() {
        return this.enemies;
    }
    /**
     * Returns the obstacle-map of the labyrinth-world.
     * @return The obstacleMap where a true value implies an obstacle is at that position.
     */
    public boolean[][] getObstacleMap() {
        return this.obstacleMap;
    }

    public float[][] getLightingMap() {
        return this.lightingMap;
    }
    /**
     * Returns the current level of the world.
     * @return The current level of the world.
     */
    public int getLevel() {
        return this.level;
    }
    /**
     * Sets the level of the world, as long as the provided level is bigger than 0
     * @param level The level to set.
     */
    public void setLevel(int level) {
        this.level = level < 0 ? 0 : level;
    }
    /**
     * Increases the level of the world by 1.
     */
    public void incLevel() {
        this.level++;
    }

    public float getGlobalBrightness() {
        return this.globalBrightness;
    }

    /**
     * returns whether the checked field has a collision object in it
     *
     * @param xPos The x-position to check
     * @param yPos The y-position to check
     * @return true in case of a
     */
    public boolean getField(int xPos, int yPos) {
        return xPos < 0 || xPos >= this.getWidth() || yPos < 0 || yPos >= this.getHeight() || this.obstacleMap[yPos][xPos];
    }

    /**
     * Sets the given field as an obstacle on the map (if possible).
     *
     * @param xPos The x-coordinate of the field to set.
     * @param yPos The y-coordinate of the field to set.
     */
    ///////////////////////////////////////////////////////////////////////////
    // Player Management
    void setObstacleInField(int xPos, int yPos) {
        if (xPos >= 0 && xPos < this.getWidth() && yPos >= 0 && yPos < this.getHeight())
            this.obstacleMap[yPos][xPos] = true;
        else
            System.out.println("Error: Tried setting obstacle outside map");
    }

    /**
     * Removes the given field as an obstacle on the map (if possible).
     *
     * @param xPos The x-coordinate of the field to remove.
     * @param yPos The y-coordinate of the field to remove.
     */
    ///////////////////////////////////////////////////////////////////////////
    // Player Management
    void removeObstacleInField(int xPos, int yPos) {
        if (xPos >= 0 && xPos < this.getWidth() && yPos >= 0 && yPos < this.getHeight()) {
            this.obstacleMap[yPos][xPos] = false;
            this.emptyFields.add(new Point2d(xPos, yPos));
        } else {
            System.out.println("Error: Tried removing obstacle outside map");
        }
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
     *
     * @param xPos The x-coordinate
     * @param yPos The y-coordinate
     */
    public void setPlayerLocation(final int xPos, final int yPos) {
        setPlayerX(xPos);
        setPlayerY(yPos);
    }

    public float[][] getPlayerDistanceLightingMap() {
        return this.playerDistanceLightingMap;
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
    /**
     * Calculates the (diagonal) distance inbetween two two-dimensional points.
     * 
     * @param p1 The first point
     * @param p2 The second point
     * @return The (diagonal) distance inbetween these points.
     */
    static double getDistance(Point2d p1, Point2d p2) {
        return getDistance(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }
    /**
     * Calculates the (diagonal) distance inbetween two two-dimensional points, given by their coordinates.
     * 
     * @param x1 The x-coordinate of the first point.
     * @param y1 The y-coordinate of the first point.
     * @param x2 The x-coordinate of the second point.
     * @param y2 The y-coordinate of the second point.
     * @return
     */
    static double getDistance(final int x1, final int y1, final int x2, final int y2) {
        return Math.sqrt(Math.pow(y1 - y2, 2.0) + Math.pow(x1 - x2, 2.0));
    }
}
