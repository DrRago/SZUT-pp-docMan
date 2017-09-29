package Location;

/**
 * @author Leonhard Gahr
 * @author Pascal de Vries
 */
public class LocationFactory {
    public static Location getLocation(LocationTypes type, String[] arguments) {
        switch (type) {
            case URL:
                assert arguments.length == 1;
                return new URL(arguments[0]);
            case FILE:
                assert arguments.length == 1;
                return new File(arguments[0]);
            case ARCHIVE:
                assert arguments.length == 4;
                return new Archive(Integer.parseInt(arguments[0]), arguments[1], arguments[2], arguments[3]);
            default:
                // never reachable because of ENUM
                return null;
        }
    }
}
