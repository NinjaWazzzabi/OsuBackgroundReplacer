package backend.osubackgroundhandler;

import backend.core.MainSongFolder;
import backend.core.image.Image;
import backend.core.image.ImageHelper;
import lombok.Getter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Manages backgrounds in an osu folder.
 */
public class BackgroundManager implements IOsuBackgroundHandler{

    private String currentSongFolderPath;
    @Getter private String osuAbsolutePath;
    private boolean isWorking;
    private WorkListeners workListeners;
    @Getter private MainSongFolder songFolder;

    public BackgroundManager(){
        this.isWorking = false;
        try {
            this.osuAbsolutePath = findOsuDirectory();
        } catch (FileNotFoundException e) {
            this.osuAbsolutePath = "";
        }
        updateSongFolder();
        this.workListeners = new WorkListeners();
    }

    @Override
    public void replaceAll(String imagePath) {
        if (!isWorking && Image.validImagePath(imagePath)){
            Image image = new ImageHelper(imagePath);
            replaceAll(image);
        }
    }
    @Override
    public void replaceAll(Image image) {
        if (!isWorking) {
            workListeners.alertListenersWorkStarted();
            Thread thread = new Thread(() -> {
                updateSongFolder();
                songFolder.replaceAllImages(image);
                workListeners.alertListenersWorkFinished();
            });
            thread.start();
        }
    }
    @Override
    public void saveAll(String directory) {
        saveAll(directory,"/savedOsuBeatmapImages");
    }
    @Override
    public void saveAll(String directory, String folderName) {
        final String subDirectory = directory + folderName;

        boolean success = false;
        if (!new File(subDirectory).exists()) {
            success = new File(subDirectory).mkdir();
        }

        if (!isWorking && success) {
            workListeners.alertListenersWorkStarted();
            Thread thread = new Thread(() -> {
                updateSongFolder();
                songFolder.copyAll(subDirectory);
                workListeners.alertListenersWorkFinished();
            });
            thread.start();
        }
    }
    @Override
    public void removeAll() {
        if (!isWorking){
            workListeners.alertListenersWorkStarted();
            Thread thread = new Thread(() -> {
                    updateSongFolder();
                    songFolder.removeAll();
                    workListeners.alertListenersWorkFinished();
            });
            thread.start();
        }
    }

    private void updateSongFolder(){
        if (!isSongFolderUpdated()){
            songFolder = new MainSongFolder(osuAbsolutePath + "/" + "Songs");
        }
    }
    private boolean isSongFolderUpdated(){
        if (this.songFolder == null) {
            return false;
        } else {
            return songFolder.getPath().equals(currentSongFolderPath);
        }
    }

    @Override
    public void setOsuDirectory(String path) throws IOException {
        if (OsuInstallationFinder.isOsuDirectory(path)){
            osuAbsolutePath = path;
            currentSongFolderPath = osuAbsolutePath + "/" + "Songs";
        } else {
            throw new IOException("Not an osu directory");
        }
    }
    @Override
    public void setOsuFile(String path) throws IOException {
        File osuFile = new File(path);
        setOsuDirectory(osuFile.getParent());
    }

    private String findOsuDirectory() throws FileNotFoundException {
        OsuInstallationFinder oif = new OsuInstallationFinder();
        String installationPath = oif.getInstallationPath();

        if (installationPath == null) {
            throw new FileNotFoundException("Osu installation not found");
        }

        return installationPath;
    }

    public WorkListeners getWorkListeners(){
        return workListeners;
    }

    @Override
    public boolean installationFound() {
        return OsuInstallationFinder.isOsuDirectory(getOsuAbsolutePath());
    }
}
