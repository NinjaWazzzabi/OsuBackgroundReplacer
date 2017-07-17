package frontend.windows;

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
public class Loading extends WindowBase {

    private int FADE_LENGTH = 250;

    @FXML private Text text;
    @FXML private JFXSpinner spinner;
    @FXML private AnchorPane anchor;

    private Runnable closingAction;

    public Loading() {
        super("/fxml/loading.fxml");
    }

    public void start(){
        text.setText("0.00%");
        visualComponent.setOpacity(0);
        text.setOpacity(1);
        spinner.setOpacity(1);
        FadeTransition ft = new FadeTransition(Duration.millis(FADE_LENGTH_LONG),visualComponent);
        ft.setToValue(1);
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
        closeTransition.setOnFinished(event -> {
            if (closingAction != null) {
                closingAction.run();
            }
        });
        closeTransition.play();
    }

    public void setPercentage(double percentage){
        String string = String.valueOf(percentage*100);

        if (string.length() > 4) {
            string = string.substring(0,4);
        }

        this.text.setText(string + "%");
    }

    public void setOnClosed(Runnable runnable) {
        closingAction = runnable;
    }
}
