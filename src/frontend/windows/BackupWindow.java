package frontend.windows;

import backend.BackupManager.BackupManager;
import backend.beatmapcore.Beatmap;
import backend.osucore.OsuDirectory;
import backend.utility.WorkObservers;
import com.jfoenix.controls.JFXTextField;
import frontend.screens.BackupPrompt;
import frontend.screens.BackupPromptListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;

import java.io.File;
import java.io.FileNotFoundException;
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

    private final OsuDirectory osu;
    private BackupManager backupManager;

    public BackupWindow(OsuDirectory osu) {
        super(FXML_LOCATION);

        errorMessage.setOpacity(0);

        this.osu = osu;

        backupManager = new BackupManager(osu);

        if (!backupManager.isBackupExists()){
            new BackupPrompt(this);
        } else if (!backupManager.allIsBackedUp()) {
            new BackupPrompt(this)
                    .setTitle("Missing backups")
                    .setText("There are beatmaps that are not  backed up yet. Would you like to try to save the beatmap images?");
        }

        autoUpdateLists();
    }
    private void autoUpdateLists(){
        setBackedUpVisualList(backupManager.getBackedUpFolders());
        setNonBackedUpVisualList(backupManager.getMissingBackups());
    }
    private void setBackedUpVisualList(List<File> folders) {
        List<String> backedUpNames = new ArrayList<>();

        for (File folder : folders) {
            backedUpNames.add(folder.getName());
        }
        backedUpNames.add("");

        setList(backedUp, backedUpNames);
    }
    private void setNonBackedUpVisualList(List<Beatmap> beatmaps) {
        ArrayList<String> texts = new ArrayList<>();

        for (Beatmap beatmap : beatmaps) {
            texts.add(beatmap.getFolderName());
        }
        texts.add("");

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
        if (osu.getOsuInstallation().installationFound()) {
            backupManager.runBackup();
            refreshAll();
        }
    }

    @FXML
    void restore(ActionEvent event){
        if (osu.getOsuInstallation().installationFound()) {
            try {
                backupManager.restoreImages();
            } catch (FileNotFoundException e) {
                installationNotFound();
            }
            refreshAll();
        }
    }


    @FXML
    void refresh(ActionEvent event){
        refreshAll();
    }

    private void refreshAll(){
        try {
            backupManager.refresh();
        } catch (FileNotFoundException e) {
            installationNotFound();
        }
        autoUpdateLists();
    }


    private void installationNotFound() {
        notBackedUp.getItems().clear();
        notBackedUp.getItems().add("OSU INSTALLATION NOT FOUND");
        backedUp.getItems().clear();
        backedUp.getItems().add("OSU INSTALLATION NOT FOUND");
    }

    public WorkObservers getWorkListeners(){
        return backupManager.getWorkListeners();
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
