package view;

import utility.Point2d;

/**
 * Just in case u want to render particles.
 */
public class ParticleRenderObject extends TextureRenderObject {
    
    /**
     * Constructionable and Constructable Constructios to construct an instace of this class
     * 
     * @param position The position of the particles.
     * @param lighting The lighting of the particles.
     * @param texture The texture of the particles.
     * @param scaleFactor The Sclae of the particles.
     */
    public ParticleRenderObject(Point2d position, Lighting lighting, AnimationTexture texture, float scaleFactor) {
        super(position, lighting, texture.clone());
        this.setScaleFactor(scaleFactor);
    }

    /**
     * Update every frame.
     * 
     * @param deltaTime The time passed by since the last call to this method.
     */
    public void updateFrame(float deltaTime) {
        this.getTexture().updateFrame(deltaTime);
    }

    /**
     * Returns whether this shit is playing rn.
     * 
     * @return Whether this shit is playing or not.
     */
    public boolean getIsPlaying() {
        return ((AnimationTexture)this.getTexture()).getIsPlaying();
    }
}
