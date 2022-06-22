package model;

import java.util.Random;

/* Project */
import utility.Point2d;
import utility.Utility;

public class CaveWorldGenerator implements WorldGenerator {
    private World world;
    
    public CaveWorldGenerator(World world) {
        this.world = world;
    }
    
    @Override
    public void generateWorld() {
        if (Utility.DEBUG) {
            System.out.println("Generating Cave World...");
        }

        /* Random start and end point TODO: handle case start = finish */
        Random rnd = new Random();
        this.world.setStart(Point2d.RandomPoint2d(this.world.getWidth(), this.world.getHeight()));
        this.world.setFinish(Point2d.RandomPoint2d(this.world.getWidth(), this.world.getHeight()));
        /* Fill the whole map with obstacles */
        for (int i = 0; i < this.world.getWidth(); ++i) {
            for (int j = 0; j < this.world.getHeight(); ++j) {
                this.world.getObstacleMap()[j][i] = true;
            }
        }
        /* Create a path from start to finish */
        int currentX = this.world.getStartX(), currentY = this.world.getStartY();
        this.world.getObstacleMap()[currentY][currentX] = false;
        while (this.world.getObstacleMap()[this.world.getFinishY()][this.world.getFinishX()]) {
            int[][] directions = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
            utility.Utility.shuffleArray(directions);
            /* try to move to a field that hasn't been visited yet */
            boolean hasMoved = false;
            for (int[] dir : directions) {
                int newX = currentX + dir[0], newY = currentY + dir[1];
                if (newX < 0 || newX >= this.world.getWidth() || newY < 0 || newY >= this.world.getHeight() || !this.world.getObstacleMap()[newY][newX]) {
                    continue;
                } else if (this.world.collisionAroundField(newX, newY) < 3) {
                    continue;
                } else {
                    this.world.getObstacleMap()[newY][newX] = false;
                    this.world.getEmptyFields().add(new Point2d(newX, newY));
                    currentX = newX;
                    currentY = newY;
                    hasMoved = true;
                    break;
                }
            }
            /* if no (new) move possible -> move directly towards finish */
            if (!hasMoved) {
                int distX = this.world.getFinishX() - currentX;
                int distY = this.world.getFinishY() - currentY;
                if (Math.abs(distX) >= Math.abs(distY)) {
                    currentX += distX > 0 ? 1 : -1;
                } else {
                    currentY += distY > 0 ? 1 : -1;
                }
                this.world.getObstacleMap()[currentY][currentX] = false;
            }
        }

        int upperBound = this.world.getWidth() * this.world.getHeight() / 4;
        int numRandomPaths = upperBound * this.world.getEmptyFields().size();
        for (int i = 0; i < numRandomPaths && this.world.getEmptyFields().size() < upperBound; ++i) {
            digRandomPath(rnd.nextInt((int) ((this.world.getWidth() * this.world.getHeight()) / 50)));
        }
    }

    public void digRandomPath(int pathLen) {
        Point2d current = Point2d.RandomPoint2d(this.world.getWidth(), this.world.getHeight());
        for (int i = 0; i < pathLen; ++i) {
            int[][] directions = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
            utility.Utility.shuffleArray(directions);
            /* try to move to a field that hasn't been visited yet */
            boolean hasMoved = false;
            for (int[] dir : directions) {
                int newX = current.getX() + dir[0], newY = current.getY() + dir[1];
                if (newX < 0 || newX >= this.world.getWidth() || newY < 0 || newY >= this.world.getHeight()) {
                    continue;
                } else if (!this.world.getObstacleMap()[newY][newX]) {
                    continue;
                } else if (this.world.collisionAroundField(newX, newY) < 3) {
                    continue;
                } else {
                    this.world.getObstacleMap()[newY][newX] = false;
                    this.world.getEmptyFields().add(new Point2d(newX, newY));
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
        while (this.world.getObstacleMap()[current.getY()][current.getX()]) {
            if (rnd.nextBoolean()) {
                int distX = this.world.getStartX() - current.getX();
                int distY = this.world.getStartY() - current.getY();
                if (Math.abs(distX) >= Math.abs(distY)) {
                    current.addX(distX > 0 ? 1 : -1);
                } else {
                    current.addY(distY > 0 ? 1 : -1);
                }
            }
        }
    }
}