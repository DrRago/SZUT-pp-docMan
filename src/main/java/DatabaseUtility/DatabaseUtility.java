package DatabaseUtility;

import Document.Document;
import Document.Tag;
import Location.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Leonhard Gahr
 * @author Pascal de Vries
 */
public class DatabaseUtility {
    private Connection conn;

    public DatabaseUtility(final String path) throws SQLException, ClassNotFoundException {
        conn = DriverManager.getConnection(path);
    }

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

    private List<Tag> getTaglistFromDocumentID(final String ID) throws SQLException {
        final List<Tag> TAGS = new ArrayList<Tag>();

        ResultSet tagReferenceRS = conn.createStatement().executeQuery("SELECT TagID FROM TagReference WHERE DocumentID='" + ID + "'");

        while (tagReferenceRS.next()) {
            ResultSet tagRS = conn.createStatement().executeQuery("SELECT * FROM Tag WHERE ID='" + tagReferenceRS.getInt("TagID") + "'");

            tagRS.next();
            TAGS.add(new Tag(tagRS.getInt("ID"), tagRS.getString("Name")));
        }

        return TAGS;
    }

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
                    location = LocationFactory.getLocation(LocationTypes.FILE, new String[]{rs.getString("FilePath")});
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

            final List<Tag> TAGS = getTaglistFromDocumentID(ID);

            documentList.add(new Document(ID, TITLE, AUTHOR, location, TAGS));
        }
        return documentList;
    }

    private void removeIDFromTable(final String TABLE, final int ID) throws SQLException {
        conn.createStatement().execute(String.format("DELETE FROM %s WHERE ID='%d'", TABLE, ID));
    }

    public void deleteCodument(final String ID) {
        // TODO
    }

    public void update(final Archive ARCHIVE) throws SQLException {
        conn.createStatement().execute(String.format("UPDATE Archive SET shed='%s', rack=%s, folder=%s WHERE ID='%d'", ARCHIVE.getShed(), ARCHIVE.getRack(), ARCHIVE.getFolder(), ARCHIVE.getId()));
    }

    public void update(final Tag TAG) throws SQLException {
        conn.createStatement().execute(String.format("UPDATE Tag SET Name='%s' WHERE ID='%d'", TAG.getName(), TAG.getId()));
    }

    public void update(final Document DOCUMENT) throws SQLException {
        Location location = DOCUMENT.getLocation();
        int locationInt;

        // update the location of document
        if (location.getClass().equals(Archive.class)) {
            update((Archive) location);
            locationInt = 2;
        } else if (location.getClass().equals(URL.class)) {
            conn.createStatement().execute(String.format("UPDATE Document SET URL='%s' WHERE ID='%s'", location.getLocation(), DOCUMENT.getID()));
            locationInt = 1;
        } else if (location.getClass().equals(File.class)) {
            conn.createStatement().execute(String.format("UPDATE Document SET FilePath='%s' WHERE ID='%s'", location.getLocation(), DOCUMENT.getID()));
            locationInt = 0;
        } else {
            throw new IllegalArgumentException("Unknown Location");
        }

        // tags handled by GUI

        // update author and title of the document
        conn.createStatement().execute(String.format("UPDATE Document SET Author='%s', Title='%s', LocationType='%d' WHERE ID='%s'", DOCUMENT.getAuthor(), DOCUMENT.getTitle(), locationInt, DOCUMENT.getID()));
    }

    public void createTag(String tag) throws SQLException {
        conn.createStatement().execute(String.format("INSERT INTO Tag (Name) VALUES ('%s')", tag));
    }

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
}
