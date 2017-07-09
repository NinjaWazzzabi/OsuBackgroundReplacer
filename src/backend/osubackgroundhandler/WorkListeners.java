package backend.osubackgroundhandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that handles objects that want to be alerted about a work that has been started.
 */
public class WorkListeners {

    private boolean isWorking;
    private List<WorkListener> listeners;

    public WorkListeners(){
        listeners = new ArrayList<>();
        isWorking = false;
    }

    public void alertListenersWorkStarted() {
        isWorking = true;
        for (WorkListener listener : listeners) {
            listener.alertWorkStarted();
        }
    }
    public void alertListenersWorkFinished() {
        isWorking = false;
        for (WorkListener listener : listeners) {
            listener.alertWorkFinished();
        }
    }

    public void addListener(WorkListener listener){
        listeners.add(listener);
    }
    public void removeListener(WorkListener listener){
        listeners.remove(listener);
    }
}
