package backend;

/**
 * Created by Anthony on 11/02/2017.
 */
public class OsuBackgroundHandlerFactory {

    private OsuBackgroundHandlerFactory(){

    }

    public static IOsuBackgroundHandler getOsuBackgroundHandler() {
        return new OsuBackgroundHandler();
    }

}
