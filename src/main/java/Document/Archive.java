package Document;

/**
 * @author Leonhard Gahr
 * @author Marcel Lillig
 */
public class Archive {
    private String shed;
    private String rack;
    private String folder;

    public Archive(final String SHED, final String RACK, final String FOLDER) {
        this.shed = SHED;
        this.rack = RACK;
        this.folder = FOLDER;
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
