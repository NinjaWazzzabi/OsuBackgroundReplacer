package backend.osubackgroundhandler;

import lombok.Getter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Handles all background images in a beatmap folder.
 */
class Beatmap {
    @Getter private final List<Image> images;
    @Getter private final String folderPath;

    /**
     * @param path The path to the directory this object will bind itself to.
     * @throws IOException Throws the exception if the directory is not found, or if there is no osu file present.
     */
    Beatmap(String path) throws IOException {
        //Assigns the directory that this class will work with.
        this.folderPath = path;

        // Gets all of the .osu files in the directory.
        ArrayList<String> osuFiles = findOsuFiles();

        // If there is no .osu files present in the directory then there's no use
        // for this class to be working in that folder.
        if (osuFiles.size() == 0) throw new IOException("No .osu file in directory: " + folderPath);

        // Adds all of the image names to a list.
        images = findAllImageNames(osuFiles);
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
    //TODO Split methods findAllImageNames up into smaller methods.
    /**
     * @param osuFileNames Uses .osu files to find the name of an image.
     * @return Returns an ArrayList with the names of the images in the song folder.
     * @throws IOException No osu background images found.
     */
    private ArrayList<Image> findAllImageNames(ArrayList<String> osuFileNames) throws IOException {
        //List that will hold all of the found image names.
        ArrayList<Image> foundImages = new ArrayList<>(0);

        //Goes through every osu file and searches after an image.
        for (String osuFileName : osuFileNames) {
            InputStream osuFile = new FileInputStream(folderPath + "/" + osuFileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(osuFile));

            //Goes to the events section of the osu file
            String line = reader.readLine();
            while (line != null && !line.equals("[Events]")) {
                line = reader.readLine();
            }

            //Searches for a image with the use of file endings, and if found adds it to the list.
            while (line != null && !line.contains("[TimingPoints]")) {

                // Checks if this line contains an image.
                if (    line.toLowerCase().contains(".png") ||
                        line.toLowerCase().contains(".jpg") ||
                        line.toLowerCase().contains(".jpeg")
                    ) {

                    // The line that contains the image name has it surrounded by quotes, so we split the string to
                    // get the image name and ignore the coordinates. The image name will always be in index one.
                    String[] splittedSentence = line.split("\"");
                    String imageName = splittedSentence[1];

                    // Will only add the image name to the imageName list if there's no duplicate of it there.
                    if (!new File(folderPath+"/"+imageName).exists()) {
                        System.out.println("Image file \"" + imageName +"\" doesn't exist in the directory \"" + folderPath +"\"");
                    }

                    Image foundImage = new Image(imageName,folderPath);
                    boolean isDuplicate = false;
                    for (Image osuBackgrounds : foundImages){
                        if (isSameOsuBackground(osuBackgrounds, foundImage)){
                            isDuplicate = true;
                            break;
                        }
                    }

                    if (isDuplicate){
//                            System.out.println("Duplicate found and discarded");
                    } else {
                        foundImages.add(foundImage);
                    }


                    // This method will only look for the first background file,
                    // after it is found there's no need to look more.
                    break;
                }
                //If a picture is not found on this line, we read in the next one and repeat the while loop.
                line = reader.readLine();
            }
            reader.close();
        }

        // Returns the list with the image names.
        return foundImages;
    }
    /**
     * Compares two osu backgrounds and determines if they're the same.
     * @param first Image to compare.
     * @param second Image to compare.
     * @return true if first equals second.
     */
    private boolean isSameOsuBackground(Image first, Image second){
        if (first.hashCode() == second.hashCode()){
            if (first.equals(second)){
                return true;
            }
        }
        return false;
    }


    /**
     * @param filePath Path to the folder containing the background image.
     * @param fileName Name of the background image that will replace the ones in this song directory.
     */
    void replaceBackgrounds(String fileName, String filePath) {
        //By now the files should have been verified to exist so no need to add exception to method.
        //Only problem is if the user would manually remove or change files, then there's problems.
        //Todo fix this problem ^

        // Goes through every background image in the directory and replaces the image with the one in the arguments.
        for (Image bg : images) {
            Path source = Paths.get(filePath + "/" + fileName);
            Path target = Paths.get(bg.getImagePath(), bg.getImageName());
            try {
                Files.copy(source, target, REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Copies all of the images in this osu song folder to the folder specified in the argument.
     *
     * @param saveDirectory The directory that the images from the song directory will be copied to.
     * @throws IOException If directory is not found.
     */
    void copyBackgrounds(String saveDirectory) throws IOException {
        for (Image bg: images) {
            if (bg.getImageName().chars().allMatch(c -> c < 128)) {
                Path source = Paths.get(bg.getImagePath() + "/" + bg.getImageName());
                Path target = Paths.get(saveDirectory + "/" + getBeatmapName() + "/" + bg.getImageName());

                //If save directory doesn't exist, create it.
                if (!new File(saveDirectory + "/" + getBeatmapName()).exists()){
                    boolean successful = new File(saveDirectory + "/" + getBeatmapName()).mkdir();
                    if (!successful){
                        throw new IOException("Couldn't create folder: " + saveDirectory + "/" + getBeatmapName());
                    }
                }

                Files.copy(source, target, REPLACE_EXISTING);
            } else {
                //TODO Can't save the image if it contains characters outside of the ANSI Range.
                //Image names currently only supports ANSI standard.
                //If you use characters not in ANSI then you won't be able to copy it using the copy to method below.
                System.out.println("Couldn't save image: " + bg.getImageName());
            }
        }
    }
    /**
     * Removes all the background images in the folder.
     */
    void removeAllBackgrounds() {
        for (Image bg : images) {
            Path target = Paths.get(bg.getImagePath() + "/" + bg.getImageName());

            //By now the files should have been verified to exist so no need to add exception to method.
            //Only problem is if the user would manually remove or change files, then there's problems.
            //Todo fix this problem ^
            try {
                Files.delete(target);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * @return the name of the folder.
     */
    String getBeatmapName() {
        File path = new File(folderPath);
        return path.getName();
    }
}