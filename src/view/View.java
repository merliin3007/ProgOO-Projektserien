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
	 * @param world The world of which the level changed.
	 */
	void onLevelChanged(World world);

	void spawnExplosion(Point2d position, float size);

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
