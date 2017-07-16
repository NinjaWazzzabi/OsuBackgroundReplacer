package frontend.windows;

import backend.utility.SingleColour;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amk19 on 03/07/2017.
 */
public class ColourGrid {

    private static final int TILE_OFFSET = 1;

    private final int tileSize;

    private List<ColourTileObserver> observers;
    private List<ColourTile> colourTiles;

    private AnchorPane anchorPane;

    ColourGrid(AnchorPane anchorPane, int tileSize) {
        this.anchorPane = anchorPane;
        this.tileSize = tileSize;

        observers = new ArrayList<>();
        colourTiles = new ArrayList<>();

        spawnColourTiles();
    }


    private void spawnColourTiles() {

        double totalWidth = this.anchorPane.getPrefWidth();
        double totalHeight = this.anchorPane.getPrefHeight();


        double xAmount = (totalWidth-tileSize) / (tileSize+TILE_OFFSET);
        double yAmount = (totalHeight-tileSize) / (tileSize+TILE_OFFSET);


        for (int i = 0; i < yAmount; i++) {
            for (int j = 0; j < xAmount; j++) {
                ColourTile tile;
                //First row is only back and white
                if (i == 0) {
                    tile = new ColourTile(
                            new SingleColour(
                                    0,
                                    0,
                                    1 - (float) j / (float) xAmount
                            )
                    );
                } else {
                    tile = new ColourTile(
                            new SingleColour(
                                    (float) j / (float) xAmount,
                                    1.0f,
                                    (float) i / (float) yAmount
                            ),
                            this.tileSize
                    );
                }

                addObserversToTile(tile);
                colourTiles.add(tile);
                this.anchorPane.getChildren().add(tile.getVisualComponent());
                tile.getVisualComponent().setTranslateX(j * (tileSize+TILE_OFFSET));
                tile.getVisualComponent().setTranslateY(i * (tileSize+TILE_OFFSET));
            }
        }
    }

    private void addObserversToTile(ColourTile tile) {
        for (ColourTileObserver observer : observers) {
            tile.addObserver(observer);
        }
    }

    public void addObserver(ColourTileObserver observer) {
        for (ColourTile colourTile : colourTiles) {
            colourTile.addObserver(observer);
        }
        this.observers.add(observer);
    }
    public void removeObserver(ColourTileObserver observer) {
        for (ColourTile colourTile : colourTiles) {
            colourTile.removeObserver(observer);
        }
        this.observers.remove(observer);
    }
}