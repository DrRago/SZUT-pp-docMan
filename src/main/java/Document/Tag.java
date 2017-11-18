package Document;

/**
 * @author Leonhard Gahr
 * @author Marcel Lillig
 */
public class Tag {
    private final long id;
    private final String name;

    public Tag(final long ID, final String NAME) {
        this.id = ID;
        this.name = NAME;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != Tag.class) {
            return false;
        }
        Tag tag = (Tag) obj;
        return name.equals(tag.name);
    }
}
