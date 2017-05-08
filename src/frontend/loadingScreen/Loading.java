package frontend.loadingScreen;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;

/**
 * Simple loading screen.
 */
public class Loading {

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

        visualComponent.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(250),visualComponent);
        ft.setToValue(1);

        stage.show();
        ft.play();
    }


    public void close(){
        FadeTransition ft = new FadeTransition(Duration.millis(250),visualComponent);
        ft.setToValue(0);
        ft.setOnFinished(event -> stage.close());
        ft.play();
    }

}
