package backend.osubackgroundhandler;

import lombok.Getter;

/**
 * Holds a full path (filename included) and name of an image.
 */
public class Image {

    @Getter private final String imageName;
    @Getter private final String imagePath;

    Image(String imageName, String imagePath) {
        this.imageName = imageName;
        this.imagePath = imagePath;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Image)) {
            return false;
        }

        Image that = (Image) o;

        return imageName.equals(that.imageName) && imagePath.equals(that.imagePath);
    }
    @Override
    public final int hashCode() {
        int result = imageName.hashCode();
        result = 31 * result + imagePath.hashCode();
        return result;
    }
}
