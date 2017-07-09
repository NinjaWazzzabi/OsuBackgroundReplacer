package backend.osubackgroundhandler;

import backend.core.MainSongFolder;
import backend.core.image.Image;
import backend.core.image.ImageHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages backgrounds in an osu folder.
 */
public class BackgroundManager implements IOsuBackgroundHandler{

    //TODO implement project lombok here
    private String currentSongFolderPath;
    private String osuDirectoryPath;
    private boolean isWorking;
    private WorkListeners workListeners;
    private MainSongFolder songFolder;

    public BackgroundManager(){
        this.isWorking = false;
        try {
            this.osuDirectoryPath = findOsuDirectory();
        } catch (FileNotFoundException e) {
            this.osuDirectoryPath = "";
        }
        updateSongFolder();
        this.workListeners = new WorkListeners();
    }

    @Override
    public void replaceAll(String imagePath) {
        if (!isWorking && isImageFile(new File(imagePath))){
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
            songFolder = new MainSongFolder(osuDirectoryPath + "/" + "Songs");
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
        if (isOsuDirectory(path)){
            osuDirectoryPath = path;
            currentSongFolderPath = osuDirectoryPath + "/" + "Songs";
        } else {
            throw new IOException("Not an osu directory");
        }
    }
    @Override
    public void setOsuFile(String path) throws IOException {
        File osuFile = new File(path);
        setOsuDirectory(osuFile.getParent());
    }

    @Override
    public String getOsuAbsolutePath() {
        return osuDirectoryPath;
    }

    @Override
    public MainSongFolder getMainSongFolder() {
        return songFolder;
    }

    @Override
    public String findOsuDirectory() throws FileNotFoundException {
        OsuInstallationFinder oif = new OsuInstallationFinder();
        String installationPath = oif.getInstallationPath();

        if (installationPath == null) {
            throw new FileNotFoundException("Osu installation not found");
        }

        return installationPath;
    }


    //TODO Move to OsuInstallationFinder
    /**
     * Checks if the directory contains a osu!.exe
     *
     * @param directory to be checked.
     * @return true if osu!.exe is found.
     */
    private boolean isOsuDirectory(String directory) {
        File osuFolder = new File(directory);
        if (!osuFolder.exists() || !osuFolder.isDirectory()) {
            return false;
        }

        for (File file : osuFolder.listFiles()) {
            if (isOsuExe(file.getAbsolutePath())) return true;
        }

        return false;
    }
    //TODO Move to OsuInstallationFinder
    /**
     * Checks if the string contains "osu!.exe"
     * @param path total path to the exe, or just the exe name itself.
     * @return true if string contains "osu!.exe".
     */
    private boolean isOsuExe(String path){
        return new File(path).getName().contains("osu!.exe");
    }
    /**
     * Checks if the file is a png or jpg file type.
     * @param file to be checked.
     * @return true if file type is jpg or png.
     */
    private boolean isImageFile(File file){
        return Image.validImagePath(file.getAbsolutePath());
    }

    public WorkListeners getWorkListeners(){
        return workListeners;
    }


    @Override
    public boolean installationFound() {
        return isOsuDirectory(getOsuAbsolutePath());
    }
}
