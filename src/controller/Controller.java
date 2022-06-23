package controller;

import model.Enemy;
import model.MovementDirection;
import model.World;
import utility.Point2d;
import view.EnvironmentEvent;

import javax.swing.*;
import java.awt.event.*;


/**
 * Our controller listens for key events on the main window.
 */
public class Controller extends JFrame implements KeyListener, ActionListener, MouseListener {

    /**
     * The world that is updated upon every key press.
     */
    private World world;

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

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
    }

    /**
     * Key Events
     */

    /**
     * Gets called when a key is pressed.
     * 
     * @param e Contains information such as the keycode about the key being pressed.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        // Check if we need to do something. Tells the world to move the player.
        MovementDirection dir;
        switch (e.getKeyCode()) {
            /* Move up */
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                dir = MovementDirection.UP;
                break;

            /* Move down */
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                dir = MovementDirection.DOWN;
                break;

            /* Move left */
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                dir = MovementDirection.LEFT;
                break;

            /* Move right */
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                dir = MovementDirection.RIGHT;
                break;

            /* Change difficulty */
            case KeyEvent.VK_ENTER:
                this.world.cycleDifficutly();
                return;

            /* Reset the game */
            case KeyEvent.VK_R:
                this.world.resetGame();
                return;

            default:
                dir = MovementDirection.NONE;
        }

        /* Calculate the (possibly) new location of the player */
        int newLocationX = world.getPlayerX() + dir.deltaX, newLocationY = world.getPlayerY() + dir.deltaY;
        /* Update player location if the player can move to the new field. */
        if (world.canMoveToField(newLocationX, newLocationY) && !Point2d.equalPoints(new Point2d(newLocationX, newLocationY), world.getPlayerLocation())) {
            /* Player can move to field -> move to field */
            world.setPlayerLocation(newLocationX, newLocationY);
            world.triggerEnvironmentEvent(EnvironmentEvent.PLAYER_STEP);
        } else {
            /* Player can not move to field -> yell at the user for the premium gaming experience */
            if (!Point2d.equalPoints(new Point2d(newLocationX, newLocationY), world.getPlayerLocation())) {
                System.out.println("You shall not pass!");
            } else {
                System.out.println("Das war der falsche Knopf!");
            }
            return; // TODO check - enemy only updated on player move action 
        }

        /* finish is a language */
        if (world.getPlayerX() == world.getFinishX() && world.getPlayerY() == world.getFinishY()) {
            this.world.reset();
        }
        
        /* update the enemies */
        this.world.updateEnemies();
        
        /* Gelegentlich auch mal sterben */
        for (Enemy enemy : this.world.getEnemies()) {
            if (enemy.getPositionX() == world.getPlayerX() && enemy.getPositionY() == world.getPlayerY()) {
                this.world.resetGame();
                this.world.triggerEnvironmentEvent(EnvironmentEvent.PLAYER_DIED);
            }
        }

        /* Update player location */
        world.setPlayerLocation(world.getPlayerX(), world.getPlayerY());
    }
    

    @Override
    public void keyReleased(KeyEvent e) { }

    /**
     * Action Events
     */

    @Override
    public void actionPerformed(ActionEvent e) { }

    /**
     * Mouse Events
     */

    @Override
    public void mouseClicked(MouseEvent e) {
        this.world.cycleDifficutly();
    }

    @Override
    public void mousePressed(MouseEvent e) { }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }
}
