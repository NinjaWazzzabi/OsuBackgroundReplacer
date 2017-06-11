package frontend.windows;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;
import frontend.screens.About;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Window;
import javafx.util.Duration;
import lombok.Getter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainWindow {

    private static int ERROR_FADE_LENGTH = 500;

    @Getter
    private Parent visualComponent;
    private List<MainWindowListener> listeners;

    @FXML
    private AnchorPane topSection;
    @FXML
    private JFXButton about;
    @FXML
    private JFXButton exit;
    @FXML
    private Text errorText;
    @FXML
    private JFXTabPane tabPane;

    private double xOffset;
    private double yOffset;

    private Transition lastAnimation;

    public MainWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainwindow.fxml"));
        loader.setController(this);
        try {
            this.visualComponent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        listeners = new ArrayList<>();
    }

    @FXML
    void about(ActionEvent event) {
        new About();
    }

    @FXML
    void pressed(MouseEvent event) {
        Window stage = topSection.getScene().getWindow();
        xOffset = stage.getX() - event.getScreenX();
        yOffset = stage.getY() - event.getScreenY();
    }

    @FXML
    void dragged(MouseEvent event) {
        Window stage = topSection.getScene().getWindow();
        stage.setX(event.getScreenX() + xOffset);
        stage.setY(event.getScreenY() + yOffset);
    }

    @FXML
    void exit(ActionEvent event) {
        FadeTransition ft = new FadeTransition(Duration.millis(250), topSection.getScene().getWindow().getScene().getRoot());
        ft.setToValue(0);
        ft.setOnFinished(event1 -> {
            for (MainWindowListener listener : listeners) {
                listener.exit();
            }
        });
        ft.play();
    }

    public void promtError(String errorMessage) {
        if (lastAnimation != null){
            lastAnimation.stop();
        }
        errorText.setText(errorMessage);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(ERROR_FADE_LENGTH),errorText);
        fadeIn.setToValue(1);

        PauseTransition delay = new PauseTransition(Duration.millis(ERROR_FADE_LENGTH*4));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(ERROR_FADE_LENGTH),errorText);
        fadeOut.setToValue(0);

        SequentialTransition combine = new SequentialTransition(fadeIn,delay,fadeOut);
        combine.play();
        lastAnimation = combine;
    }


    public void addListener(MainWindowListener listener) {
        listeners.add(listener);
    }
    public void removeListener(MainWindowListener listener){
        listeners.remove(listener);
    }

    public void addNewTab(String tabName, Parent visualComponent) {
        Tab tab = new Tab();
        tab.setText(tabName);
        tab.setContent(visualComponent);

        tabPane.getTabs().add(tab);
    }

    public void goToTab(String name) {
        ObservableList<Tab> tabs = tabPane.getTabs();
        for (Tab tab : tabs) {
            if (tab.getText().equals(name)) {
                tabPane.getSelectionModel().select(tab);
                break;
            }
        }
    }
}
