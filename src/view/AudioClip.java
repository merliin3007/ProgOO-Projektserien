package view;

/* Sound */
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class AudioClip {

    private Clip clip;
    
    public AudioClip(Clip clip, float volume) {
        this.clip = clip;
        this.setVolume(volume);
    }

    public void play() {
		if (clip != null) {
            clip.stop();
			clip.setFramePosition(0);
			clip.start();
		}
	}

    public void playLoop() {
        if (clip != null) {
            clip.stop();
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
    
    public float getVolume() {
        if (this.clip == null) {
            return 0.f;
        }

        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);        
        return (float) Math.pow(10f, gainControl.getValue() / 20f);
    }
    
    public void setVolume(float volume) {
        if (this.clip == null) {
            return;
        }

        volume = volume < 0.f ? 0.f : volume > 1.f ? 1.f : volume;
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);        
        gainControl.setValue(20f * (float) Math.log10(volume));
    }

    void setClip(Clip clip) {
        this.clip = clip;
    }
}
