package frontend.windows;

import backend.osucore.OsuDirectory;
import com.jfoenix.controls.JFXTextField;
import com.sun.istack.internal.Nullable;
import frontend.screens.About;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class SettingsWindow extends WindowBase{

    private static final String FXML_LOCATION = "/fxml/settings.fxml";

    private final OsuDirectory obh;
    private File lastFolder = null;

    @FXML private JFXTextField osuFolderLocation;
    @FXML private Text errorMessage;

    public SettingsWindow(OsuDirectory obh) {
        super(FXML_LOCATION);

        this.errorMessage.setOpacity(0);
        this.obh = obh;
        osuFolderLocation.focusedProperty().addListener((observable, oldValue, newValue) -> setOsuInstallation(new File(osuFolderLocation.getText())));
        osuFolderLocation.setOnKeyTyped(event -> {
          setOsuInstallation(new File(osuFolderLocation.getText() + event.getText()));
        });
        osuFolderLocation.setText(obh.getOsuInstallation().getDirectoryPath().getAbsolutePath());
        osuFolderLocation.setDisable(true);
        if (!obh.getOsuInstallation().installationFound()) {
            showError("No osu installation found");
        }
    }

    private void showError(String text) {
        errorMessage.setText(text);
        errorMessage.setOpacity(1);
    }

    private void hideError(){
        errorMessage.setOpacity(0);
    }

    @FXML
    void browseInstallation(ActionEvent event) {
        File file = exeBrowseExplorer();

        if (file != null) {
            setOsuInstallation(file);
        }
    }
    /**
     * Opens a file browser.
     *
     * @return {@link File} to the chosen path.
     */
    @Nullable
    private File exeBrowseExplorer() {
        //Create directory chooser
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Find osu!.exe file");

        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("exe files (*.exe)", "*.exe");
        chooser.getExtensionFilters().addAll(extFilterJPG);

        File directory;
        //If there's an already last folderstart from there, otherwise at user home.
        if (lastFolder != null) {
            directory = lastFolder;
        } else {
            directory = new File(System.getProperty("user.home"));
        }

        //Sets initial directory and shows directory chooser
        File defaultDirectory = directory;
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showOpenDialog(new Stage());

        //Saves only if the user selected a folder and didn't just close down the window.
        if (selectedDirectory != null) {
            lastFolder = selectedDirectory;
        }

        return selectedDirectory;
    }

    @FXML
    private void about(ActionEvent event) {
        new About();
    }

    @FXML
    void osuFolderLocationAction(ActionEvent event) {
        setOsuInstallation(new File(osuFolderLocation.getText()));
    }
    private void setOsuInstallation(File file) {
        boolean isValid = false;

        if (file != null) {

            isValid = obh.getOsuInstallation().setOsuFile(file);

            if (!isValid) {
                isValid = obh.getOsuInstallation().setOsuDirectory(file);
            }

        }

        if (isValid) {
            osuFolderLocation.setText(obh.getOsuInstallation().getDirectoryPath().getAbsolutePath());
        } else {
            showError("Not a valid osu installation");
        }
    }
}
