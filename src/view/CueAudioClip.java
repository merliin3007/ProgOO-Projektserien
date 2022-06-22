package view;

import java.util.ArrayList;
import java.util.Random;

/* Sound */
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class CueAudioClip extends AudioClip {
    float volume;
    Random rnd = new Random();

    ArrayList<Clip> clips = new ArrayList<Clip>();

    public CueAudioClip(float volume) {
        super(null, volume);
        this.volume = volume;
    }

    @Override
    public void play() {
        if (clips.size() == 0) {
            return;
        }

        this.setClip(clips.get(rnd.nextInt(clips.size())));
        super.setVolume(this.volume);
        super.play();
    }

    @Override
    public void playLoop() {
        // TODO: implement        
    }

    public void addClip(Clip clip) {
        this.clips.add(clip);
    }

    @Override
    public void setVolume(float volume) {
        this.volume = volume;
        super.setVolume(volume);
    }

    @Override
    public float getVolume() {
        return this.volume;
    }
}
