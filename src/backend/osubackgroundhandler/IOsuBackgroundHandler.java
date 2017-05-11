package backend.osubackgroundhandler;

import com.sun.istack.internal.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Interface for osu background handlers
 */
public interface IOsuBackgroundHandler {

    /**
     * Used to replace all of the osu backgrounds with the image from the method argument.
     * @param imageName Name of image that will replace all background images.
     * @param imageDirectory Folder containing the image.
     */
    void replaceAll(String imageName, String imageDirectory) throws IOException;
    /**
     * Saves all of the osu background images to the directory specified.
     * @param directory of the images that will be saved to.
     */
    void saveAll(String directory) throws IOException;
    /**
     * Removes all the osu background images.
     */
    void removeAll();


    /**
     * Assigns where the Osu! directory is.
     * @param path The path to the directory.
     * @throws IOException if directory doesn't contain osu installation.
     */
    void setOsuDirectory(String path) throws IOException;
    /**
     * Assigns where the Osu! file is.
     * @param path The path to the file.
     * @throws IOException if file isn't an osu.exe file.
     */
    void setOsuFile(String path) throws IOException;


    /**
     *
     * @return Current osu installation directory.
     * @throws IllegalStateException If no directory yet specified.
     */
    @Nullable
    String getOsuAbsolutePath();
    /**
     * @return All of the osu song folders names.
     */
    List<String> getSongDirectoryNames();
    /**
     * Goes through the three default osu installation locations and finds the one with the osu installation.
     *
     * @return directory path with osu installation.
     * @throws FileNotFoundException if no osu installation is found.
     */
    String findOsuDirectory() throws FileNotFoundException;


    /**
     * @return true if the object is changing background images.
     */
    boolean isWorking();
    void addWorkListener(WorkListener listener);
    void removeWorkListener(WorkListener listener);
}