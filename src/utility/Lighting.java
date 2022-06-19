package utility;

import java.awt.Color;

public class Lighting {
    private float val = 0.0f;
    
    public Lighting(float val) {
        this.val = val < 0.0f ? 0.0f : val > 1.0f ? 1.0f : val;
    }

    public Color applyToRgb(int r, int g, int b) {
        return new Color((int)(r * this.val), (int)(g * this.val), (int)(b * this.val));
    }

    public float getVal() { return this.val; }

    public void setVal(float val) { this.val = val; }
}