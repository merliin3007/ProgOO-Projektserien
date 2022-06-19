package model;

import java.nio.file.PathMatcher;
import java.util.Arrays;
import java.util.Random;

import utility.*;

public class PathWorldGenerator implements WorldGenerator {

    private World world;
    
    public PathWorldGenerator(World world) {
        this.world = world;
    }
    
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
        //start further point generation
        Random r = new Random();
        int amountOfRandomPoints = r.nextInt(10, 20); // TODO better values
        Point2d[] pathPoints = new Point2d[amountOfRandomPoints + 1];
        // create primary path to connect
        for (int curPoint = 0; curPoint < pathPoints.length; curPoint++) {
            if (curPoint == 0) {
                pathPoints[curPoint] = getRandomPointOnMap();
            } else {
                do {
                    pathPoints[curPoint] = getRandomPointOnMap();
                } while (pathPoints[curPoint - 1].inRangeOf(pathPoints[curPoint], 3) && getQuadrant(pathPoints[curPoint]) == getQuadrant(pathPoints[curPoint - 1])); // TODO fixed range here
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

    private Point2d getRandomPointOnMap() {
        return Point2d.RandomPoint2d(this.world.getWidth(), this.world.getHeight());
    }

    private Point2d getCornerPointOnMap() {
        Point2d tmp = getRandomPointOnMap();
        do {
            tmp = getRandomPointOnMap();
        }
        while (!(tmp.getX() > (int) (this.world.getWidth() / 4) || tmp.getX() < (int) (this.world.getWidth() / 4 * 3) ||
                tmp.getY() > (int) (this.world.getHeight() / 4) || tmp.getY() < (int) (this.world.getHeight() / 4 * 3)));
        return tmp;
    }

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
