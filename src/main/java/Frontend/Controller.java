package Frontend;

import DatabaseUtility.DatabaseUtility;
import Document.Document;
import config.Config;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
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
    private TextArea textLocation;

    private DatabaseUtility db;
    private List<Document> documentList;
    private boolean createDocumentState;

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

        updateContent();
    }

    public void updateContent() {
        Document e = getFocusedDocument();
        if (e != null) {
            textID.setText(e.getID());
            textAuthor.setText(e.getAuthor());
            textTitle.setText(e.getTitle());
            textTags.setText(e.getTags().toString().replaceAll("\\[|\\]", ""));
            textLocation.setText(e.getLocation().getLocation());
        } else {
            if (createDocumentState) {
                textID.setEditable(true);
            }
            textID.clear();
            textAuthor.clear();
            textTitle.clear();
            textTags.clear();
            textLocation.clear();
        }
    }

    private String getFocusedDocumentID() {
        if (objectTable.getFocusModel().getFocusedItem() != null) {
            return objectTable.getFocusModel().getFocusedItem().get("ID");
        }
        return null;
    }

    private Document getFocusedDocument() {
        final String ID = this.getFocusedDocumentID();
        // using final array because of lambda expression
        final Document[] document = {null};
        documentList.forEach(e -> {
            if (e.getID().equals(ID)) {
                document[0] = e;
            }
        });
        return document[0];
    }

    public void deleteAction(ActionEvent actionEvent) throws SQLException {
        String id = objectTable.getFocusModel().getFocusedItem().get("ID");
        db.deleteDocument(id);
        documentList = db.read();
        updateTable();
    }

    @FXML
    private void addDocumentToTable(){
        Map<String, String> dataRow = new HashMap<>();

        dataRow.put("ID", "");
        dataRow.put("Author", "");
        dataRow.put("Title", "");

        objectTable.getItems().add(dataRow);
        objectTable.getFocusModel().focus(objectTable.getItems().size()-1);
        updateContent();

        createDocumentState = true;
        // TODO
    }

    @FXML
    private void updateDocument() throws SQLException {
        Document document = getFocusedDocument();
        if (textAuthor.getText().isEmpty() | textTitle.getText().isEmpty()) {
            Alert emptyAlert = new Alert(Alert.AlertType.ERROR, "Please fill all required information");
            emptyAlert.showAndWait();
        } else {
            document.setAuthor(textAuthor.getText());
            document.setTitle(textTitle.getText());
            db.update(document);
            updateTable();
        }
    }

    @FXML
    private void openTagManager() throws IOException {
        final FXMLLoader loader = new FXMLLoader();

        URL FXMLResource = getClass().getClassLoader().getResource("tags.fxml");

        assert FXMLResource != null;

        Parent root = loader.load(FXMLResource.openStream());

        //Get the Controller from the FXMLLoader
        TagsController controller = loader.getController();
        controller.init(getFocusedDocument(), db);

        final String title = objectTable.getFocusModel().getFocusedItem().get("Title");

        Stage stage = new Stage();
        stage.setTitle(String.format("Tags of %s", title));
        stage.setScene(new Scene(root));

        // block primary window
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(objectTable.getScene().getWindow());

        stage.showAndWait();
        updateTable();
    }

    @FXML
    private void openReferenceManager() throws IOException {
        final FXMLLoader loader = new FXMLLoader();

        //Load the gui.fxml
        Parent root = loader.load(getClass().getClassLoader().getResource("reference.fxml").openStream());


        //Get the Controller from the FXMLLoader
        ReferenceController controller = loader.getController();
        controller.init(getFocusedDocument(), db);

        final String title = objectTable.getFocusModel().getFocusedItem().get("Title");

        Stage stage = new Stage();
        stage.setTitle(String.format("Reference of %s", title));
        stage.setScene(new Scene(root));

        // block primary window
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(objectTable.getScene().getWindow());

        stage.showAndWait();
        updateTable();
    }
}
