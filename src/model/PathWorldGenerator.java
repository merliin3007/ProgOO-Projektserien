package model;

import java.util.Random;

/* Project */
import utility.*;

/**
 * All roads lead to rom.
 */
public class PathWorldGenerator implements WorldGenerator {

    /** 
     * I find it kind of funny
     * I find it kind of sad
     * The dreams in which I′m dying
     * Are the best I've ever had
     * 
     * I find it hard to tell you
     * I find it hard to take
     * When people run in circles it′s a very, very
     * Mad world
     * Mad world
     */
    private World world;
    
    /**
	 * As a wise man once said: Creates a new instance.
     * 
     * @param world "If everyone lived like an average resident of the USA, a total of four Earths would be required to regenerate humanity's annual demand on nature."
     */
    public PathWorldGenerator(World world) {
        this.world = world;
    }
    
    /**
     * I hear babies cryin', I watch them grow
     * They'll learn much more, than I′ll ever know
     * And I think to myself
     * What a wonderful world
     */
    @Override
    public void generateWorld() {
        if (Utility.DEBUG) {
            System.out.println("Generating Path World...");
        }

        /* Fill the whole map with obstacles */
        for (int i = 0; i < this.world.getWidth(); ++i) {
            for (int j = 0; j < this.world.getHeight(); ++j) {
                this.world.getObstacleMap()[j][i] = true;
            }
        }
        Point2d endPoint = this.getCornerPointOnMap();
        // set start, finish
        this.world.setFinish(endPoint);
        // start further point generation
        Random r = new Random();
        int amountOfRandomPoints = r.nextInt(10, 20);
        Point2d[] pathPoints = new Point2d[amountOfRandomPoints + 1];
        // create primary path to connect
        for (int curPoint = 0; curPoint < pathPoints.length; curPoint++) {
            if (curPoint == 0) {
                pathPoints[curPoint] = getRandomPointOnMap();
            } else {
                do {
                    pathPoints[curPoint] = getRandomPointOnMap();
                } while (pathPoints[curPoint - 1].inRangeOf(pathPoints[curPoint], 3) && getQuadrant(pathPoints[curPoint]) == getQuadrant(pathPoints[curPoint - 1]));
            }
        }
        Utility.shuffleArray(pathPoints);
        pathPoints[pathPoints.length - 1] = endPoint.copy();
        // connect points
        for (int curPoint = 0; curPoint < pathPoints.length - 1; curPoint++) {
            Point2d moveOrigin = pathPoints[curPoint].copy();
            Point2d currentNext = pathPoints[curPoint + 1];
            do {
                this.world.removeObstacleInField(moveOrigin.getX(), moveOrigin.getY());
                if (r.nextBoolean()) {
                    int newX = moveOrigin.getX() + (currentNext.getX() > moveOrigin.getX() ? 1 : -1);
                    if (this.world.isValidField(newX, moveOrigin.getY())) {
                        moveOrigin.addX(currentNext.getX() > moveOrigin.getX() ? 1 : -1);
                    }
                } else {
                    int newY = moveOrigin.getY() + (currentNext.getY() > moveOrigin.getY() ? 1 : -1);
                    if (this.world.isValidField(moveOrigin.getX(), newY)) {
                        moveOrigin.addY(currentNext.getY() > moveOrigin.getY() ? 1 : -1);
                    }
                }
                
            } while (!Point2d.equalPoints(moveOrigin, pathPoints[curPoint + 1]));
            this.world.removeObstacleInField(currentNext.getX(), currentNext.getY());
        }
        double[] distanceToFinish = new double[pathPoints.length-1];
        for (int spawnPoint = 0; spawnPoint < pathPoints.length-1; spawnPoint++) {
            distanceToFinish[spawnPoint] = World.getDistance(pathPoints[spawnPoint], endPoint);
        }
        int furthestDistance = 0;
        for (int index = 1; index < pathPoints.length - 1; index++) {
            if (distanceToFinish[furthestDistance] < distanceToFinish[index] && r.nextDouble() < 0.33) {
                furthestDistance = index;
            }
        }
        this.world.setStart(pathPoints[furthestDistance]);
    }

    /**
     * Gets not a random, but he most random point on the map.
     * 
     * @return Kiel-Oppendorf
     */
    private Point2d getRandomPointOnMap() {
        return Point2d.RandomPoint2d(this.world.getWidth(), this.world.getHeight());
    }

    /**
     * Computes a point near a corner of the map.
     * 
     * @return /|\ That point. lol.
     *          |
     */
    private Point2d getCornerPointOnMap() {
        Point2d tmp = getRandomPointOnMap();
        do {
            tmp = getRandomPointOnMap();
        }
        while (!(tmp.getX() > (int) (this.world.getWidth() / 4) || tmp.getX() < (int) (this.world.getWidth() / 4 * 3) ||
                tmp.getY() > (int) (this.world.getHeight() / 4) || tmp.getY() < (int) (this.world.getHeight() / 4 * 3)));
        return tmp;
    }

    /**
     * Gets whether a given point is in the top-left, top-right, bottom-left or bottom-right quadrant.
     * 
     * @param pos That point.
     * @return The quadrant (1-4).
     */
    private int getQuadrant(Point2d pos) {
        if (pos.getX() < (int) (this.world.getWidth() / 2)) {
            if (pos.getY() < (int) (this.world.getHeight() / 2))
                return 1;
            else
                return 3;
        } else {
            if (pos.getY() < (int) (this.world.getHeight() / 2))
                return 2;
            else
                return 4;
        }
    }
}
