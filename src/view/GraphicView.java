package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;

import utility.Utility;
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

	private final File TEXTURE_PATH = new File("resources");

	/* Textures */
	Texture playerTexture;
	Texture houseTexture;
	Texture zombieTexture;
	Texture drownedTexture;
	Texture creeperTexture;
	Texture creeperTriggeredTexture;
	Texture stoneTexture;
	Texture cobbleStoneTexture;

	AnimationTexture particleExplosionTexture;

	/* Colors */
	private Color pathColor = new Color(200, 200, 200);
	private Color obstacleColor = new Color(100, 100, 100);
	
	/**
	 * Creates a new instance.
	 */
	public GraphicView(int width, int height, Dimension fieldDimension) {
		this.WIDTH = width;
		this.HEIGHT = height;
		this.fieldDimension = fieldDimension;
		this.cameraDimension = new Dimension(
			(int)(fieldDimension.getWidth() * this.zoom), 
			(int)(fieldDimension.getHeight() * this.zoom)
		);

		if (Utility.DEBUG) {
			System.out.println(String.format("Created Window of Size %d * %d", this.WIDTH, this.HEIGHT));
		}

		/* Load all textures. */
		this.loadTextures();

		/* Init RenderObjects */
		this.player = new TextureRenderObject(new Point2d(0, 0), new Lighting(1.f), this.playerTexture);
		this.finish = new TextureRenderObject(new Point2d(0, 0), new Lighting(1.f), this.houseTexture);
	}
	
	/** The level text */
	private final Rectangle levelCounter = new Rectangle(1, 1);
	/** The text the level counter ist displaying */
	private String levelCounterContent = "";
	/** The global brighness. */
	private final Lighting globalLighting = new Lighting(1.0f);
	/** Sets whether this view is enabled or not. */
	private boolean isEnabled = true;

	/** The Player RenderObject */
	private RenderObject player;
	/** The Finish-Field RenderObject */
	private RenderObject finish;
	/** A list of all PathField RenderObjects */
	private ArrayList<RenderObject> pathFields = new ArrayList<RenderObject>();
	/** A list of all obstacles RenderObjects */
	private ArrayList<RenderObject> obstacles = new ArrayList<RenderObject>();
	/** A list of all enemy RenderObjects */
	private ArrayList<RenderObject> enemies = new ArrayList<RenderObject>();
	private ArrayList<ParticleRenderObject> particles = new ArrayList<ParticleRenderObject>();

	/** The position of the camera */
	private Point2f cameraPosition = new Point2f(10.f, 5.f);
	/** The zoom of the camera (higher -> zoom further in) */
	private float zoom = 2.f;
	/** The speed the camera is following the player at */
	private float cameraFollowSpeed = 1.5f; // 2.5f

	/** The shading map for the level, only changes, if a new level is generated (static lighting) */
	private float[][] levelLightingMap;
	/** The shading map for the player, changes every frame (dynamic lighting) */
	private float[][] playerDistanceLightingMap;

	@Override
	public void paint(Graphics g) {
		if (!isEnabled) {
			return;
		}

		/* Game */

		/* Paint path */
		for (RenderObject pathField : this.pathFields) {
			pathField.draw(g, this);
		}

		/* Paint Finish field */
		this.finish.draw(g, this);

		/* Paint obstacles */
		for (RenderObject obstacle : this.obstacles) {
			obstacle.draw(g, this);
		}

		/* Paint player */
		this.player.draw(g, this);

		/* Paint enemies */
		for (RenderObject enemy : this.enemies) {
			enemy.draw(g, this);
		}

		/* Paint particles */
		for (ParticleRenderObject particle : this.particles) {
			particle.draw(g, this);
		}

		/* Overlay */

		/* Draw Level Counter */
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
		
		/* Update Finish Field */
		this.finish.setPosition(world.getFinishCopy());
		this.finish.setLighting(getLighting(world.getFinishLocation()));

		/* Update obstacles */
		this.obstacles.clear();
		this.pathFields.clear();
		for (int i = 0; i < world.getObstacleMap().length; ++i) {
			for (int j = 0; j < world.getObstacleMap()[i].length; ++j) {
				if (world.getObstacleMap()[i][j]) {
					/* field is obstacle */
					Lighting lighting = new Lighting(this.levelLightingMap[i][j]);
					lighting.multVal(this.playerDistanceLightingMap[i][j]);
					this.obstacles.add(new ColorRenderObject(new Point2d(j, i), lighting, this.obstacleColor));
				} else {
					/* field is path */
					Lighting lighting = getLighting(j, i);
					lighting.multVal(lighting.getVal());
					this.pathFields.add(new ColorRenderObject(new Point2d(j, i), lighting, this.pathColor));
				}
			}
		}

		/* Update player */
		this.player.setPosition(new Point2d(world.getPlayerX(), world.getPlayerY()));
		this.player.setLighting(getLighting(world.getPlayerLocation()));

		/* Update enemies */
		this.enemies.clear();
		for (Enemy enemy : world.getEnemies()) {
			Point2d position = new Point2d(enemy.getPositionX(), enemy.getPositionY());
			switch (enemy.getRenderState()) {
			case ZOMBIE:
				this.enemies.add(new TextureRenderObject(position, this.getLighting(position), this.zombieTexture));
				break;
			case DROWNED:
				this.enemies.add(new TextureRenderObject(position, this.getLighting(position), this.drownedTexture));
				break;
			case CREEPER:
				this.enemies.add(new TextureRenderObject(position, this.getLighting(position), this.creeperTexture));
				break;
			case TRIGGERED_CREEPER:
				this.enemies.add(new TextureRenderObject(position, this.getLighting(position), this.creeperTriggeredTexture));
				break;
			}
		}

		/* Overlay */

		/* Level Counter */
		this.levelCounter.setLocation(
			(int)(1 * this.fieldDimension.width),
			(int)(world.getHeight() * this.fieldDimension.height - this.fieldDimension.height)
		);

		this.levelCounterContent = String.format("Level: %d", world.getLevel());

		repaint();
	}

	/**
	 * Updates the camera.
	 * Gets called every frame.
	 * 
	 * @param world the world that is rendered.
	 * @param deltaTime the time that passed by since the last call.
	 */
	@Override
	public void updateCamera(World world, float deltaTime) {
		/* Refresh the dynamic lighting map */
		this.generatePlayerDistanceLightingMap(world, deltaTime);

		/* Update textures */
		this.updateTextures(deltaTime);

		/* Update particles */
		this.updateParticles(deltaTime);

		/* Update zoom. */
		this.zoom = 1.f + (float)Math.log((double)world.getLevel()) * 0.5f;
		this.cameraDimension.setSize((double)(this.fieldDimension.getWidth() * this.zoom), (double)(this.fieldDimension.getHeight() * this.zoom));

		deltaTime /= 100.f;

		float moveX = 0.0f;
		float moveY = 0.0f;

		/* Update Follow X */
		float viewCenterX = this.getWidth() / 2.f;
		float playerPixelX = this.worldCoordinatesToPixel(this.player.getPosition()).getX();
		if (!Utility.intcmp((int)(playerPixelX), (int)viewCenterX, 5)) {
			float dir = playerPixelX < viewCenterX ? 1.f : -1.f;
			moveX = dir * Math.abs((float)playerPixelX - viewCenterX) * this.cameraFollowSpeed * deltaTime;
		}

		/* Update Follow Y */
		float viewCenterY = this.getHeight() / 2.f;
		float playerPixelY = this.worldCoordinatesToPixel(this.player.getPosition()).getY();
		if (!Utility.floatcmp((float)(playerPixelY), viewCenterY, 1.f)) {
			float dir = playerPixelY < viewCenterY ? 1.f : -1.f;
			moveY = dir * Math.abs((float)playerPixelY- viewCenterY) * this.cameraFollowSpeed * deltaTime;
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

		/* Print debug info */
		if (Utility.DEBUG_GRAPHICS) {
			System.out.println("Camera Position: " + this.cameraPosition.toString());
			System.out.println("Window Width: " + String.valueOf(this.getWidth()) + " Window Height: " + String.valueOf(this.getHeight()));
			System.out.println("Fps: " + String.valueOf(1.f / (deltaTime / 10.f)));
			System.out.println("Zoom: " + String.valueOf(this.zoom));
		}

		this.update(world);
	}

	/**
	 * Is called whenever the level changes
	 * 
	 * @param world The world in which the level changed.
	 */
	@Override
	public void onLevelChanged(World world) {
		/* Generate a shading map for the new level */
		this.generateLevelLightingMap(world);
	}

	@Override
	public void spawnExplosion(Point2d position, float size) {
		this.particles.add(new ParticleRenderObject(position, new Lighting(size), this.particleExplosionTexture, size));
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

	private void updateParticles(float deltaTime) {
		for (int i = 0; i < this.particles.size(); ++i) {
			ParticleRenderObject particle = this.particles.get(i);
			particle.updateFrame(deltaTime);
			if (!particle.getIsPlaying()) {
				this.particles.remove(i);
				i -= 1;
			}
		}
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
	 * 
	 * @param world The world to generate the shading map for.
     */
    public void generateLevelLightingMap(World world) {
		/* lighting map should not be null */
		if (this.levelLightingMap == null) {
			this.levelLightingMap = new float[world.getHeight()][world.getWidth()];
		}

        /* Set empty fields to brightness 1.0f and obstacle fields to -1.0f */
        for (int i = 0; i < world.getWidth(); ++i) {
            for (int j = 0; j < world.getHeight(); ++j) {
                if (world.getObstacleMap()[j][i]) {
                    this.levelLightingMap[j][i] = -1.0f;
                } else {
                    this.levelLightingMap[j][i] = 1.0f;
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
            if (this.levelLightingMap[top.getY()][top.getX()] == -1.0f) {
                int numLightedNeighbors = 0;
                float lightingSum = 0.0f;
                /* Calculate Lighting for this field. */
                for (int[] dir : directions) {
                    int x = top.getX() + dir[0];
                    int y = top.getY() + dir[1];
                    if (x < 0 || x >= world.getWidth() || y < 0 || y >= world.getHeight()) {
                        continue;
                    }
                    float fieldVal = this.levelLightingMap[y][x];
                    if (fieldVal != -1.0f) {
                        lightingSum += fieldVal;
                        numLightedNeighbors++;
                    } else {
                        /* Push unlit neighbors to stack. */
                        s.push(new Point2d(x, y));
                    }
                }
                this.levelLightingMap[top.getY()][top.getX()] = (lightingSum / (float) numLightedNeighbors) * 0.95f;
            /* This field is already lit af. */
            } else {
                /* Push unlit neighbors to stack. */
                for (int[] dir : directions) {
                    int x = top.getX() + dir[0];
                    int y = top.getY() + dir[1];
                    if (x < 0 || x >= world.getWidth() || y < 0 || y >= world.getHeight()) {
                        continue;
                    }
                    float fieldVal = this.levelLightingMap[y][x];
                    if (fieldVal == -1.0f) {
                        s.push(new Point2d(x, y));
                    }
                }
            }
        }
    }

	/**
	 * Translates world coordinates into pixel coordinates.
	 * 
	 * @param x The world-x-axis coordinate.
	 * @param y The world-y-axis coordinate.
	 * @return The position of the coresponding pixel.
	 */
	Point2d worldCoordinatesToPixel(int x, int y) {
		return new Point2d(
			(int)((x * cameraDimension.width) + this.cameraPosition.getX()),
			(int)((y * cameraDimension.height) + this.cameraPosition.getY())
		);
	}

	/**
	 * Translates world coordinates into pixel coordinates.
	 * 
	 * @param worldCoordinates The worl-coordinates.
	 * @return The position of the coresponding pixel.
	 */
	Point2d worldCoordinatesToPixel(Point2d worldCoorindates) {
		return new Point2d(
			(int)((worldCoorindates.getX() * cameraDimension.width) + this.cameraPosition.getX()),
			(int)((worldCoorindates.getY() * cameraDimension.height) + this.cameraPosition.getY())
		);
	}

	/**
	 * Loads all game textures.
	 */
	private void loadTextures() {
		this.playerTexture = this.loadTexture("steve.png");
		this.houseTexture = this.loadTexture("diamond_ore.png");
		this.zombieTexture = this.loadTexture("zombie.png");
		this.drownedTexture = this.loadTexture("drowned.png");
		this.creeperTexture = this.loadTexture("creeper.png");
		this.creeperTriggeredTexture = this.loadAnimationTexture(new String[] { "creeper.png", "creeper_triggered.png" }, 10.f);
		this.stoneTexture = this.loadTexture("stone.png");
		this.cobbleStoneTexture = this.loadTexture("cobblestone.png");

		this.particleExplosionTexture = this.loadAnimationTexture(new String[] { 
			"explosion_1.png", "explosion_2.png", "explosion_3.png", "explosion_4.png", "explosion_5.png", "explosion_6.png" }, 10.f);
		this.particleExplosionTexture.setLoop(false);
	}

	/**
	 * Updates all textures.
	 * Gets called every frame.
	 * 
	 * @param deltaTime The time passed by since the last udpate.
	 */
	private void updateTextures(float deltaTime) {
		this.playerTexture.updateFrame(deltaTime);
		this.houseTexture.updateFrame(deltaTime);
		this.creeperTexture.updateFrame(deltaTime);
		this.creeperTriggeredTexture.updateFrame(deltaTime);
		this.stoneTexture.updateFrame(deltaTime);
		this.cobbleStoneTexture.updateFrame(deltaTime);
	}
	
	/**
	 * loads a texture
	 * 
	 * @param filename
	 * @return the loaded texture if successfull, else null
	 */
	private Texture loadTexture(String filename) {
		return new Texture(this.loadImage(filename));
	}

	/**
	 * loads an animation texture.
	 * 
	 * @param filenames The filenames of the frames.
	 * @param framerate The framerate of the animation.
	 * @return The loaded animation texture.
	 */
	private AnimationTexture loadAnimationTexture(String[] filenames, float framerate) {
		AnimationTexture animationTexture = new AnimationTexture(framerate);
		for (String filename : filenames) {
			animationTexture.addFrame(this.loadImage(filename));
		}
		return animationTexture;
	}

	/**
	 * Loads a BufferedImage.
	 * 
	 * @param filename The filename of the image.
	 * @return The loaded image or null if loading the image failed.
	 */
	private BufferedImage loadImage(String filename) {
		try {
			return ImageIO.read(new File(this.TEXTURE_PATH, filename));
		} catch (IOException e) {
			System.out.println(String.format("Loading Texture '%s' failed.", filename));
			return null;
		}
	}

	/**
	 * Gets the lighting object of a specific field.
	 * 
	 * @param locationX The x-axis coordinate of the field.
	 * @param locationY The y-axis coordinate of the field.
	 * @return The lighting for that specific field.
	 */
	private Lighting getLighting(int locationX, int locationY) {
		return new Lighting(this.playerDistanceLightingMap[locationY][locationX]);
	}

	/**
	 * Gets the lighting object of a specific field.
	 * 
	 * @param location The position of the field.
	 * @return The lighting for that specific field.
	 */
	private Lighting getLighting(Point2d location) {
		return new Lighting(this.playerDistanceLightingMap[location.getY()][location.getX()]);
	}

	/**
	 * Gets the camera dimension.
	 * 
	 * @return The camera dimension
	 */
	public Dimension getCameraDimension() {
		return this.cameraDimension;
	}

	/**
	 * Gets whether this view is enabled or not.
	 * 
	 * @return Whether this view is enabled or not.
	 */
	@Override
	public boolean getIsEnabled() { return this.isEnabled; }

	/**
	 * Sets whether this view is enabled or not.
	 * 
	 * @return Wheter this view is enabled or not.
	 */
	@Override
	public void setIsEnabled(boolean isEnabled) { this.isEnabled = isEnabled; }
	
}
