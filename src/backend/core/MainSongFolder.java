package backend.core;

import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the main song folder in the osu directory.
 */
public class MainSongFolder {
    @Getter private final String path;
    @Getter private final List<Beatmap> beatmaps;

    public MainSongFolder(String path) {
        this.path = path;

        this.beatmaps = findBeatmaps();
    }
    private List<Beatmap> findBeatmaps(){
        List<Beatmap> foundBeatmaps = new ArrayList<>();
        File[] folders = new File(this.path).listFiles();

        for (File folder : folders) {
            Beatmap beatmap = new Beatmap(folder.getAbsolutePath());
            foundBeatmaps.add(beatmap);
        }
        return foundBeatmaps;
    }

    public synchronized void replaceAllImages(String imagePath){
        synchronized (this) {
            for (Beatmap beatmap : beatmaps) {
                beatmap.replaceBackgrounds(imagePath);
            }
        }
    }
    public synchronized void removeAll(){
        synchronized (this) {
            for (Beatmap beatmap : beatmaps) {
                beatmap.removeAllBackgrounds();
            }
        }
    }
    public synchronized void copyAll(String saveDirectory){
        synchronized (this) {
            for (Beatmap beatmap : beatmaps) {
                beatmap.copyBackgrounds(saveDirectory);
            }
        }
    }
}
