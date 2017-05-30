package frontend;

import backend.osubackgroundhandler.OsuBackgroundHandler;
import backend.osubackgroundhandler.IOsuBackgroundHandler;
import backend.osubackgroundhandler.WorkListener;
import com.sun.istack.internal.Nullable;
import frontend.about.About;
import frontend.backupprompt.BackupPrompt;
import frontend.backupprompt.BackupPromptListener;
import frontend.loadingscreen.Loading;
import frontend.mainscreen.MainScreen;
import frontend.mainscreen.MainScreenListener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main extends Application implements MainScreenListener, WorkListener, BackupPromptListener{

    private MainScreen mainScreen;
    private IOsuBackgroundHandler obh;

    private String saveFolder;
    private String imagePath;

    @Override
    public void start(Stage stage) {
        //Creates loading screen
        Loading loading = new Loading();

        //Creates main screen
        mainScreen = new MainScreen();
        mainScreen.addListener(this);

        try {
            initializeBackend();
            mainScreen.setOsuPathText(obh.getOsuAbsolutePath());
        } catch (FileNotFoundException e) {
            mainScreen.promptErrorText("No osu found, find it manually below");
        }

        //Closes loading screen
        loading.close();

        new BackupPrompt(this);
    }

    /**
     * Starts up the backend.
     * @throws FileNotFoundException if osu installation wasn't found.
     */
    private void initializeBackend() throws FileNotFoundException {
        //Creates backend and adds this as listener.
        obh = new OsuBackgroundHandler();
        obh.addWorkListener(this);

        //Auto searches after osu install directory.
        obh.findOsuDirectory();
    }


    @Override
    public void exitPressed() {
        Platform.exit();
    }
    @Override
    public void saveAll() {
        if (saveFolder != null && saveFolder.length() > 1) {
            try {
                obh.saveAll(saveFolder);
            } catch (IOException e) {
                mainScreen.promptErrorText(e.getMessage());
            }
        } else {
            mainScreen.promptErrorText("No save folder specified");
        }
    }
    @Override
    public void replaceAll() {

        if (imagePath != null && imagePath.length() > 1){
            //String imageName, String imagePath
            File file = new File(imagePath);

            try {
                obh.replaceAll(
                        file.getName(),
                        file.getParent()
                );
            } catch (IOException e) {
                mainScreen.promptErrorText(e.getMessage());
            }
        } else {
            mainScreen.promptErrorText("No image chosen");
        }

    }
    @Override
    public void removeAll() {
        obh.removeAll();
    }


    @Override
    public void installationBrowse() {
        //Tries to get path from user
        String tempPath = exeBrowseExplorer();

        //Updates both backend and frontend about new path only if it's a valid path.
        if (tempPath != null){
            try {
                obh.setOsuFile(tempPath);
                mainScreen.setOsuPathText(tempPath);
            } catch (IOException e) {
                mainScreen.promptErrorText("Not a valid osu installation");
            }
        }
    }
    @Override
    public void imageBrowse() {
        String tempImagePath = imageFileBrowseExplorer();

        if (tempImagePath != null){
            imagePath = tempImagePath;
            mainScreen.setImageLocation(imagePath);
        } else if (imagePath == null) {
            mainScreen.setImageLocation("No image specified");
        }

    }
    @Override
    public void saveBrowse() {
        String tempSaveFolder = folderBrowseExplorer();


        if (tempSaveFolder != null){
            saveFolder = tempSaveFolder;
            mainScreen.setSavePathText(saveFolder);
        } else if (saveFolder == null){
            mainScreen.setSavePathText("No folder specified");
        }
    }
    @Override
    public void about() {
            new About();
    }

    @Override
    public void osuFolderLocationChange(String path) {
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

        if (!isValid){
            mainScreen.promptErrorText("Not a valid osu installation");
        }
    }
    @Override
    public void imageLocationChange(String path) {
        imagePath = path;
    }
    @Override
    public void savePathChange(String path) {
        saveFolder = path;
    }


    private String lastFolder = null;
    /**
     * Opens a file browser in explorer.
     * @return path to file chosen.
     * @throws NullPointerException if user doesn't choose a file.
     */
    @Nullable
    private String imageFileBrowseExplorer() {
        //Create directory chooser
        FileChooser chooser = new FileChooser();

        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        chooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);

        chooser.setTitle("Choose image file");
        String dir;

        //If there's an already selected osu directory start from there, otherwise at user home.
        if (lastFolder != null){
            dir = lastFolder;
        } else {
            dir = System.getProperty("user.home");
        }

        //Sets initial directory and shows directory chooser
        File defaultDirectory = new File(dir);
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showOpenDialog(new Stage());

        //Saves only if the user selected a folder and didn't just close down the window.
        String filePath = null;
        if (selectedDirectory != null) {
            filePath = selectedDirectory.getAbsolutePath().replace("\\","/");
            lastFolder = new File(filePath).getParent();
        }

        return filePath;
    }
    /**
     * Opens a folder browser.
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
        if (lastFolder != null){
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
            folderPath = selectedDirectory.getAbsolutePath().replace("\\","/");
            lastFolder = new File(folderPath).getParent();
        }
        return folderPath;
    }

    private String folderBrowseExplorer(){
        //Create directory chooser
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose .exe file");
        String dir;

        //If there's an already last folderstart from there, otherwise at user home.
        if (lastFolder != null){
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
            folderPath = selectedDirectory.getAbsolutePath().replace("\\","/");
            lastFolder = folderPath;
        }
        return folderPath;
    }


    @Override
    public void alertWorkStarted() {
        mainScreen.workStarted();
    }
    @Override
    public void alertWorkFinished() {
        mainScreen.workFinished();
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void backupYes() {
        System.out.println("yes");
    }

    @Override
    public void backupNo() {
        System.out.println("no");
    }
}
