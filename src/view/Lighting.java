package view;

import java.awt.Color;

public class Lighting {
    /** The brightness */
    private float light = 0.0f;
    
    /** Color grading dark tint. */
    private Color dark = new Color(24, 32, 44);
    /** Color grading mid tint. */
    private Color mid = new Color(128, 100, 80);
    /** Color grading hi tint. */
    private Color hi = new Color(190, 200, 210);

    /** Dark grading strength. */
    private float darkStrenght = 1.f;
    /** Mid grading strength. */
    private float midStrength = 0.4f;
    /** Hi grading strength. */
    private float hiStrength = 0.1f;

    /**
     * Con Can Constr Construct a Construcotr Constructor.
     * 
     * @param val The lit-a-f-value
     */
    public Lighting(float val) {
        this.light = val < 0.0f ? 0.0f : val > 1.0f ? 1.0f : val;
    }

    /**
     * Applay lighting to a color.
     * 
     * @param r The Red-Channel-Value of the color.
     * @param g The Green-Channel-Value of the color.
     * @param b The Blue-Channel-Value of the color.
     * @return
     */
    public Color applyToRgb(int r, int g, int b) {
        return this.applyGrading(new Color((int) (r * this.light), (int) (g * this.light), (int) (b * this.light)));
    }

    /**
     * 
     * @param color
     * @return
     */
    public Color applyToColor(Color color) {
        return this.applyToRgb(color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * Multiplay the brighness value with some other value. Clip at 0.f an 1.f
     * 
     * @param val The other value.
     */
    public void multVal(float val) {
        this.light *= val < 0.f ? 0.f : val >= 1.f ? 1.f : val;
    }

    /**
     * Apply the grade.
     * 
     * @param color Some Color.
     * @return The graded Color.
     */
    private Color applyGrading(Color color) {
        float darkVal = (float)Math.pow(1.f - light, 4.f);
        float midVal = (float)Math.pow(1.0f - Math.abs(0.5f - light), 4.f) * 0.2f;
        float hiVal = (float)Math.pow(light, 2.f);
        float div = 1.f + darkStrenght + midStrength + hiStrength;
        return new Color(
            (int)((color.getRed() + dark.getRed() * darkVal + mid.getRed() * midVal + hi.getRed() * hiVal) / div),
            (int)((color.getGreen() + dark.getGreen() * darkVal + mid.getGreen() * midVal + hi.getGreen() * hiVal) / div),
            (int)((color.getBlue() + dark.getBlue() * darkVal + mid.getBlue() * midVal + hi.getBlue() * hiVal) / div)
        );
    }

    /**
     * Gets the brighness.
     * 
     * @return The brighness.
     */
    public float getVal() {
        return this.light;
    }

    /**
     * Sets the brighness.
     * @param val The brightness.
     */
    public void setVal(float val) {
        this.light = val;
    }
}