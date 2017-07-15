package frontend.windows;

import com.jfoenix.controls.*;
import frontend.customfadeeffects.BlurFade;
import frontend.screens.About;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class MainWindow extends WindowBase{

    private static final String FXML_LOCATION = "/fxml/mainwindow.fxml";

    private List<MainWindowListener> listeners;

    @FXML private AnchorPane topSection;
    @FXML private JFXButton about;
    @FXML private JFXButton exit;
    @FXML private Text errorText;
    @FXML private JFXTabPane tabPane;

    private double xOffset;
    private double yOffset;

    private Transition lastAnimation;
    private boolean loadingScreenShown;

    private BlurFade customEffect;
    private Popup popup;
    private Loading loading;

    public MainWindow() {
        super(FXML_LOCATION);

        loadingScreenShown = false;
        listeners = new ArrayList<>();
        customEffect = new BlurFade();
        getVisualComponent().setEffect(customEffect);

        initializePopup();
    }

    private void initializePopup() {
        loading = new Loading();
        loading.setOnClosed(() -> popup.hide());


        popup = new Popup();
        popup.getContent().add(loading.getVisualComponent());
        popup.setOpacity(0.95);
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
    @FXML
    void about(ActionEvent event) {
        new About();
    }


    public void promtError(String errorMessage) {
        if (lastAnimation != null){
            lastAnimation.stop();
        }
        errorText.setText(errorMessage);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(FADE_LENGTH_LONG),errorText);
        fadeIn.setToValue(1);

        PauseTransition delay = new PauseTransition(Duration.millis(FADE_LENGTH_LONG*4));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(FADE_LENGTH_LONG),errorText);
        fadeOut.setToValue(0);

        SequentialTransition combine = new SequentialTransition(fadeIn,delay,fadeOut);
        combine.play();
        lastAnimation = combine;
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

    public void loadingEnbled(boolean value){
        if (loadingScreenShown != value) {
            showLoading(value);
            if (value) {
                getVisualComponent().setDisable(true);
                customEffect.fadeIn();
            } else {
                customEffect.stop();
                customEffect.fadeOut();
                getVisualComponent().setDisable(false);
            }
        }
    }

    private void showLoading(boolean value) {
        if (value) {
            loadingScreenShown = true;
            loading.start();
            popup.show(this.getVisualComponent().getScene().getWindow());
        } else {
            loadingScreenShown = false;
            loading.close();
        }
    }

    public void addListener(MainWindowListener listener) {
        listeners.add(listener);
    }
    public void removeListener(MainWindowListener listener){
        listeners.remove(listener);
    }
}
