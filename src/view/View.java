package view;

import model.World;

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

}
