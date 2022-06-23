package view;

/* Rendering */
import java.awt.image.BufferedImage;

public class Texture {
    /* The texture-pixel-dala itself and in person. */
    BufferedImage image;
    
    /**
     * Cosntrucsadfkjlasdfasöldkfjaölsdkfjaslödfkjasödlf
     * 
     * @param image The texture itself.
     */
    public Texture(BufferedImage image) {
        this.image = image;
    }

    /**
     * Update each frame.
     * 
     * @param deltaTime The time passed by since the last call to this method.
     */
    public void updateFrame(float deltaTime) {
        /* It took me quite a long time to code the body for this method. */
        return;
    }

    /**
     * Gets the texutre itself
     * 
     * @return The texture itself.
     */
    public BufferedImage getImage() {
        return this.image;
    }

    /**
     * Sets the texture image.
     * 
     * @param image The texture image.
     */
    public void setImage(BufferedImage image) {
        this.image = image;
    }
}   
