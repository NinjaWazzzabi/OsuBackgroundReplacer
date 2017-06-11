package frontend.windows;

import backend.osubackgroundhandler.IOsuBackgroundHandler;
import com.jfoenix.controls.JFXTextField;
import com.sun.istack.internal.Nullable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SettingsWindow {

    @Getter
    private Parent visualComponent;
    private String lastFolder = null;
    private final IOsuBackgroundHandler obh;
    private List<SettingsWindowListener> listeners;

    @FXML
    private JFXTextField osuFolderLocation;

    public SettingsWindow(IOsuBackgroundHandler obh) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/settings.fxml"));
        loader.setController(this);
        try {
            this.visualComponent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.obh = obh;
        this.listeners = new ArrayList<>();
        osuFolderLocation.focusedProperty().addListener((observable, oldValue, newValue) -> setOsuInstallation(osuFolderLocation.getText()));
    }

    @FXML
    void browseInstallation(ActionEvent event) {
        String tempPath = exeBrowseExplorer();

        if (tempPath != null) {
            try {
                obh.setOsuFile(tempPath);
            } catch (IOException e) {
                listeners.forEach(listener -> listener.errorOccurred("Not a valid osu installation"));
            }
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

        try {
            obh.setOsuFile(path);
            isValid = true;
        } catch (IOException ignored) {
        }

        try {
            obh.setOsuDirectory(path);
            isValid = true;
        } catch (IOException ignored) {
        }

        if (!isValid) {
            listeners.forEach(listener -> listener.errorOccurred("Not a valid osu installation"));
        }
    }

    @FXML
    void removeAll(ActionEvent event) {
        obh.removeAll();
    }


    public void addListener(SettingsWindowListener listener) {
        listeners.add(listener);
    }

    public void removeListener(SettingsWindowListener listener) {
        listeners.remove(listener);
    }
}
