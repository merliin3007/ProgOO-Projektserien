package view;

/* Sound */
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

/**
 * An AudioClip can play audio. incredible!
 */
public class AudioClip {

    /** The actual clip to play */
    private Clip clip;
    
    /**
     * As a wise man once said: Constructor do constructor things.
     * 
     * @param clip The clip to play.
     * @param volume The volume at which audio should be played.
     */
    public AudioClip(Clip clip, float volume) {
        this.clip = clip;
        this.setVolume(volume);
    }

    /**
     * Play the clip.
     * Sets the clip to the beginning if its already playing or paused.
     */
    public void play() {
		if (clip != null) {
            clip.stop();
			clip.setFramePosition(0);
			clip.start();
		} else {
            /**
             * I wake up to the sounds of the silence that allows
             * For my mind to run around with my ear up to the ground
             * I'm searching to behold the stories that are told
             * When my back is to the world that was smiling when I turned
             * Tell you you're the greatest
             * But once you turn, they hate us
             * Oh, the misery
             * Everybody wants to be my enemy
             * Spare the sympathy
             * Everybody wants to be my enemy
             * (Look out for yourself)
             * My enemy (look, look, look, look)
             * (Look out for yourself)
             * But I'm ready
             */
        }
	}

    /** 
     * Plays the clip in a loop.
     */
    public void playLoop() {
        if (clip != null) {
            clip.stop();
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
    
    /**
     * Gets the current volume of the clip.
     * 
     * @return volume.
     */
    public float getVolume() {
        if (this.clip == null) {
            return 0.f;
        }

        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);        
        return (float) Math.pow(10f, gainControl.getValue() / 20f);
    }
    
    /**
     * Sets the volume of the clip.
     * 
     * @param volume The desired volume. 0.f - 1.f
     */
    public void setVolume(float volume) {
        if (this.clip == null) {
            return;
        }

        volume = volume < 0.f ? 0.f : volume > 1.f ? 1.f : volume;
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);        
        gainControl.setValue(20f * (float) Math.log10(volume));
    }

    /**
     * Sets the clip.
     * 
     * @param clip The clip to set.
     */
    void setClip(Clip clip) {
        this.clip = clip;
    }
}
