package backend;

/**
 * Listener to OsuBackgroundHandlers that will respond to when work is done or started.
 */
public interface WorkListener {

    void alertWorkStarted();
    void alertWorkFinished();

}
