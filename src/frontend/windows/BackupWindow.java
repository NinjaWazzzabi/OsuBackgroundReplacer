package frontend.windows;


import backend.osubackgroundhandler.IOsuBackgroundHandler;
import com.jfoenix.controls.JFXTextField;
import frontend.screens.BackupPrompt;
import frontend.screens.BackupPromptListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import lombok.Getter;


import java.io.File;
import java.io.IOException;

public class BackupWindow implements BackupPromptListener {

    private final IOsuBackgroundHandler obh;
    @Getter
    private Parent visualComponent;
    @FXML
    private JFXTextField savePath;
    @FXML
    private Text errorMessage;
    private String saveFolder;
    private String lastFolder;

    public BackupWindow(IOsuBackgroundHandler obh) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/save.fxml"));
        loader.setController(this);
        try {
            this.visualComponent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        errorMessage.setOpacity(0);

        this.obh = obh;
        savePath.focusedProperty().addListener((observable, oldValue, newValue) -> {
            saveFolder = savePath.getText();
        });


        //TODO Check if backup folder is found

        //If no backup is found
        new BackupPrompt(this);
    }

    @FXML
    void browseSave(ActionEvent event) {
        String tempSaveFolder = folderBrowseExplorer();


        if (tempSaveFolder != null) {
            saveFolder = tempSaveFolder;
            savePath.setText(saveFolder);
        } else if (saveFolder == null) {
            savePath.setText("No folder specified");
        }
    }

    private String folderBrowseExplorer() {
        //Create directory chooser
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose .exe file");
        String dir;

        //If there's an already last folderstart from there, otherwise at user home.
        if (lastFolder != null) {
            dir = lastFolder;
        } else {
            dir = System.getProperty("user.home");
        }

        //Sets initial directory and shows directory chooser
        File defaultDirectory = new File(dir);
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showDialog(new Stage());

        //Saves only if the user selected a folder and didn't just close down the window.
        String folderPath = null;
        if (selectedDirectory != null) {
            folderPath = selectedDirectory.getAbsolutePath().replace("\\", "/");
            lastFolder = folderPath;
        }
        return folderPath;
    }

    @FXML
    void saveAll(ActionEvent event) {
        if (saveFolder != null && saveFolder.length() > 1) {
            try {
                obh.saveAll(saveFolder);
            } catch (IOException e) {
                //TODO Handle error message
            }
        } else {
            //TODO Handle no save folder specified
        }
    }

    @FXML
    void savePathAction(ActionEvent event) {
        saveFolder = savePath.getText();
    }

    @Override
    public void backupYes() {
        //TODO implement
    }

    @Override
    public void backupNo() {
        //TODO Implement
    }
}
