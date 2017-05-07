package frontend.mainscreen;

/**
 * Interface for {@link MainScreen} listeners.
 */
public interface MainScreenListener {

    /**
     * Called when exit button has been pressed.
     */
    void exitPressed();

    /**
     * Called when save button has been pressed.
     */
    void saveAll();

    /**
     * Called when replaceAll button has been pressed.
     */
    void replaceAll();

    /**
     * Called when removeAll button has been pressed.
     */
    void removeAll();

    /**
     * Called when installationBrowse button has been pressed.
     */
    void installationBrowse();

    /**
     * Called when the imageBrowse has been pressed.
     */
    void imageBrowse();

    /**
     * Called when saveBrowse has been pressed.
     */
    void saveBrowse();
}
