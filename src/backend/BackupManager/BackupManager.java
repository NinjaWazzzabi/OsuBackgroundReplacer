package backend.BackupManager;

import backend.beatmapcore.Beatmap;
import backend.osucore.OsuDirectory;
import backend.utility.FileHandler;
import backend.utility.WorkObservers;
import lombok.Getter;

import java.io.File;
import java.io.FileNotFoundException;
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
    private WorkObservers workObservers;
    @Getter
    private List<Beatmap> missingBackups;

    private final OsuDirectory osu;
    @Getter
    private boolean backupExists = false;

    public BackupManager(OsuDirectory osu) {
        this.osu = osu;
        backedUpFolders = new ArrayList<>();
        workObservers = new WorkObservers();
        searchForBackup();
        try {
            missingBackups = findMissingBackups();
        } catch (FileNotFoundException e) {
            missingBackups = new ArrayList<>();
        }
    }

    private void searchForBackup() {
        File osuDirectory = osu.getOsuInstallation().getDirectoryPath();

        File backupFolder = new File(osuDirectory.getAbsolutePath() + "/" + BACKUPFOLDER_RELATIVE_PATH);
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

    private List<Beatmap> findMissingBackups() throws FileNotFoundException {
        List<Beatmap> missingBackups = new ArrayList<>();
        List<Beatmap> beatmaps = osu.getSongFolder().getBeatmaps();

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
        if (!osu.getOsuInstallation().installationFound()) {
            return;
        }


        File backupFolder = new File(osu.getOsuInstallation().getDirectoryPath() + "/" + BACKUPFOLDER_RELATIVE_PATH);
        if (!backupFolder.exists()) {
            backupFolder.mkdir();
        }

        osu.getBackgroundChanger().manipulateBeatmaps(
                missingBackups,
                beatmap -> beatmap.copyBackgrounds(osu.getOsuInstallation().getDirectoryPath() + "/" + BACKUPFOLDER_RELATIVE_PATH)
        );

        workObservers.alertListenersWorkFinished();
    }

    public WorkObservers getWorkListeners() {
        return workObservers;
    }

    public void refresh() throws FileNotFoundException {
        if (!osu.getOsuInstallation().installationFound()) {
            throw new FileNotFoundException("Osu installation not found");
        }

        backedUpFolders = new ArrayList<>();
        missingBackups = new ArrayList<>();
        searchForBackup();
        missingBackups = findMissingBackups();
    }

    public void restoreImages() throws FileNotFoundException {
        if (!osu.getOsuInstallation().installationFound()) {
            return;
        }

        final String path = osu.getSongFolder().getDirectory().getAbsolutePath();

        workObservers.alertListenersWorkStarted();
        Thread restore = new Thread(() -> {
            synchronized (this) {

                for (File backedUpFolder : backedUpFolders) {
                    FileHandler.copyFolder(backedUpFolder, new File(path));
                }
                workObservers.alertListenersWorkFinished();
            }
        });
        restore.start();

    }

    public File getBackupDirectory() {
        return new File(osu.getOsuInstallation().getDirectoryPath() + "/" + BACKUPFOLDER_RELATIVE_PATH);
    }
}
