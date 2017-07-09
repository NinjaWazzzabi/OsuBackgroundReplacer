package frontend.windows;

import backend.osubackgroundhandler.IOsuBackgroundHandler;
import com.jfoenix.controls.JFXTextField;
import com.sun.istack.internal.Nullable;
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

    private final IOsuBackgroundHandler obh;
    private String lastFolder = null;

    @FXML private JFXTextField osuFolderLocation;
    @FXML private Text errorMessage;

    public SettingsWindow(IOsuBackgroundHandler obh) {
        super(FXML_LOCATION);

        this.errorMessage.setOpacity(0);
        this.obh = obh;
        osuFolderLocation.focusedProperty().addListener((observable, oldValue, newValue) -> setOsuInstallation(osuFolderLocation.getText()));
        osuFolderLocation.setOnKeyTyped(event -> {
          setOsuInstallation(osuFolderLocation.getText() + event.getText());
        });
        osuFolderLocation.setText(obh.getOsuAbsolutePath());
        osuFolderLocation.setDisable(true);
        if (!obh.installationFound()) {
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
        String tempPath = exeBrowseExplorer();

        if (tempPath != null) {
            setOsuInstallation(tempPath);
        }
    }
    /**
     * Opens a folder browser.
     *
     * @return String to the chosen path.
     */
    @Nullable
    private String exeBrowseExplorer() {
        //Create directory chooser
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose .exe file");
        String dir;

        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("exe files (*.exe)", "*.exe");
        chooser.getExtensionFilters().addAll(extFilterJPG);

        //If there's an already last folderstart from there, otherwise at user home.
        if (lastFolder != null) {
            dir = lastFolder;
        } else {
            dir = System.getProperty("user.home");
        }

        //Sets initial directory and shows directory chooser
        File defaultDirectory = new File(dir);
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showOpenDialog(new Stage());

        //Saves only if the user selected a folder and didn't just close down the window.
        String folderPath = null;
        if (selectedDirectory != null) {
            folderPath = selectedDirectory.getAbsolutePath().replace("\\", "/");
            lastFolder = new File(folderPath).getParent();
        }
        return folderPath;
    }

    @FXML
    void osuFolderLocationAction(ActionEvent event) {
        setOsuInstallation(osuFolderLocation.getText());
    }
    private void setOsuInstallation(String path) {
        boolean isValid = false;

        if (path != null) {
            try {
                obh.setOsuFile(path);
                isValid = true;
            } catch (IOException ignored) {}

            if (!isValid) {
                try {
                    obh.setOsuDirectory(path);
                    isValid = true;
                } catch (IOException ignored) {}
            }

        }

        if (isValid) {
            osuFolderLocation.setText(obh.getOsuAbsolutePath());
        } else {
            showError("Not a valid osu installation");
        }
    }
}
