package view;

import java.util.ArrayList;
import java.util.Random;

/* Sound */
import javax.sound.sampled.Clip;

/**
 * Playes a different sound every time.
 */
public class CueAudioClip extends AudioClip {
    /** The volume to play the sound at. */
    float volume;
    /** Some random attribute. */
    Random rnd = new Random();

    /** The pool of clips to choose from. */
    ArrayList<Clip> clips = new ArrayList<Clip>();

    /**
     * Constructor does constructor.
     * 
     * @param volume
     */
    public CueAudioClip(float volume) {
        super(null, volume);
        this.volume = volume;
    }

    /**
     * Play the next randomly chosen sound. ound mount dev/sdb1
     */
    @Override
    public void play() {
        if (clips.size() == 0) {
            return;
        }

        this.setClip(clips.get(rnd.nextInt(clips.size())));
        super.setVolume(this.volume);
        super.play();
    }

    /**
     * Play a loop. poop.
     */
    @Override
    public void playLoop() {
        // TODO: implement        
    }

    /**
     * Add a clip to the pool.
     * 
     * @param clip The clip to add to the pool.
     */
    public void addClip(Clip clip) {
        this.clips.add(clip);
    }

    /**
     * Sets the volume at which the clips are played at.
     * 
     * @volume The volume at which the clips should be played at.
     */
    @Override
    public void setVolume(float volume) {
        this.volume = volume;
        super.setVolume(volume);
    }

    /**
     * Gets the volume at which the eClipsE are played.
     * 
     * @return The volume at which the clips are played
     */
    @Override
    public float getVolume() {
        return this.volume;
    }
}
