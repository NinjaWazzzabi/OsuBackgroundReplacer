package backend.osubackgroundhandler;

import lombok.Getter;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Generates images of only one colour.
 */
public class SingleColour {

    @Getter private final int r;
    @Getter private final int g;
    @Getter private final int b;

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

}
