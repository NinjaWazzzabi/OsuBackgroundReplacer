package backend.osubackgroundhandler;

import backend.core.MainSongFolder;
import backend.core.image.Image;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A fake background manager that acts like {@link BackgroundManager} but doesn't change any files.
 */
public class BackgroundManagerFrontendTester implements IOsuBackgroundHandler {

    private WorkListeners listeners;
    private boolean isWorking;

    public BackgroundManagerFrontendTester() {
        isWorking = false;
        listeners = new WorkListeners();
    }

    @Override
    public void replaceAll(String imagePath) throws IOException {
        startFakeWork(4000);
    }

    @Override
    public void replaceAll(Image image) {
        startFakeWork(4000);
    }

    @Override
    public void saveAll(String directory) throws IOException {
        startFakeWork(2000);
    }

    @Override
    public void saveAll(String directory, String folderName) throws IOException {
        startFakeWork(2000);
    }

    @Override
    public void removeAll() {
        startFakeWork(1000);
    }


    private void startFakeWork(int sleepTime){
        if (!isWorking){
            listeners.alertListenersWorkStarted();
            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ignored) {}
                listeners.alertListenersWorkFinished();
            });
            thread.start();
        }
    }

    @Override
    public void setOsuDirectory(String path) throws IOException {
        if (!path.equals("C:/Users/Anthony/AppData/Local/osu!")) {
            throw new IOException("Not a valid osu location");
        }
    }

    @Override
    public void setOsuFile(String path) throws IOException {
        setOsuDirectory(path);
    }

    @Override
    public String getOsuAbsolutePath() {
        return "C:/Users/Anthony/AppData/Local/osu!";
    }

    @Override
    public MainSongFolder getMainSongFolder() {
        return null;
    }

    @Override
    public String findOsuDirectory() throws FileNotFoundException {
        return "C:/Users/Anthony/AppData/Local/osu!";
    }

    @Override
    public WorkListeners getWorkListeners() {
        return listeners;
    }


    @Override
    public boolean installationFound() {
        return false;
    }
}
