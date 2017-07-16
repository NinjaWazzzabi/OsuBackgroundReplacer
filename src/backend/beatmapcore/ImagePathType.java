package backend.beatmapcore;

import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Holds a full fullPath (filename included) and name of an image.
 */
public class ImagePathType implements Image{

    @Getter private final String name;
    @Getter private final String fullPath;

    ImagePathType(String fullPath) {
        this.name = getImageName(fullPath);
        this.fullPath = fullPath;
    }
    private String getImageName(String pathToImage){
        return new File(pathToImage).getName();
    }

    @Override
    public boolean remove() {
        Path imageFilePath = Paths.get(this.fullPath);

        try {
            Files.delete(imageFilePath);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    @Override
    public boolean replace(String pathToImage) {
        Path source = Paths.get(this.fullPath);
        Path target = Paths.get(pathToImage);

        if (Files.exists(source) && Files.exists(target)){
            try {
                Files.copy(source,target, REPLACE_EXISTING);
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }
    @Override
    public boolean replace(Image image) {
        return replace(image.getFullPath());
    }
    @Override
    public boolean copyTo(String directory) {
        if (!name.chars().allMatch(c -> c < 128)) {
            return false;
        }

        Path source = Paths.get(this.fullPath);
        Path target = Paths.get(directory + "/" + this.name);

        try {
            Files.copy(source, target, REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            return false;
        }
    }


    @Override
    public boolean isVirtualImage() {
        return false;
    }
    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ImagePathType)) {
            return false;
        }

        ImagePathType that = (ImagePathType) o;

        return name.equals(that.name) && fullPath.equals(that.fullPath);
    }
    @Override
    public final int hashCode() {
        int result = name.hashCode();
        result = 31 * result + fullPath.hashCode();
        return result;
    }
}
