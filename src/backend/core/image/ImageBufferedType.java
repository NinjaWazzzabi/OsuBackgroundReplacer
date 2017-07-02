package backend.core.image;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageBufferedType implements Image {

    private String imageName;
    private BufferedImage image;

    public ImageBufferedType(String imageName, BufferedImage image) {
        this.imageName = imageName;
        this.image = image;
    }

    @Override
    public boolean replace(String targetFilePath) {
        try {
            ImageIO.write(this.image,"PNG",new File(targetFilePath));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean replace(Image image) {
        if (image.isVirtualImage()){
            return false;
        } else {
            return replace(image.getFullPath());
        }
    }

    @Override
    public boolean copyTo(String targetDirectoryPath) {
        try {
            ImageIO.write(this.image,"PNG",new File(targetDirectoryPath + "/" + this.imageName));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean remove() {
        //Image is only stored in temporary memory so there is no need for removal.
        return false;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getFullPath() {
        return null;
    }

    @Override
    public boolean isVirtualImage() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImageBufferedType)) return false;

        ImageBufferedType that = (ImageBufferedType) o;

        if (!imageName.equals(that.imageName)) return false;
        return image.equals(that.image);
    }

    @Override
    public int hashCode() {
        int result = imageName.hashCode();
        result = 31 * result + image.hashCode();
        return result;
    }
}
