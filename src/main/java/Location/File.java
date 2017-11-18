package Location;

/**
 * The type File.
 *
 * @author Leonhard Gahr
 * @author Pascal de Vries
 */
public class File implements Location {
    private final String filePath;

    /**
     * Construct the file
     *
     * @param FILEPATH the filepath of the stored file
     */
    public File(final String FILEPATH) {
        this.filePath = FILEPATH;
    }

    @Override
    public String toString() {
        return filePath;
    }

    @Override
    public LocationTypes getLocationType() {
        return LocationTypes.FILE;
    }

    @Override
    public String getLocation() {
        return this.toString();
    }

    @Override
    public Object getLocationObject() {
        return this;
    }
}
