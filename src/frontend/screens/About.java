package frontend.screens;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Anthony on 07/05/2017.
 */
public class About {
    private static final int FADE_LENGTH = 100;
    private Parent visualComponent;

    @FXML
    private AnchorPane topSection;

    private double xOffset;
    private double yOffset;


    public About() {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/about.fxml"));
        loader.setController(this);
        try {
            this.visualComponent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        initializeWindowMovement();

        Stage stage = new Stage();
        stage.setTitle("About section");
        stage.setScene(new Scene(visualComponent, 600, 300));

        stage.initStyle(StageStyle.TRANSPARENT);
        stage.getScene().setFill(null);
        stage.setResizable(false);

        visualComponent.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(FADE_LENGTH), visualComponent);
        fadeIn.setToValue(1);

        stage.show();
        fadeIn.play();

    }

    private void initializeWindowMovement() {
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
    }

    @FXML
    void exit(ActionEvent event) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(250), visualComponent);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(event1 -> {
            Stage stage = (Stage) topSection.getScene().getWindow();
            stage.close();
        });
        fadeOut.play();
    }

    @FXML
    void linkTextClicked(MouseEvent event) {
        try {
            Desktop.getDesktop().browse(new URI("https://github.com/NinjaWazzzabi/OsuBackgroundReplacer"));
        } catch (IOException | URISyntaxException e1) {
            e1.printStackTrace();
        }
    }

}
