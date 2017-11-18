package Location;


/**
 * @author Leonhard Gahr
 * @author Pascal de Vries
 */
public interface Location {
    /**
     * Getter for the location type
     * @return the type of the location as enum
     */
    LocationTypes getLocationType();

    /**
     * Get the location string
     * @return the location string
     */
    String getLocation();

    /**
     * Getter for the object
     * @return this object
     */
    Object getLocationObject();
}