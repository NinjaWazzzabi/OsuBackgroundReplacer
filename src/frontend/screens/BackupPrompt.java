package frontend.screens;

import com.jfoenix.controls.JFXButton;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A screen that warns the user about missing backup files. Notifies and closes on button click.
 */
public class BackupPrompt {

    @FXML
    private AnchorPane topSection;
    @FXML
    private JFXButton exit;

    @FXML
    private JFXButton yes;
    @FXML
    private JFXButton no;

    private double xOffset;
    private double yOffset;

    private Parent visualComponent;
    private List<BackupPromptListener> listeners;

    public BackupPrompt(BackupPromptListener listener){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/backupPrompt.fxml"));
        loader.setController(this);

        try {
            this.visualComponent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        xOffset = 0;
        yOffset = 0;
        listeners = new ArrayList<>();
        listeners.add(listener);

        Stage stage = new Stage();
        stage.setTitle("About section");
        stage.setScene(new Scene(visualComponent, 450, 200));

        stage.initStyle(StageStyle.TRANSPARENT);
        stage.getScene().setFill(null);
        stage.setResizable(false);
        stage.setAlwaysOnTop(true);

        visualComponent.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(250),visualComponent);
        fadeIn.setToValue(1);

        stage.show();
        fadeIn.play();

        initializeActionListeners();
    }

    private void initializeActionListeners() {
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

        //Exit button clicked
        exit.setOnMouseClicked(event -> {
            close();
        });

        //Yes button clicked
        yes.setOnMouseClicked(event -> {
            listeners.forEach(BackupPromptListener::backupYes);
            close();
        });

        //No button clicked
        no.setOnMouseClicked(event -> {
            listeners.forEach(BackupPromptListener::backupNo);
            close();
        });
    }

    public void addListener(BackupPromptListener listener){
        listeners.add(listener);
    }
    public void removeListener(BackupPromptListener listener){
        listeners.remove(listener);
    }

    private void close(){
        listeners.clear();
        FadeTransition fadeOut = new FadeTransition(Duration.millis(250), visualComponent);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(event1 -> {
            Stage stage = (Stage) topSection.getScene().getWindow();
            stage.close();
        });
        fadeOut.play();
    }
}
