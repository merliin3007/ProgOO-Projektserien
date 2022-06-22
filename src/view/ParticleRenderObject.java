package view;

import utility.Point2d;
import view.Lighting;

public class ParticleRenderObject extends TextureRenderObject {
    
    public ParticleRenderObject(Point2d position, Lighting lighting, AnimationTexture texture, float scaleFactor) {
        super(position, lighting, texture.clone());
        this.setScaleFactor(scaleFactor);
    }

    public void updateFrame(float deltaTime) {
        this.getTexture().updateFrame(deltaTime);
    }

    public boolean getIsPlaying() {
        return ((AnimationTexture)this.getTexture()).getIsPlaying();
    }
}
