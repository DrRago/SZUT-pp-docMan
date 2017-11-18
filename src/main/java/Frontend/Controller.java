package Frontend;

import DatabaseUtility.DatabaseUtility;
import Document.Document;
import Document.Tag;
import Location.LocationFactory;
import Location.LocationTypes;
import config.Config;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * The type Controller.
 */
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
    private Button buttonSearch;
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

    // document creation variables
    private boolean createDocumentState = false;
    private final String[] documentID = new String[]{""};
    private final String[] documentTitle = new String[]{""};
    private final String[] documentAuthor = new String[]{""};
    private boolean cancelAdding;

    // search states
    private final int IDLE = 0;
    private final int FILLFORMULAR = 1;

    private int currentState = IDLE;
    private List<Tag> searchedTags;


    // the javafx initialization method
    @FXML
    private void initialize() throws SQLException, ClassNotFoundException {
        db = new DatabaseUtility(Config.databasePath);
        documentList = db.read();
        updateTable();

        objectTable.getFocusModel().focusedItemProperty().addListener((obs, oldSelection, newSelection) -> updateContent());

        createFocusEvent(textID, documentID);
        createFocusEvent(textTitle, documentTitle);
        createFocusEvent(textAuthor, documentAuthor);

    }

    /**
     * create the focus events for the textFields if we create a new document
     *
     * @param textField  the textfield
     * @param saveString the string array to store the textField text in
     */
    private void createFocusEvent(TextField textField, final String[] saveString) {
        textField.focusedProperty().addListener((ov, t, t1) -> {
            if (!t1 & createDocumentState & !textField.getText().isEmpty()) {
                saveString[0] = textField.getText();
                if (!saveString[0].isEmpty() & !documentTitle[0].isEmpty() & !documentAuthor[0].isEmpty() & !cancelAdding) {
                    try {
                        db.createDocument(documentID[0], documentTitle[0], documentAuthor[0]);
                        createDocumentState = false;
                        updateTable();

                        documentID[0] = "";
                        documentTitle[0] = "";
                        documentAuthor[0] = "";

                        textID.setEditable(false);
                    } catch (SQLException e) {
                        System.out.println(e.getLocalizedMessage());
                        Alert idTakenAlert = new Alert(Alert.AlertType.WARNING, "Document ID is already taken");
                        idTakenAlert.showAndWait();
                        textID.requestFocus();
                    }
                } else if (cancelAdding) {
                    cancelAdding = false;
                }
            }
        });
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
     *
     * @param documents if given, the table is updated with these documents
     */
    private void updateTable(List<Document> documents) throws SQLException {
        if (documents.isEmpty()) {
            documentList = db.read();
        } else {
            documentList = documents;
        }

        int selection = objectTable.getSelectionModel().getFocusedIndex();

        // set a factory for the cells
        columnID.setCellValueFactory(new MapValueFactory<>("ID"));
        columnAuthor.setCellValueFactory(new MapValueFactory<>("Author"));
        columnTitle.setCellValueFactory(new MapValueFactory<>("Title"));

        // add items to the table
        objectTable.setItems(generateDataInMap());

        objectTable.getSelectionModel().select(selection);

        updateContent();
    }

    /**
     * Update the content of the textFields
     */
    private void updateContent() {
        if (currentState == FILLFORMULAR) return;
        if (!createDocumentState) {
            Document e = getFocusedDocument();
            if (e != null) {
                textID.setText(e.getID());
                textAuthor.setText(e.getAuthor());
                textTitle.setText(e.getTitle());
                textTags.setText(e.getTags().toString().replaceAll("\\[|]", ""));
                textLocation.setText(e.getLocation().getLocation());
            } else {
                clearInformation();
            }
        } else {
            if (textID.getText().isEmpty() | textTitle.getText().isEmpty() | textAuthor.getText().isEmpty()) {
                objectTable.getItems().remove(objectTable.getItems().size() - 1);
                createDocumentState = false;
                updateContent();
            }
        }
    }

    /**
     * clear the textFields
     */
    private void clearInformation() {
        textID.clear();
        textAuthor.clear();
        textTitle.clear();
        textTags.clear();
        textLocation.clear();
    }

    /**
     * get the focused document ID in the table
     *
     * @return the ID
     */
    private String getFocusedDocumentID() {
        if (objectTable.getFocusModel().getFocusedItem() != null) {
            return objectTable.getFocusModel().getFocusedItem().get("ID");
        }
        return null;
    }

    /**
     * Get the focused document in the table
     *
     * @return the document
     */
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

    /**
     * Delete action.
     *
     * @throws SQLException the sql exception
     */
    public void deleteAction() throws SQLException {
        String id = objectTable.getFocusModel().getFocusedItem().get("ID");
        db.deleteDocument(id);
        documentList = db.read();
        updateTable();

        objectTable.getSelectionModel().select(objectTable.getItems().size() - 1);
        updateContent();
    }

    /**
     * start the add document process and ad a empty document to the table
     */
    @FXML
    private void addDocumentToTable() {
        Map<String, String> dataRow = new HashMap<>();

        dataRow.put("ID", "Fill out information below");
        dataRow.put("Author", "Fill out information below");
        dataRow.put("Title", "Fill out information below");

        objectTable.getItems().add(dataRow);
        objectTable.getSelectionModel().select(objectTable.getItems().size() - 1);

        createDocumentState = true;

        clearInformation();
        textID.setEditable(true);
    }

    /**
     * Update a document in the table
     *
     * @throws SQLException the sql exception
     */
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

    /**
     * Open the tag manager for the selected document
     *
     * @throws IOException the io exception
     * @throws SQLException the sql exception
     */
    @FXML
    private void openTagManager() throws IOException, SQLException {
        final FXMLLoader loader = new FXMLLoader();

        URL FXMLResource = getClass().getClassLoader().getResource("tags.fxml");

        assert FXMLResource != null;

        Parent root = loader.load(FXMLResource.openStream());


        Document emptyDoc = new Document("", "", "", LocationFactory.getLocation(LocationTypes.URL, new String[]{""}), new ArrayList<>());

        //Get the Controller from the FXMLLoader
        TagsController controller = loader.getController();
        if (currentState == FILLFORMULAR) {
            controller.initForSelectionOnly(emptyDoc, db);
        } else {
            controller.init(getFocusedDocument(), db);
        }
        final String title = objectTable.getFocusModel().getFocusedItem().get("Title");

        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle(String.format("Tags of %s", title));
        stage.setScene(new Scene(root));

        // block primary window
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(objectTable.getScene().getWindow());

        stage.showAndWait();

        db.updateTagList();

        if (currentState == FILLFORMULAR) {
            textTags.setText(emptyDoc.getTags().toString().replaceAll("\\[|]", ""));
            this.searchedTags = emptyDoc.getTags();
        }
        updateTable();
    }

    /**
     * Open the reference manager for the selected document
     *
     * @throws IOException the io exception
     * @throws SQLException the sql exception
     */
    @FXML
    private void openReferenceManager() throws IOException, SQLException {
        final FXMLLoader loader = new FXMLLoader();

        //Load the gui.fxml
        java.net.URL URL = getClass().getClassLoader().getResource("reference.fxml");
        assert URL != null;
        Parent root = loader.load(URL.openStream());

        Document emptyDoc = new Document("", "", "", LocationFactory.getLocation(LocationTypes.URL, new String[]{""}), new ArrayList<>());

        //Get the Controller from the FXMLLoader
        ReferenceController controller = loader.getController();
        if (currentState == FILLFORMULAR) {
            controller.initForSelectionOnly(emptyDoc);
        } else {
            controller.init(getFocusedDocument(), db);
        }

        final String title = objectTable.getFocusModel().getFocusedItem().get("Title");

        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle(String.format("Reference of %s", title));
        stage.setScene(new Scene(root));

        // block primary window
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(objectTable.getScene().getWindow());

        stage.showAndWait();

        if (currentState == FILLFORMULAR) {
            textLocation.setText(emptyDoc.getLocation().getLocation());
        }
        updateTable();
    }

    /**
     * Execute search.
     *
     * @throws SQLException the sql exception
     */
    public void executeSearch() throws SQLException {
        final int ACTIVESEARCH = 2;
        switch (currentState) {
            case IDLE:
                buttonSearch.setText("execute");
                clearInformation();
                textID.setEditable(true);
                currentState = FILLFORMULAR;
                break;
            case FILLFORMULAR:
                buttonSearch.setText("reset");
                List<Document> searchResults = db.search(textID.getText(), searchedTags, textLocation.getText(), textTitle.getText(), textAuthor.getText());
                updateTable(searchResults);
                currentState = ACTIVESEARCH;
                break;
            case ACTIVESEARCH:
                buttonSearch.setText("search");
                updateTable();
                textID.setEditable(false);
                currentState = IDLE;
        }
    }

    private void updateTable() throws SQLException {
        updateTable(new ArrayList<>());
    }

    /**
     * Close the program
     */
    public void close() {
        ((Stage) textLocation.getScene().getWindow()).close();
    }
}
