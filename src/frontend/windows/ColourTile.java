package frontend.windows;

import backend.osubackgroundhandler.SingleColour;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

public class ColourTile extends WindowBase {

    private static final String FXML_LOCATION = "/fxml/coloursquare.fxml";

    private SingleColour colour;
    private List<ColourTileObserver> observers;

    @FXML
    private ImageView image;

    public ColourTile(SingleColour colour, int tileSize) {
        super(FXML_LOCATION);
        this.colour = colour;
        image.setImage(SwingFXUtils.toFXImage(this.colour.getImage(), null));

        observers = new ArrayList<>();
    }
    public ColourTile(SingleColour colour) {
        this(colour,10);
    }

    @FXML
    void clicked(MouseEvent event) {
        for (ColourTileObserver observer : observers) {
            observer.tileClicked(this.colour);
        }
    }

    @FXML
    void enterHover(MouseEvent event) {
        super.enlarge(super.visualComponent);
        super.getVisualComponent().toFront();

    }
    @FXML
    void exitHover(MouseEvent event) {
        super.normalSize(super.visualComponent);
        super.getVisualComponent().toBack();
    }

    @FXML
    void mousePressed(MouseEvent event) {

    }
    @FXML
    void mouseReleased(MouseEvent event) {

    }


    public void addObserver(ColourTileObserver observer) {
        this.observers.add(observer);
    }
    public void removeObserver(ColourTileObserver observer) {
        this.observers.remove(observer);
    }
}
