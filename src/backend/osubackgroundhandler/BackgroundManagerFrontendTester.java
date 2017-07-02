package backend.osubackgroundhandler;

import backend.core.image.Image;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A fake background manager that acts like {@link BackgroundManager} but doesn't change any files.
 */
public class BackgroundManagerFrontendTester implements IOsuBackgroundHandler {

    private List<WorkListener> listeners;
    private boolean isWorking;

    public BackgroundManagerFrontendTester() {
        isWorking = false;
        listeners = new ArrayList<>();
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
    public void removeAll() {
        startFakeWork(1000);
    }


    private void startFakeWork(int sleepTime){
        if (!isWorking){
            startedWorking();
            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ignored) {}
                finishedWorking();
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
    public String findOsuDirectory() throws FileNotFoundException {
        return "C:/Users/Anthony/AppData/Local/osu!";
    }

    @Override
    public boolean isWorking() {
        return isWorking;
    }


    private void startedWorking() {
        isWorking = true;
        for (WorkListener workListener : listeners) {
            workListener.alertWorkStarted();
        }
    }

    private void finishedWorking() {
        isWorking = false;
        for (WorkListener workListener : listeners) {
            workListener.alertWorkFinished();
        }
    }

    @Override
    public void addWorkListener(WorkListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeWorkListener(WorkListener listener) {
        listeners.add(listener);
    }
}
