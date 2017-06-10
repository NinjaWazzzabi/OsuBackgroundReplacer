package backend.osubackgroundhandler;

import backend.core.MainSongFolder;

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
    private boolean isWorking;
    private List<WorkListener> workListeners;
    private MainSongFolder songFolder;

    public BackgroundManager(){
        this.isWorking = false;
        try {
            this.currentSongFolder = findOsuDirectory();
        } catch (FileNotFoundException e) {
            this.currentSongFolder = "C:/";
        }
        updateSongFolder();
        this.workListeners = new ArrayList<>();
    }

    @Override
    public void replaceAll(String imagePath) {
        if (!isWorking && isImageFile(new File(imagePath))){
            startedWorking();
            Thread thread = new Thread(() -> {
                updateSongFolder();
                songFolder.replaceAllImages(imagePath);
                finishedWorking();
            });
            thread.start();
        }
    }
    @Override
    public void saveAll(String directory) {
        if (!isWorking) {
            startedWorking();
            Thread thread = new Thread(() -> {
                    updateSongFolder();
                    songFolder.copyAll(directory);
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
            songFolder = new MainSongFolder(currentSongFolder + "/" + "Songs");
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
        return songFolder.getPath();
    }

    @Override
    public String findOsuDirectory() throws FileNotFoundException {

        //Default osu locations
        String homeDir = System.getProperty("user.home");
        homeDir = homeDir.replace("\\", "/");
        String defDirOne = homeDir + "/AppData/Local/osu!";
        String defDirTwo = "C:/Program Files/osu!";
        String defDirThree = "C:/Program Files(x86)/osu!";
        String[] defaultDirectories = {defDirOne,defDirTwo,defDirThree};


        for (String defaultDirectory : defaultDirectories) {
            if (isOsuDirectory(defaultDirectory)){
                try {
                    setOsuDirectory(defaultDirectory);
                    return defaultDirectory;
                } catch (IOException ignored) {
                    //Osu installation not found on a location, no need to catch exception.
                }
            }
        }

        throw new FileNotFoundException("Osu directory not found");
    }
    /**
     * Checks if the directory contains a osu!.exe
     *
     * @param directory to be checked.
     * @return true if osu!.exe is found.
     */
    private boolean isOsuDirectory(String directory) {
        File osuFolder = new File(directory);
        if (!osuFolder.exists()) {
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
        String extension = "";

        int i = file.getAbsolutePath().lastIndexOf('.');
        if (i > 0) {
            extension = file.getAbsolutePath().substring(i+1);
        }
        extension = extension.toLowerCase();

        return extension.equals("jpg") || extension.equals("png");
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
}
