package view;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Graphics;

import utility.Point2d;

/**
 * This Class represents a RenderObject that is rendered as a Texture
 */
public class TextureRenderObject extends RenderObject {

    /* The texture to render this object as */
    BufferedImage texture;

    /**
	 * Creates a new instance.
	 */
    public TextureRenderObject(Point2d position, Lighting lighting, BufferedImage texture) {
        this.setPosition(position);
        this.setLighting(lighting);
        this.texture = texture;
    }

    /**
     * Draws the RenderObject to a GraphicView.
     * 
     * @param g the Graphics to draw on
     * @param view the parent-view of the RenderObject
     */
    public void draw(Graphics g, GraphicView view) {
        Point2d pixelPosition = view.worldCoordinatesToPixel(this.getPosition());
        /* Draw the texture */
        if (this.texture == null) {
            /* Draw red square if texture is missing. */
            g.setColor(new Color(255, 0, 0));
            g.fillRect(pixelPosition.getX(), pixelPosition.getY(), 
            (int)view.getCameraDimension().getWidth(), (int)view.getCameraDimension().getHeight());
        } else {
            /* Draw the actual texture. */
		    g.drawImage(texture, pixelPosition.getX(), pixelPosition.getY(), 
                (int)view.getCameraDimension().getWidth(), (int)view.getCameraDimension().getHeight(), null);
        }
        /* Draw the shadow above the texture */
		g.setColor(new Color(0, 0, 0, (int)(255 * (1.f - (this.getLighting().getVal())))));
		g.fillRect(pixelPosition.getX(), pixelPosition.getY(), 
            (int)view.getCameraDimension().getWidth(), (int)view.getCameraDimension().getHeight());
    }

    /**
     * Gets the texture.
     * 
     * @return the texture.
     */
    public BufferedImage getTexture() {
        return this.texture;
    }

    /**
     * Sets the texture
     * 
     * @param texture the texture.
     */
    public void setTexture(BufferedImage texture) {
        this.texture = texture;
    }
}
