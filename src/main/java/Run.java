import DatabaseUtility.DatabaseUtility;
import Document.Tag;

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
        DatabaseUtility db = new DatabaseUtility("jdbc:sqlite:src\\main\\resources\\testdb.db3");
        List<Tag> tagList = new ArrayList<>();
        tagList.add(new Tag(1, "TestTag"));
        System.out.println(db.search(tagList));
    }
}
