package Location;

/**
 * The type Archive.
 *
 * @author Leonhard Gahr
 * @author Marcel Lillig
 */
public class Archive implements Location {
    private final long id;
    private final String shed;
    private final String rack;
    private final String folder;

    /**
     * Construct a new archive
     *
     * @param ID     the ID of the archive in the database
     * @param SHED   the shed of the archive
     * @param RACK   the rack of the archive
     * @param FOLDER the folder of the archive
     */
    public Archive(final long ID, final String SHED, final String RACK, final String FOLDER) {
        this.id = ID;
        this.shed = SHED;
        this.rack = RACK;
        this.folder = FOLDER;
    }

    @Override
    public String toString() {
        return "Shed " + this.shed + "\nRack " + this.rack + "\nFolder " + this.folder;
    }

    @Override
    public LocationTypes getLocationType() {
        return LocationTypes.ARCHIVE;
    }

    /**
     * Getter for the ID
     *
     * @return the ID
     */
    public long getId() {
        return id;
    }

    /**
     * Getter for the shed
     *
     * @return the shed
     */
    public String getShed() {
        return shed;
    }

    /**
     * Getter for the rack
     *
     * @return the rack
     */
    public String getRack() {
        return rack;
    }

    /**
     * Getter for the folder
     *
     * @return the folder
     */
    public String getFolder() {
        return folder;
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
