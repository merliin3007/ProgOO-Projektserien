package controller;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.Timer;

import utility.Utility;
import model.World;
import view.ConsoleView;
import view.GraphicView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * This is our main program. It is responsible for creating all of the objects
 * that are part of the MVC pattern and connecting them with each other.
 */
public class Labyrinth {

    public static final int fieldDimensionX = 15;       // Default: 25
    public static final int fieldDimensionY = 15;       // Default: 25
    public static boolean enableConsoleView = false;    // Default: true

	public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Utility.killRAM();

            	// Dimension of the game board (10x10).
            	int width = 100;
    			int height = 50;
    			// Create a new game world.
            	World world = new World(width, height);
            	
            	// Size of a field in the graphical view.
            	Dimension fieldDimensions = new Dimension(fieldDimensionX, fieldDimensionY);
            	// Create and register graphical view.
            	GraphicView gview = new GraphicView(width * fieldDimensions.width, height * fieldDimensions.height, fieldDimensions);
            	world.registerView(gview);
                gview.setVisible(true);
            	
            	// Create and register console view.
                if (Labyrinth.enableConsoleView) {
        		    ConsoleView cview = new ConsoleView();
            	    world.registerView(cview);
                }
            	
            	// Create controller and initialize JFrame.
                Controller controller = new Controller(world);
                controller.setTitle("Der Bus nach Raisdorf");
                controller.setResizable(false);                
                controller.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                controller.getContentPane().add(gview);
                // pack() is needed before JFrame size can be calculated.
                controller.pack();

                // Calculate size of window by size of insets (titlebar + border) and size of graphical view.
                Insets insets = controller.getInsets();
                
                int windowX = width * fieldDimensions.width + insets.left + insets.right;
                int windowY = height * fieldDimensions.height + insets.bottom + insets.top;
                Dimension size = new Dimension(windowX, windowY);
                controller.setSize(size);
                controller.setMinimumSize(size);
                controller.setVisible(true);





                ActionListener actionListener = new ActionListener() {
                    float time = 0.0f;
                    public void actionPerformed(ActionEvent actionEvent) {
                        world.timerTick(time);
                        time += 0.01f; // TODO: measure delta time
                    }
                };
                Timer timer = new Timer(3, actionListener);
                timer.start();
            }
        });

        /*
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                ActionListener actionListener = new ActionListener() {

                    public void actionPerformed(ActionEvent actionEvent) {
                        System.out.println("Hello");
                    }
                };
                Timer timer = new Timer(500, actionListener);
                timer.start();
            }
        });
        */


    }
}
