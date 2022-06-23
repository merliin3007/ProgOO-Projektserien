package model;

/**
 * U know, u just need an interface to generate the world.
 */
public interface WorldGenerator {
    /**
     * Basically not a void, which creates an obstacle map, a start- and an end-point for the player and map
     */
    void generateWorld();
}