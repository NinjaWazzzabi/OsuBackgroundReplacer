package backend.utility;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that handles objects that want to be alerted about a work that has been started.
 */
public class WorkObservers {

    @Getter private boolean isWorking;
    private List<WorkObserver> listeners;

    public WorkObservers(){
        listeners = new ArrayList<>();
        isWorking = false;
    }

    public void alertListenersWorkStarted() {
        isWorking = true;
        for (WorkObserver listener : listeners) {
            listener.workStarted();
        }
    }
    public void alertListenersWorkFinished() {
        isWorking = false;
        for (WorkObserver listener : listeners) {
            listener.workFinished();
        }
    }

    public void alertWorkProgress(double percentage) {
        for (WorkObserver listener : listeners) {
            listener.workProgress(percentage);
        }
    }

    public void addListener(WorkObserver listener){
        listeners.add(listener);
    }
    public void removeListener(WorkObserver listener){
        listeners.remove(listener);
    }
}
