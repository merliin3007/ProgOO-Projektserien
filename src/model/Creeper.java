package model;

import java.util.Random;

import utility.Point2d;

public class Creeper extends Enemy {

    private final int EXPLOSION_TRIGGER_SIZE = 7;
    private final int EXPLOSION_SIZE = 18;
    private final float EXPLOSION_COUNTDOWN = 8.f;

    private boolean triggered = false;
    private float countdown = 0.f;

    public Creeper(int x, int y) {
        super(x, y);
    }

    @Override
    public void update(World world) {
        if (!this.triggered) {
            /* Only follow player if creeper is not triggered. */
            super.update(world);
        }

        if (World.getDistance(world.getPlayerLocation(), this.getLocation()) < EXPLOSION_TRIGGER_SIZE) {
            this.triggered = true;
            this.countdown = EXPLOSION_COUNTDOWN;
        }
    }

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

    private void reset() {
        this.triggered = false;
        this.countdown = 0.f;
    }

    private void explodeAway(World world) {
        /* Ein heftiges Loch in die Map ballern */
        for (int i = 0; i < EXPLOSION_SIZE; ++i) {
            for (int j = 0; j < EXPLOSION_SIZE; ++j) {
                int x = this.getPositionX() + (j - EXPLOSION_SIZE / 2);
                int y = this.getPositionY() + (i - EXPLOSION_SIZE / 2);
                double distance = World.getDistance(this.getLocation(), new Point2d(x, y)); 
                if (distance < EXPLOSION_SIZE / 2) {
                    if (world.isValidField(x, y)) {
                        world.removeObstacleInField(x, y);
                    }
                    if (Point2d.equalPoints(world.getPlayerLocation(), new Point2d(x, y))) {
                        /* Kill Player */
                        world.resetGame();
                    }
                }

            }
        }

        /* Respawn */
        Random rnd = new Random();
        Point2d respawnPosition = world.getEmptyFields().get(rnd.nextInt(world.getEmptyFields().size()));
        this.setPositionX(respawnPosition.getX());
        this.setPositionY(respawnPosition.getY());

        /* Reset */
        this.reset();
    }

    @Override
    public EnemyRenderState getRenderState() {
        return this.triggered ? EnemyRenderState.TRIGGERED_CREEPER : EnemyRenderState.CREEPER;
    }
}
