package frontend.windows;

import backend.osubackgroundhandler.IOsuBackgroundHandler;
import backend.osubackgroundhandler.SingleColour;
import com.jfoenix.controls.JFXSlider;
import javafx.beans.value.ChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;

import java.util.Random;

/**
 * Let's the user pick one colour to be used as a background.
 */
public class SingleColourWindow extends WindowBase implements ColourTileObserver {

    private static final String FXML_LOCATION = "/fxml/singlecolour.fxml";

    private IOsuBackgroundHandler obh;


    @FXML private JFXSlider red;
    @FXML private JFXSlider green;
    @FXML private JFXSlider blue;
    @FXML private FlowPane colorSquaresPane;
    @FXML private ImageView preview;

    private boolean colourSlidersChangeListenerActive;
    private SingleColour selectedColour;

    public SingleColourWindow(IOsuBackgroundHandler osuBackgroundHandler) {
        super(FXML_LOCATION);

        this.obh = osuBackgroundHandler;

        colourSlidersChangeListenerActive = true;
        ChangeListener<Number> sliderValueChangeListener = (observable, oldValue, newValue) -> {
            if (colourSlidersChangeListenerActive) {
                updateSelectedColour();
                updatePreview();
            }
        };

        red.valueProperty().addListener(sliderValueChangeListener);
        green.valueProperty().addListener(sliderValueChangeListener);
        blue.valueProperty().addListener(sliderValueChangeListener);

        colorSquaresPane.setHgap(1);
        colorSquaresPane.setVgap(1);

        updateSelectedColour();
        updatePreview();
        spawnColourTiles();
    }

    private void updateSliders() {
        this.red.setValue(this.selectedColour.getR());
        this.green.setValue(this.selectedColour.getG());
        this.blue.setValue(this.selectedColour.getB());
    }

    private void updateSelectedColour() {
        selectedColour = new SingleColour(
                (int) red.getValue(),
                (int) green.getValue(),
                (int) blue.getValue()
        );
    }
    private void updatePreview() {
        preview.setImage(SwingFXUtils.toFXImage(selectedColour.getImage(), null));
    }

    private void spawnColourTiles() {
        Random r = new Random();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 20; j++) {
                ColourTile tile = new ColourTile(new SingleColour(r.nextInt(255),r.nextInt(255),r.nextInt(255)));
                tile.addObserver(this);
                colorSquaresPane.getChildren().add(tile.getVisualComponent());
            }
        }
    }

    @FXML
    void setSingleColour(ActionEvent event) {
        //TODO Implement.
    }

    @Override
    public void tileClicked(SingleColour colour) {
        this.selectedColour = colour;
        this.colourSlidersChangeListenerActive = false;
        updateSliders();
        this.colourSlidersChangeListenerActive = true;
        updatePreview();
    }
}
