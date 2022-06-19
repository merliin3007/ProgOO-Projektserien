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
import javax.swing.plaf.DimensionUIResource;

import java.awt.image.BufferedImage;

import utility.Utility;
import utility.Lighting;
import utility.Point2f;
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
	private Dimension fieldDimension, cameraDimension;

	BufferedImage playerTexture;
	BufferedImage houseTexture;
	
	public GraphicView(int width, int height, Dimension fieldDimension) {
		this.WIDTH = width;
		this.HEIGHT = height;
		this.fieldDimension = fieldDimension;
		this.cameraDimension = new Dimension(
			(int)(fieldDimension.getWidth() * this.zoom), 
			(int)(fieldDimension.getHeight() * this.zoom)
		);
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

	private Point2f cameraPosition = new Point2f(10.f, 5.f);
	private float zoom = 2.f;
	private float cameraFollowSpeed = 2.5f;

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
		finishField.setSize(cameraDimension);
		finishField.setLocation(
			(int)(world.getFinishX() * cameraDimension.width),
			(int)(world.getFinishY() * cameraDimension.height)
		);
		// Update obstacles
		this.obstacles.clear();
		this.obstacleBrighness.clear();
		for (int i = 0; i < world.getObstacleMap().length; ++i) {
			for (int j = 0; j < world.getObstacleMap()[i].length; ++j) {
				if (world.getObstacleMap()[i][j]) {
					Rectangle obstacle = new Rectangle(1, 1);
					obstacle.setSize(this.cameraDimension);
					obstacle.setLocation(
						(int)((j * cameraDimension.width) + this.cameraPosition.getX()),
						(int)((i * cameraDimension.height) + this.cameraPosition.getY())
					);
					this.obstacles.add(obstacle);
					this.obstacleBrighness.add(new Lighting(world.getLightingMap()[i][j] * world.getGlobalBrightness()));
				}
			}
		}
		// Update players size and location
		player.setSize(cameraDimension);
		player.setLocation(
			(int)(world.getPlayerX() * cameraDimension.width),
			(int)(world.getPlayerY() * cameraDimension.height)
		);
		// Update enemies
		this.enemies.clear();
		for (Enemy enemy : world.getEnemies()) {
			Rectangle enemyRectangle = new Rectangle(1, 1);
			enemyRectangle.setSize(cameraDimension);
			enemyRectangle.setLocation(
				(int)(enemy.getPositionX() * cameraDimension.width),
				(int)(enemy.getPositionY() * cameraDimension.height)
			);
			this.enemies.add(enemyRectangle);
		}

		/* Overlay */

		// Level Counter
		this.levelCounter.setLocation(
			(int)(1 * this.cameraDimension.width),
			(int)(world.getHeight() * this.cameraDimension.height - this.cameraDimension.height)
		);

		this.levelCounterContent = String.format("Level: %d", world.getLevel());

		repaint();
	}

	@Override
	public void updateCamera(World world, float deltaTime) {
		float sceneMinX = -1000000.f;
		float sceneMaxX = 1000000.f;
		// x
        float center_offset = (1.f / this.zoom) / 2.f;
        float view_center_x = this.cameraPosition.getX() + center_offset;
        if (utility.Utility.floatcmp((float)(world.getPlayerX()), view_center_x, 0.00001f))
            return;
        float dir = world.getPlayerX() < view_center_x ? -1.f : 1.f;
        float border_diff = dir == -1.f ? Math.abs(view_center_x - sceneMaxX) : Math.abs(view_center_x - sceneMaxX);
        this.move(dir * Math.abs(world.getPlayerX() - view_center_x) * this.cameraFollowSpeed * deltaTime, 0.f);
	}

	private void move(float dirX, float dirY) {
		this.cameraPosition.addX(dirX);
		this.cameraPosition.addY(dirY);
	}

	@Override
	public boolean getIsEnabled() { return this.isEnabled; }

	@Override
	public void setIsEnabled(boolean isEnabled) { this.isEnabled = isEnabled; }
	
}
