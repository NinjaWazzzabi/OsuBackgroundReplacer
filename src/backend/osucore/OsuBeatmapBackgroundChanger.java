package backend.osucore;

import backend.beatmapcore.Beatmap;
import backend.beatmapcore.Image;
import backend.beatmapcore.ImageHelper;
import backend.utility.WorkObservers;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

/**
 * Manipulates images that are found in {@link backend.beatmapcore.Beatmap}.
 */
public class OsuBeatmapBackgroundChanger {

    @Getter @Setter
    private WorkObservers workObservers;
    @Setter
    private MainSongFolder songFolder;

    OsuBeatmapBackgroundChanger(WorkObservers workObservers) {
        this.workObservers = workObservers;
    }

    public void replaceAll(File file) {
        Image image = new ImageHelper(file);
        replaceAll(image);
    }
    public void replaceAll(Image image) {
        manipulateBeatmaps(
                songFolder.getBeatmaps(),
                beatmap -> beatmap.replaceBackgrounds(image)
        );
    }
    public void removeAll() {
        manipulateBeatmaps(
                songFolder.getBeatmaps(),
                Beatmap::removeAllBackgrounds
        );
    }
    public void copyAll(String saveDirectory) {
        manipulateBeatmaps(
                songFolder.getBeatmaps(),
                beatmap -> beatmap.copyBackgrounds(saveDirectory)
        );
    }

    public void manipulateBeatmaps(List<Beatmap> beatmapList, Consumer<Beatmap> consumer) {
        if (beatmapList == null || beatmapList.size() == 0) {
            return;
        }

        runWorkInNewThread(() -> {
            for (int i = 0; i < beatmapList.size(); i++) {
                if (!beatmapList.get(i).isLoaded()) {
                    beatmapList.get(i).loadImages();
                }
                consumer.accept(beatmapList.get(i));
                if (i % 10 == 0) {
                    workObservers.alertWorkProgress((double) i / beatmapList.size());
                }
            }
        });
    }
    private void runWorkInNewThread(Runnable runnable) {
        synchronized (songFolder) {
            if (workObservers.isWorking()) {
                return;
            }
            workObservers.alertListenersWorkStarted();
            Thread work = new Thread(() -> {
                runnable.run();
                workObservers.alertListenersWorkFinished();
            });
            work.start();
        }
    }
}
