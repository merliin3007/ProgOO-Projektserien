package view;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class AnimationTexture extends Texture {
    float frameTime;
    float nextFrameIn;
    int currentFrame;

    boolean loop;
    boolean isPlaying;

    ArrayList<BufferedImage> frames = new ArrayList<BufferedImage>();

    public AnimationTexture(float framerate, boolean loop) {
        super(null);
        this.frameTime = 1.f / framerate;
        this.nextFrameIn = frameTime;
        currentFrame = 0;

        this.loop = loop;
        this.isPlaying = true;
    }

    public AnimationTexture(float framerate) {
        super(null);
        this.frameTime = 1.f / framerate;
        this.nextFrameIn = frameTime;
        currentFrame = 0;

        this.loop = true;
        this.isPlaying = true;
    }

    @Override
    public void updateFrame(float deltaTime) {
        if (frames.size() <= 1){
            return;
        }
        if (!isPlaying) {
            return;
        }

        deltaTime /= 1000.f;

        nextFrameIn += deltaTime;
        if (nextFrameIn >= frameTime) {
            nextFrameIn = 0.f;
            if (currentFrame == this.frames.size() - 1 && this.loop == false) {
                this.isPlaying = false;
            }
            currentFrame = (currentFrame + 1) % this.frames.size();
            this.setImage(this.getCurrentFrame());
        }
    }

    public void addFrame(BufferedImage frame) {
        if (frame != null) {
            this.frames.add(frame);
        }
    }

    public AnimationTexture clone() {
        AnimationTexture cloneAnimationTexture = new AnimationTexture(this.frameTime / 1.f);
        for (BufferedImage frame : this.frames) {
            cloneAnimationTexture.addFrame(frame);
        }
        return cloneAnimationTexture;
    }

    public boolean getIsPlaying() {
        return this.isPlaying;
    }

    private BufferedImage getCurrentFrame() {
        return this.frames.get(this.currentFrame);
    }
}
