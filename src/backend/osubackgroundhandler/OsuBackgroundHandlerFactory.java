package backend.osubackgroundhandler;

/**
 * Factory class for osu background handlers
 */
public final class OsuBackgroundHandlerFactory {

    private OsuBackgroundHandlerFactory(){

    }

    public static IOsuBackgroundHandler getOsuBackgroundHandler() {
        return new OsuBackgroundHandler();
    }

}
