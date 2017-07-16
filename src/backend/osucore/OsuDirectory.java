package backend.osucore;

import backend.utility.WorkObservers;
import lombok.Getter;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by amk19 on 16/07/2017.
 */
public class OsuDirectory {

    @Getter
    private final OsuInstallation osuInstallation;
    private MainSongFolder songFolder;
    @Getter
    private WorkObservers workObservers;
    @Getter private OsuBeatmapBackgroundChanger backgroundChanger;

    public OsuDirectory() {
        workObservers = new WorkObservers();
        backgroundChanger = new OsuBeatmapBackgroundChanger(workObservers);
        osuInstallation = new OsuInstallation();

        File osuPath = osuInstallation.getDirectoryPath();
        if (osuPath != null) {
            songFolder = new MainSongFolder(osuInstallation.getSongFolderPath());
            backgroundChanger.setSongFolder(songFolder);
        }

        osuInstallation.setOnDirectoryChange(() -> {
            if (songFolder != null) {
                songFolder = new MainSongFolder(osuInstallation.getSongFolderPath());
            } else {
                songFolder.setDirectory(osuInstallation.getSongFolderPath());
            }
            backgroundChanger.setSongFolder(songFolder);
        });
    }

    public MainSongFolder getSongFolder() throws FileNotFoundException {
        if (songFolder == null) {
            throw new FileNotFoundException("No Osu installation found!");
        }
        return songFolder;
    }
}
