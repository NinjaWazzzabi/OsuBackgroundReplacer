package backend.utility;

import lombok.Getter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * Generates images of only one colour.
 */
public class SingleColour {

    @Getter private final int r;
    @Getter private final int g;
    @Getter private final int b;

    public SingleColour(float h, float s, float v) {
        Color colour  = Color.getHSBColor(h,s,v);
        r = colour.getRed();
        g = colour.getGreen();
        b = colour.getBlue();
    }
    public SingleColour(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public BufferedImage getImage(){
        BufferedImage generatedImage = new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB);

        Graphics2D drawer = generatedImage.createGraphics();

        drawer.setColor(new Color(this.r,this.g,this.b));
        drawer.fill(new Rectangle(0,0,1,1));
        drawer.dispose();

        return generatedImage;
    }

    private float[] getHSV(){
        float[] hsv = new float[3];
        Color.RGBtoHSB(r,g,b,hsv);
        return hsv;
    }

    public float getHue(){
        return getHSV()[0];
    }

    public float getSaturation(){
        return getHSV()[1];
    }

    public float getValue(){
        return getHSV()[2];
    }

}
