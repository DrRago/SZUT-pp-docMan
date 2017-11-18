package Frontend;

import DatabaseUtility.DatabaseUtility;
import Document.Document;
import Document.Tag;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Tags controller.
 *
 * @author Leonhard Gahr
 * @author Pascal de Vries
 */
public class TagsController {

    @FXML
    private TextField newTag;
    @FXML
    private GridPane gridPane;
    @FXML
    private Button tagButtonDelete;
    @FXML
    private Button tagButtonAdd;
    @FXML
    private ListView<String> tagListView;

    private List<Tag> tagList;
    private DatabaseUtility database;
    private Document document;
    private final Map<String, SimpleBooleanProperty> tags = new HashMap<>();

    private boolean selectOnly = false;

    /**
     * Init.
     *
     * @param document the document
     * @param database the database
     */
    void init(Document document, DatabaseUtility database) {
        this.database = database;
        tagList = database.getTags();
        this.document = document;

        Map<String, Long> tagHashMap = new HashMap<>();

        List<String> stringTagList = new ArrayList<>(tagList.size());

        tagList.forEach(e -> {
            tags.put(e.getName(), new SimpleBooleanProperty(document.getTags().contains(e)));
            tagHashMap.put(e.getName(), e.getId());
            stringTagList.add(e.toString());

        });

        ObservableList<String> items = FXCollections.observableArrayList(stringTagList);
        tagListView.setItems(items);

        tagListView.setCellFactory(CheckBoxListCell.forListView((String param) -> {
            BooleanProperty observable = tags.get(param);
            observable.addListener((obs, wasSelected, isNowSelected) -> {
                tags.get(param).set(isNowSelected);
                if (wasSelected) document.getTags().remove(new Tag(tagHashMap.get(param), param));
                if (isNowSelected) document.getTags().add(new Tag(tagHashMap.get(param), param));
            });
            return observable;
        }));

        if (tagListView.getItems().size() == 0) {
            tagButtonDelete.setDisable(true);
        }
    }

    /**
     * Save the changes to the database and close the program
     */
    @FXML
    private void saveAndClose() {
        if (!selectOnly) {
            tagList.forEach(tag -> {
                try {
                    if (document.getTags().contains(tag)) {
                        database.createTagReference(tag.getId(), document.getID());
                    } else {
                        database.deleteTagReference(tag.getId(), document.getID());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }

        ((Stage) gridPane.getScene().getWindow()).close();
    }

    /**
     * Add a tag to the database
     *
     * @throws SQLException the sql exception
     */
    @FXML
    private void addTag() throws SQLException {
        if (newTag.getText().isEmpty()) {
            return;
        }
        if (tagButtonDelete.isDisabled()) {
            tagButtonDelete.setDisable(false);
        }
        tags.put(newTag.getText(), new SimpleBooleanProperty(true));
        tagListView.getItems().add(newTag.getText());
        Tag tag = database.createTag(newTag.getText());
        tagList.add(tag);
        document.getTags().add(tag);
        newTag.clear();
    }

    /**
     * Delete a tag from the database
     *
     * @throws SQLException the sql exception
     */
    @FXML
    private void deleteTag() throws SQLException {
        if (tagListView.getItems().size() == 1) {
            tagButtonDelete.setDisable(true);
        }

        int removeIndex = tagListView.getFocusModel().getFocusedIndex();

        if (removeIndex == -1) {
            return;
        }

        Tag removeTag = tagList.get(removeIndex);

        tagList.remove(removeIndex);
        tagListView.getItems().remove(removeIndex);

        database.removeTag(removeTag.getId());
    }

    /**
     * Init for selection only.
     *
     * @param emptyDoc the empty doc
     * @param database the database
     */
    void initForSelectionOnly(Document emptyDoc, DatabaseUtility database) {
        this.document = emptyDoc;
        selectOnly = true;

        tagList = database.getTags();

        Map<String, Long> tagHashMap = new HashMap<>();

        List<String> stringTagList = new ArrayList<>(tagList.size());

        tagList.forEach(e -> {
            tags.put(e.getName(), new SimpleBooleanProperty(document.getTags().contains(e)));
            tagHashMap.put(e.getName(), e.getId());
            stringTagList.add(e.toString());

        });

        ObservableList<String> items = FXCollections.observableArrayList(stringTagList);
        tagListView.setItems(items);

        tagListView.setCellFactory(CheckBoxListCell.forListView((String param) -> {
            BooleanProperty observable = tags.get(param);
            observable.addListener((obs, wasSelected, isNowSelected) -> {
                if (document.getTags().contains(new Tag(tagHashMap.get(param), param))) {
                    if (wasSelected) {
                        document.getTags().removeIf(e -> e.getName().trim().equals(param.trim()));
                    }
                    return;
                }
                tags.get(param).set(isNowSelected);
                document.getTags().add(new Tag(tagHashMap.get(param), param));
            });
            return observable;
        }));

        tagButtonDelete.setDisable(true);
        tagButtonAdd.setDisable(true);
    }
}