package view;

/** Graphics */
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Just an animated texture.
 */
public class AnimationTexture extends Texture {
    /** How long this a frame should be displayed until the next frame is displayed. */
    float frameTime;
    /** How long it takes until the next fram e. */
    float nextFrameIn;
    /** Th ecurren tfram */
    int currentFrame;

    /** If u want to u can loop animations. But u dont have to. */
    boolean loop;
    /** Is it playint at the moment? */
    boolean isPlaying;

    /** Alle the frames of the animation. */
    ArrayList<BufferedImage> frames = new ArrayList<BufferedImage>();

    /**
     * As a wise man once said: Creates a new Instance.
     * 
     * @param framerate The Framerate of the animation.
     * @param loop Whether to loop the animation or not.
     */
    public AnimationTexture(float framerate, boolean loop) {
        super(null);
        this.frameTime = 1.f / framerate;
        this.nextFrameIn = frameTime;
        currentFrame = 0;

        this.loop = loop;
        this.isPlaying = true;
    }

    /**
     * As a wise man once said: Creates a new Instance.
     * 
     * @param framerate The Framerate of the animation.
     */
    public AnimationTexture(float framerate) {
        super(null);
        this.frameTime = 1.f / framerate;
        this.nextFrameIn = frameTime;
        currentFrame = 0;

        this.loop = true;
        this.isPlaying = true;
    }

    /**
     * Update the frame. 
     * Gets called every frame.
     * 
     * @param deltaTime Time might be passed by since the last frame. if not u have a problem.
     */
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

    /**
     * Adds a frame to the animation.
     * 
     * @param frame The frame to add to the animation.
     */
    public void addFrame(BufferedImage frame) {
        if (frame != null) {
            this.frames.add(frame);
        }
    }

    /**
     * Creates and returns a deep copy of the animation object.
     * The copies isPlaying value is always true.
     * 
     * @return The deep-copy of the AnimationTexture.
     */
    public AnimationTexture clone() {
        AnimationTexture cloneAnimationTexture = new AnimationTexture(this.frameTime / 1.f);
        cloneAnimationTexture.frameTime = this.frameTime;
        cloneAnimationTexture.isPlaying = true;
        cloneAnimationTexture.loop = this.loop;
        for (BufferedImage frame : this.frames) {
            cloneAnimationTexture.addFrame(frame);
        }
        return cloneAnimationTexture;
    }

    /**
     * Get whether the AnimationTexture is playing rn.
     * 
     * @return True of false who knows? not ne.
     */
    public boolean getIsPlaying() {
        return this.isPlaying;
    }

    /**
     * Gets whether the animation is looped.
     * 
     * @return Whether the animation is looped.
     */
    public boolean getLoop() {
        return this.loop;
    }

    /**
     * Sets whether the animation is looped.
     * 
     * @param value Whether the animation should be looped.
     */
    public void setLoop(boolean value) {
        this.loop = value;
    }

    /**
     * Gets the current frame as a BufferedImage.
     * 
     * @return The current Frame as a BufferedImage.
     */
    private BufferedImage getCurrentFrame() {
        return this.frames.get(this.currentFrame);
    }
}
