package backend.osucore;

import backend.beatmapcore.Beatmap;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the main song folder in the osu directory.
 */
public class MainSongFolder {
    @Setter @Getter private File directory;
    @Getter private List<Beatmap> beatmaps;

    public MainSongFolder(File directory) {
        this.directory = directory;
        this.beatmaps = new ArrayList<>();
        quickUpdate();
    }

    /**
     * Only looks for all of the beatmaps available. Doesn't load images.
     */
    public void quickUpdate() {
        clearBeatmapList();

        //If this parent directory isn't an osu directory, then don't search for beatmaps.
        if (!OsuInstallationFinder.isOsuDirectory(directory.getParent())) {
            return;
        }

        File[] folders = directory.listFiles();
        if (folders != null) {
            for (File folder : folders) {
                if (folder.isDirectory()) {
                    beatmaps.add(new Beatmap(folder));
                }
            }
        }
    }
    /**
     * Updates the whole beatmap list, and loads background images for the non loaded beatmaps.
     */
    public void fullUpdate(){
        quickUpdate();

        for (Beatmap beatmap : beatmaps) {
            if (!beatmap.isLoaded()) {
                beatmap.loadImages();
            }
        }
    }
    /**
     * Force searches for all beatmaps and force updates all image paths.
     */
    public void forceCompleteUpdate(){
        quickUpdate();
        for (Beatmap beatmap : beatmaps) {
            beatmap.loadImages();
        }
    }
    private boolean isSongFolderUpdated(){
        //If there are no beatmaps, return false.
        if (beatmaps == null || beatmaps.size() == 0) {
             return false;
        }

        //If a beatmap is not loaded, return false.
        for (Beatmap beatmap : beatmaps) {
            if (!beatmap.isLoaded()) {
                return false;
            }
        }

        //Otherwise return true.
        return true;
    }

    public void clearBeatmapList(){
        beatmaps = new ArrayList<>();
    }
}
