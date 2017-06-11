package frontend.screens;

import com.jfoenix.controls.JFXButton;
import javafx.animation.FadeTransition;
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

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Anthony on 07/05/2017.
 */
public class About{
    private Parent visualComponent;

    @FXML
    private JFXButton exit;
    @FXML
    private AnchorPane topSection;
    @FXML
    private Text linkText;

    private double xOffset;
    private double yOffset;


    public About(){

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/about.fxml"));
        loader.setController(this);
        try {
            this.visualComponent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        initializeActionListeners();

        Stage stage = new Stage();
        stage.setTitle("About section");
        stage.setScene(new Scene(visualComponent, 600, 300));

        /*
        There's currently a bug in the code that's creating
        a white glow when closing or opening the about tab.
        This bug is now a feature, because I like it.

        If by any means you would want to fade it to transparent
        instead of white, then change the "StageStyle.UNDECORATED"
        to "StageStyle.TRANSPARENT".
         */
        stage.initStyle(StageStyle.UNDECORATED);
        stage.getScene().setFill(null);
        stage.setResizable(false);

        visualComponent.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(250),visualComponent);
        fadeIn.setToValue(1);

        stage.show();
        fadeIn.play();

    }

    private void initializeActionListeners() {
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
                FadeTransition fadeOut = new FadeTransition(Duration.millis(250), visualComponent);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(event1 -> {
                    Stage stage = (Stage) topSection.getScene().getWindow();
                    stage.close();
                });
                fadeOut.play();
            }
        });


        //linkText text pressed (They visited my github page ^^)
        linkText.setOnMouseClicked(event -> {
                    try {
                        Desktop.getDesktop().browse(new URI("https://github.com/NinjaWazzzabi/OsuBackgroundReplacer"));
                    } catch (IOException | URISyntaxException e1) {
                        e1.printStackTrace();
                    }
                }
        );
    }

}
