package Document;

import Location.Location;

import java.util.List;


/**
 * @author Leonhard Gahr
 * @author Marcel Lillig
 */
public class Document {
    private String id;
    private String title;
    private String author;
    private Location location;
    private List<Tag> tags;

    public Document(final String ID, final String TITLE, final String AUTHOR, final Location LOCATION, final List<Tag> TAGS) {
        this.id = ID;
        this.title = TITLE;
        this.author = AUTHOR;
        this.location = LOCATION;
        this.tags = TAGS;
    }

    public void validateRefecences() {
        // TODO: 01/09/2017 add things here
    }

    public String getID() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<Tag> getTags() {
        return tags;
    }

    //TODO REMOVE METHOD

    @Override
    public String toString() {
        return "Document " + this.id;
    }
}
