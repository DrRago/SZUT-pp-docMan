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
        DatabaseUtility db = new DatabaseUtility("jdbc:sqlite:C:\\Users\\Pascal\\IdeaProjects\\DocMan\\src\\main\\resources\\testdb.db3");
        List<Tag> tagList = new ArrayList<Tag>();
        tagList.add(new Tag(0, "Book"));
        tagList.add(new Tag(5, "Porn"));
        db.search(tagList);
    }
}
