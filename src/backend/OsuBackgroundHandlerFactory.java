package backend;

import java.io.File;
import java.io.IOException;

/**
 * Created by Anthony on 11/02/2017.
 */
public class OsuBackgroundHandlerFactory {

    private OsuBackgroundHandlerFactory(){

    }

    public static OsuBackgroundHandlers getOsuBackgroundHandler() {
        return new OsuBackgroundHandler();
    }

}
