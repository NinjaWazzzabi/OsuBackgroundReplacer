package backend.core;

import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

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

    void remove() throws IOException {
        Path imageFilePath = Paths.get(this.imagePath);

        Files.delete(imageFilePath);
    }
    void replace(String pathToImage) throws IOException {
        Path source = Paths.get(pathToImage);
        Path target = Paths.get(this.imagePath);

        if (Files.exists(source) && Files.exists(target)){
            Files.copy(source,target, REPLACE_EXISTING);
        } else {
            throw new IOException("File not found");
        }
    }
    void copyTo(String directory) throws IOException {
        if (!imageName.chars().allMatch(c -> c < 128)) {
            throw new IOException("Image could not be saved");
        }

        Path source = Paths.get(this.imagePath);
        Path target = Paths.get(directory + "/" + this.imageName);

        Files.copy(source, target, REPLACE_EXISTING);
    }

    public static boolean isValidImage(String pathToImage){
        return  pathToImage.toLowerCase().contains(".png") ||
                pathToImage.toLowerCase().contains(".jpg") ||
                pathToImage.toLowerCase().contains(".jpeg");
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
