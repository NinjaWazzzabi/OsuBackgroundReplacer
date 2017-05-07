package backend;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * A class that can handle all background images in a osu song folder.
 */
class OsuSongFolder {

    // The list that holds all of the background images in the directory.
    private final ArrayList<OsuBackground> osuBackgrounds;
    // The File that indicates which directory this class will operate in.
    private final String folderPath;

    /**
     * @param path The path to the directory this object will bind itself to.
     * @throws IOException Throws the exception if the directory is not found, or if there is no osu file present.
     */
    OsuSongFolder(String path) throws IOException {
        //Assigns the directory that this class will work with.
        this.folderPath = path;

        // Gets all of the .osu files in the directory.
        ArrayList<String> osuFiles = findOsuFiles();

        // If there is no .osu files present in the directory then there's no use
        // for this class to be working in that folder.
        if (osuFiles.size() == 0) throw new IOException("No .osu file in directory: " + folderPath);

        // Adds all of the image names to a list.
        osuBackgrounds = findAllImageNames(osuFiles);
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
        for (File file : listOfFiles) {
            String fileName = file.getName();
            if (fileName.contains(".osu")) {
                tempOsuNames.add(fileName);
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
    private ArrayList<OsuBackground> findAllImageNames(ArrayList<String> osuFileNames) throws IOException {
        //List that will hold all of the found image names.
        ArrayList<OsuBackground> foundOsuBackgrounds = new ArrayList<>(0);

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
                if (line.contains(".png")
                        || line.contains(".PNG")
                        || line.contains(".jpg")
                        || line.contains(".JPG")
                        || line.contains(".Jpg")
                        || line.contains(".Png")) {

                    // The line that contains the image name has it surrounded by quotes, so we split the string to
                    // get the image name and ignore the coordinates. The image name will always be in index one.
                    String[] splittedSentence = line.split("\"");
                    String imageName = splittedSentence[1];

                    // Will only add the image name to the imageName list if there's no duplicate of it there.
                    if (!new File(folderPath+"/"+imageName).exists()) {
                        System.out.println("Image file \"" + imageName +"\" doesn't exist in the directory \"" + folderPath +"\"");
                    }

                    OsuBackground foundOsuBackground = new OsuBackground(imageName,folderPath);
                    boolean isDuplicate = false;
                    for (OsuBackground osuBackgrounds : foundOsuBackgrounds){
                        if (isSameOsuBackgrounds(osuBackgrounds,foundOsuBackground)){
                            isDuplicate = true;
                            break;
                        }
                    }

                    if (isDuplicate){
//                            System.out.println("Duplicate found and discarded");
                    } else {
                        foundOsuBackgrounds.add(foundOsuBackground);
                    }


                    // This method will only look for the first background file,
                    // after it is found there's no need to look more.
                    break;
                }
                //If a picture is not found on this line, we read in the next one and repeat the while loop.
                line = reader.readLine();
            }

        }

        // Returns the list with the image names.
        return foundOsuBackgrounds;
    }
    /**
     * Compares two osu backgrounds and determines if they're the same.
     * @param first OsuBackground to compare.
     * @param second OsuBackground to compare.
     * @return true if first equals second.
     */
    private boolean isSameOsuBackgrounds(OsuBackground first, OsuBackground second){
        if (first.hashCode() == second.hashCode()){
            if (first.equals(second)){
                return true;
            }
        }
        return false;
    }


    /**
     * @return Returns the path of all of the images in the directory separately in a list.
     */
    ArrayList<String> getAllOsuBackgroundFilePaths() {
        ArrayList<String> pathsToImages = new ArrayList<>(0);

        // For every image in the folder, adds the file path and file name to the list.
        for (OsuBackground bg : osuBackgrounds) {
            pathsToImages.add(bg.getFilePath()+"/"+bg.getFileName());
        }
        return pathsToImages;
    }
    /**
     * @return the name of the folder.
     */
    String getDirectoryName() {
        File path = new File(folderPath);
        return path.getName();
    }
    /**
     *
     * @return the path to the osu installation directory
     */
    String getDirectoryPath(){
        return folderPath;
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
        for (OsuBackground bg : osuBackgrounds) {
            Path source = Paths.get(filePath + "/" + fileName);
            Path target = Paths.get(bg.getFilePath(), bg.getFileName());
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
        for (OsuBackground bg: osuBackgrounds) {
            if (bg.getFileName().chars().allMatch(c -> c < 128)) {
                Path source = Paths.get(bg.getFilePath() + "/" + bg.getFileName());
                Path target = Paths.get(saveDirectory + "/" + getDirectoryName() + "/" + bg.getFileName());

                //If save directory doesn't exist, create it.
                if (!new File(saveDirectory + "/" + getDirectoryName()).exists()){
                    new File(saveDirectory + "/" + getDirectoryName()).mkdir();
                }

                Files.copy(source, target, REPLACE_EXISTING);
            } else {
                //TODO Can't save the image if it contains characters outside of the ANSI Range.
                //Image names currently only supports ANSI standard.
                //If you use characters not in ANSI then you won't be able to copy it using the copy to method below.
                System.out.println("COULDN'T SAVE IMAGE: " + bg.getFileName());
            }
        }
    }
    /**
     * Removes all the background images in the folder.
     */
    void removeAllBackgrounds() {
        for (OsuBackground bg : osuBackgrounds) {
            Path target = Paths.get(bg.getFilePath() + "/" + bg.getFileName());

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
}