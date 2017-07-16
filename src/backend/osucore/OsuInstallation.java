package backend.osucore;

import lombok.Getter;

import java.io.File;
import java.io.IOException;

/**
 * Represents an osu installation.
 */
public class OsuInstallation {

    @Getter private File directoryPath;
    private Runnable onDirectoryChange;

    OsuInstallation() {
        OsuInstallationFinder installationFinder = new OsuInstallationFinder();
        String path = installationFinder.getInstallationPath();

        if (path != null) {
            directoryPath = new File(path);
        }
    }

    public boolean setOsuDirectory(File directory) {
        if (OsuInstallationFinder.isOsuDirectory(directory.getAbsolutePath())){
            directoryPath = directory;
            onDirectoryChange.run();
            return true;
        } else {
            return false;
        }
    }
    public boolean setOsuFile(File file) {
        File osuFolder = file.getParentFile();
        return setOsuDirectory(osuFolder);
    }

    public boolean installationFound(){
        return directoryPath != null;
    }

    public File getSongFolderPath(){
        return new File(directoryPath + "/" + "Songs");
    }

    public void setOnDirectoryChange(Runnable onDirectoryChange) {
        this.onDirectoryChange = onDirectoryChange;
    }
}
