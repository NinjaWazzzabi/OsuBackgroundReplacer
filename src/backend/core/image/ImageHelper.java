package backend.core.image;

import java.awt.image.BufferedImage;

public class ImageHelper implements Image{

    private Image image;

    public ImageHelper(BufferedImage image, String imageName){
        this.image = new ImageBufferedType(imageName,image);
    }
    public ImageHelper(String fullPathToFile) {
        this.image = new ImagePathType(fullPathToFile);
    }

    @Override
    public boolean replace(String targetFilePath) {
        return this.image.replace(targetFilePath);
    }

    @Override
    public boolean replace(Image image) {
        return image.replace(this.image);
    }

    @Override
    public boolean copyTo(String targetDirectoryPath) {
        return this.image.copyTo(targetDirectoryPath);
    }

    @Override
    public boolean remove() {
        return this.image.remove();
    }

    @Override
    public String getName() {
        return image.getName();
    }

    @Override
    public String getFullPath() {
        return image.getFullPath();
    }

    @Override
    public boolean isVirtualImage() {
        return image.isVirtualImage();
    }
}
