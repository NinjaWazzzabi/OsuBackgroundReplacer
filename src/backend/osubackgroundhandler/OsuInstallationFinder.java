package backend.osubackgroundhandler;

import java.io.*;
import java.util.Arrays;

/**
 * Created by amk19 on 09/07/2017.
 */
public class OsuInstallationFinder {

    private final static String OSU_REG_KEY_INSTALLATION_CMD = "reg query HKEY_CLASSES_ROOT\\osu!\\DefaultIcon\\ /ve";
    private final static String OSU_EXE_NAME = "osu!.exe";

    OsuInstallationFinder() {

    }


    String getInstallationPath() {
        String osuPath = null;
        try {
            Process process = Runtime.getRuntime().exec(OSU_REG_KEY_INSTALLATION_CMD);
            BufferedReader errorIn = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));

            if (errorOccured(errorIn)) {
                return null;
            }

            osuPath = getInstallationPath(in);

        } catch (IOException e) {
            return null;
        }

        return osuPath;
    }

    private boolean errorOccured(BufferedReader errorIn) throws IOException {
        String line;
        while ((line = errorIn.readLine()) != null) {
            if (line.contains("ERROR")) {
                return true;
            }
        }

        return false;
    }

    private String getInstallationPath(BufferedReader in) throws IOException {
        String line;


        String[] lineSegments = null;
        while ((line = in.readLine()) != null) {
            if (line.contains(OSU_EXE_NAME)) {
                lineSegments = line.split("\"");
                break;
            }
        }
        if (lineSegments != null) {
            for (String lineSegment : lineSegments) {
                if (lineSegment.contains(OSU_EXE_NAME)) {
                    String installationPath = lineSegment;
                    installationPath = installationPath.substring(0,installationPath.length()-OSU_EXE_NAME.length());
                    return installationPath;
                }
            }
        }

        return null;
    }

    /**
     * Checks if the directory contains a osu!.exe
     *
     * @param directory to be checked.
     * @return true if osu!.exe is found.
     */
    static boolean isOsuDirectory(String directory) {
        File osuFolder = new File(directory);
        if (!osuFolder.exists() || !osuFolder.isDirectory()) {
            return false;
        }

        for (File file : osuFolder.listFiles()) {
            if (isOsuExe(file.getAbsolutePath())) return true;
        }

        return false;
    }
    /**
     * Checks if the string contains "osu!.exe"
     * @param path total path to the exe, or just the exe name itself.
     * @return true if string contains "osu!.exe".
     */
    static boolean isOsuExe(String path){
        return new File(path).getName().contains("osu!.exe");
    }

}
