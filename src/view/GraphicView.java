package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;

import utility.Lighting;
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
	/** The scaling factor. */
	private Dimension fieldDimension;

	BufferedImage playerTexture;
	BufferedImage houseTexture;
	
	public GraphicView(int width, int height, Dimension fieldDimension) {
		this.WIDTH = width;
		this.HEIGHT = height;
		this.fieldDimension = fieldDimension;
		this.bg = new Rectangle(WIDTH, HEIGHT);

		try {
			File path = new File("resources");
			this.playerTexture = ImageIO.read(new File(path, "char.png"));
			this.houseTexture = ImageIO.read(new File(path, "house.png"));
		} catch (IOException e) {
			System.out.println("Loading a Texture failed.");
		}
	}
	
	/** The background rectangle. */
	private final Rectangle bg;
	/** The rectangle we're moving. */
	private final Rectangle player = new Rectangle(1, 1);
	/** A list of all obstacles in the world */
	private final ArrayList<Rectangle> obstacles = new ArrayList<Rectangle>();
	private final ArrayList<Lighting> obstacleBrighness = new ArrayList<Lighting>();  
	/** A list of all enemies in the world */
	private final ArrayList<Rectangle> enemies = new ArrayList<Rectangle>();
	/** The finish field */
	private final Rectangle finishField = new Rectangle(1, 1);
	/** The level text */
	private final Rectangle levelCounter = new Rectangle(1, 1);
	/** The text the level counter ist displaying */
	private String levelCounterContent = "";
	/** The global brighness. */
	private final Lighting globalLighting = new Lighting(1.0f);

	private boolean isEnabled = true;

	/**
	 * Creates a new instance.
	 */
	@Override
	public void paint(Graphics g) {
		if (!isEnabled) {
			return;
		}

		/* Game */

		// Paint background
		g.setColor(this.globalLighting.applyToRgb(200, 200, 200));
		g.fillRect(bg.x, bg.y, bg.width, bg.height);
		// Paint Finish field
		g.drawImage(this.houseTexture, finishField.x, finishField.y, finishField.width, finishField.height, null);
		// Paint obstacles
		for (int i = 0; i < this.obstacles.size(); ++i) {
			Rectangle obstacle = this.obstacles.get(i);
			Lighting lighting = this.obstacleBrighness.get(i);
			g.setColor(lighting.applyToRgb(100, 100, 100));
			g.fillRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
		}
		// Paint player
		g.drawImage(this.playerTexture, player.x, player.y, player.width, player.height, null);
		// Paint enemies
		for (Rectangle enemy : this.enemies) {
			g.setColor(Color.RED);
			g.fillRect(enemy.x, enemy.y, enemy.width, enemy.height);
		}
		
		/* Overlay */

		// Draw Level Counter
		g.setColor(Color.WHITE);
		g.drawString(String.format(this.levelCounterContent), levelCounter.x, levelCounter.y);
	}

	@Override
	public void update(World world) {
		if (!isEnabled) {
			return;
		}

		/* Game */

		this.globalLighting.setVal(world.getGlobalBrightness());
		// Update Finish Field
		finishField.setSize(fieldDimension);
		finishField.setLocation(
			(int)(world.getFinishX() * fieldDimension.width),
			(int)(world.getFinishY() * fieldDimension.height)
		);
		// Update obstacles
		this.obstacles.clear();
		this.obstacleBrighness.clear();
		for (int i = 0; i < world.getObstacleMap().length; ++i) {
			for (int j = 0; j < world.getObstacleMap()[i].length; ++j) {
				if (world.getObstacleMap()[i][j]) {
					Rectangle obstacle = new Rectangle(1, 1);
					obstacle.setSize(fieldDimension);
					obstacle.setLocation(
						(int)(j * fieldDimension.width),
						(int)(i * fieldDimension.height)
					);
					this.obstacles.add(obstacle);
					this.obstacleBrighness.add(new Lighting(world.getLightingMap()[i][j] * world.getGlobalBrightness()));
				}
			}
		}
		// Update players size and location
		player.setSize(fieldDimension);
		player.setLocation(
			(int)(world.getPlayerX() * fieldDimension.width),
			(int)(world.getPlayerY() * fieldDimension.height)
		);
		// Update enemies
		this.enemies.clear();
		for (Enemy enemy : world.getEnemies()) {
			Rectangle enemyRectangle = new Rectangle(1, 1);
			enemyRectangle.setSize(fieldDimension);
			enemyRectangle.setLocation(
				(int)(enemy.getPositionX() * fieldDimension.width),
				(int)(enemy.getPositionY() * fieldDimension.height)
			);
			this.enemies.add(enemyRectangle);
		}

		/* Overlay */

		// Level Counter
		this.levelCounter.setLocation(
			(int)(1 * this.fieldDimension.width),
			(int)(world.getHeight() * this.fieldDimension.height - this.fieldDimension.height)
		);

		this.levelCounterContent = String.format("Level: %d", world.getLevel());

		repaint();
	}

	@Override
	public boolean getIsEnabled() { return this.isEnabled; }

	@Override
	public void setIsEnabled(boolean isEnabled) { this.isEnabled = isEnabled; }
	
}
