package frontend.windows;

import backend.osubackgroundhandler.IOsuBackgroundHandler;
import com.jfoenix.controls.JFXTextField;
import com.sun.istack.internal.Nullable;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;


public class ReplaceWindow extends WindowBase{

    private static String FXML_LOCATION = "/fxml/replaceimage.fxml";

    private IOsuBackgroundHandler obh;
    private String imagePath;
    private String lastFolder;

    @FXML private JFXTextField imageLocationText;
    @FXML private Text inlineErrorMessage;
    @FXML private ImageView imageView;

    public ReplaceWindow(IOsuBackgroundHandler backgroundHandler) {
        super(FXML_LOCATION);


        this.inlineErrorMessage.setOpacity(0);
        this.imagePath = "";
        this.obh = backgroundHandler;

        this.imageLocationText.focusedProperty().addListener((observable, oldValue, newValue) -> {
            updateImagePath(imageLocationText.getText());
        });

        this.imageLocationText.setOnKeyTyped(event -> {
            int i = imageLocationText.getCaretPosition();
            imageLocationText.setText(imageLocationText.getText() + event.getCharacter());
            event.consume();
            imageLocationText.positionCaret(i + 1);
            updateImagePath(imageLocationText.getText());
        });
    }

    @FXML
    void replaceAll(ActionEvent event) {
        if (imagePath == null || imagePath.length() < 1) {
            inlineErrorMessage.setText("No image selected");
            showInlineErrorMessage();
        } else if (!new File(imagePath).exists()) {
            inlineErrorMessage.setText("Image does not exist");
            showInlineErrorMessage();
        } else {
            try {
                obh.replaceAll(imagePath);
            } catch (IOException e) {
                inlineErrorMessage.setText(e.getMessage());
                showInlineErrorMessage();
            }
        }
    }

    @FXML
    void browseImage(ActionEvent event) {
        String tempImagePath = imageFileBrowseExplorer();
        if (tempImagePath != null) {
            this.imagePath = tempImagePath;
            updateImagePath(imagePath);
            imageLocationText.setText(imagePath);
        }
    }
    /**
     * Opens a file browser in explorer.
     *
     * @return path to file chosen.
     */
    @Nullable
    private String imageFileBrowseExplorer() {
        //Create directory chooser
        FileChooser chooser = new FileChooser();

        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        chooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);

        chooser.setTitle("Choose image file");
        String dir;

        //If there's an already selected osu directory start from there, otherwise at user home.
        if (lastFolder != null) {
            dir = lastFolder;
        } else {
            dir = System.getProperty("user.home");
        }

        //Sets initial directory and shows directory chooser
        File defaultDirectory = new File(dir);
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showOpenDialog(new Stage());

        //Saves only if the user selected a folder and didn't just close down the window.
        String filePath = null;
        if (selectedDirectory != null) {
            filePath = selectedDirectory.getAbsolutePath().replace("\\", "/");
            lastFolder = new File(filePath).getParent();
        }

        return filePath;
    }

    @FXML
    void imageLocationChange(ActionEvent event) {
        hideInlineErrorMessage();
        updateImagePath(imageLocationText.getText());
    }
    @FXML
    void imageLocationChange(KeyEvent event) {
        hideInlineErrorMessage();
        updateImagePath(imageLocationText.getText());
    }
    private void updateImagePath(String updatedImagePath) {
        imagePath = updatedImagePath;
        Image image = new Image("file:///" + updatedImagePath);
        imageView.setImage(image);
    }


    private void showInlineErrorMessage() {
        super.flashRevealVisualComponent(inlineErrorMessage, FADE_LENGTH_MEDIUM);
    }
    private void hideInlineErrorMessage() {
        super.hideVisualComponent(inlineErrorMessage,FADE_LENGTH_SHORT);
    }
}
