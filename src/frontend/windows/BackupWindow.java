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
    private ListView<String> notBackedUp;
    @FXML
    private ListView<String> backedUp;

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

        setList(backedUp, backedUpNames);
    }
    private void setNonBackedUpVisualList(List<Beatmap> beatmaps) {
        ArrayList<String> texts = new ArrayList<>();

        for (Beatmap beatmap : beatmaps) {
            texts.add(beatmap.getFolderName());
        }

        setList(notBackedUp, texts);
    }
    private <T> void setList(ListView<T> listView,List<T> list){
        listView.getItems().clear();
        for (T item : list) {
            listView.getItems().add(item);
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
