package Location;

/**
 * @author Leonhard Gahr
 * @author Pascal de Vries
 */
public class URL implements Location{
    private final String URL;

    /**
     * Construct the URL
     * @param URL the URL to the location
     */
    public URL(final String URL) {
        this.URL = URL;
    }

    /**
     * toString override
     * @return a printable string of the url
     */
    @Override
    public String toString() {
        return URL;
    }

    /**
     * Getter for the location type
     * @return the type of the location as enum
     */
    @Override
    public LocationTypes getLocationType() {
        return LocationTypes.URL;
    }

    /**
     * Get the location string
     * @return the location string
     */
    @Override
    public String getLocation() {
        return URL;
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
