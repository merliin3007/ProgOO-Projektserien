package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.ObjectStreamClass;
import java.util.ArrayList;

import javax.swing.JPanel;

import model.World;
import model.Enemy;

/**
 * A graphical view of the world.
 */
public class GraphicView extends JPanel implements View {
	
	/** The view's width. */
	private final int WIDTH;
	/** The view's height. */
	private final int HEIGHT;
	
	private Dimension fieldDimension;
	
	public GraphicView(int width, int height, Dimension fieldDimension) {
		this.WIDTH = width;
		this.HEIGHT = height;
		this.fieldDimension = fieldDimension;
		this.bg = new Rectangle(WIDTH, HEIGHT);
	}
	
	/** The background rectangle. */
	private final Rectangle bg;
	/** The rectangle we're moving. */
	private final Rectangle player = new Rectangle(1, 1);

	private final ArrayList<Rectangle> obstacles = new ArrayList<Rectangle>();

	private final ArrayList<Rectangle> enemies = new ArrayList<Rectangle>();
	
	/**
	 * Creates a new instance.
	 */
	@Override
	public void paint(Graphics g) {
		// Paint background
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(bg.x, bg.y, bg.width, bg.height);
		// Paint player
		g.setColor(Color.BLACK);
		g.fillRect(player.x, player.y, player.width, player.height);
		// Paint obstacles
		for (Rectangle obstacle : this.obstacles) {
			g.setColor(Color.DARK_GRAY);
			g.fillRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
		}
		// Paint enemies
		for (Rectangle enemy : this.enemies) {
			g.setColor(Color.RED);
			g.fillRect(enemy.x, enemy.y, enemy.width, enemy.height);
		}
	}

	@Override
	public void update(World world) {
		
		// Update players size and location
		player.setSize(fieldDimension);
		player.setLocation((int)
				(world.getPlayerX() * fieldDimension.width),
				(int) (world.getPlayerY() * fieldDimension.height));
		// Update obstacles
		this.obstacles.clear();
		for (int i = 0; i < world.getObstacleMap().length; ++i) {
			for (int j = 0; j < world.getObstacleMap()[i].length; ++j) {
				if (world.getObstacleMap()[i][j]) {
					Rectangle obstacle = new Rectangle(1, 1);
					obstacle.setSize(fieldDimension);
					obstacle.setLocation(
						(int) (j * fieldDimension.width),
						(int) (i * fieldDimension.height));
					this.obstacles.add(obstacle);
				}
			}
		}
		// Update enemies
		this.enemies.clear();
		for (Enemy enemy : world.getEnemies()) {
			Rectangle enemyRectangle = new Rectangle(1, 1);
			enemyRectangle.setSize(fieldDimension);
			enemyRectangle.setLocation(
				(int) (enemy.getPositionX() * fieldDimension.width),
				(int) (enemy.getPositionY() * fieldDimension.height));
			this.enemies.add(enemyRectangle);
		}
		repaint();
	}
	
}
