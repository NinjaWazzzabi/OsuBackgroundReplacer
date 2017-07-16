package frontend.windows;

import backend.osucore.OsuDirectory;
import backend.utility.WorkObservers;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * Created by amk19 on 16/07/2017.
 */
public class TestingWindow extends WindowBase {
    private static final String FXML_FILE = "/fxml/testing.fxml";

    private OsuDirectory osu;

    public TestingWindow(OsuDirectory osu) {
        super(FXML_FILE);

        this.osu = osu;
    }

    @FXML
    private void workOne(ActionEvent event) {
        startWork(1);
    }

    @FXML
    private void workFive(ActionEvent event) {
        startWork(5);
    }

    @FXML
    private void workTen(ActionEvent event) {
        startWork(10);
    }


    private void startWork(int seconds) {
        Thread work = new Thread(() -> {
            WorkObservers workObservers = osu.getWorkObservers();
            workObservers.alertListenersWorkStarted();

            for (double i = 0; i < seconds; i+=0.05) {
                workObservers.alertWorkProgress(i/seconds);

                try {
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {}
            }

            workObservers.alertListenersWorkFinished();
        });

        work.start();
    }
}
