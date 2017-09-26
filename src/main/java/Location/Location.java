package Location;


/**
 * @author Leonhard Gahr
 * @author Pascal de Vries
 */
public class Location {
    private Object location;

    @Override
    public String toString() {
        return location.toString();
    }

    public Object getLocation() {
        return location;
    }

    public void setLocation(final Object LOCATION) {
        this.location = LOCATION;
    }
}