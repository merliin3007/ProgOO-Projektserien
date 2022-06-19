package model;

import java.security.cert.PolicyNode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Stack;

import org.jetbrains.annotations.NotNull;
import view.View;
import model.Enemy;
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
    private float globalBrightness = 1.0f;
    private ArrayList<Point2d> emptyFields;

    /**
     * Creates a new world with the given size.
     */
    public World(int width, int height) {
        // Normally, we would check the arguments for proper values
        this.width = width;
        this.height = height;

        this.resetWorld();
    }

    public void resetWorld() {
        this.obstacleMap = new boolean[height][width];
        this.lightingMap = new float[height][width];
        this.emptyFields = new ArrayList<Point2d>();

        this.enemies = new ArrayList<Enemy>();

        Random rnd = new Random();
        /*this.startX = rnd.nextInt(this.width);
        this.startY = rnd.nextInt(this.height);
        this.finishX = rnd.nextInt(this.width);
        this.finishY = rnd.nextInt(this.height);*/

        //this.generateWorld();
        this.generatePathsOnMap();
        this.generateLightingMap();
    }

    /**
     * TODO: aufräumen :D
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
            /*  */
            if (this.lightingMap[top.getY()][top.getX()] == -1.0f) {
                int numLightedNeighbors = 0;
                float lightingSum = 0.0f;
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
                        s.push(new Point2d(x, y));
                    }
                }
                this.lightingMap[top.getY()][top.getX()] = (lightingSum / (float) numLightedNeighbors) * 0.95f;
            } else {
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

    private Point2d getRandomPointOnMap() {
        return Point2d.RandomPoint2d(this.getWidth(), this.getHeight());
    }

    private Point2d getCornerPointOnMap() {
        Point2d tmp = getRandomPointOnMap();
        do {
            tmp = getRandomPointOnMap();
        }
        while (!(tmp.getX() > (int) (this.getWidth() / 4) || tmp.getX() < (int) (this.getWidth() / 4 * 3) ||
                tmp.getY() > (int) (this.getHeight() / 4) || tmp.getY() < (int) (this.getHeight() / 4 * 3)));
        return tmp;
    }

    private int getQuadrant(Point2d pos) {
        if (pos.getX() < (int) (this.getWidth() / 2)) {
            if (pos.getY() < (int) (this.getHeight() / 2))
                return 1;
            else
                return 3;
        } else {
            if (pos.getY() < (int) (this.getHeight() / 2))
                return 2;
            else
                return 4;
        }

    }

    public void generatePathsOnMap() {
        /* Fill the whole map with obstacles */
        for (int i = 0; i < this.getWidth(); ++i) {
            for (int j = 0; j < this.getHeight(); ++j) {
                this.obstacleMap[j][i] = true;
            }
        }
        Point2d startPoint = getCornerPointOnMap();
        Point2d endPoint = getCornerPointOnMap();
        // handle edge case where start, end are same point
        do {
            endPoint = getCornerPointOnMap();
        } while (startPoint.inRangeOf(endPoint, 2));
        // set start, finish
        setStart(startPoint);
        setFinish(endPoint);
        //start further point generation
        Random r = new Random();
        int amountOfRandomPoints = r.nextInt(8, 15); // TODO better values
        Point2d[] pathPoints = new Point2d[amountOfRandomPoints + 2];
        // create primary path to connect
        for (int curPoint = 0; curPoint < pathPoints.length; curPoint++) {
            if (curPoint == 0) {
                pathPoints[curPoint] = startPoint;
            } else if (curPoint == pathPoints.length - 1) {
                pathPoints[curPoint] = endPoint;
            } else {
                do {
                    pathPoints[curPoint] = getRandomPointOnMap();
                } while (pathPoints[curPoint - 1].inRangeOf(pathPoints[curPoint], 3)); // TODO fixed range here
            }
        }
        // connect points
        for (int curPoint = 0; curPoint < pathPoints.length - 1; curPoint++) {
            int xDist = pathPoints[curPoint + 1].getX() - pathPoints[curPoint].getX();
            int yDist = pathPoints[curPoint + 1].getY() - pathPoints[curPoint].getY();
            Point2d moveOrigin = pathPoints[curPoint].copy();
            while (xDist != 0 && yDist != 0) {
                boolean xRatherThanY = r.nextBoolean();
                if (xRatherThanY) {
                    int xMovement = r.nextInt(1, Math.abs(xDist) + 1);
                    int negative = xDist < 0 ? -1 : 1;
                    for (int vel = 0; vel < xMovement; vel++) {
                        removeObstacleInField(moveOrigin.getX() + negative * vel, moveOrigin.getY());
                    }
                    moveOrigin.addX(negative * xMovement);
                    xDist -= negative * xMovement;
                } else {
                    int yMovement = r.nextInt(1, Math.abs(yDist) + 1);
                    int negative = yDist < 0 ? -1 : 1;
                    for (int vel = 0; vel < yMovement; vel++) {
                        removeObstacleInField(moveOrigin.getX(), moveOrigin.getY() + negative * vel);
                    }
                    moveOrigin.addY(negative * yMovement);
                    yDist -= negative * yMovement;
                }
            }

        }
    }

    /**
     * TODO: in eigene Klasse auslagern (mit digRandomPath())
     */
    public void generateWorld() {
        /* Fill the whole map with obstacles */
        for (int i = 0; i < this.getWidth(); ++i) {
            for (int j = 0; j < this.getHeight(); ++j) {
                this.obstacleMap[j][i] = true;
            }
        }
        /* Create a path from start to finish */
        int currentX = this.startX, currentY = this.startY;
        this.obstacleMap[currentY][currentX] = false;
        while (this.obstacleMap[this.finishY][this.finishX]) {
            int[][] directions = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
            utility.Utility.shuffleArray(directions);
            /* try to move to a field that hasn't been visited yet */
            boolean hasMoved = false;
            for (int[] dir : directions) {
                int newX = currentX + dir[0], newY = currentY + dir[1];
                if (newX < 0 || newX >= this.getWidth() || newY < 0 || newY >= this.getHeight() || !this.obstacleMap[newY][newX]) {
                    continue;
                } else if (this.collisionAroundField(newX, newY) < 3) {
                    continue;
                } else {
                    obstacleMap[newY][newX] = false;
                    this.emptyFields.add(new Point2d(newX, newY));
                    currentX = newX;
                    currentY = newY;
                    hasMoved = true;
                    break;
                }
            }
            /* if no (new) move possible -> move directly towards finish */
            if (!hasMoved) {
                int distX = finishX - currentX;
                int distY = finishY - currentY;
                if (Math.abs(distX) >= Math.abs(distY)) {
                    currentX += distX > 0 ? 1 : -1;
                } else {
                    currentY += distY > 0 ? 1 : -1;
                }
                this.obstacleMap[currentY][currentX] = false;
            }
        }

        int upperBound = this.width * this.height / 4;
        int numRandomPaths = upperBound * this.emptyFields.size();
        Random rnd = new Random();
        for (int i = 0; i < numRandomPaths && this.getEmptyFields().size() < upperBound; ++i) {
            digRandomPath(rnd.nextInt((int) ((this.width * this.height) / 50)));
        }
    }

    public void digRandomPath(int pathLen) {
        Point2d current = Point2d.RandomPoint2d(this.width, this.height);
        for (int i = 0; i < pathLen; ++i) {
            int[][] directions = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
            utility.Utility.shuffleArray(directions);
            /* try to move to a field that hasn't been visited yet */
            boolean hasMoved = false;
            for (int[] dir : directions) {
                int newX = current.getX() + dir[0], newY = current.getY() + dir[1];
                if (newX < 0 || newX >= this.getWidth() || newY < 0 || newY >= this.getHeight()) {
                    continue;
                } else if (!this.obstacleMap[newY][newX]) {
                    continue;
                } else if (this.collisionAroundField(newX, newY) < 3) {
                    continue;
                } else {
                    obstacleMap[newY][newX] = false;
                    this.emptyFields.add(new Point2d(newX, newY));
                    current.setX(newX);
                    current.setY(newY);
                    hasMoved = true;
                    break;
                }
            }
            if (!hasMoved) {
                break;
            }
        }

        Random rnd = new Random();
        while (this.obstacleMap[current.getY()][current.getX()]) {
            if (rnd.nextBoolean()) {
                int distX = startX - current.getX();
                int distY = startY - current.getY();
                if (Math.abs(distX) >= Math.abs(distY)) {
                    current.addX(distX > 0 ? 1 : -1);
                } else {
                    current.addY(distY > 0 ? 1 : -1);
                }
            }
        }
    }

	private long lastTime = System.nanoTime();
	public void timerTick(float time) {
		// TODO: was mitm timer machen, weils geht

		long currentTime = System.nanoTime();
		//this.globalBrightness = 1.0f - ((float)Math.sin((double)time) + 1.0f) / 10.0f;
		//this.views.get(0).update(this);
    	float deltaTime = (currentTime - this.lastTime) / 1000000.f;
		lastTime = currentTime;

        this.views.get(0).updateCamera(this, deltaTime);
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

    public boolean canMoveToField(int xPos, int yPos) {
        if (xPos >= this.getWidth() || xPos < 0
                || yPos >= this.getHeight() || yPos < 0) {
            return false;
        }
        return !this.getField(xPos, yPos);
    }

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

    public final ArrayList<Point2d> getEmptyFields() {
        return this.emptyFields;
    }

    public ArrayList<Enemy> getEnemies() {
        return this.enemies;
    }

    public boolean[][] getObstacleMap() {
        return this.obstacleMap;
    }

    public float[][] getLightingMap() {
        return this.lightingMap;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level < 0 ? 0 : level;
    }

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
        if (xPos >= 0 && xPos < this.obstacleMap.length && yPos >= 0 && yPos < this.obstacleMap[0].length)
            this.obstacleMap[xPos][yPos] = true;
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
        if (xPos >= 0 && xPos < this.obstacleMap.length && yPos >= 0 && yPos < this.obstacleMap[0].length)
            this.obstacleMap[xPos][yPos] = false;
        else
            System.out.println("Error: Tried removing obstacle outside map");
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

    private static String pointToString(final int x, final int y) {
        return String.format("%d,%d", x, y);
    }

    private static double getDistance(Point2d p1, Point2d p2) {
        return getDistance(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    private static double getDistance(final int x1, final int y1, final int x2, final int y2) {
        return Math.sqrt(Math.pow(y1 - y2, 2.0) + Math.pow(x1 - x2, 2.0));
    }
}
