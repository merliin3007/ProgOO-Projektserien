package view;

import model.World;
import utility.Point2d;

/**
 * Classes that want to display the {@link World} must implement this interface
 * to be notified when the world updates.
 */
public interface View {

	/**
	 * Called whenever the world updates.
	 * 
	 * @param world the {@link World} object which called this method.
	 */
	void update(World world);

	/**
	 * Updates the views camera.
	 * Gets called every frame.
	 * 
	 * @param world The current world.
	 * @param deltaTime The time that passed by since the last call to any implementation of this method.
	 */
	void updateCamera(World world, float deltaTime);

	/**
	 * Gets called whenever the level has changed.
	 * 
	 * @param world But if this ever changin' world, in which we're livin', makes you give in and cry: Say live and let die.
	 */
	void onLevelChanged(World world);

	/**
	 * Explosion goes boooooom!
	 * 
	 * @param position The position to spawn some fancy explosion.
	 * @param size The diameter of the explosion.
	 */
	void spawnExplosion(Point2d position, float size);

	/**
	 * This gets called by the world to trigger some events
	 * that the view has to decide whether todo something
	 * corresponding or not. Alle events should have purely
	 * cosmetical effects.
	 * 
	 * @param event The Evenet that got triggered hard af.
	 */
	void triggerEnvironmentEvent(EnvironmentEvent event);

	/**
	* Gets whether this view is enabled or not.
	* 
	* @return Whether this view is enabled or not.
	*/
	boolean getIsEnabled();

	/**
	 * Sets whether this view is enabled or not.
	 * 
	 * @return Wheter this view is enabled or not.
	 */
	void setIsEnabled(boolean isEnabled);
}
