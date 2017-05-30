package backend.osubackgroundhandler;

import lombok.Getter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A class than controls all of the {@link OsuSongFolder} in the osu! song folder.
 */
class OsuBackgroundHandler implements IOsuBackgroundHandler {

    private File directory;
    private ArrayList<OsuSongFolder> songFolders;
    private File songDirectory;

    private boolean allBackgroundsLoaded;
    @Getter private boolean isWorking;
    private List<WorkListener> workListeners;

    OsuBackgroundHandler() {
        isWorking = false;
        allBackgroundsLoaded = false;
        directory = null;
        workListeners = new ArrayList<>();
        songFolders = new ArrayList<>(0);
    }

    @Override
    public synchronized void replaceAll(String imageName, String imageDirectory) throws IOException {
        //TODO Make sure the path is pointed at a picture, and not something else.
        File file = new File(imageDirectory + "/" + imageName);

        if (!file.exists()) {
            throw new IOException("Image not found");
        } else if (!isImageFile(file)) {
            throw new IOException("File is not a png or jpg");
        } else {
            //Run in new thread to not delay other processes.
            startedWorking();
            Thread thread = new Thread(() -> {
                if (!allBackgroundsLoaded){
                    loadAllSongFolders();
                }
                for (OsuSongFolder obg : songFolders) {
                    obg.replaceBackgrounds(imageName, imageDirectory);
                }
                finishedWorking();
            });
            thread.start();
        }
    }
    @Override
    public synchronized void saveAll(String directory) throws IOException {
        if (!new File(directory).exists()){
            throw new IOException("Save location not found");
        }
        String saveDirectory = directory + "/OsuBackgrounds";
        if (!new File(saveDirectory).exists()) {
            boolean successful = new File(saveDirectory).mkdir();
            if (!successful) {
                throw new IOException("Couldn't create folder: " + saveDirectory);
            }
        }
            //Run in new thread to not delay other processes.
            startedWorking();
            Thread thread = new Thread(() -> {
                if (!allBackgroundsLoaded){
                    loadAllSongFolders();
                }
                for (OsuSongFolder background : songFolders) {
                    try {
                        background.copyBackgrounds(saveDirectory);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                finishedWorking();
            });
            thread.start();
    }
    @Override
    public synchronized void removeAll() {
        //Run in new thread to not delay other processes.
        startedWorking();
        Thread thread = new Thread(() -> {
            if (!allBackgroundsLoaded){
                loadAllSongFolders();
            }
            for (OsuSongFolder songFolder : songFolders) {
                songFolder.removeAllBackgrounds();
            }
            finishedWorking();
        });
        thread.start();
    }


    @Override
    public void setOsuDirectory(String path) throws IOException {
        if (isOsuDirectory(path)){
            directory = new File(path);
            songDirectory = new File(path+"/Songs");
            allBackgroundsLoaded = false;
        } else {
            throw new IOException("Not a valid osu installation folder");
        }
    }
    @Override
    public void setOsuFile(String path) throws IOException {
        if (isOsuExe(path)){
            directory = new File(path).getParentFile();
            songDirectory = new File(path+"/Songs");
            allBackgroundsLoaded = false;
        } else {
            throw new IOException("Not a osu executable");
        }
    }


    @Override
    public String getOsuAbsolutePath() {
        if (directory == null) {
            return null;
        }
        return directory.getAbsolutePath();
    }
    @Override
    public List<String> getSongDirectoryNames() {
        ArrayList<String> allOsuSongFolderNames = new ArrayList<>();

        for (OsuSongFolder osuSongFolder : songFolders) {
            allOsuSongFolderNames.add(osuSongFolder.getDirectoryName());
        }

        return allOsuSongFolderNames;
    }
    @Override
    public String findOsuDirectory() throws FileNotFoundException {

        //Combines the three default installation locations to an array
        String homeDir = System.getProperty("user.home");
        homeDir = homeDir.replace("\\", "/");
        String defDirOne = homeDir + "/AppData/Local/osu!";
        String defDirTwo = "C:/Program Files/osu!";
        String defDirThree = "C:/Program Files(x86)/osu!";
        String[] defaultDirectories = {defDirOne,defDirTwo,defDirThree};

        //Checks every element if it contains an osu installation.
        for (String defaultDirectory : defaultDirectories) {
            if (isOsuDirectory(defaultDirectory)){
                try {
                    setOsuDirectory(defaultDirectory);
                    return defaultDirectory;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        throw new FileNotFoundException("Osu directory not found");
    }


    private void loadAllSongFolders() {
        File[] listOfFolders = songDirectory.listFiles();
        ArrayList<OsuSongFolder> tempOsuSongsBackgrounds = new ArrayList<>(0);

        if (listOfFolders != null){
            for (File folder : listOfFolders) {
                try {
                    tempOsuSongsBackgrounds.add(new OsuSongFolder(folder.getAbsolutePath()));
                } catch (IOException io) {
                    System.out.println(io.toString());
                }
            }
        }
        songFolders = tempOsuSongsBackgrounds;
        allBackgroundsLoaded = true;
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


    /**
     * Alerts all listeners that this object has started working.
     */
    private synchronized void startedWorking(){
        isWorking = true;
        workListeners.forEach(WorkListener::alertWorkStarted);
    }
    /**
     * Alerts all listeners that this object has finished working.
     */
    private synchronized void finishedWorking(){
        isWorking = false;
        workListeners.forEach(WorkListener::alertWorkFinished);
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
