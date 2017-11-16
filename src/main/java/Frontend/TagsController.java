package Frontend;

import DatabaseUtility.DatabaseUtility;
import Document.Document;
import Document.Tag;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Leonhard Gahr
 * @author Pascal de Vries
 */
public class TagsController {

    @FXML
    private GridPane gridPane;

    @FXML
    private Button tagButtonDelete;
    @FXML
    private Button tagButtonAdd;
    @FXML
    private ListView<String> tagListview;

    List<Tag> tagList;
    DatabaseUtility database;
    Document document;

    void init(Document document, DatabaseUtility database) {
        // Keyevents to close the window on press escape
        // Not working in listview, because it blocks the key for edit cancel
        gridPane.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if(key.getCode()== KeyCode.ESCAPE) {
                close();
            }
        });

        this.database = database;
        tagList = document.getTags();
        this.document = document;

        List<String> stringTagList = new ArrayList<>(tagList.size());
        tagList.forEach(e -> stringTagList.add(e.toString()));

        ObservableList<String> items = FXCollections.observableArrayList(stringTagList);
        tagListview.setItems(items);

        tagListview.setCellFactory(TextFieldListCell.forListView());

        if (tagListview.getItems().size() == 0) {
            tagButtonDelete.setDisable(true);
        }
    }

    private void close() {
        ((Stage) gridPane.getScene().getWindow()).close();
    }

    @FXML
    private void updateTags(ListView.EditEvent<String> stringEditEvent) throws SQLException {
        String newValue = stringEditEvent.getNewValue();
        String oldValue = tagListview.getItems().get(stringEditEvent.getIndex());

        if (newValue.trim().equals(oldValue.trim())) {
            return;
        }

        if (newValue.trim().equals("")) {
            tagListview.getItems().remove(stringEditEvent.getIndex());
            return;
        }

        if (tagListview.getItems().contains(newValue)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "That tag already exists in this list!");
            alert.showAndWait();
            return;
        }

        if (tagList.size() == 0) {
            tagList.add(database.createTag(newValue, document.getID()));
        } else {
            Tag toAdd = null;

            for (Tag e : tagList) {
                if (e.getName().equals(oldValue)) {
                    e.setName(newValue);

                    database.update(e);
                } else if (tagList.indexOf(e) == tagList.size() - 1) {
                    toAdd = database.createTag(newValue, document.getID());
                }
            }
            if (toAdd != null) {
                tagList.add(toAdd);
            }
        }
        tagListview.getItems().set(stringEditEvent.getIndex(), newValue);
    }

    @FXML
    private void addTag() {
        // TODO No edit if it's the very first element in listview
        if (tagButtonDelete.isDisabled()) {
            tagButtonDelete.setDisable(false);
        }
        tagListview.getItems().add("");
        tagListview.edit(tagListview.getItems().size() - 1);
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

        database.removeTag(removeTag.getId(), document.getID());
    }
}
