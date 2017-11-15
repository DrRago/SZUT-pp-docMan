package Frontend;

import DatabaseUtility.DatabaseUtility;
import Document.Document;
import Location.Archive;
import Location.File;
import Location.Location;
import Location.URL;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


/**
 * @author Leonhard Gahr
 * @author Pascal de Vries
 */
public class ReferenceController {
    @FXML
    private TabPane tabPane;

    @FXML
    private TextField locationArchiveShed;

    @FXML
    private TextField locationArchiveRack;

    @FXML
    private TextField locationArchiveFolder;

    @FXML
    private TextField locationTextFile;

    @FXML
    private TextField locationUrlText;

    void init(Document document, DatabaseUtility database) {
        Location location = document.getLocation();
        switch (location.getLocationType()) {
            case ARCHIVE:
                setLocationArchive((Archive) location);
                break;
            case FILE:
                setLocationFile((File) location);
                break;
            case URL:
                setLocationURL((URL) location);
        }

        final String[] textFileBefore = new String[1];
        locationTextFile.focusedProperty().addListener((ov, t, t1) -> {
            if (t1) {
                textFileBefore[0] = locationTextFile.getText();
            } else {
                if (!textFileBefore[0].equals(locationTextFile.getText())) {
                    clearForFileInput();
                }
            }
        });

        final String[] textURLBefore = new String[1];
        locationUrlText.focusedProperty().addListener((ov, t, t1) -> {
            if (t1) {
                textURLBefore[0] = locationUrlText.getText();
            } else {
                if (!textURLBefore[0].equals(locationUrlText.getText())) {
                    clearForUrlInput();
                }
            }
        });


        final String[] textArchiveShedBefore = new String[1];
        locationArchiveShed.focusedProperty().addListener((ov, t, t1) -> {
            if (t1) {
                textArchiveShedBefore[0] = locationArchiveShed.getText();
            } else {
                if (!textArchiveShedBefore[0].equals(locationArchiveShed.getText())) {
                    clearForArchiveInput();
                }
            }
        });

        final String[] textArchiveRackBefore = new String[1];
        locationArchiveRack.focusedProperty().addListener((ov, t, t1) -> {
            if (t1) {
                textArchiveRackBefore[0] = locationArchiveRack.getText();
            } else {
                if (!textArchiveRackBefore[0].equals(locationArchiveRack.getText())) {
                    clearForArchiveInput();
                }
            }
        });

        final String[] textArchiveFolderBefore = new String[1];
        locationArchiveFolder.focusedProperty().addListener((ov, t, t1) -> {
            if (t1) {
                textArchiveFolderBefore[0] = locationArchiveFolder.getText();
            } else {
                if (!textArchiveFolderBefore[0].equals(locationArchiveFolder.getText())) {
                    clearForArchiveInput();
                }
            }
        });
    }

    private void clearForArchiveInput() {
        locationTextFile.clear();
        locationUrlText.clear();
    }

    private void clearForFileInput() {
        locationUrlText.clear();
        locationArchiveFolder.clear();
        locationArchiveRack.clear();
        locationArchiveShed.clear();
    }

    private void clearForUrlInput() {
        locationTextFile.clear();
        locationArchiveFolder.clear();
        locationArchiveRack.clear();
        locationArchiveShed.clear();
    }

    private void setLocationArchive(Archive archive) {
        locationArchiveShed.setText(archive.getShed());
        locationArchiveRack.setText(archive.getRack());
        locationArchiveFolder.setText(archive.getFolder());

        tabPane.getSelectionModel().select(2);
    }

    private void setLocationFile(File file) {
        locationTextFile.setText(file.getLocation());

        tabPane.getSelectionModel().select(0);
    }

    private void setLocationURL(URL url) {
        locationUrlText.setText(url.toString());

        tabPane.getSelectionModel().select(1);
    }

    @FXML
    private void close() {
        ((Stage) tabPane.getScene().getWindow()).close();
    }

    @FXML
    private void saveAndClose() {
        //TODO
    }
}