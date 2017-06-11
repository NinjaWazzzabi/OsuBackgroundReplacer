package frontend.screens;

import com.jfoenix.controls.JFXSpinner;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;

/**
 * Simple loading screen.
 */
public class Loading {

    private int FADE_LENGTH = 250;

    @FXML
    private Text text;
    @FXML
    private JFXSpinner spinner;
    @FXML
    private AnchorPane anchor;

    private Parent visualComponent;
    private Stage stage;

    public Loading(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/loading.fxml"));
        loader.setController(this);
        try {
            this.visualComponent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage = new Stage();
        stage.setTitle("Loading...");
        stage.setScene(new Scene(visualComponent, 250, 250));
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.getScene().setFill(null);
        stage.setResizable(false);
        stage.setAlwaysOnTop(true);

        visualComponent.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(250),visualComponent);
        ft.setToValue(1);

        stage.show();
        ft.play();
    }


    public void close(){

        //Progressbar fade out animation 
        FadeTransition ftProgressBar = new FadeTransition(Duration.millis(FADE_LENGTH),spinner);
        ftProgressBar.setFromValue(spinner.getOpacity());
        ftProgressBar.setToValue(0);

        //Text fade out and change string of text
        FadeTransition textFadeOut = new FadeTransition(Duration.millis(FADE_LENGTH), text);
        textFadeOut.setFromValue(text.getOpacity());
        textFadeOut.setToValue(0);
        textFadeOut.setOnFinished(event -> text.setText("Finished"));

        //Text delay
        PauseTransition delay = new PauseTransition(Duration.millis(FADE_LENGTH /2));

        //Text fade in animation 
        FadeTransition textFadeIn = new FadeTransition(Duration.millis(FADE_LENGTH),text);
        textFadeIn.setFromValue(0);
        textFadeIn.setToValue(1);

        //Text stay visible 
        PauseTransition textStayVisible = new PauseTransition(Duration.millis(FADE_LENGTH));

        //Combine text animation 
        SequentialTransition textSeq = new SequentialTransition(textFadeOut,delay,textFadeIn,textStayVisible);
        textSeq.setOnFinished(event -> exit());

        textSeq.play();
        ftProgressBar.play();
    }

    private void exit(){
        FadeTransition closeTransition = new FadeTransition(Duration.millis(FADE_LENGTH),visualComponent);
        closeTransition.setToValue(0);
        closeTransition.setOnFinished(event -> stage.close());
        closeTransition.play();
    }
}
