package backend.osubackgroundhandler;

/**
 * Created by Anthony on 06/05/2017.
 */
public class OsuBackground {

    private final String fileName;
    private final String filePath;

    OsuBackground(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
    }

    final String getFileName() {
        return fileName;
    }
    final String getFilePath() {
        return filePath;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OsuBackground)) {
            return false;
        }

        OsuBackground that = (OsuBackground) o;

        return fileName.equals(that.fileName) && filePath.equals(that.filePath);
    }
    @Override
    public final int hashCode() {
        int result = fileName.hashCode();
        result = 31 * result + filePath.hashCode();
        return result;
    }
}