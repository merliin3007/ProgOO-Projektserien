package model;

import java.util.Random;

import utility.*;

public class PathWorldGenerator implements WorldGenerator {

    private World world;
    
    public PathWorldGenerator(World world) {
        this.world = world;
    }
    
    @Override
    public void generateWorld() {
        /* Fill the whole map with obstacles */
        for (int i = 0; i < this.world.getWidth(); ++i) {
            for (int j = 0; j < this.world.getHeight(); ++j) {
                this.world.getObstacleMap()[j][i] = true;
            }
        }
        Point2d startPoint = this.getCornerPointOnMap();
        Point2d endPoint = this.getCornerPointOnMap();
        // handle edge case where start, end are same point
        do {
            endPoint = getCornerPointOnMap();
        } while (startPoint.inRangeOf(endPoint, 2));
        // set start, finish
        this.world.setStart(startPoint);
        this.world.setFinish(endPoint);
        //start further point generation
        Random r = new Random();
        int amountOfRandomPoints = r.nextInt(20, 40); // TODO better values
        Point2d[] pathPoints = new Point2d[amountOfRandomPoints + 2];
        // create primary path to connect
        for (int curPoint = 0; curPoint < pathPoints.length; curPoint++) {
            if (curPoint == 0) {
                pathPoints[curPoint] = startPoint.copy();
            } else if (curPoint == pathPoints.length - 1) {
                pathPoints[curPoint] = endPoint.copy();
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
            while (xDist != 0 || yDist != 0) {
                boolean xRatherThanY;
                if (xDist == 0) {
                    xRatherThanY = false;
                } else if (yDist == 0) {
                    xRatherThanY = true;
                } else {
                    xRatherThanY = r.nextBoolean();
                }
                if (xRatherThanY) {
                    int xMovement = r.nextInt(1, Math.abs(xDist) + 1);
                    xMovement = xMovement % 4;
                    int negative = xDist < 0 ? -1 : 1;
                    for (int curStep = 0; curStep <= xMovement; curStep++) {
                        this.world.removeObstacleInField(moveOrigin.getX() + negative * curStep, moveOrigin.getY());
                    }
                    moveOrigin.addX(negative * xMovement);
                    xDist -= negative * xMovement;
                } else {
                    int yMovement = r.nextInt(1, Math.abs(yDist) + 1);
                    yMovement = yMovement % 4;
                    int negative = yDist < 0 ? -1 : 1;
                    for (int curStep = 0; curStep <= yMovement; curStep++) {
                        this.world.removeObstacleInField(moveOrigin.getX(), moveOrigin.getY() + negative * curStep);
                    }
                    moveOrigin.addY(negative * yMovement);
                    yDist -= negative * yMovement;
                }
            }

        }
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
