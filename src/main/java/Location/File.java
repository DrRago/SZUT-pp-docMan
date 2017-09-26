package Location;

/**
 * @author Leonhard Gahr
 * @author Pascal de Vries
 */
public class File {
    private String filePath;

    public File(final String FILEPATH) {
        this.filePath = FILEPATH;
    }

    @Override
    public String toString() {
        return filePath;
    }
}
