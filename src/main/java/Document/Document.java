package Document;

import java.util.List;


/**
 * @author Leonhard Gahr
 * @author Marcel Lillig
 */
public class Document {
    private String id;
    private String title;
    private String author;
    private String filePath;
    private String URL;
    private Archive archive;
    private List<Tag> tags;

    public Document(final String ID, final String TITLE, final String AUTHOR, final String FILEPATH, final String URL, final Archive ARCHIVE, final List<Tag> TAGS){
        this.id = ID;
        this.title = TITLE;
        this.author = AUTHOR;
        this.filePath = FILEPATH;
        this.URL = URL;
        this.archive = ARCHIVE;
        this.tags = TAGS;
    }

    public void validateRefecences() {
        // TODO: 01/09/2017 add things here
    }

    public String getID() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getURL() {
        return URL;
    }

    public Archive getArchive() {
        return archive;
    }

    public List<Tag> getTags() {
        return tags;
    }
}
