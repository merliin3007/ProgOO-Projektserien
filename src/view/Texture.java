package view;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Graphics;

import utility.Point2d;

public class Texture {
    BufferedImage image;
    
    public Texture(BufferedImage image) {
        this.image = image;
    }

    public void updateFrame(float deltaTime) {
        return;
    }

    public BufferedImage getImage() {
        return this.image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
}   
