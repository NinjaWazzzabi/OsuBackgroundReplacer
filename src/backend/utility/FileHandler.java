package backend.utility;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Handles operations with files.
 */
public class FileHandler {

    public static boolean copyFolder(File source, File target) {
        //Checks if source exists
        if (!source.exists() || !source.isDirectory()) {
            return false;
        }

        //Checks if target exists
        if (!target.exists() || !target.isDirectory()) {
            return false;
        }

        //Creates the copied folder
        File folderNew = new File(target.getAbsolutePath() + "//" + source.getName());
        if (!folderNew.exists()) {
            boolean dirCreateSuccessful = folderNew.mkdir();
            if (!dirCreateSuccessful) {
                return false;
            }
        }

        //Copies all sub folders and files in folder
        File[] filesInSource = source.listFiles();
        if (filesInSource != null) {
            for (File file : filesInSource) {
                if (file.isDirectory()) {
                    copyFolder(file, new File(folderNew + "//" + file.getName()));
                } else {
                    copyFile(file, folderNew);
                }
            }

        }

        return true;
    }

    public static boolean copyFile(File source, File folderPath) {
        if (source.isDirectory() || !folderPath.isDirectory()) {
            return false;
        }

        try {
            Files.copy(Paths.get(source.getAbsolutePath()), Paths.get(folderPath.getAbsolutePath() + "//" + source.getName()), REPLACE_EXISTING);
        } catch (IOException e) {
            return false;
        }

        return true;
    }
}
