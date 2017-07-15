import backend.osubackgroundhandler.BackgroundManager;
import backend.osubackgroundhandler.BackgroundManagerFrontendTester;
import backend.osubackgroundhandler.IOsuBackgroundHandler;
import backend.osubackgroundhandler.WorkListener;
import frontend.windows.*;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.effect.Effect;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.FileNotFoundException;

public class Main extends Application implements WorkListener, MainWindowListener {

    private MainWindow mainWindow;
    private ReplaceWindow replaceWindow;
    private SingleColourWindow singleColourWindow;
    private SettingsWindow settingsWindow;
    private BackupWindow backupWindow;

    private Stage stage;
    private IOsuBackgroundHandler obh;
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
            obh = new BackgroundManager();
            obh.getWorkListeners().addListener(this);
        });

        backendStartup.start();
        try {
            backendStartup.join();
        } catch (InterruptedException ignored) {}
    }

    private void initializeFrontend(Stage stage){
        replaceWindow = new ReplaceWindow(obh);
        singleColourWindow = new SingleColourWindow(obh);
        settingsWindow = new SettingsWindow(obh);
        backupWindow = new BackupWindow(obh);
        backupWindow.getWorkListeners().addListener(this);

        mainWindow = new MainWindow();
        mainWindow.addListener(this);

        mainWindow.addNewTab("Replace Image", replaceWindow.getVisualComponent());
        mainWindow.addNewTab("Single Colour",singleColourWindow.getVisualComponent());
        mainWindow.addNewTab("Backup", backupWindow.getVisualComponent());
        mainWindow.addNewTab("Settings", settingsWindow.getVisualComponent());

        if (!obh.installationFound()) {
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
    public void alertWorkStarted() {
        if (startupComplete) {
            Platform.runLater(() -> mainWindow.loadingEnbled(true));
        }
    }
    @Override
    public void alertWorkFinished() {
        if (startupComplete) {
            Platform.runLater(() -> mainWindow.loadingEnbled(false));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
