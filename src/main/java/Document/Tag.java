package Document;

/**
 * @author Leonhard Gahr
 * @author Marcel Lillig
 */
public class Tag {
    private final long id;
    private final String name;

    /**
     * Construct the tag
     * @param ID the id of the tag in the database
     * @param NAME the name of the tag
     */
    public Tag(final long ID, final String NAME) {
        this.id = ID;
        this.name = NAME;
    }

    /**
     * toString override
     * @return a printable string of the string
     */
    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Getter for the ID
     * @return the IF
     */
    public long getId() {
        return id;
    }

    /**
     * Getter for the name
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * equals override to filter tags in the filter search of the database
     * @return a boolean if the names are matching
     */
    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != Tag.class) {
            return false;
        }
        Tag tag = (Tag) obj;
        return name.equals(tag.name);
    }
}
