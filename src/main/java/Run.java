import DatabaseUtility.DatabaseUtility;
import Document.Document;
import Document.Tag;
import config.Config;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Leonhard Gahr
 * @author Pascal de Vries
 * @author Marcel Lillig
 */
public class Run {


    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        DatabaseUtility db = new DatabaseUtility(Config.databasePath);
        List<Tag> tagList = new ArrayList<>();
        tagList.add(new Tag(2, "Tagggggg"));
        tagList.add(new Tag(1, "TestTag"));
        Document doc = db.read().get(0);
        System.out.println();

        System.out.println("".isEmpty());
    }
}
