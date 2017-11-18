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
    private ListView<String> tagListview;

    private List<Tag> tagList;
    private DatabaseUtility database;
    private Document document;
    private final Map<String, SimpleBooleanProperty> tags = new HashMap<>();

    private boolean selectOnly = false;

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
        tagListview.setItems(items);

        tagListview.setCellFactory(CheckBoxListCell.forListView((String param) -> {
            BooleanProperty observable = tags.get(param);
            observable.addListener((obs, wasSelected, isNowSelected) -> {
                tags.get(param).set(isNowSelected);
                if (wasSelected) document.getTags().remove(new Tag(tagHashMap.get(param), param));
                if (isNowSelected) document.getTags().add(new Tag(tagHashMap.get(param), param));
            });
            return observable;
        }));

        if (tagListview.getItems().size() == 0) {
            tagButtonDelete.setDisable(true);
        }
    }

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

    @FXML
    private void addTag() throws SQLException {
        if (newTag.getText().isEmpty()) {
            return;
        }
        if (tagButtonDelete.isDisabled()) {
            tagButtonDelete.setDisable(false);
        }
        tags.put(newTag.getText(), new SimpleBooleanProperty(true));
        tagListview.getItems().add(newTag.getText());
        document.getTags().add(database.createTag(newTag.getText()));
        newTag.clear();
    }

    @FXML
    private void deleteTag() throws SQLException {
        if (tagListview.getItems().size() == 1) {
            tagButtonDelete.setDisable(true);
        }

        int removeIndex = tagListview.getFocusModel().getFocusedIndex();

        if (removeIndex == -1) {
            return;
        }

        Tag removeTag = tagList.get(removeIndex);

        tagList.remove(removeIndex);
        tagListview.getItems().remove(removeIndex);

        database.removeTag(removeTag.getId());
    }

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
        tagListview.setItems(items);

        tagListview.setCellFactory(CheckBoxListCell.forListView((String param) -> {
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