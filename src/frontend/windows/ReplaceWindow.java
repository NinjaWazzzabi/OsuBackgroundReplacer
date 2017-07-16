package frontend.windows;

import backend.osucore.OsuDirectory;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.event.KeyEvent;
import java.io.File;


public class ReplaceWindow extends WindowBase{

    private static String FXML_LOCATION = "/fxml/replaceimage.fxml";

    private OsuDirectory obh;
    private File imageFile;
    private File lastFolder;

    @FXML private JFXTextField imageLocationText;
    @FXML private Text inlineErrorMessage;
    @FXML private ImageView imageView;

    public ReplaceWindow(OsuDirectory backgroundHandler) {
        super(FXML_LOCATION);


        this.inlineErrorMessage.setOpacity(0);
        this.imageFile = new File("");
        this.obh = backgroundHandler;

        this.imageLocationText.focusedProperty().addListener((observable, oldValue, newValue) -> {
            updateImagePath(new File(imageLocationText.getText()));
        });

        this.imageLocationText.setOnKeyTyped(event -> {
            int i = imageLocationText.getCaretPosition();
            imageLocationText.setText(imageLocationText.getText() + event.getCharacter());
            event.consume();
            imageLocationText.positionCaret(i + 1);
            updateImagePath(new File(imageLocationText.getText()));
        });
    }

    @FXML
    void replaceAll(ActionEvent event) {
        if (imageFile == null || imageFile.length() < 1) {
            inlineErrorMessage.setText("No image selected");
            showInlineErrorMessage();
        } else if (!imageFile.exists()) {
            inlineErrorMessage.setText("ImagePathType does not exist");
            showInlineErrorMessage();
        } else {
            obh.getBackgroundChanger().replaceAll(imageFile);
        }
    }

    @FXML
    void browseImage(ActionEvent event) {
        File file = imageFileBrowseExplorer();
        if (file != null) {
            this.imageFile = file;
            updateImagePath(imageFile);
            imageLocationText.setText(imageFile.getAbsolutePath());
        }
    }
    /**
     * Opens a file browser in explorer.
     *
     * @return path to file chosen.
     */
    private File imageFileBrowseExplorer() {
        //Create directory chooser
        FileChooser chooser = new FileChooser();

        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        chooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);

        chooser.setTitle("Choose image file");
        File directory;

        //If there's an already selected osu directory start from there, otherwise at user home.
        if (lastFolder != null) {
            directory = lastFolder;
        } else {
            directory = new File(System.getProperty("user.home"));
        }

        //Sets initial directory and shows directory chooser
        File defaultDirectory = directory;
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showOpenDialog(new Stage());

        //Saves only if the user selected a folder and didn't just close down the window.
        if (selectedDirectory != null && selectedDirectory.getParentFile() != null) {
            lastFolder = selectedDirectory.getParentFile();
        }

        return selectedDirectory;
    }

    @FXML
    void imageLocationChange(ActionEvent event) {
        hideInlineErrorMessage();
        updateImagePath(new File(imageLocationText.getText()));
    }
    @FXML
    void imageLocationChange(KeyEvent event) {
        hideInlineErrorMessage();
        updateImagePath(new File(imageLocationText.getText()));
    }
    private void updateImagePath(File updatedImageFile) {
        // TODO: 16/07/2017 Make cross platorm
        //Not sure if the part with "file:///" will work on systems other than windows.

        imageFile = updatedImageFile;
        Image image = new Image("file:///" + updatedImageFile.getAbsolutePath());
        imageView.setImage(image);
    }


    private void showInlineErrorMessage() {
        super.flashRevealVisualComponent(inlineErrorMessage, FADE_LENGTH_MEDIUM);
    }
    private void hideInlineErrorMessage() {
        super.hideVisualComponent(inlineErrorMessage,FADE_LENGTH_SHORT);
    }
}
