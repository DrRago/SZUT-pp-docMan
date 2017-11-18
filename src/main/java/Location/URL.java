package Location;

/**
 * @author Leonhard Gahr
 * @author Pascal de Vries
 */
public class URL implements Location{
    private final String URL;

    public URL(final String URL) {
        this.URL = URL;
    }

    @Override
    public String toString() {
        return URL;
    }

    @Override
    public LocationTypes getLocationType() {
        return LocationTypes.URL;
    }

    @Override
    public String getLocation() {
        return URL;
    }

    @Override
    public Object getLocationObject() {
        return this;
    }
}
