package backend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Created by Anthony on 09/02/2017.
 */
public interface OsuBackgroundHandlers {

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
    void setDirectory(String path) throws IOException;
    /**
     *
     * @return Current osu installation directory.
     * @throws IllegalStateException If no directory yet specified.
     */
    String getOsuAbsolutePath() throws ClassNotFoundException;
    /**
     * @return All of the osu song folders names.
     */
    List<String> getSongDirectoryNames();
    /**
     * Autosearches the common osu install directories for an osu installation and returns the path if found.
     * @return absolute path to osu directory.
     * @throws FileNotFoundException Osu installation directory not found.
     */
    String findOsuDirectory() throws FileNotFoundException;

    /**
     * @return true if the object is changing background images.
     */
    boolean isWorking();


    void addWorkListener(WorkListener listener);

    void removeWorkListener(WorkListener listener);
}
