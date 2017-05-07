package backend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anthony on 09/02/2017.
 */
class OsuBackgroundHandler implements OsuBackgroundHandlers {

    private File directory;
    private ArrayList<OsuSongFolder> songFolders;
    private File songDirectory;

    public OsuBackgroundHandler() {
        directory = null;
        songFolders = new ArrayList<>(0);
    }

    @Override
    public synchronized void replaceAll(String imageName, String imageDirectory) throws IOException {
        //TODO Make sure the path is pointed at a picture, and not something else.

        if (!new File(imageDirectory + "/" + imageName).exists()) {
            throw new IOException("Image not found");
        } else {
            //Run in new thread to not delay other processes.
            Thread thread = new Thread(() -> {
                for (OsuSongFolder obg : songFolders) {
                    obg.replaceBackgrounds(imageName, imageDirectory);
                }
            });
            thread.start();
        }
    }
    @Override
    public synchronized void saveAll(String directory) throws IOException {
        directory = directory+ "/OsuBackgrounds";
        if (!new File(directory).exists()) {
            new File(directory).mkdir();
        } else {
            String finalDirectory = directory;
            //Run in new thread to not delay other processes.
            Thread thread = new Thread(() -> {
                for (OsuSongFolder background : songFolders) {
                    try {
                        background.copyBackgrounds(finalDirectory);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    }
    @Override
    public synchronized void removeAll() {
        //Run in new thread to not delay other processes.
        Thread thread = new Thread(() -> {
            for (OsuSongFolder songFolder : songFolders) {
                songFolder.removeAllBackgrounds();
            }
        });
        thread.start();
    }

    @Override
    public void setDirectory(String path) throws IOException {
        directory = new File(path);
        songDirectory = new File(path+"/Songs");
        songFolders = getAllSongFolders();
    }
    @Override
    public String getOsuAbsolutePath() throws ClassNotFoundException {
        if (directory == null) throw new ClassNotFoundException("NO DIRECTORY YET SPECIFIED");
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
    /**
     * Goes through the three default osu installation locations and find the one with the osu installation.
     *
     * @return directory path with osu installation.
     * @throws FileNotFoundException if no osu installation is found.
     */
    @Override
    public String findOsuDirectory() throws FileNotFoundException {
        String homeDir = System.getProperty("user.home");
        homeDir = homeDir.replace("\\", "/");
        String defDirOne = homeDir + "/AppData/Local/osu!";
        String defDirTwo = "C:/Program Files/osu!";
        String defDirThree = "C:/Program Files(x86)/osu!";

        if (isOsuDirectory(defDirOne)) {
            try {
                setDirectory(defDirOne);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return defDirOne;
        }
        if (isOsuDirectory(defDirTwo)) {
            try {
                setDirectory(defDirTwo);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return defDirTwo;
        }
        if (isOsuDirectory(defDirThree)) {
            try {
                setDirectory(defDirThree);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return defDirThree;
        }

        throw new FileNotFoundException("Osu directory not found");
    }
    private ArrayList<OsuSongFolder> getAllSongFolders() {
        File[] listOfFolders = songDirectory.listFiles();
        ArrayList<OsuSongFolder> tempOsuSongsBackgrounds = new ArrayList<>(0);

        for (File folder : listOfFolders) {
            try {
                tempOsuSongsBackgrounds.add(new OsuSongFolder(folder.getAbsolutePath()));
            } catch (IOException io) {
                System.out.println(io.toString());
            }
        }

        return tempOsuSongsBackgrounds;
    }

    /**
     * Checks if the directory contains a osu!.exe
     *
     * @param directory to be checked.
     * @return true if osu!.exe is found.
     */
    private boolean isOsuDirectory(String directory) {
        File osuFolder = new File(directory);
        if (!osuFolder.exists()) return false;

        for (File file : osuFolder.listFiles()) {
            if (file.getName().contains("osu!.exe")) return true;
        }

        return false;
    }
}
