package Document;

/**
 * @author Leonhard Gahr
 * @author Marcel Lillig
 */
public class Archive {
    private long id;
    private String shed;
    private String rack;
    private String folder;

    public Archive(final long ID, final String SHED, final String RACK, final String FOLDER) {
        this.id = ID;
        this.shed = SHED;
        this.rack = RACK;
        this.folder = FOLDER;
    }

    @Override
    public String toString() {
        return this.shed + " " + this.rack + " " + this.folder;
    }

    public long getId() {
        return id;
    }

    public String getShed() {
        return shed;
    }

    public String getRack() {
        return rack;
    }

    public String getFolder() {
        return folder;
    }
}
