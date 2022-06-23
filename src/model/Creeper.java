package model;

import java.util.Random;

/* Project */
import utility.Point2d;
import view.EnvironmentEvent;

/**
 * Wuuaaa, ein Creeper! Wuauauaa! (Uuuuh, Supercreeper)
 * Ein Creeper, Creeper!
 * Ein Creeper, Creeper!
 * Ein Creeper kam herbei.
 * Du Creeper, Creepermann,
 * Du Creepermann,
 * den ich nicht leiden kann!
 * Zzzzt - Bumm!
 */
public class Creeper extends Enemy {

    /* The distance the creeper can see. Be careful or the creeper will get triggered faster than twitter. */
    private final int EXPLOSION_TRIGGER_SIZE = 12; // 7
    /* The diameter of the explosion. */
    private final int EXPLOSION_SIZE = 14;
    /* The time between the trigger and the explosion. */
    private final float EXPLOSION_COUNTDOWN = 16.f; // 8.f

    /* Sometimes a creeper is mad about u. Only this attribute knows whether that is the case now or not. */
    private boolean triggered = false;
    /* The countdown between trigger an explosion. BOOM */
    private float countdown = 0.f;

    /**
	 * As a wise man once said: Creates a new instance.
     * 
     * @param x The x-axis coordinate.
     * @param y The y-axis coordinate.
	 */
    public Creeper(int x, int y) {
        super(x, y);
    }

    /**
     * Microsoft missed this in offce.
     * 
     * @param world Then it feels that always
     *              Love's enough for us growing
     *              Make a better world
     *              So make a better world
     */
    @Override
    public void update(World world) {
        if (!this.triggered) {
            /* Only follow player if creeper is not triggered. */
            super.update(world);
        }

        if (!this.triggered && World.getDistance(world.getPlayerLocation(), this.getLocation()) < EXPLOSION_TRIGGER_SIZE / 2) {
            this.triggered = true;
            this.countdown = EXPLOSION_COUNTDOWN;
            world.triggerEnvironmentEvent(EnvironmentEvent.CREEPER_TRIGGERED);
        }
    }

    /**
     * Updates the creeper every frame.
     * 
     * @param world I see trees of green, red roses too
     *              I see them bloom, for me and you
     *              And I think to myself
     *              What a wonderful world
     * @param deltaTime airlines.
     */
    @Override
    public void updateFrame(World world, float deltaTime) {
        super.updateFrame(world, deltaTime);

        if (this.triggered) {
            if (this.countdown <= 0.f) {
                this.explodeAway(world);
            } else {
                this.countdown -= deltaTime / 100.f;
            }
        }
    }

    /**
     * Resets the creeper.
     */
    private void reset() {
        this.triggered = false;
        this.countdown = 0.f;
    }

    /**
     * Boom boom boom boom
     * I want you in my room
     * we'll spend the night together
     * From now until forever
     * Boom boom boom boom
     * I wanna go boom boom
     * And spend the night together
     * Together in my room
     * 
     * @param world from the new world.
     */
    private void explodeAway(World world) {
        Random rnd = new Random();
        /* Ein heftiges Loch in die Map ballern */
        for (int i = 0; i < EXPLOSION_SIZE; ++i) {
            for (int j = 0; j < EXPLOSION_SIZE; ++j) {
                int x = this.getPositionX() + (j - EXPLOSION_SIZE / 2);
                int y = this.getPositionY() + (i - EXPLOSION_SIZE / 2);
                double distance = World.getDistance(this.getLocation(), new Point2d(x, y)); 
                if (distance < EXPLOSION_SIZE / 2) {
                    if (world.isValidField(x, y)) {
                        if (rnd.nextInt(100) > 30) {
                            /* Dont remove all blocks in radius to make the result look more natural. */
                            world.removeObstacleInField(x, y);
                        }
                    }
                    if (Point2d.equalPoints(world.getPlayerLocation(), new Point2d(x, y))) {
                        /* Kill Player */
                        world.resetGame();
                    }
                }
            }
        }

        /* Tell everbody that something has changed. */
        world.levelChanged();

        /* Make the enemies realize they can now move through walls */
        world.getEnemyPathTable().removeCollisionObjects(world.getObstacleMap());

        /* Spawn the explosion animation */
        world.spawnExplosion(this.getLocation().copy(), this.EXPLOSION_SIZE);

        // Kill Enemy
        this.setIsAlive(false);

        /* Reset */
        this.reset();
    }

    /**
     * Depends on whether u assumed the creepers gender or not. Probably u did. Keep social distancing.
     */
    @Override
    public EnemyRenderState getRenderState() {
        return this.triggered ? EnemyRenderState.TRIGGERED_CREEPER : EnemyRenderState.CREEPER;
    }
}
