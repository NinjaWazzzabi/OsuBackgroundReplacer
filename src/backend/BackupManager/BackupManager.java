package backend.BackupManager;

import backend.core.Beatmap;
import backend.osubackgroundhandler.IOsuBackgroundHandler;
import backend.osubackgroundhandler.WorkListener;
import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Handles backups of osu images.
 */
public class BackupManager {

    private static final String BACKUPFOLDER_RELATIVE_PATH = "bgbackup";

    @Getter
    private List<File> backedUpFolders;
    private List<WorkListener> workListeners;
    @Getter
    private List<Beatmap> missingBackups;

    private final IOsuBackgroundHandler obh;
    private boolean backupExists = false;

    public BackupManager(IOsuBackgroundHandler obh) {
        backedUpFolders = new ArrayList<>();
        workListeners = new ArrayList<>();
        this.obh = obh;
        searchForBackup();
        missingBackups = findMissingBackups();
    }

    private void searchForBackup() {
        String osuPath = obh.getOsuAbsolutePath();

        File backupFolder = new File(osuPath + "/" + BACKUPFOLDER_RELATIVE_PATH);
        backupExists = backupFolder.exists();

        if (backupExists) {
            File[] backupArray = backupFolder.listFiles((dir, name) -> new File(dir, name).isDirectory());
            if (backupArray != null) {
                backedUpFolders = Arrays.asList(backupArray);
            } else {
                backedUpFolders = new ArrayList<>();
            }
        }
    }

    private List<Beatmap> findMissingBackups() {
        List<Beatmap> missingBackups = new ArrayList<>();
        List<Beatmap> beatmaps = obh.getMainSongFolder().getBeatmaps();

        for (Beatmap beatmap : beatmaps) {
            boolean beatmapBackedUp = false;
            for (File backedUpFolder : backedUpFolders) {
                if (beatmap.getFolderName().equals(backedUpFolder.getName())) {
                    beatmapBackedUp = true;
                    break;
                }
            }
            if (!beatmapBackedUp) {
                missingBackups.add(beatmap);
            }
        }

        return missingBackups;
    }

    public boolean allIsBackedUp() {
        return missingBackups.size() == 0;
    }

    public void runBackup() {

        File backupFolder = new File(obh.getOsuAbsolutePath() + "/" + BACKUPFOLDER_RELATIVE_PATH);
        if (!backupFolder.exists()) {
            backupFolder.mkdir();
        }

        for (Beatmap missingBackup : missingBackups) {
            missingBackup.copyBackgrounds(obh.getOsuAbsolutePath() + "/" + BACKUPFOLDER_RELATIVE_PATH);
        }
    }

    public void addWorkListener(WorkListener listener) {
        workListeners.add(listener);
    }

    public void removeWorkListener(WorkListener listener) {
        workListeners.remove(listener);
    }
}