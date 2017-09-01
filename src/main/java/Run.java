import DatabaseUtility.DatabaseUtility;

import java.sql.SQLException;

/**
 * @author Leonhard Gahr
 * @author Pascal de Vries
 * @author Marcel Lillig
 */
public class Run {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        DatabaseUtility db = new DatabaseUtility("jdbc:sqlite:C:\\Users\\Leonhard.Gahr\\IdeaProjects\\DocMann\\src\\main\\resources\\testdb.db3");

        System.out.println(db.read().get(0).getArchive().getShed());
    }
}
