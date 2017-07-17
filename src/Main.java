import backend.osucore.OsuDirectory;
import backend.utility.WorkObserver;
import frontend.windows.*;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application implements WorkObserver, MainWindowListener {

    private MainWindow mainWindow;
    private ReplaceWindow replaceWindow;
    private SingleColourWindow singleColourWindow;
    private SettingsWindow settingsWindow;
    private BackupWindow backupWindow;

    private Stage stage;
    private OsuDirectory osu;
    private boolean startupComplete;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        startupComplete = false;
        initializeBackend();
        initializeFrontend(stage);
        startupComplete = true;
    }


    private void initializeBackend() {
        Thread backendStartup = new Thread(() -> {
            osu = new OsuDirectory();
            osu.getWorkObservers().addListener(this);
        });

        backendStartup.start();
        try {
            backendStartup.join();
        } catch (InterruptedException ignored) {}
    }

    private void initializeFrontend(Stage stage){
        replaceWindow = new ReplaceWindow(osu);
        singleColourWindow = new SingleColourWindow(osu);
        settingsWindow = new SettingsWindow(osu);
        backupWindow = new BackupWindow(osu);
        backupWindow.getWorkListeners().addListener(this);

        mainWindow = new MainWindow();
        mainWindow.addListener(this);

        mainWindow.addNewTab("Replace Image", replaceWindow.getVisualComponent());
        mainWindow.addNewTab("Single Colour",singleColourWindow.getVisualComponent());
        mainWindow.addNewTab("Backup", backupWindow.getVisualComponent());
        mainWindow.addNewTab("Settings", settingsWindow.getVisualComponent());
        mainWindow.addNewTab("Testing", new TestingWindow(osu).getVisualComponent());

        if (!osu.getOsuInstallation().installationFound()) {
            mainWindow.goToTab("Settings");
        }

        stage.setScene(new Scene(mainWindow.getVisualComponent(), 800, 600));
        stage.setResizable(false);
        stage.setTitle("Osu Background Replacer");
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.getScene().setFill(null);
        stage.show();
    }

    @Override
    public void exit() {
        Platform.exit();
    }

    @Override
    public void minimise() {
        stage.setIconified(true);
    }

    @Override
    public void workStarted() {
        if (startupComplete) {
            Platform.runLater(() -> mainWindow.loadingEnbled(true));
        }
    }
    @Override
    public void workFinished() {
        if (startupComplete) {
            Platform.runLater(() -> mainWindow.loadingEnbled(false));
        }
    }

    @Override
    public void workProgress(double percentage) {
        if (startupComplete) {
            Platform.runLater(() -> mainWindow.setLoadingPercentage(percentage));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
