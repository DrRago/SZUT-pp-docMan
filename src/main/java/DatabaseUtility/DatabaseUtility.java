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

    private Archive getArchiveFromID(final int ARCHIVEID) throws SQLException {
        if (ARCHIVEID != 0) {
            ResultSet archiveRS = conn.createStatement().executeQuery("SELECT * FROM Archive WHERE ID='" + ARCHIVEID + "'");

            archiveRS.next();
            final long ID = archiveRS.getInt("ID");
            final String SHED = archiveRS.getString("shed");
            final String RACK = archiveRS.getString("rack");
            final String FOLDER = archiveRS.getString("folder");

            return new Archive(ID, SHED, RACK, FOLDER);
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
            final Location LOCATION = new Location();

            switch (rs.getInt("LocationType")){
                case 0:
                    LOCATION.setLocation(new File(rs.getString("FilePath")));
                    break;
                case 1:
                    LOCATION.setLocation(new URL(rs.getString("URL")));
                    break;
                case 2:
                    LOCATION.setLocation(getArchiveFromID(rs.getInt("Archive")));
            }

            final List<Tag> TAGS = getTaglistFromDocumentID(ID);

            documentList.add(new Document(ID, TITLE, AUTHOR, LOCATION, TAGS));
        }
        return documentList;
    }

    public void removeIDFromTable(final String TABLE, final int ID) throws SQLException {
        conn.createStatement().execute(String.format("DELETE FROM %s WHERE ID='%d'", TABLE, ID));
    }

    public void update(final Archive ARCHIVE) throws SQLException {
        conn.createStatement().execute(String.format("UPDATE Archive SET shed='%s', rack=%s, folder=%s WHERE ID='%d'", ARCHIVE.getShed(), ARCHIVE.getRack(), ARCHIVE.getFolder(), ARCHIVE.getId()));
    }

    public void update(final Tag TAG) throws SQLException {
        conn.createStatement().execute(String.format("UPDATE Tag SET Name='%s' WHERE ID='%d'", TAG.getName(), TAG.getId()));
    }

    public void update(final Document DOCUMENT) {

    }

    public List<Document> search(List<Tag> tags) throws SQLException {
        List<Document> documentList = this.read();
        documentList.removeIf(e -> !e.getTags().containsAll(tags));
        return documentList;
    }
}
