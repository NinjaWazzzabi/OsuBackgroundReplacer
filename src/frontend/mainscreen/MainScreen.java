package frontend.mainscreen;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainScreen {
    @FXML
    private AnchorPane topSection;
    @FXML
    private JFXButton exit;
    @FXML
    private JFXButton saveAll;
    @FXML
    private JFXButton replaceAll;
    @FXML
    private JFXButton removeAll;
    @FXML
    private JFXButton browseInstallation;
    @FXML
    private JFXTextField osuFolderLocation;
    @FXML
    private JFXButton browseImage;
    @FXML
    private JFXTextField imageLocation;
    @FXML
    private JFXButton browseSave;
    @FXML
    private JFXTextField savePath;

    @FXML
    private JFXSpinner progressBar;
    @FXML
    private Text workDone;

    @FXML
    private JFXButton about;

    private Parent visualComponent;
    private Stage stage;
    private List<MainScreenListener> listeners;

    private double xOffset;
    private double yOffset;


    public MainScreen(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainScreen.fxml"));
        loader.setController(this);
        try {
            this.visualComponent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage = new Stage();
        stage.setScene(new Scene(getVisualComponent(), 800, 600));
        stage.setResizable(false);
        stage.setTitle("Osu Background Replacer");
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.getScene().setFill(null);

        //Fade in
        stage.getScene().getRoot().setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(325),stage.getScene().getRoot());
        fadeIn.setToValue(1);
        stage.show();
        fadeIn.play();

        listeners = new ArrayList<>();
        initializeActionListeners();
    }
    /**
     * Initializes all listeners in the object.
     */
    private void initializeActionListeners(){
        //Screen window movement
        topSection.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Window stage = topSection.getScene().getWindow();
                xOffset = stage.getX() - event.getScreenX();
                yOffset = stage.getY() - event.getScreenY();
            }
        });
        topSection.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Window stage = topSection.getScene().getWindow();
                stage.setX(event.getScreenX() + xOffset);
                stage.setY(event.getScreenY() + yOffset);
            }
        });

        //Exit button pressed
        exit.setOnMouseClicked(event -> {
            FadeTransition ft = new FadeTransition(Duration.millis(250),stage.getScene().getRoot());
            ft.setToValue(0);
            ft.setOnFinished(event1 -> {
                stage.close();
                listeners.forEach(MainScreenListener::exitPressed);
            });
            ft.play();
        });

        //SaveAll button pressed
        saveAll.setOnMouseClicked(event -> listeners.forEach(MainScreenListener::saveAll));

        //ReplaceAll button pressed
        replaceAll.setOnMouseClicked(event -> listeners.forEach(MainScreenListener::replaceAll));

        //RemoveAll button pressed
        removeAll.setOnMouseClicked(event -> listeners.forEach(MainScreenListener::removeAll));

        //BrowseInstallation button clicked
        browseInstallation.setOnMouseClicked(event -> listeners.forEach(MainScreenListener::installationBrowse));

        //BrowseImage clicked
        browseImage.setOnMouseClicked(event -> listeners.forEach(MainScreenListener::imageBrowse));

        //BrowseSave clicked
        browseSave.setOnMouseClicked(event -> listeners.forEach(MainScreenListener::saveBrowse));

        //About clicked
        about.setOnMouseClicked(event -> listeners.forEach(MainScreenListener::about));
    }


    public void setOsuPathText(String path){
        osuFolderLocation.setText(path.replace("/","\\"));
    }
    public void setSavePathText(String path){
        savePath.setText(path);
    }
    public void setImageLocationText(String path) {
        imageLocation.setText(path);
    }

    private final int fadeLength = 500;
    public void workStarted(){
        //Progressbar fade in animation
        FadeTransition progressFade = new FadeTransition(Duration.millis(fadeLength),progressBar);
        progressFade.setFromValue(progressBar.getOpacity());
        progressFade.setToValue(1);
        progressFade.play();

        workDone.getTransforms().clear();
        //Text fade out if it still has some opacity
        FadeTransition textFade = new FadeTransition(Duration.millis(fadeLength/2),progressBar);
        textFade.setFromValue(workDone.getOpacity());
        textFade.setToValue(0);
        textFade.play();
    }

    public void workFinished(){
        //Progressbar fade out animation
        FadeTransition ftProgressBar = new FadeTransition(Duration.millis(fadeLength),progressBar);
        ftProgressBar.setFromValue(progressBar.getOpacity());
        ftProgressBar.setToValue(0);

        //Text delay
        //Text stay visible
        PauseTransition delay = new PauseTransition(Duration.millis(fadeLength/2));

        //Text fade in animation
        FadeTransition textFadeIn = new FadeTransition(Duration.millis(fadeLength),workDone);
        textFadeIn.setFromValue(workDone.getOpacity());
        textFadeIn.setToValue(1);

        //Text stay visible
        PauseTransition textStayVisible = new PauseTransition(Duration.millis(fadeLength*4));

        //Text fade out animation
        FadeTransition textFadeOut = new FadeTransition(Duration.millis(fadeLength),workDone);
        textFadeOut.setFromValue(1);
        textFadeOut.setToValue(0);

        //Combine text animation
        SequentialTransition textSeq = new SequentialTransition(delay,textFadeIn,textStayVisible,textFadeOut);

        textSeq.play();
        ftProgressBar.play();
    }


    public Parent getVisualComponent() {
        return visualComponent;
    }
    /**
     * Adds the listener to the object.
     * @param listener the specified listener.
     */
    public void addListener(MainScreenListener listener){
        listeners.add(listener);
    }
    /**
     * Removes the listener to the object.
     * @param listener the specified listener.
     */
    public void removeListener(MainScreenListener listener){
        listeners.remove(listener);
    }
    /**
     * Removes all the listeners to the object.
     */
    public void clearListeners(){
        listeners.clear();
    }
}