package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.AlphaComposite;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.plaf.DimensionUIResource;

import java.awt.image.BufferedImage;

import utility.Utility;
import utility.Lighting;
import utility.Point2d;
import utility.Point2f;
import model.World;
import model.Enemy;

/**
 * A graphical view of the world.
 */
public class GraphicView extends JPanel implements View {

	private final boolean ENABLE_SMOOTH_LIGHTING_ANIM = true;
	private final float SMOOTH_LIGHTING_ANIM_FACTOR = .5f;
	
	/** The view's width. */
	private final int WIDTH;
	/** The view's height. */
	private final int HEIGHT;
	/** The scaling factor. */
	private Dimension fieldDimension, cameraDimension;

	BufferedImage playerTexture;
	BufferedImage houseTexture;
	BufferedImage creeperTexture;
	
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
			this.playerTexture = ImageIO.read(new File(path, "steve.png"));
			this.houseTexture = ImageIO.read(new File(path, "house.png"));
			this.creeperTexture = ImageIO.read(new File(path, "creeper.png"));
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

	private float[][] lightingMap;
	private float[][] playerDistanceLightingMap;

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
		for (int i = 0; i < this.playerDistanceLightingMap[0].length; ++i) {
			for (int j = 0; j < this.playerDistanceLightingMap.length; ++j) {
				Lighting lighting = new Lighting(this.playerDistanceLightingMap[j][i] * this.playerDistanceLightingMap[j][i]);
				g.setColor(lighting.applyToRgb(200, 200, 200));
				g.fillRect(
					(int)(i * this.cameraDimension.getWidth() + this.cameraPosition.getX()), 
					(int)(j * this.cameraDimension.getHeight() + this.cameraPosition.getY()), 
					(int)(this.cameraDimension.getWidth()), 
					(int)(this.cameraDimension.getHeight())
				);
			}
		}
		// Paint Finish field
		g.drawImage(this.houseTexture, finishField.x, finishField.y, finishField.width, finishField.height, null);
		// Paint obstacles
		for (int i = 0; i < this.obstacles.size(); ++i) {
			Rectangle obstacle = this.obstacles.get(i);
			Lighting lighting = this.obstacleBrighness.get(i);

			int y = (int)(obstacle.y / this.cameraDimension.getHeight());
			int x = (int)(obstacle.x / this.cameraDimension.getWidth());
			x -= this.cameraPosition.getX() / this.cameraDimension.getWidth();
			y -= this.cameraPosition.getY() / this.cameraDimension.getHeight();
			y = y >= this.playerDistanceLightingMap.length ? this.playerDistanceLightingMap.length : y < 0 ? 0 : y;
			x = x >= this.playerDistanceLightingMap[0].length ? this.playerDistanceLightingMap.length : x < 0 ? 0 : x;
			lighting.multVal(this.playerDistanceLightingMap[y][x]);

			g.setColor(lighting.applyToRgb(100, 100, 100));
			g.fillRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
		}
		// Paint player
		g.drawImage(this.playerTexture, player.x, player.y, player.width, player.height, null);
		g.fillRect(player.x, player.y, player.width, player.height);
		// Paint enemies
		for (Rectangle enemy : this.enemies) {
			//g.setColor(Color.RED);
			//g.fillRect(enemy.x, enemy.y, enemy.width, enemy.height);
			g.drawImage(this.creeperTexture, enemy.x, enemy.y, enemy.width, enemy.height, null);
			g.setColor(new Color(0, 0, 0, 128));
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
					this.obstacleBrighness.add(new Lighting(this.lightingMap[i][j] * world.getGlobalBrightness()));
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
				(int)((enemy.getPositionY() * cameraDimension.height) + this.cameraPosition.getY())
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
		//this.playerDistanceLightingMap = world.getPlayerDistanceLightingMap();
		this.generatePlayerDistanceLightingMap(world, deltaTime);

		/* Update zoom. */
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

		/* Move the camera. */
		this.move(moveX, moveY);

		/* limit scene x */
		float factorX = (float)(this.fieldDimension.getWidth() - this.cameraDimension.getWidth()) * world.getWidth();
		if ((int)this.cameraPosition.getX() > 0) {
			this.cameraPosition.setX(0.f);
		} else if (this.cameraPosition.getX() < factorX) {
			this.cameraPosition.setX(factorX);
		}
		
		/* limit scene y */
		float factorY = (float)(this.fieldDimension.getHeight() - this.cameraDimension.getHeight()) * world.getHeight();
		if (this.cameraPosition.getY() > 0.f) {
			this.cameraPosition.setY(0.f);
		} else if (this.cameraPosition.getY() < factorY) {
			this.cameraPosition.setY(factorY);
		}

		if (Utility.DEBUG_GRAPHICS) {
			System.out.println("Camera Position: " + this.cameraPosition.toString());
			System.out.println("Window Width: " + String.valueOf(this.getWidth()) + " Window Height: " + String.valueOf(this.getHeight()));
			System.out.println("Fps: " + String.valueOf(1.f / (deltaTime / 10.f)));
			System.out.println("Zoom: " + String.valueOf(this.zoom));
		}

		this.update(world);
	}

	@Override
	public void onLevelChanged(World world) {
		System.out.println("uffbuff");
		this.generateLevelLightingMap(world);
	}

	/**
	 * Moves the camera.
	 * 
	 * @param dirX the amount the camera should move on the x-axis.
	 * @param dirY the amount the camera should move on the y-axis.
	 */
	private void move(float dirX, float dirY) {
		this.cameraPosition.addX(dirX);
		this.cameraPosition.addY(dirY);
	}

	/**
	 * Generate a lighting-map based on the players distance.
	 * 
	 * @param world the world to generate the lighting map for.
	 */
	public void generatePlayerDistanceLightingMap(World world, float deltaTime) {
		/* Lightingmap should not be null */
		if (this.playerDistanceLightingMap == null) {
			this.playerDistanceLightingMap = new float[world.getHeight()][world.getWidth()];
		}

		/* Calculate lighting map */
        for (int i = 0; i < world.getWidth(); ++i) {
            for (int j = 0; j < world.getHeight(); ++j) {
                float levelFactor = (float)(Math.log(world.getLevel() > 1 ? world.getLevel() / 2.f : 1.f) + 1.f);
				if (!this.ENABLE_SMOOTH_LIGHTING_ANIM) {
					/* not lighting animation */
                	this.playerDistanceLightingMap[j][i] = 1.f / ((float)World.getDistance(i, j, world.getPlayerX(), world.getPlayerY()) * 0.1f * levelFactor);
				} else {
					/* smooth lighting animation */
					float lightVal = 1.f / ((float)World.getDistance(i, j, world.getPlayerX(), world.getPlayerY()) * 0.1f * levelFactor);
					lightVal = lightVal >= 1.f ? 1.f : lightVal;
					float smooth = this.SMOOTH_LIGHTING_ANIM_FACTOR * deltaTime;
					this.playerDistanceLightingMap[j][i] = (this.playerDistanceLightingMap[j][i] * smooth + lightVal) / (smooth + 1.f);
				}
            }
        }
    }

	/**
     * Generates a lighting map for the level world.
     */
    public void generateLevelLightingMap(World world) {
		/* lighting map should not be null */
		if (this.lightingMap == null) {
			this.lightingMap = new float[world.getHeight()][world.getWidth()];
		}

        /* Set empty fields to brightness 1.0f and obstacle fields to -1.0f */
        for (int i = 0; i < world.getWidth(); ++i) {
            for (int j = 0; j < world.getHeight(); ++j) {
                if (world.getObstacleMap()[j][i]) {
                    this.lightingMap[j][i] = -1.0f;
                } else {
                    this.lightingMap[j][i] = 1.0f;
                }
            }
        }

        /* Push all empty field points onto a stack */
        Stack<Point2d> s = new Stack<Point2d>();
        for (Point2d p : world.getEmptyFields()) {
            s.push(p);
        }

        int[][] directions = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, 1}, {1, -1}, {-1, -1}};
        while (s.size() != 0) {
            Point2d top = s.pop();
            /* Light this field if its not lit. */
            if (this.lightingMap[top.getY()][top.getX()] == -1.0f) {
                int numLightedNeighbors = 0;
                float lightingSum = 0.0f;
                /* Calculate Lighting for this field. */
                for (int[] dir : directions) {
                    int x = top.getX() + dir[0];
                    int y = top.getY() + dir[1];
                    if (x < 0 || x >= world.getWidth() || y < 0 || y >= world.getHeight()) {
                        continue;
                    }
                    float fieldVal = this.lightingMap[y][x];
                    if (fieldVal != -1.0f) {
                        lightingSum += fieldVal;
                        numLightedNeighbors++;
                    } else {
                        /* Push unlit neighbors to stack. */
                        s.push(new Point2d(x, y));
                    }
                }
                this.lightingMap[top.getY()][top.getX()] = (lightingSum / (float) numLightedNeighbors) * 0.95f;
            /* This field is already lit af. */
            } else {
                /* Push unlit neighbors to stack. */
                for (int[] dir : directions) {
                    int x = top.getX() + dir[0];
                    int y = top.getY() + dir[1];
                    if (x < 0 || x >= world.getWidth() || y < 0 || y >= world.getHeight()) {
                        continue;
                    }
                    float fieldVal = this.lightingMap[y][x];
                    if (fieldVal == -1.0f) {
                        s.push(new Point2d(x, y));
                    }
                }
            }
        }
    }

	@Override
	public boolean getIsEnabled() { return this.isEnabled; }

	@Override
	public void setIsEnabled(boolean isEnabled) { this.isEnabled = isEnabled; }
	
}
