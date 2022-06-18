package controller;

import model.Enemy;
import model.MovementDirection;
import model.World;
import utility.Point2d;
import view.View;

import javax.swing.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;


/**
 * Our controller listens for key events on the main window.
 */
public class Controller extends JFrame implements KeyListener, ActionListener, MouseListener {

    /**
     * The world that is updated upon every key press.
     */
    private World world;
    private List<View> views;

    private boolean enemy_timout = false, alreadyPressed = false;

    /**
     * Creates a new instance.
     *
     * @param world the world to be updated whenever the player should move.
     *              // @param caged the {//@link GraphicsProgram} we want to listen for key presses
     *              on.
     */
    public Controller(World world) {
        // Remember the world
        this.world = world;
        world.getEnemies().add(new Enemy(0, 0));

        // Listen for key events
        addKeyListener(this);
        // Listen for mouse events.
        // Not used in the current implementation.
        addMouseListener(this);
    }

    public void updateEnemies() {
        for (Enemy enemy : world.getEnemies()) {

			/*
			Point2d moveTo = this.findFirstPath(enemy.getPositionX(), enemy.getPositionY(), world.getPlayerX(), world.getPlayerY());
			if (moveTo.getPosX() != enemy.getPositionX() || moveTo.getPosY() != enemy.getPositionY()) {
				enemy.setPositionX(moveTo.getPosStartX());
				enemy.setPositionY(moveTo.getPosStartY());
				System.out.println("jajaja");
				continue;
			}*/

            int distanceX = world.getPlayerX() - enemy.getPositionX();
            int distanceY = enemy.getPositionY() - world.getPlayerY();

            boolean moveTowardsPlayer = true;
            int[] newPosValues = new int[2];
            boolean handleXFirst = Math.abs(distanceX) >= Math.abs(distanceY);
            for (int i = 0; i < 4; i++) {
                if (handleXFirst) {
                    if (distanceX == 0) // already in same row, moving here is useless
                        continue;
                    // handle x-value
                    newPosValues[0] = enemy.getPositionX() + (distanceX > 0 == moveTowardsPlayer ? 1 : -1);
                    newPosValues[1] = enemy.getPositionY();
                } else {
                    if (distanceY == 0) // already in same row, moving here is useless
                        continue;
                    // handle y-value
                    newPosValues[0] = enemy.getPositionX();
                    newPosValues[1] = enemy.getPositionY() + (distanceY > 0 == moveTowardsPlayer ? -1 : 1);
                }
                if (canMoveToField(newPosValues[0], newPosValues[1]) && 3 > collisionAroundField(newPosValues[0], newPosValues[1])) {
                    if (handleXFirst)
                        enemy.setPositionX(newPosValues[0]);
                    else
                        enemy.setPositionY(newPosValues[1]);
                    break; // successful move
                }
                handleXFirst = !handleXFirst; // toggle if move unsuccessful
                if (i == 1)  // tried moving in player direction, now try opposite
                    moveTowardsPlayer = false;
            }
        }
    }

    public Point2d findFirstPath(int fromX, int fromY, int toX, int toY) {
        /**
         * TODO: Djikstra
         */
        Stack<Point2d> stack = new Stack<Point2d>();
        //stack.push(new Point2d(fromX, fromY, -1, -1));
        // TODO remove
        //Point2d init = new Point2d(fromX, fromY, -1, -1);
        stack.push(new Point2d(fromX, fromY + 1, fromX, fromY + 1, 0));
        stack.push(new Point2d(fromX + 1, fromY + 1, fromX + 1, fromY + 1, 0));
        stack.push(new Point2d(fromX - 1, fromY + 1, fromX - 1, fromY + 1, 0));
        stack.push(new Point2d(fromX, fromY - 1, fromX, fromY - 1, 0));
        stack.push(new Point2d(fromX + 1, fromY - 1, fromX + 1, fromY - 1, 0));
        stack.push(new Point2d(fromX - 1, fromY - 1, fromX - 1, fromY - 1, 0));
        stack.push(new Point2d(fromX + 1, fromY, fromX + 1, fromY, 0));
        stack.push(new Point2d(fromX - 1, fromY, fromX - 1, fromY, 0));
        HashSet<String> visited = new HashSet<String>();
        Point2d currentShortest = new Point2d(fromX, fromY, fromX, fromY, Integer.MAX_VALUE);
        while (stack.size() != 0) {
            Point2d top = stack.pop();
            if (!visited.contains(top.toString()) && canMoveToField(top.getPosX(), top.getPosY())) {
                System.out.println(String.valueOf(top.getPosX()) + "," + String.valueOf(top.getPosY()));
                System.out.println(top.toString());
                visited.add(top.toString());
                if (toX == top.getPosX() && toY == top.getPosY()) {
                    if (top.getPathLength() <= currentShortest.getPathLength()) {
                        currentShortest = top;
                    }
                    continue;
                }
                stack.push(new Point2d(top.getPosX(), top.getPosY() + 1, top.getPosStartX(), top.getPosStartY(), top.getPathLength() + 1));
                stack.push(new Point2d(top.getPosX(), top.getPosY() - 1, top.getPosStartX(), top.getPosStartY(), top.getPathLength() + 1));
                stack.push(new Point2d(top.getPosX() + 1, top.getPosY() + 1, top.getPosStartX(), top.getPosStartY(), top.getPathLength() + 1));
                stack.push(new Point2d(top.getPosX() - 1, top.getPosY() + 1, top.getPosStartX(), top.getPosStartY(), top.getPathLength() + 1));
                stack.push(new Point2d(top.getPosX() + 1, top.getPosY() - 1, top.getPosStartX(), top.getPosStartY(), top.getPathLength() + 1));
                stack.push(new Point2d(top.getPosX() - 1, top.getPosY() - 1, top.getPosStartX(), top.getPosStartY(), top.getPathLength() + 1));
                stack.push(new Point2d(top.getPosX() + 1, top.getPosY(), top.getPosStartX(), top.getPosStartY(), top.getPathLength() + 1));
                stack.push(new Point2d(top.getPosX() - 1, top.getPosY(), top.getPosStartX(), top.getPosStartY(), top.getPathLength() + 1));
            }
        }
        System.out.print(currentShortest.toString() + " " + currentShortest.getPathLength());
        return currentShortest;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
    }

    /////////////////// Key Events ////////////////////////////////

    @Override
    public void keyPressed(KeyEvent e) {
        if (!alreadyPressed) {
            alreadyPressed = true;
            // Check if we need to do something. Tells the world to move the player.
            MovementDirection dir;
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    dir = MovementDirection.UP;
                    break;

                case KeyEvent.VK_DOWN:
                    dir = MovementDirection.DOWN;
                    break;

                case KeyEvent.VK_LEFT:
                    dir = MovementDirection.LEFT;
                    break;

                case KeyEvent.VK_RIGHT:
                    dir = MovementDirection.RIGHT;
                    break;

                default:
                    dir = MovementDirection.NONE;
            }

            int newLocationX = world.getPlayerX() + dir.deltaX, newLocationY = world.getPlayerY() + dir.deltaY;
            if (canMoveToField(newLocationX, newLocationY)) {
                world.setPlayerLocation(newLocationX, newLocationY);
            } else {
                System.out.println("Da ist ne Wand!");
            }

            if (enemy_timout) {
                updateEnemies();
            }
            enemy_timout = !enemy_timout;

            world.setPlayerLocation(world.getPlayerX(), world.getPlayerY());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub
        alreadyPressed = false;
    }

    /////////////////// Action Events ////////////////////////////////

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub

    }

    /////////////////// Mouse Events ////////////////////////////////

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    private boolean canMoveToField(int xPos, int yPos) {
        if (xPos >= this.world.getWidth() || xPos < 0
                || yPos >= this.world.getHeight() || yPos < 0) {
            return false;
        }
        return !world.getField(xPos, yPos);
    }

    private int collisionAroundField(int xPos, int yPos) {
        int sum = 0;
        for (MovementDirection move : MovementDirection.values()) {
            if (!canMoveToField(xPos + move.deltaX, yPos + move.deltaY))
                sum++;
        }
        return sum;
    }
}
