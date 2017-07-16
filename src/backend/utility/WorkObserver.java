package backend.utility;

/**
 * Observer to WorkObersvers that will respond to when work is done or started.
 */
public interface WorkObserver {

    void workStarted();
    void workFinished();
    void workProgress(double percentage);

}
