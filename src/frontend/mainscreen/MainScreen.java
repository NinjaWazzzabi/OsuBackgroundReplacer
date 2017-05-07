package frontend.mainscreen;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import frontend.util.VisualComponent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Window;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainScreen implements VisualComponent {
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

    private Parent visualComponent;
    private List<MainScreenListener> listeners;

    private double xOffset;
    private double yOffset;


    public MainScreen(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainScreen.fxml"));
        loader.setController(this);
        try {
            this.visualComponent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        exit.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                listeners.forEach(MainScreenListener::exitPressed);
            }
        });

        //SaveAll button pressed
        saveAll.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                listeners.forEach(MainScreenListener::saveAll);
            }
        });

        //ReplaceAll button pressed
        replaceAll.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                listeners.forEach(MainScreenListener::replaceAll);
            }
        });

        //RemoveAll button pressed
        removeAll.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                listeners.forEach(MainScreenListener::removeAll);
            }
        });

        //BrowseInstallation button clicked
        browseInstallation.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                listeners.forEach(MainScreenListener::installationBrowse);
            }
        });

        //BrowseImage clicked
        browseImage.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                listeners.forEach(MainScreenListener::imageBrowse);
            }
        });

        //BrowseSave clicked
        browseSave.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                listeners.forEach(MainScreenListener::saveBrowse);
            }
        });
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


    @Override
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