package Location;


/**
 * @author Leonhard Gahr
 * @author Pascal de Vries
 */
public interface Location {
    LocationTypes getLocationType();

    String getLocation();

    Object getLocationObject();
}