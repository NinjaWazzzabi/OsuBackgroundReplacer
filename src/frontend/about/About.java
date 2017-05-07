package frontend.about;

import com.jfoenix.controls.JFXButton;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;

/**
 * Created by Anthony on 07/05/2017.
 */
public class About{
    private Parent visualComponent;

    @FXML
    private JFXButton exit;
    @FXML
    private AnchorPane topSection;

    private double xOffset;
    private double yOffset;


    public About(){

        FXMLLoader loader = new FXMLLoader(getClass().getResource("about.fxml"));
        loader.setController(this);
        try {
            this.visualComponent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        initializeActionListeners();

        Stage stage = new Stage();
        stage.setTitle("About section");
        stage.setScene(new Scene(visualComponent, 450, 250));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(false);
        stage.show();
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
                Stage stage = (Stage) topSection.getScene().getWindow();
                stage.close();
            }
        });
    }
}