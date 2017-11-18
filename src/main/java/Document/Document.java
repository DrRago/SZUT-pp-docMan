package Document;

import Location.Location;

import java.util.List;


/**
 * The type Document.
 *
 * @author Leonhard Gahr
 * @author Marcel Lillig
 */
public class Document {
    private final String id;
    private String title;
    private String author;
    private Location location;
    private final List<Tag> tags;

    /**
     * Construct the document
     *
     * @param ID       the ID of the document in the database
     * @param TITLE    the title of the document
     * @param AUTHOR   the author of the document
     * @param LOCATION the location of the document
     * @param TAGS     the tagList of the document
     */
    public Document(final String ID, final String TITLE, final String AUTHOR, final Location LOCATION, final List<Tag> TAGS) {
        this.id = ID;
        this.title = TITLE;
        this.author = AUTHOR;
        this.location = LOCATION;
        this.tags = TAGS;
    }

    /**
     * Getter for the ID
     *
     * @return the ID
     */
    public String getID() {
        return id;
    }

    /**
     * Setter for the title
     *
     * @param title the new title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter for the title
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter for the author
     *
     * @param author the new author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Getter for the author
     *
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Setter for the location
     *
     * @param location the new location
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Getter for the location
     *
     * @return the location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Getter for the tags
     *
     * @return the tags
     */
    public List<Tag> getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return "Document " + this.id;
    }
}
