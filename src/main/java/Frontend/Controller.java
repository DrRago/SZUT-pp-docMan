package Frontend;

import DatabaseUtility.DatabaseUtility;
import Document.Document;
import config.Config;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.input.MouseEvent;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Controller {

    @FXML
    private TableView<Map<String, String>> objectTable;
    @FXML
    private TableColumn<Map, String> columnID;
    @FXML
    private TableColumn<Map, String> columnAuthor;
    @FXML
    private TableColumn<Map, String> columnTitle;
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

    private DatabaseUtility db;
    private List<Document> documentList;

    @FXML
    private void initialize() throws SQLException, ClassNotFoundException {
        db = new DatabaseUtility(Config.databasePath);
        documentList = db.read();
        updateTable();
    }

    /**
     * Generate the Attribute table in a HashMap datastructure
     * one row is a Map with two strings (Map<String, String>)
     * The rows are added to a ObservableList which is the datatype for the table items
     *
     * @return A list to set as table items
     */
    private ObservableList<Map<String, String>> generateDataInMap() {
        ObservableList<Map<String, String>> allData = FXCollections.observableArrayList();

        for (Document document : documentList) {
            Map<String, String> dataRow = new HashMap<>();

            String id = document.getID();
            String author = document.getAuthor();
            String title = document.getTitle();

            dataRow.put("ID", id);
            dataRow.put("Author", author);
            dataRow.put("Title", title);

            allData.add(dataRow);
        }
        return allData;
    }

    /**
     * Update the attribute table
     */
    private void updateTable() {
        // set a factory for the cells
        columnID.setCellValueFactory(new MapValueFactory<>("ID"));
        columnAuthor.setCellValueFactory(new MapValueFactory<>("Author"));
        columnTitle.setCellValueFactory(new MapValueFactory<>("Title"));

        // add items to the table
        objectTable.setItems(generateDataInMap());
    }

    public void updateContent(MouseEvent mouseEvent) {

    }
}
