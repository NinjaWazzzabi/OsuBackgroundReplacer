package backend.beatmapcore;

import java.io.File;

public interface Image {

    /**
     * Replaces the target file with this image.
     *
     * @param targetFilePath full path to target file.
     * @return true if replacement is successful.
     */
    boolean replace(String targetFilePath);

    boolean replace(Image image);

    /**
     * Copies this image to the specified directory.
     *
     * @param targetDirectoryPath full path to the target directory.
     * @return true if copy is successful.
     */
    boolean copyTo(String targetDirectoryPath);

    /**
     * Removes this image from permanent storage.
     *
     * @return true if removal is successful.
     */
    boolean remove();

    /**
     * @return the name of the image.
     */
    String getName();

    /**
     * @return the full path to the image, will return a string with length 0 if a virtual image.
     */
    String getFullPath();

    /**
     * Function to check if the image is only in memory and not saved on the disk.
     *
     * @return true if only stored in ram and has no path.
     */
    boolean isVirtualImage();

    /**
     * Checks if a file path is a valid path to a supported image.
     * Supported images are: png, jpg/jpeg
     *
     * @param path full system path to the image.
     * @return true if a valid image path.
     */
    static boolean validImagePath(String path) {
        File file = new File(path);

        if (!file.exists()) {
            return false;
        }

        String extension = "";

        int i = file.getAbsolutePath().lastIndexOf('.');
        if (i > 0) {
            extension = file.getAbsolutePath().substring(i + 1);
        }
        extension = extension.toLowerCase();

        return extension.equals("jpg") || extension.equals("png") || extension.equals("jpeg");
    }
}
