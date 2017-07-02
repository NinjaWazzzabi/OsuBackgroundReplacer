package frontend.windows;

import backend.BackupManager.BackupManager;
import backend.core.Beatmap;
import backend.osubackgroundhandler.IOsuBackgroundHandler;
import com.jfoenix.controls.JFXTextField;
import frontend.screens.BackupPrompt;
import frontend.screens.BackupPromptListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BackupWindow extends WindowBase implements BackupPromptListener {

    private static String FXML_LOCATION = "/fxml/save.fxml";

    @FXML
    private ListView<Text> notBackedUp;
    @FXML
    private ListView<Text> backedUp;

    @FXML private JFXTextField savePath;
    @FXML private Text errorMessage;

    private final IOsuBackgroundHandler obh;
    private final BackupManager backupManager;

    public BackupWindow(IOsuBackgroundHandler obh) {
        super(FXML_LOCATION);

        errorMessage.setOpacity(0);

        this.obh = obh;

        backupManager = new BackupManager(obh);

        if (!backupManager.allIsBackedUp()){
            new BackupPrompt(this);
        }



        setBackedUpVisualList(backupManager.getBackedUpFolders());
        setNonBackedUpVisualList(backupManager.getMissingBackups());
    }

    private void setBackedUpVisualList(List<File> folders) {
        List<String> backedUpNames = new ArrayList<>();

        for (File folder : folders) {
            backedUpNames.add(folder.getName());
        }

        backedUp.getItems().clear();
        for (String text : backedUpNames) {
            Text textToAdd = new Text(text);
            textToAdd.setStyle("-fx-fill: #eeeeee;");
            backedUp.getItems().add(textToAdd);
        }
    }

    private void setNonBackedUpVisualList(List<Beatmap> beatmaps) {
        ArrayList<String> texts = new ArrayList<>();

        for (Beatmap beatmap : beatmaps) {
            texts.add(beatmap.getFolderName());
        }

        notBackedUp.getItems().clear();
        for (String text : texts) {
            Text textToAdd = new Text(text);
            textToAdd.setStyle("-fx-fill: #eeeeee;");
            notBackedUp.getItems().add(textToAdd);
        }
    }

    @FXML
    void backup(ActionEvent event) {
        backupManager.runBackup();
    }

    @Override
    public void backupYes() {
        backupManager.runBackup();
    }
    @Override
    public void backupNo() {
        //Nothing needs to be done if the user selects no.
    }
}
