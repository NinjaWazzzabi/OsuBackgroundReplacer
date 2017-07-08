package frontend.windows;

import backend.core.image.ImageHelper;
import backend.osubackgroundhandler.IOsuBackgroundHandler;
import backend.osubackgroundhandler.SingleColour;
import com.jfoenix.controls.JFXSlider;
import javafx.beans.value.ChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

/**
 * Let's the user pick one colour to be used as a background.
 */
public class SingleColourWindow extends WindowBase implements ColourTileObserver {

    private static final String FXML_LOCATION = "/fxml/singlecolour.fxml";

    private IOsuBackgroundHandler obh;


    @FXML private JFXSlider red;
    @FXML private JFXSlider green;
    @FXML private JFXSlider blue;

    @FXML private JFXSlider hue;
    @FXML private JFXSlider saturation;
    @FXML private JFXSlider value;

    @FXML private AnchorPane colourTiles;
    @FXML private ImageView preview;

    private boolean slidersChangeListenerActive;
    private SingleColour selectedColour;

    private ColourGrid colourGrid;

    public SingleColourWindow(IOsuBackgroundHandler osuBackgroundHandler) {
        super(FXML_LOCATION);

        this.obh = osuBackgroundHandler;

        slidersChangeListenerActive = true;
        ChangeListener<Number> rgbSliderChangeListener = (observable, oldValue, newValue) -> {
            if (slidersChangeListenerActive) {
                updateSelectedColourRGB();
                updatePreview();
                updateHSVSlider();
            }
        };
        ChangeListener<Number> hueSliderChangeListener = (observable, oldValue, newValue) -> {
            if (slidersChangeListenerActive) {
                updateSelectedColourHSV();
                updatePreview();
                updateRGBSlider();
            }
        };

        red.valueProperty().addListener(rgbSliderChangeListener);
        green.valueProperty().addListener(rgbSliderChangeListener);
        blue.valueProperty().addListener(rgbSliderChangeListener);

        hue.valueProperty().addListener(hueSliderChangeListener);
        saturation.valueProperty().addListener(hueSliderChangeListener);
        value.valueProperty().addListener(hueSliderChangeListener);


        updateSelectedColourRGB();
        updateHSVSlider();
        updateRGBSlider();
        updatePreview();

        colourGrid = new ColourGrid(colourTiles, 10);
        colourGrid.addObserver(this);
    }

    private void updateSelectedColourHSV() {
        selectedColour = new SingleColour(
                (float) hue.getValue()/255,
                (float) saturation.getValue()/255,
                (float) value.getValue()/255
        );
    }

    private void updateRGBSlider() {
        slidersChangeListenerActive = false;
        this.red.setValue(this.selectedColour.getR());
        this.green.setValue(this.selectedColour.getG());
        this.blue.setValue(this.selectedColour.getB());
        slidersChangeListenerActive = true;
    }
    private void updateHSVSlider() {
        slidersChangeListenerActive = false;
        this.hue.setValue(this.selectedColour.getHue()*255);
        this.saturation.setValue(this.selectedColour.getSaturation()*255);
        this.value.setValue(this.selectedColour.getValue()*255);
        slidersChangeListenerActive = true;
    }

    private void updateSelectedColourRGB() {
        selectedColour = new SingleColour(
                (int) red.getValue(),
                (int) green.getValue(),
                (int) blue.getValue()
        );
    }
    private void updatePreview() {
        preview.setImage(SwingFXUtils.toFXImage(selectedColour.getImage(), null));
    }

    @FXML
    void setSingleColour(ActionEvent event) {
        obh.replaceAll(
                new ImageHelper(selectedColour.getImage(),
     String.valueOf(selectedColour.getR())+ "_" +
                String.valueOf(selectedColour.getG())+ "_" +
                String.valueOf(selectedColour.getB())
        ));
    }

    @Override
    public void tileClicked(SingleColour colour) {
        this.selectedColour = colour;
        updateRGBSlider();
        updateHSVSlider();
        updatePreview();
    }
}
