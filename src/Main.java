import backend.osubackgroundhandler.BackgroundManagerFrontendTester;
import backend.osubackgroundhandler.IOsuBackgroundHandler;
import backend.osubackgroundhandler.WorkListener;
import frontend.windows.*;
import frontend.customfadeeffects.BlurFade;
import frontend.screens.Loading;

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

    private IOsuBackgroundHandler obh;
    private Loading loading;

    @Override
    public void start(Stage stage) {
        initializeBackend();


        mainWindow = new MainWindow();
        mainWindow.addListener(this);


        replaceWindow = new ReplaceWindow(obh);

        singleColourWindow = new SingleColourWindow(obh);

        settingsWindow = new SettingsWindow(obh);

        backupWindow = new BackupWindow(obh);

        mainWindow.addNewTab("Replace Image", replaceWindow.getVisualComponent());
        mainWindow.addNewTab("Single Colour",singleColourWindow.getVisualComponent());
        mainWindow.addNewTab("Backup", backupWindow.getVisualComponent());
        mainWindow.addNewTab("Settings", settingsWindow.getVisualComponent());

        if (obh.getOsuAbsolutePath().equals("C:/")) {
            mainWindow.promtError("No osu found, find it manually below");
            mainWindow.goToTab("Settings");
        }

        stage.setScene(new Scene(mainWindow.getVisualComponent(), 800, 600));
        stage.setResizable(false);
        stage.setTitle("Osu Background Replacer");
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.getScene().setFill(null);
        stage.show();
    }
    /**
     * Starts up the backend.
     * @throws FileNotFoundException if osu installation wasn't found.
     */
    private void initializeBackend() {
        obh = new BackgroundManagerFrontendTester();
        obh.addWorkListener(this);
    }

    @Override
    public void exit() {
        Platform.exit();
    }

    @Override
    public void alertWorkStarted() {
        loading = new Loading();
        BlurFade darkening = new BlurFade();
        darkening.fadeIn();
        mainWindow.getVisualComponent().setDisable(true);
        mainWindow.getVisualComponent().setEffect(darkening);
    }
    @Override
    public void alertWorkFinished() {
        loading.close();
        mainWindow.getVisualComponent().setDisable(false);

        Effect effect = mainWindow.getVisualComponent().getEffect();
        if (effect instanceof BlurFade) {
            BlurFade blurEffect = (BlurFade) effect;
            blurEffect.fadeOut();
        } else {
            mainWindow.getVisualComponent().setEffect(null);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
