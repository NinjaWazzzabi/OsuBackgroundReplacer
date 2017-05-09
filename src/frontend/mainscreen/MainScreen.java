package frontend.mainscreen;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
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
    private JFXButton exit;
    @FXML
    private JFXButton about;

    @FXML
    private JFXButton saveAll;
    @FXML
    private JFXButton replaceAll;
    @FXML
    private JFXButton removeAll;

    @FXML
    private JFXSpinner progressBar;
    @FXML
    private Text workDone;
    @FXML
    private Text errorText;

    private Stage stage;
    private Parent visualComponent;
    private List<MainScreenListener> listeners;

    private double xOffset;
    private double yOffset;

    /**
     * A menu screen is created and creates a new stage to use.
     */
    public MainScreen(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainScreen.fxml"));
        loader.setController(this);
        try {
            this.visualComponent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage = new Stage();
        stage.setScene(new Scene(visualComponent, 800, 600));
        stage.setResizable(false);
        stage.setTitle("Osu Background Replacer");
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.getScene().setFill(null);

        listeners = new ArrayList<>();
        initializeActionListeners();

        //Fade in
        visualComponent.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(325),visualComponent);
        fadeIn.setToValue(1);
        stage.show();
        fadeIn.play();
    }
    /**
     * Initializes all listeners in the object.
     */
    private void initializeActionListeners(){
        //Screen window movement
        topSection.setOnMousePressed(event -> {
            Window stage = topSection.getScene().getWindow();
            xOffset = stage.getX() - event.getScreenX();
            yOffset = stage.getY() - event.getScreenY();
        });
        topSection.setOnMouseDragged(event -> {
            Window stage = topSection.getScene().getWindow();
            stage.setX(event.getScreenX() + xOffset);
            stage.setY(event.getScreenY() + yOffset);
        });

        initializeBrowses();
        initializeButtons();
        initializeTextField();

    }

    private void initializeTextField(){
        osuFolderLocation.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue){
                listeners.forEach(listener -> listener.osuFolderLocationChange(osuFolderLocation.getText()));
            }
        });
        osuFolderLocation.setOnAction(event -> listeners.forEach(
                listener -> listener.osuFolderLocationChange(osuFolderLocation.getText())
        ));

        imageLocation.setOnKeyTyped(event -> listeners.forEach(
                listener -> listener.imageLocationChange(imageLocation.getText())
        ));


        savePath.setOnKeyTyped(event -> listeners.forEach(
                listener -> listener.savePathChange(savePath.getText())
        ));
    }
    /**
     * Initializes all button listeners in the object.
     */
    private void initializeButtons(){
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

        //About clicked
        about.setOnMouseClicked(event -> listeners.forEach(MainScreenListener::about));
    }
    /**
     * Initializes all browse listeners in the object.
     */
    private void initializeBrowses(){
        //BrowseInstallation button clicked
        browseInstallation.setOnMouseClicked(event -> listeners.forEach(MainScreenListener::installationBrowse));

        //BrowseImage clicked
        browseImage.setOnMouseClicked(event -> listeners.forEach(MainScreenListener::imageBrowse));

        //BrowseSave clicked
        browseSave.setOnMouseClicked(event -> listeners.forEach(MainScreenListener::saveBrowse));
    }


    /**
     * Sets the text of the osuFolderLocation textfield.
     * @param text text that will be displayed.
     */
    public void setOsuPathText(String text){
        osuFolderLocation.setText(text.replace("/","\\"));
    }
    /**
     * Sets the text of the imageLocation textfield.
     * @param text text that will be displayed.
     */
    public void setSavePathText(String text){
        savePath.setText(text);
    }
    /**
     * Sets the text of the savePath textfield.
     * @param text text that will be displayed.
     */
    public void setImageLocationText(String text) {
        imageLocation.setText(text);
    }

    /**
     * Progress info fade time constant.
     */
    private final int fadeLength = 500;

    /**
     * Starts to fade in the spinning circle
     */
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
    /**
     * Fades out the spinning circle and displays the text "Finished" for a breif moment.
     */
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

    private Transition lastAnimation = null;
    public void promptErrorText(String text){
        if (lastAnimation != null){
            lastAnimation.stop();
        }
        errorText.setText(text);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(fadeLength),errorText);
        fadeIn.setToValue(1);

        PauseTransition delay = new PauseTransition(Duration.millis(fadeLength*4));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(fadeLength),errorText);
        fadeOut.setToValue(0);

        SequentialTransition combine = new SequentialTransition(fadeIn,delay,fadeOut);
        combine.play();
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