package backend;

/**
 * Created by Anthony on 06/05/2017.
 */
public class OsuBackground {

    private String fileName;
    private String filePath;

    public OsuBackground(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OsuBackground)) return false;

        OsuBackground that = (OsuBackground) o;

        if (!fileName.equals(that.fileName)) return false;
        return filePath.equals(that.filePath);
    }
    @Override
    public int hashCode() {
        int result = fileName.hashCode();
        result = 31 * result + filePath.hashCode();
        return result;
    }
}
