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
	private float cameraFollowSpeed = .5f;

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
			(int)((world.getFinishX() * cameraDimension.width) + this.cameraPosition.getX()),
			(int)((world.getFinishY() * cameraDimension.height) + this.cameraPosition.getY())
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
			(int)((world.getPlayerX() * cameraDimension.width) + this.cameraPosition.getX()),
			(int)((world.getPlayerY() * cameraDimension.height) + this.cameraPosition.getY())
		);
		// Update enemies
		this.enemies.clear();
		for (Enemy enemy : world.getEnemies()) {
			Rectangle enemyRectangle = new Rectangle(1, 1);
			enemyRectangle.setSize(cameraDimension);
			enemyRectangle.setLocation(
				(int)((enemy.getPositionX() * cameraDimension.width) + this.cameraPosition.getX()),
				(int)((enemy.getPositionY() * cameraDimension.height + this.cameraPosition.getY()))
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
	public void updateCamera(World world, float deltaTime) {
		this.zoom = 1.f + (float)Math.log((double)world.getLevel()) * 0.5f;
		this.cameraDimension.setSize((double)(this.fieldDimension.getWidth() * this.zoom), (double)(this.fieldDimension.getHeight() * this.zoom));

		deltaTime /= 100.f;

		float moveX = 0.0f;
		float moveY = 0.0f;

		/* Update Follow X */
		float viewCenterX = this.getWidth() / 2.f;
		if (!Utility.intcmp((int)(world.getPlayerX()), (int)viewCenterX, 5)) {
			float dir = this.player.getLocation().getX() < viewCenterX ? 1.f : -1.f;
			moveX = dir * Math.abs((float)this.player.getLocation().getX() - viewCenterX) * this.cameraFollowSpeed * deltaTime;
		}

		/* Update Follow Y */
		float viewCenterY = this.getHeight() / 2.f;
		if (!Utility.floatcmp((float)(world.getPlayerY()), viewCenterY, 1.f)) {
			float dir = this.player.getLocation().getY() < viewCenterY ? 1.f : -1.f;
			moveY = dir * Math.abs((float)this.player.getLocation().getY() - viewCenterY) * this.cameraFollowSpeed * deltaTime;
		}

		this.move(moveX, moveY);

		System.out.println(this.cameraPosition);
		System.out.println(this.getWidth());
		System.out.println(1.f / deltaTime);
		System.out.println(this.zoom);

		/* limit scene x */
		if ((int)this.cameraPosition.getX() > 0) {
			this.cameraPosition.setX(0.f);
		} else if (this.cameraPosition.getX() < -this.getWidth() * (this.zoom - 1.f)) {
			this.cameraPosition.setX((int)(-this.getWidth() * (this.zoom - 1.f)));
		}

		/* limit scene y */
		if (this.cameraPosition.getY() > 0.f) {
			this.cameraPosition.setY(0.f);
		} else if (this.cameraPosition.getY() < -this.getHeight() * (this.zoom -1.f)) {
			this.cameraPosition.setY(-this.getHeight() * (this.zoom -1.f));
		}

		this.update(world);
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
