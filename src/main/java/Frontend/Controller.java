package Frontend;

import DatabaseUtility.DatabaseUtility;
import Document.Document;
import config.Config;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

import javax.swing.table.TableColumn;
import javax.swing.text.TabableView;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;


public class Controller {

    @FXML
    private TabableView objectTable;
    @FXML
    private TableColumn columnID;
    @FXML
    private TableColumn columnAuthor;
    @FXML
    private TableColumn columnTitle;
    @FXML
    private Button buttonAdd;
    @FXML
    private Button buttonDelete;
    @FXML
    private Button buttonUpdate;
    @FXML
    private Button buttonSearch;
    @FXML
    private Button buttonAddTag;
    @FXML
    private Button buttonAddLocation;
    @FXML
    private TextField textID;
    @FXML
    private TextField textTitle;
    @FXML
    private TextField textAuthor;
    @FXML
    private TextArea textTags;
    @FXML
    private TextArea textReference;

    DatabaseUtility db;
    List<Document> documentList;

    private void init() throws SQLException, ClassNotFoundException {
        db = new DatabaseUtility(Config.databasePath);
        documentList = db.read();
        updateTable();
    }

    private void updateTable(){

    }

    public void updateContent(MouseEvent mouseEvent) {

    }
}
