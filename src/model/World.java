package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

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

    private int level = 0;
    private Difficulty difficulty = Difficulty.HARD;
    private final float CREEPER_ZOMBIE_SPAWN_RATIO = 0.2f;

	private ArrayList<Enemy> enemies;
    public EnemyPathTable enemyPathingTable;

    /* The  position of the start field. */
    private Point2d playerPosition, startPosition, finishPosition;

    /* Used to save all enemy positons */
    private HashSet<String> enemyPositions;
    /**
     * Set of views registered to be notified of world updates.
     */
    private final ArrayList<View> views = new ArrayList<View>();

    /**
     * creates a map of obstacles for the labyrinth, where "true" is an obstacle
     */
    private boolean[][] obstacleMap;
    private float globalBrightness = 1.0f;
    private ArrayList<Point2d> emptyFields;

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
        this.playerPosition = new Point2d(0, 0);
        this.startPosition = new Point2d(0, 0);
        this.finishPosition = new Point2d(0, 0);
        this.worldGenerators = new ArrayList<WorldGenerator>();
        this.worldGenerators.add(new PathWorldGenerator(this));
        this.worldGenerators.add(new CaveWorldGenerator(this));
        ///this.resetWorld();
        this.reset();
    }

    /**
     * Resets the world.
     */
    public void resetWorld() {
        this.obstacleMap = new boolean[height][width];
        this.emptyFields = new ArrayList<Point2d>();

        this.enemies = new ArrayList<Enemy>();

        Random rnd = new Random();
        this.worldGenerators.get(rnd.nextInt(this.worldGenerators.size())).generateWorld();
        this.enemyPathingTable = new EnemyPathTable(this);
        this.levelChanged();
    }

    public void levelChanged() {
        for (View view : this.views) {
            view.onLevelChanged(this);
        }
    }

    public void spawnExplosion(Point2d position, float size) {
        for (View view : this.views) {
            view.spawnExplosion(position, size);
        }
    }

    public void reset() {
        Random rnd = new Random();
        this.incLevel();
        this.resetWorld();
        this.setPlayerLocation(this.getStartX(), this.getStartY());
        for (int i = 0; i <= /*1 + (int)(this.getLevel() / 10)*/8; ++i) {
            Point2d spawnLocation = getEmptyFields().get(rnd.nextInt(getEmptyFields().size()));
            Enemy newEnemy = rnd.nextFloat() < CREEPER_ZOMBIE_SPAWN_RATIO 
                ? new Creeper(spawnLocation.getX(), spawnLocation.getY()) 
                : new Enemy(spawnLocation.getX(), spawnLocation.getY());
            this.getEnemies().add(newEnemy);
        }
    }

    public void resetGame() {
        this.setLevel(0);
        this.reset();
    }

	private long lastTime = System.nanoTime();

    /**
     * Called every 16ms.
     */
	public void timerTick(float time) {
		long currentTime = System.nanoTime();
    	float deltaTime = (currentTime - this.lastTime) / 1000000.f;
		lastTime = currentTime;

        /* Update enemies */
        for (Enemy enemy : this.enemies) {
            enemy.updateFrame(this, deltaTime);
        }

        this.views.get(0).updateCamera(this, deltaTime);
    }

    public void updateEnemies() {
        /* HashSet that contains all enemy positions, used for enemy-enemy-collision. 
         * TODO: use Point2d instead of String, implement hash-method */
        enemyPositions  = new HashSet<String>();
        
        /* update enemies */
        for (Enemy enemy : this.getEnemies()) {
            // update enemy
            enemy.update(this);
            // add position to weed set
            enemyPositions.add(enemy.getLocation().toString());
        }

        /* Don't update pathing table in easy difficulty because it's not used. */
        if (difficulty != Difficulty.EASY) {
            this.enemyPathingTable.computeMap(this.getPlayerLocation());
        }
    }

    /**
     * Returns a HashSet with the handled enemy positions.
     * 
     * @return A reference to the hash-set
     */
    public HashSet<String> getEnemyPositions(){
        return this.enemyPositions;
    }
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
     * Updates the point based upon a given point.
     * 
     * @param newPos The new position to set.
     */
    public void setPlayerLocation(Point2d newPos){
        playerPosition.setPoint(newPos);
    }

    /**
     * Returns the current player position
     * 
     * @return The current player position as a point.
     */
    public Point2d getPlayerLocation(){
        return this.playerPosition;
    }

    /**
     * Returns a copy of the current player position
     * 
     * @return A copy of the current player position as a point.
     */
    public Point2d getPlayerCopy(){
        return this.playerPosition.copy();
    }

    /**
     * Gets the x-position of the player.
     * 
     * @return The x-position of the player
     */
    public int getPlayerX() {
        return playerPosition.getX();
    }

    /**
     * Sets the x-position of the player
     * 
     * @param playerX The new x-position
     */
    public void setPlayerX(int playerX) {
        this.playerPosition.setX(playerX);
        
        updateViews();
    }

    /**
     * Get the y-position of the player
     * 
     * @return The y-position of the player
     */
    public int getPlayerY() {
        return playerPosition.getY();
    }

    /**
     * Sets the y-coordinate of the player
     * 
     * @param playerY The y-coordinate to set.
     */
    public void setPlayerY(int playerY) {
        this.playerPosition.setY(playerY);

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
        return this.startPosition.getX();
    }

    /**
     * Sets the x-coordinate of the start-field
     *
     * @param x The new x of the start-field
     */
    public void setStartX(int x) {
        this.startPosition.setX(x);
    }

    /**
     * returns the y-coordinate of the start-field
     *
     * @return The y-value.
     */
    public int getStartY() {
        return this.startPosition.getY();
    }

    /**
     * Sets the y-coordinate of the start-field
     *
     * @param y The new y of the start-field
     */
    public void setStartY(int y) {
        this.startPosition.setY(y);
    }

    /**
     * Returns the current start position
     * 
     * @return The current start position as a point.
     */
    public Point2d getStartLocation(){
        return this.startPosition;
    }

    /**
     * Returns a copy of the current start position
     * 
     * @return A copy of the current start position as a point.
     */
    public Point2d getStartCopy(){
        return this.startPosition.copy();
    }

    /**
     * Sets the start-point of the player
     *
     * @param start The start-point to set
     */
    public void setStart(Point2d start) {
        startPosition.setPoint(start);
    }

    /**
     * returns the x-coordinate of the finish-field
     *
     * @return The x-value.
     */
    public int getFinishX() {
        return this.finishPosition.getX();
    }

    /**
     * Sets the x-coordinate of the finish-field
     *
     * @param x The new x of the finish-field
     */
    public void setFinishX(int x) {
        this.finishPosition.getX();
    }

     /**
     * returns the y-coordinate of the finish-field
     *
     * @return The y-value.
     */
    public int getFinishY() {
        return this.finishPosition.getY();
    }

    /**
     * Sets the y-coordinate of the finish-field
     *
     * @param y The new y of the finish-field
     */
    public void setFinishY(int y) {
        this.finishPosition.setY(y);
    }

    /**
     * Returns the current finish position
     * 
     * @return The current finish position as a point.
     */
    public Point2d getFinishLocation(){
        return this.finishPosition;
    }

    /**
     * Returns a copy of the current finish position
     * 
     * @return A copy of the current finish position as a point.
     */
    public Point2d getFinishCopy(){
        return this.finishPosition.copy();
    }

    /**
     * Sets the finish-point given as a point
     *
     * @param finish The finish-point
     */
    public void setFinish(Point2d finish) {
        finishPosition.setPoint(finish);
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
        getPlayerLocation().add(direction.deltaX, direction.deltaY);
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

    public EnemyPathTable getEnemyPathTable() {
        return this.enemyPathingTable;
    }

    public Difficulty getDifficulty() {
        return this.difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
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
        view.onLevelChanged(this);
        view.updateCamera(this, 0.f);
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
    public static double getDistance(final int x1, final int y1, final int x2, final int y2) {
        return Math.sqrt(Math.pow(y1 - y2, 2.0) + Math.pow(x1 - x2, 2.0));
    }
}
