package frontend.windows;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.util.Duration;
import lombok.Getter;

import java.io.IOException;

public abstract class WindowBase {

    static int FADE_LENGTH_MEDIUM = 250;
    static int FADE_LENGTH_LONG = 500;
    static int FADE_LENGTH_SHORT = 100;

    @Getter protected Parent visualComponent;

    WindowBase(String fxmlFileLocation){
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileLocation));
        loader.setController(this);
        try {
            this.visualComponent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void flashRevealVisualComponent(Node visualComponent, int flashTime) {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(flashTime / 5), visualComponent);
        fadeIn.setToValue(1);

        PauseTransition longHold = new PauseTransition(Duration.millis(flashTime * 4));
        PauseTransition shortHold = new PauseTransition(Duration.millis(flashTime / 2));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(flashTime / 5), visualComponent);
        fadeOut.setToValue(0);

        FadeTransition fadeOutLong = new FadeTransition(Duration.millis(flashTime / 2), visualComponent);
        fadeOutLong.setToValue(0);

        SequentialTransition blip = new SequentialTransition(fadeIn, shortHold, fadeOut);

        SequentialTransition seq = new SequentialTransition(blip, shortHold, fadeIn, longHold, fadeOutLong);
        seq.play();
    }
    void hideVisualComponent(Node visualComponent, int length) {
        if (visualComponent.getOpacity() > 0) {
            FadeTransition fadeOutLong = new FadeTransition(Duration.millis(length), visualComponent);
            fadeOutLong.setToValue(0);
            fadeOutLong.play();
        }
    }

    void enlarge(Node visualComponent) {
        visualComponent.setScaleX(1.5);
        visualComponent.setScaleY(1.5);
    }

    void shrink(Node visualComponent) {
        visualComponent.setScaleX(0.5);
        visualComponent.setScaleY(0.5);
    }

    void normalSize(Node visualComponent) {
        visualComponent.setScaleX(1);
        visualComponent.setScaleY(1);
    }
}
