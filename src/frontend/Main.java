package frontend;

import backend.OsuBackgroundHandlerFactory;
import backend.OsuBackgroundHandlers;
import backend.WorkListener;
import frontend.about.About;
import frontend.mainscreen.MainScreen;
import frontend.mainscreen.MainScreenListener;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main extends Application implements MainScreenListener, WorkListener{

    private Stage stage;
    private MainScreen mainScreen;
    private OsuBackgroundHandlers obh;

    private String saveFolder;
    private String imageFile;


    @Override
    public void start(Stage stage) throws Exception{
        this.stage = stage;
        initializeBackend();
        initializeRoot();
        initializeStage();
    }
    private void initializeBackend(){
        //Creates backend and adds this as listener.
        obh = OsuBackgroundHandlerFactory.getOsuBackgroundHandler();
        obh.addWorkListener(this);

        //Autosearches after osu install directory.
        try {
            obh.findOsuDirectory();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            //TODO Alert user to manually find osu directory
        }
    }
    private void initializeRoot(){
        //Creates MainScreen
        mainScreen = new MainScreen();
        mainScreen.addListener(this);

        //Sets path text if there is a osu Path in the backend.
        try {
            String path = obh.getOsuAbsolutePath();
            mainScreen.setOsuPathText(path);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            //TODO Alert about no osu installation found
        }
    }
    private void initializeStage(){
        stage.setScene(new Scene(mainScreen.getVisualComponent(), 800, 600));
        stage.setTitle("Hello World");
        stage.setResizable(false);
        stage.setTitle("Osu Background Replacer");
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.getScene().setFill(null);
        stage.getScene().getRoot().setOpacity(0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(325),stage.getScene().getRoot());
        fadeIn.setToValue(1);
        stage.show();
        fadeIn.play();
    }


    @Override
    public void exitPressed() {
        FadeTransition ft = new FadeTransition(Duration.millis(250),stage.getScene().getRoot());
        ft.setToValue(0);
        ft.setOnFinished(event -> Platform.exit());
        ft.play();
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
            //TODO alert couldn't replace all
        }
    }
    @Override
    public void removeAll() {
        obh.removeAll();
    }


    @Override
    public void installationBrowse() {
        String path = folderBrowseExplorer();

        //Updates both backend and frontend about new path only if it's a valid path.
        try {
            obh.setDirectory(path);
            mainScreen.setOsuPathText(path);
        } catch (IOException e) {
            //TODO Handle if new path is invalid.
            e.printStackTrace();
        }
    }
    @Override
    public void imageBrowse() {
        imageFile = fileBrowseExplorer();

        if (imageFile != null){
            mainScreen.setImageLocationText(imageFile);
        } else {
            mainScreen.setImageLocationText("No image specified");
        }

    }
    @Override
    public void saveBrowse() {
        saveFolder = folderBrowseExplorer();
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
        File selectedDirectory = chooser.showOpenDialog(stage);

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
        File selectedDirectory = chooser.showDialog(stage);

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
