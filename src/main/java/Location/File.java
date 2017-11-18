package Location;

/**
 * @author Leonhard Gahr
 * @author Pascal de Vries
 */
public class File implements Location {
    private final String filePath;

    /**
     * Construct the file
     * @param FILEPATH the filepath of the stored file
     */
    public File(final String FILEPATH) {
        this.filePath = FILEPATH;
    }

    /**
     * toString override
     * @return a printable string of the file
     */
    @Override
    public String toString() {
        return filePath;
    }

    /**
     * Getter for the location type
     * @return the type of the location as enum
     */
    @Override
    public LocationTypes getLocationType() {
        return LocationTypes.FILE;
    }

    /**
     * Get the location string
     * @return the location string
     */
    @Override
    public String getLocation() {
        return this.toString();
    }

    /**
     * Getter for the object
     * @return this object
     */
    @Override
    public Object getLocationObject() {
        return this;
    }
}
