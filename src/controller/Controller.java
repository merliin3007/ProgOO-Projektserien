package controller;

import model.Enemy;
import model.MovementDirection;
import model.World;
import utility.Point2d;
import view.View;

import javax.swing.*;
import java.awt.event.*;
import java.util.List;
import java.util.Random;


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
        this.world.setPlayerLocation(this.world.getStartX(), this.world.getStartY());

        // Listen for key events
        addKeyListener(this);
        // Listen for mouse events.
        // Not used in the current implementation.
        addMouseListener(this);
    }

    public void updateEnemies() {
        for (Enemy enemy : world.getEnemies()) {

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
                if (world.canMoveToField(newPosValues[0], newPosValues[1]) && 3 > world.collisionAroundField(newPosValues[0], newPosValues[1])) {
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
            if (world.canMoveToField(newLocationX, newLocationY)) {
                world.setPlayerLocation(newLocationX, newLocationY);
            } else {
                System.out.println("Da ist ne Wand!");
            }

            if (world.getPlayerX() == world.getFinishX() && world.getPlayerY() == world.getFinishY()) {
                this.reset();
            }
        
            if (enemy_timout) {
                updateEnemies();
            }
            enemy_timout = !enemy_timout;

            /* Gelegentlich auch mal sterben */
            for (Enemy enemy : this.world.getEnemies()) {
                if (enemy.getPositionX() == world.getPlayerX() && enemy.getPositionY() == world.getPlayerY()) {
                    this.resetGame();
                }
            }

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

    private void reset() {
        Random rnd = new Random();
        this.world.incLevel();
        this.world.resetWorld();
        this.world.setPlayerLocation(this.world.getStartX(), this.world.getStartY());
        for (int i = 0; i < 1 + (int)(this.world.getLevel() / 10); ++i) {
            Point2d spawnLocation = world.getEmptyFields().get(rnd.nextInt(world.getEmptyFields().size()));
            this.world.getEnemies().add(new Enemy(spawnLocation.getX(), spawnLocation.getY()));
        }
    }

    private void resetGame() {
        this.world.setLevel(0);
        this.reset();
    }
}
