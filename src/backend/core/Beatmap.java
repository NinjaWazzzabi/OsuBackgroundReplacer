package backend.core;

import backend.core.image.Image;
import backend.core.image.ImageHelper;
import lombok.Getter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Handles all background images in a beatmap folder.
 */
public class Beatmap {
    @Getter private final List<Image> images;
    @Getter private final String folderPath;
    @Getter private final String folderName;

    /**
     * @param path The path to the directory this object will bind itself to.
     * @throws IOException Throws the exception if the directory is not found, or if there is no osu file present.
     */
    Beatmap(String path) {
        //Assigns the directory that this class will work with.
        this.folderPath = path;
        this.folderName = new File(path).getName();

        ArrayList<String> osuFiles = findOsuFiles();

        images = new ArrayList<>();
        for (String osuFile : osuFiles) {
            Image foundImage = (findImage(osuFile));
            if (foundImage != null && !isDuplicate(images,foundImage)) {
                images.add(foundImage);
            }
        }
    }

    /**
     * @return Returns all of the found .osu files in the directory
     */
    private ArrayList<String> findOsuFiles() {
        //Creates an ArrayList to hold all of the found .osu files,
        //and gets a list of all of the files in the song folder.
        ArrayList<String> tempOsuNames = new ArrayList<>(0);
        File[] listOfFiles = new File(folderPath).listFiles();

        //Adds every file that has the file ending ".osu" to the ArrayList
        if (listOfFiles != null){
            for (File file : listOfFiles) {
                String fileName = file.getName();
                if (fileName.contains(".osu")) {
                    tempOsuNames.add(fileName);
                }
            }
        }

        // Returns the list with all of the .osu files found.
        return tempOsuNames;
    }
    /**
     * @param osuFileName Uses .osu file to find the name of an image.
     * @return Returns an ArrayList with the names of the images in the song folder.
     * @throws IOException No osu background images found.
     */
    private Image findImage(String osuFileName) {
        String imageName = null;
        try {
            imageName = getImageNameFromBeatmap(folderPath + "/" + osuFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (imageName != null) {
            return new ImageHelper(folderPath + "/" + imageName);
        } else {
            return null;
        }
    }
    /**
     *
     * @param pathToOsuBeatmapFile path to a .osu beatmap
     * @return the name of the main image used in an osu beatmap difficulty.
     * @throws IOException if file is not found.
     */
    private String getImageNameFromBeatmap(String pathToOsuBeatmapFile) throws IOException {
        InputStream osuFile = new FileInputStream(pathToOsuBeatmapFile);
        BufferedReader reader = new BufferedReader(new InputStreamReader(osuFile));

        //Goes to the events section of the osu file
        String line = reader.readLine();
        while (line != null && !line.equals("[Events]")) {
            line = reader.readLine();
        }

        while (line != null && !line.contains("[TimingPoints]")) {
            if (stringContainsImageFileEnding(line)) {
                // The line that contains the image name has it surrounded by quotes, so we split the string to
                // get the image name and ignore the coordinates. The image name will always be in index one.
                String[] splittedSentence = line.split("\"");
                String imageName = splittedSentence[1];
                return imageName;
            }
            line = reader.readLine();
        }

        reader.close();
        return null;
    }
    private <T> boolean isDuplicate(List<T> list, T object) {
        boolean isDuplicate = false;
        for (T objectFromList : list){
            if (objectFromList.hashCode() == object.hashCode()){
                isDuplicate = true;
                break;
            }
        }

        return isDuplicate;
    }
    private boolean stringContainsImageFileEnding(String s){
        String string = s.toLowerCase(Locale.ENGLISH);
        return string.contains("jpg") || string.contains("png") || string.contains("jpeg");
    }

    /**
     * @param replacingImage Full path to the background image that will replace the ones in this song directory.
     * @return List of images that we're not replaced.
     */
    public List<Image> replaceBackgrounds(Image replacingImage) {
        ArrayList<Image> nonReplacedImages = new ArrayList<>();

        for (Image image : this.images) {
            boolean success = replacingImage.replace(image);

            if (!success) {
                nonReplacedImages.add(image);
            }
        }

        return nonReplacedImages;
    }
    /**
     * Copies all of the images in this osu song folder to the folder specified in the argument.
     *
     * @param saveDirectory The directory that the images from the song directory will be copied to.
     * @return Returns a list of the images that couldn't be copied.
     */
    public List<Image> copyBackgrounds(String saveDirectory) {
        List<Image> nonCopiedImages = new ArrayList<>();
        String copyDirectory = saveDirectory + "/" + folderName;

        if (!new File(saveDirectory).exists()) {
            return images;
        }

        if (images.size() > 0 && !new File(copyDirectory).exists()) {
            boolean mkdir = new File(copyDirectory).mkdir();
        }

        for (Image image: images) {
            boolean success = image.copyTo(copyDirectory);

            if (!success) {
                nonCopiedImages.add(image);
            }
        }

        return nonCopiedImages;
    }
    /**
     * Removes all the background images in the folder.
     * @return List of images that failed to remove.
     */
    public List<Image> removeAllBackgrounds() {
        ArrayList<Image> nonRemovedImages = new ArrayList<>();

        for (Image image : this.images) {
            boolean success = image.remove();

            if (!success) {
                nonRemovedImages.add(image);
            }
        }
        return nonRemovedImages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Beatmap)) return false;

        Beatmap beatmap = (Beatmap) o;

        if (!images.equals(beatmap.images)) return false;
        return folderName.equals(beatmap.folderName);
    }

    @Override
    public int hashCode() {
        int result = images.hashCode();
        result = 31 * result + folderName.hashCode();
        return result;
    }
}