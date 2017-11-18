package DatabaseUtility;

import Document.Document;
import Document.Tag;
import Location.Archive;
import Location.Location;
import Location.LocationFactory;
import Location.LocationTypes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static Location.LocationTypes.FILE;

/**
 * The type Database utility.
 *
 * @author Leonhard Gahr
 * @author Pascal de Vries
 */
public class DatabaseUtility {
    private Connection conn;
    private final List<Tag> tags = new ArrayList<>();

    /**
     * Instantiates a new Database utility.
     *
     * @param path the path
     * @throws SQLException the sql exception
     */
    public DatabaseUtility(final String path) throws SQLException {
        conn = DriverManager.getConnection(path);

        updateTagList();
    }

    /**
     * update the tag list of the databaseUtility
     *
     * @throws SQLException
     */
    private void updateTagList() throws SQLException {
        tags.clear();
        ResultSet rs = conn.createStatement().executeQuery("SELECT ID, Name FROM Tag");

        while (rs.next()) {
            tags.add(new Tag(rs.getLong("ID"), rs.getString("Name")));
        }
    }

    /**
     * Get an archive by ID
     *
     * @param ARCHIVEID the archive id
     * @return a String array with all information for the initialization of the archve object
     * @throws SQLException
     */
    private String[] getArchiveFromID(final int ARCHIVEID) throws SQLException {
        if (ARCHIVEID != 0) {
            ResultSet archiveRS = conn.createStatement().executeQuery("SELECT * FROM Archive WHERE ID='" + ARCHIVEID + "'");

            archiveRS.next();
            final long ID = archiveRS.getInt("ID");
            final String SHED = archiveRS.getString("shed");
            final String RACK = archiveRS.getString("rack");
            final String FOLDER = archiveRS.getString("folder");

            return new String[]{Long.toString(ID), SHED, RACK, FOLDER};
        }
        return null;
    }

    /**
     * Get all tags of a document by it's id form the database
     *
     * @param ID the document if
     * @return the tag list
     * @throws SQLException
     */
    private List<Tag> getTagListFromDocumentID(final String ID) throws SQLException {
        final List<Tag> TAGS = new ArrayList<Tag>();

        ResultSet tagReferenceRS = conn.createStatement().executeQuery("SELECT TagID FROM TagReference WHERE DocumentID='" + ID + "'");

        while (tagReferenceRS.next()) {
            ResultSet tagRS = conn.createStatement().executeQuery("SELECT * FROM Tag WHERE ID='" + tagReferenceRS.getInt("TagID") + "'");

            tagRS.next();
            try {
                TAGS.add(new Tag(tagRS.getInt("ID"), tagRS.getString("Name")));
            } catch (SQLException e) {
                // no tag exists -> nothing to do here
            }
        }

        return TAGS;
    }

    /**
     * Read all documents to a list
     *
     * @return the list
     * @throws SQLException the sql exception
     */
    public List<Document> read() throws SQLException {
        ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM Document");

        List<Document> documentList = new ArrayList<Document>();

        while (rs.next()) {
            final String ID = rs.getString("ID");
            final String TITLE = rs.getString("Title");
            final String AUTHOR = rs.getString("Author");
            Location location;

            switch (rs.getInt("LocationType")) {
                case 0:
                    location = LocationFactory.getLocation(FILE, new String[]{rs.getString("FilePath")});
                    break;
                case 1:
                    location = LocationFactory.getLocation(LocationTypes.URL, new String[]{rs.getString("URL")});
                    break;
                case 2:
                    location = LocationFactory.getLocation(LocationTypes.ARCHIVE, getArchiveFromID(rs.getInt("Archive")));
                    break;
                default:
                    throw new IllegalArgumentException("Location type not defined in database");
            }

            final List<Tag> TAGS = getTagListFromDocumentID(ID);

            documentList.add(new Document(ID, TITLE, AUTHOR, location, TAGS));
        }
        return documentList;
    }

    /**
     * Delete document.
     *
     * @param ID the id
     * @throws SQLException the sql exception
     */
    public void deleteDocument(final String ID) throws SQLException {
        PreparedStatement stmt;

        List<Document> documentList = read();
        final Document[] toDeleteArray = new Document[1];
        documentList.forEach(e -> {
            if (e.getID().equals(ID)) {
                toDeleteArray[0] = e;
            }
        });
        final Document toDelete = toDeleteArray[0];

        switch (toDelete.getLocation().getLocationType()) {
            case ARCHIVE:
                stmt = conn.prepareStatement("DELETE FROM Document WHERE ID=?");
                stmt.setString(1, ID);
                stmt.executeUpdate();

                long archiveID;
                stmt = conn.prepareStatement("DELETE FROM Archive WHERE ID=?");
                archiveID = ((Archive) toDelete.getLocation().getLocationObject()).getId();
                stmt.setLong(1, archiveID);
                break;
            default:
                stmt = conn.prepareStatement("DELETE FROM Document WHERE ID=?");
                stmt.setString(1, ID);
                break;
        }
        stmt.executeUpdate();

        stmt = conn.prepareStatement("DELETE FROM TagReference WHERE DocumentID=?");
        stmt.setString(1, ID);
        stmt.executeUpdate();
    }

    private void update(final Archive ARCHIVE) throws SQLException {
        conn.createStatement().execute(String.format("UPDATE Archive SET shed='%s', rack='%s', folder='%s' WHERE ID='%d'", ARCHIVE.getShed(), ARCHIVE.getRack(), ARCHIVE.getFolder(), ARCHIVE.getId()));
    }

    /**
     * Update a document
     *
     * @param DOCUMENT the document
     * @throws SQLException the sql exception
     */
    public void update(final Document DOCUMENT) throws SQLException {
        Location location = DOCUMENT.getLocation();
        int locationInt;

        PreparedStatement stmt;

        // update the location of document
        switch (location.getLocationType()) {
            case ARCHIVE:
                update((Archive) location);
                stmt = conn.prepareStatement("UPDATE Document SET Archive=? WHERE ID=?");
                stmt.setInt(1, ((int) ((Archive) location).getId()));
                stmt.setString(2, DOCUMENT.getID());
                locationInt = 2;
                break;
            case URL:
                stmt = conn.prepareStatement("UPDATE Document SET URL=? WHERE ID=?");
                stmt.setString(1, location.getLocation());
                stmt.setString(2, DOCUMENT.getID());
                locationInt = 1;
                break;
            case FILE:
                stmt = conn.prepareStatement("UPDATE Document SET FilePath=? WHERE ID=?");
                stmt.setString(1, location.getLocation());
                stmt.setString(2, DOCUMENT.getID());
                locationInt = 0;
                break;
            default:
                throw new IllegalArgumentException("Unknown Location");
        }

        stmt.executeUpdate();


        // tags handled by GUI

        // update author and title of the document
        stmt = conn.prepareStatement("UPDATE Document SET Author=?, Title=?, LocationType=? WHERE ID=?");
        stmt.setString(1, DOCUMENT.getAuthor());
        stmt.setString(2, DOCUMENT.getTitle());
        stmt.setInt(3, locationInt);
        stmt.setString(4, DOCUMENT.getID());
        stmt.executeUpdate();
    }

    /**
     * Create a tag in the database and returnsit
     *
     * @param tag the tag to create
     * @return the tag that has been created
     * @throws SQLException the sql exception
     */
    public Tag createTag(final String tag) throws SQLException {
        boolean exists = false;
        // using PreparedStatement to exclude SQL-injection and for the possibility to use unescaped characters
        PreparedStatement stmt;

        for (Tag tag1 : tags) {
            if (tag1.getName().equals(tag)) {
                exists = true;
            }
        }
        if (!exists) {
            stmt = conn.prepareStatement("INSERT INTO Tag (Name) VALUES (?)");
            stmt.setString(1, tag);
            stmt.executeUpdate();
        }

        stmt = conn.prepareStatement("SELECT ID FROM Tag WHERE Name=?");
        stmt.setString(1, tag);
        final long ID = stmt.executeQuery().getLong("ID");

        updateTagList();

        return new Tag(ID, tag);
    }

    /**
     * Create a tag reference to a document
     *
     * @param TAGID      the tagid
     * @param DOCUMENTID the documentid
     * @throws SQLException the sql exception
     */
    public void createTagReference(final long TAGID, final String DOCUMENTID) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM TagReference WHERE TagID=? AND DocumentID=?");
        stmt.setLong(1, TAGID);
        stmt.setString(2, DOCUMENTID);

        ResultSet rs = stmt.executeQuery();
        if (!rs.next()) {
            stmt = conn.prepareStatement("INSERT INTO TagReference (DocumentID, TagID) VALUES (?, ?)");
            stmt.setString(1, DOCUMENTID);
            stmt.setLong(2, TAGID);
            stmt.executeUpdate();
        }
    }

    /**
     * Create an archive in the databse and return it
     *
     * @param archiveData the archive data
     * @return the archive
     * @throws SQLException the sql exception
     */
    public Archive createArchive(final String[] archiveData) throws SQLException {
        assert archiveData.length == 3;

        // Add Archive to table
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Archive (shed, rack, folder) VALUES (?, ?, ?)");
        stmt.setString(1, archiveData[0]);
        stmt.setString(2, archiveData[1]);
        stmt.setString(3, archiveData[2]);
        stmt.executeUpdate();

        String[] archiveDataWithID = new String[]{String.valueOf(getAutoIncrementValueOfArchive()), archiveData[0], archiveData[1], archiveData[2]};

        // Build Archive and return it
        return (Archive) LocationFactory.getLocation(LocationTypes.ARCHIVE, archiveDataWithID);
    }

    private int getAutoIncrementValueOfArchive() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT seq FROM sqlite_sequence WHERE name=?");
        stmt.setString(1, "Archive");
        ResultSet rs = stmt.executeQuery();
        rs.next();

        return rs.getInt("seq");
    }

    /**
     * Remove a tag form the database
     *
     * @param tagID the tag id
     * @throws SQLException the sql exception
     */
    public void removeTag(final long tagID) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM TAG WHERE ID=?");
        stmt.setLong(1, tagID);
        stmt.executeUpdate();

        stmt = conn.prepareStatement("DELETE FROM TagReference WHERE TagID=?");
        stmt.setLong(1, tagID);
        stmt.executeUpdate();
    }

    /**
     * Search the documents with the given values
     *
     * @param ID       the id
     * @param tags     the tags
     * @param location the location
     * @param title    the title
     * @param author   the author
     * @return the list
     * @throws SQLException the sql exception
     */
    public List<Document> search(final String ID, final List<Tag> tags, final String location, final String title, final String author) throws SQLException {
        final List<Document> documents = this.read();

        // filter ID
        if (!ID.isEmpty()) {
            documents.removeIf(e -> !e.getID().equals(ID));
        }

        // filter Tags (removes nothing if tags is empty
        documents.removeIf(e -> !e.getTags().containsAll(tags));

        // filter location
        if (!location.isEmpty()) {
            documents.removeIf(e -> !e.getLocation().getLocation().equals(location));
        }

        // filter title
        if (!title.isEmpty()) {
            documents.removeIf(e -> !e.getTitle().equals(title));
        }

        // filter author
        if (!author.isEmpty()) {
            documents.removeIf(e -> !e.getAuthor().equals(author));
        }

        return documents;
    }

    /**
     * Create a document in the database
     *
     * @param documentID     the document id
     * @param documentTitle  the document title
     * @param documentAuthor the document author
     * @throws SQLException the sql exception
     */
    public void createDocument(String documentID, String documentTitle, String documentAuthor) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Document (ID, Title, Author) VALUES (?, ?, ?)");
        stmt.setString(1, documentID);
        stmt.setString(2, documentTitle);
        stmt.setString(3, documentAuthor);
        stmt.executeUpdate();
    }

    /**
     * Getter for the complete tagList
     *
     * @return the tags
     */
    public List<Tag> getTags() {
        return tags;
    }

    /**
     * Delete tag reference from the database
     *
     * @param tagId  the tag id
     * @param documentID the document id
     * @throws SQLException the sql exception
     */
    public void deleteTagReference(long tagId, String documentID) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM TagReference WHERE DocumentID=? AND TagID=?");
        stmt.setString(1, documentID);
        stmt.setLong(2, tagId);
        stmt.executeUpdate();
    }
}
