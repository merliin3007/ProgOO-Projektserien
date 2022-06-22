package view;

import java.awt.Color;
import java.awt.Graphics;

import utility.Point2d;

/**
 * This Class represents a RenderObject that is rendered as plain Color.
 */
public class ColorRenderObject extends RenderObject {
    /* The color the object is rendered as. */
    private Color color;

    /**
	 * Creates a new instance.
	 */
    public ColorRenderObject(Point2d position, Lighting lighting, Color color) {
        this.setPosition(position);
        this.setLighting(lighting);
        this.color = color;
    }

    /**
     * Draws the RenderObject to a GraphicView.
     * 
     * @param g the Graphics to draw on
     * @param view the parent-view of the RenderObject
     */
    public void draw(Graphics g, GraphicView view) {
        Point2d pixelPosition = view.worldCoordinatesToPixel(this.getPosition());
		g.setColor(this.getLighting().applyToColor(color));
		g.fillRect(
			pixelPosition.getX(), 
			pixelPosition.getY(), 
			(int)view.getCameraDimension().getWidth(), 
			(int)view.getCameraDimension().getHeight()
		);
    }

    /**
     * Gets the color of the object.
     * 
     * @return the color.
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * Sets the color of the object.
     * 
     * @param color the color.
     */
    public void setColor(Color color) {
        this.color = color;
    }
}
