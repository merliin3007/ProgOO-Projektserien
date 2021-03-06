package view;

/* Rendering */
import java.awt.Color;
import java.awt.Graphics;

import utility.Point2d;

/**
 * This Class represents a RenderObject that is rendered as a Texture
 */
public class TextureRenderObject extends RenderObject {

    /* The texture to render this object as */
    Texture texture;
    float scaleFactor;

    /**
	 * Creates a new instance.
	 */
    public TextureRenderObject(Point2d position, Lighting lighting, Texture texture) {
        this.setPosition(position);
        this.setLighting(lighting);
        this.texture = texture;
        this.scaleFactor = 1.f;
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
        if (this.texture.getImage() == null) {
            /* Draw red square if texture is missing. */
            g.setColor(new Color(255, 0, 0));
            g.fillRect(pixelPosition.getX(), pixelPosition.getY(), 
            (int)view.getCameraDimension().getWidth(), (int)view.getCameraDimension().getHeight());
        } else {
            /* Draw the actual texture. */
            if (this.scaleFactor == 1.f) {
		        g.drawImage(texture.getImage(), 
                    pixelPosition.getX(), 
                    pixelPosition.getY(), 
                    (int)(view.getCameraDimension().getWidth() * this.scaleFactor), 
                    (int)(view.getCameraDimension().getHeight() * this.scaleFactor), null);
            } else {
                g.drawImage(texture.getImage(), 
                    (int)(pixelPosition.getX() - (view.getCameraDimension().getWidth() * this.scaleFactor) / 2.f), 
                    (int)(pixelPosition.getY() - (view.getCameraDimension().getHeight() * this.scaleFactor) / 2.f), 
                    (int)(view.getCameraDimension().getWidth() * this.scaleFactor), 
                    (int)(view.getCameraDimension().getHeight() * this.scaleFactor), null);
            }
        }
        /* Draw the shadow above the texture */
		g.setColor(new Color(0, 0, 0, (int)(255 * (1.f - (this.getLighting().getVal())))));
		g.fillRect(pixelPosition.getX(), pixelPosition.getY(), 
            (int)view.getCameraDimension().getWidth(), (int)view.getCameraDimension().getHeight());
    }

    /**
     * Sets the scale of this RenderObject.
     * 
     * @param scaleFactor The new scale-factor.
     */
    public void setScaleFactor(float scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    /**
     * Gets the texture.
     * 
     * @return the texture.
     */
    public Texture getTexture() {
        return this.texture;
    }

    /**
     * Sets the texture
     * 
     * @param texture the texture.
     */
    public void setTexture(Texture texture) {
        this.texture = texture;
    }
}
