package Document;

/**
 * @author Leonhard Gahr
 * @author Marcel Lillig
 */
public class Tag {
    private long id;
    private String name;

    public Tag(final long ID, final String NAME){
        this.id = ID;
        this.name = NAME;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
