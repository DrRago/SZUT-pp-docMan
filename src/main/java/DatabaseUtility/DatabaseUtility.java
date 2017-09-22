package DatabaseUtility;

import Document.Document;
import Document.Tag;
import Document.Archive;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
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

    public List<Document> read() throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM Document");

        List<Document> documentList = new ArrayList<Document>();

        while (rs.next()) {
            final String ID = rs.getString("ID");
            final String TITLE = rs.getString("Title");
            final String AUTHOR = rs.getString("Author");
            final String FILE = rs.getString("FilePath");
            final String URL = rs.getString("URL");

            final int ARCHIVEID = rs.getInt("Archive");

            Archive archive = null;

            final List<Tag> TAGLIST = new ArrayList<Tag>();

            if (ARCHIVEID != 0) {
                ResultSet archiveRS = stmt.executeQuery("SELECT * FROM Archive WHERE ID='" + ARCHIVEID + "'");
                while (archiveRS.next()) {
                    final String SHED = archiveRS.getString("shed");
                    final String RACK = archiveRS.getString("rack");
                    final String FOLDER = archiveRS.getString("folder");

                    archive = new Archive(SHED, RACK, FOLDER);
                }
            }

            ResultSet tagReferenceRS = stmt.executeQuery("SELECT TagID FROM TagReference WHERE DocumentID='" + ID + "'");

            while (tagReferenceRS.next()) {
                ResultSet tagRS = stmt.executeQuery("SELECT * FROM Tag WHERE ID='"+ tagReferenceRS.getInt("TagID") + "'");

                while (tagRS.next()) {
                    TAGLIST.add(new Tag(tagRS.getInt("ID"), tagRS.getString("Name")));
                }
            }


            documentList.add(new Document(ID, TITLE, AUTHOR, FILE, URL, archive, TAGLIST));
        }

        return documentList;
    }

    public List<Document> search(List<Tag> tags) throws SQLException {
        /// TODO: 15.09.2017 merge with read when working properly
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM Document");

        List<Document> documentList = new ArrayList<Document>();

        while (rs.next()) {
            final String ID = rs.getString("ID");
            final String TITLE = rs.getString("Title");
            final String AUTHOR = rs.getString("Author");
            final String FILE = rs.getString("FilePath");
            final String URL = rs.getString("URL");

            final int ARCHIVEID = rs.getInt("Archive");

            Archive archive = null;

            List<Tag> TAGLIST = new ArrayList<Tag>();

            if (ARCHIVEID != 0) {
                ResultSet archiveRS = stmt.executeQuery("SELECT * FROM Archive WHERE ID='" + ARCHIVEID + "'");
                while (archiveRS.next()) {
                    final String SHED = archiveRS.getString("shed");
                    final String RACK = archiveRS.getString("rack");
                    final String FOLDER = archiveRS.getString("folder");

                    archive = new Archive(SHED, RACK, FOLDER);
                }
            }

            ResultSet tagReferenceRS = stmt.executeQuery("SELECT TagID FROM TagReference WHERE DocumentID='" + ID + "'");

            while (tagReferenceRS.next()) {
                ResultSet tagRS = stmt.executeQuery("SELECT * FROM Tag WHERE ID='"+ tagReferenceRS.getInt("TagID") + "'");

                while (tagRS.next()) {
                    TAGLIST.add(new Tag(tagRS.getInt("ID"), tagRS.getString("Name")));
                }
            }

            TAGLIST = new ArrayList<Tag>();
            TAGLIST.add(new Tag(0, "Book"));
            TAGLIST.add(new Tag(5, "Porn"));

            System.out.println("TAGLIST:");
            System.out.println(TAGLIST.get(0).getId());
            System.out.println(TAGLIST.get(0).getName());
            System.out.println(TAGLIST.get(1).getId());
            System.out.println(TAGLIST.get(1).getName());
            System.out.println("\ntags");
            System.out.println(tags.get(0).getId());
            System.out.println(tags.get(0).getName());
            System.out.println(tags.get(1).getId());
            System.out.println(tags.get(1).getName());

            System.out.println(TAGLIST.containsAll(tags));

            if ((TAGLIST.containsAll(tags))){
                System.out.println("HEE");
                documentList.add(new Document(ID, TITLE, AUTHOR, FILE, URL, archive, TAGLIST));}
        }

        return documentList;
    }
}
