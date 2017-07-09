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

    private String currentSongFolder;
    private String osuDirectoryPath;
    private boolean isWorking;
    private List<WorkListener> workListeners;
    private MainSongFolder songFolder;

    public BackgroundManager(){
        this.isWorking = false;
        try {
            this.osuDirectoryPath = findOsuDirectory();
        } catch (FileNotFoundException e) {
            this.osuDirectoryPath = "";
        }
        updateSongFolder();
        this.workListeners = new ArrayList<>();
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
            startedWorking();
            Thread thread = new Thread(() -> {
                updateSongFolder();
                songFolder.replaceAllImages(image);
                finishedWorking();
            });
            thread.start();
        }
    }

    @Override
    public void saveAll(String directory) {
        System.out.println(directory);
        final String subDirectory = directory + "/savedOsuBeatmapImages";

        boolean success = false;
        if (!new File(subDirectory).exists()) {
            success = new File(subDirectory).mkdir();
        }

        if (!isWorking && success) {
            startedWorking();
            Thread thread = new Thread(() -> {
                    updateSongFolder();
                    songFolder.copyAll(subDirectory);
                    finishedWorking();
                });
            thread.start();
        }
    }
    @Override
    public void removeAll() {
        if (!isWorking){
            startedWorking();
            Thread thread = new Thread(() -> {
                    updateSongFolder();
                    songFolder.removeAll();
                    finishedWorking();
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
            return songFolder.getPath().equals(currentSongFolder);
        }
    }

    @Override
    public void setOsuDirectory(String path) throws IOException {
        if (isOsuDirectory(path)){
            currentSongFolder = path;
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


    private void startedWorking(){
        isWorking = true;
        for (WorkListener workListener : workListeners) {
            workListener.alertWorkStarted();
        }
    }
    private void finishedWorking(){
        isWorking = false;
        for (WorkListener workListener : workListeners) {
            workListener.alertWorkFinished();
        }
    }

    @Override
    public boolean isWorking() {
        return isWorking;
    }
    @Override
    public void addWorkListener(WorkListener listener) {
        workListeners.add(listener);
    }
    @Override
    public void removeWorkListener(WorkListener listener) {
        workListeners.remove(listener);
    }

    @Override
    public boolean installationFound() {
        return isOsuDirectory(getOsuAbsolutePath());
    }
}
