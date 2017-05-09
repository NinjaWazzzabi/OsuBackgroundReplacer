package frontend;

import backend.OsuBackgroundHandlerFactory;
import backend.OsuBackgroundHandlers;
import backend.WorkListener;
import frontend.about.About;
import frontend.loadingScreen.Loading;
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

public class Main extends Application implements MainScreenListener, WorkListener{

    private MainScreen mainScreen;
    private OsuBackgroundHandlers obh;

    private String saveFolder;
    private String imageFile;

    @Override
    public void start(Stage stage) throws Exception{
        //Creates loading screen
        Loading loading = new Loading();

        //Creates main screen
        mainScreen = new MainScreen();
        mainScreen.addListener(this);

        //Creates backend and adds this as listener.
        obh = OsuBackgroundHandlerFactory.getOsuBackgroundHandler();
        obh.addWorkListener(this);

        //Auto searches after osu install directory.
        try {
            obh.findOsuDirectory();
            String path = obh.getOsuAbsolutePath();
            mainScreen.setOsuPathText(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            mainScreen.promptErrorText("No osu found, find it manually below");
        }

        //Closes loading screen
        loading.close();
    }




    @Override
    public void exitPressed() {
        Platform.exit();
    }
    @Override
    public void saveAll() {
        if (saveFolder != null) {
            try {
                obh.saveAll(saveFolder);
            } catch (IOException e) {
                //Alert path invalid
                e.printStackTrace();
            }
        } else {
            mainScreen.setSavePathText("No save folder specified");
        }
    }
    @Override
    public void replaceAll() {
        //String imageName, String imagePath
        File file = new File(imageFile);

        try {
            obh.replaceAll(
                    file.getName(),
                    file.getParent()
            );
        } catch (IOException e) {
            mainScreen.promptErrorText(e.getMessage());
        }
    }
    @Override
    public void removeAll() {
        obh.removeAll();
    }


    @Override
    public void installationBrowse() {
        //Tries to get path from user
        boolean directoryChosen = false;
        String path = "";
        try {
            path = folderBrowseExplorer();
            directoryChosen = true;
        } catch (NullPointerException n){
            //User exited browser without choosing folder
        }

        //Updates both backend and frontend about new path only if it's a valid path.
        if (directoryChosen){
            try {
                obh.setDirectory(path);
                mainScreen.setOsuPathText(path);
            } catch (IOException e) {
                mainScreen.promptErrorText("Not a valid osu installation folder");
            }
        }
    }
    @Override
    public void imageBrowse() {
        try {
            imageFile = fileBrowseExplorer();
        } catch (NullPointerException n){
            //User exited browser without choosing file
        }

        if (imageFile != null){
            mainScreen.setImageLocationText(imageFile);
        } else {
            mainScreen.setImageLocationText("No image specified");
        }

    }
    @Override
    public void saveBrowse() {
        try {
            saveFolder = folderBrowseExplorer();
        } catch (NullPointerException n){
            //User exited browser without choosing folder
        }
        if (saveFolder != null){
            mainScreen.setSavePathText(saveFolder);
        } else {
            mainScreen.setSavePathText("No folder specified");
        }
    }
    @Override
    public void about() {
            new About();
    }


    private String lastFolder = null;
    /**
     * Opens a file browser in explorer.
     * @return path to file chosen.
     * @throws NullPointerException if user doesn't choose a file.
     */
    private String fileBrowseExplorer() throws NullPointerException{
        //Create directory chooser
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose file");
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
            return filePath;
        }

        throw new NullPointerException("No folder chosen");
    }
    /**
     * Opens a folder browser.
     * @return String to the chosen path.
     */
    private String folderBrowseExplorer() throws NullPointerException{
        //Create directory chooser
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose folder");
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
            return folderPath;
        }

        throw new NullPointerException("No folder chosen");
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
}
