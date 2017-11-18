package Frontend;

import DatabaseUtility.DatabaseUtility;
import Document.Document;
import Location.*;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.sql.SQLException;


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

    private LocationTypes locationType;
    private DatabaseUtility database;
    private Document document;

    private boolean selectOnly = false;

    void init(Document document, DatabaseUtility database) {
        this.document = document;
        this.database = database;
        Location location = document.getLocation();
        locationType = location.getLocationType();

        setInitialLocation();
        addKeyEventListener();

        createFocusEvent(locationTextFile, LocationTypes.FILE);

        createFocusEvent(locationUrlText, LocationTypes.URL);

        createFocusEvent(locationArchiveShed, LocationTypes.ARCHIVE);
        createFocusEvent(locationArchiveRack, LocationTypes.ARCHIVE);
        createFocusEvent(locationArchiveFolder, LocationTypes.ARCHIVE);
    }

    private void createFocusEvent(TextField textField, LocationTypes type) {
        final String[] textBefore = new String[]{""};
        textField.focusedProperty().addListener((ov, t, t1) -> {
            if (t1) {
                textBefore[0] = textField.getText();
            } else {
                if (textBefore[0] != null && !textBefore[0].equals(textField.getText())) {
                    switch (type) {
                        case ARCHIVE:
                            clearForArchiveInput();
                            break;
                        case URL:
                            clearForUrlInput();
                            break;
                        case FILE:
                            clearForFileInput();
                            break;
                    }
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
    private void saveAndClose() throws SQLException {
        Location newLocation;

        if (!locationUrlText.getText().isEmpty()) {
            newLocation = LocationFactory.getLocation(LocationTypes.URL, new String[]{locationUrlText.getText()});
        } else if (!locationTextFile.getText().isEmpty()) {
            newLocation = LocationFactory.getLocation(LocationTypes.FILE, new String[]{locationTextFile.getText()});
        } else if (!locationArchiveShed.getText().isEmpty() | !locationArchiveRack.getText().isEmpty() | !locationArchiveFolder.getText().isEmpty()) {
            if (locationArchiveShed.getText().isEmpty() | locationArchiveRack.getText().isEmpty() | locationArchiveFolder.getText().isEmpty()) {
                Alert archiveIncomplete = new Alert(Alert.AlertType.ERROR, "All information for archives is required");
                archiveIncomplete.showAndWait();
                return;
            }
            String[] archiveData = new String[3];
            archiveData[0] = locationArchiveShed.getText();
            archiveData[1] = locationArchiveRack.getText();
            archiveData[2] = locationArchiveFolder.getText();

            if (!selectOnly) {
                newLocation = database.createArchive(archiveData);
            } else {
                newLocation = LocationFactory.getLocation(LocationTypes.ARCHIVE, new String[]{"1", archiveData[0], archiveData[1], archiveData[2]});
            }
        } else {
            throw new IllegalArgumentException("Location Invalid");
        }
        document.setLocation(newLocation);
        if (!selectOnly) database.update(document);

        this.close();
    }

    public void openAction() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open XML File");
        chooser.setInitialDirectory(new java.io.File(System.getProperty("user.dir")));
        try {
            locationTextFile.setText(chooser.showOpenDialog(new Stage()).getAbsolutePath());
        } catch (NullPointerException e) {
            //
        }
    }

    private void setInitialLocation() {
        Location location = document.getLocation();
        locationType = location.getLocationType();
        switch (locationType) {
            case ARCHIVE:
                setLocationArchive((Archive) location);
                break;
            case FILE:
                setLocationFile((File) location);
                break;
            case URL:
                setLocationURL((URL) location);
        }
    }

    private void addKeyEventListener() {
        tabPane.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if (key.getCode() == KeyCode.ESCAPE) {
                this.close();
            } else if (key.getCode() == KeyCode.ENTER) {
                try {
                    this.saveAndClose();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void initForSelectionOnly(Document emptyDoc) {
        selectOnly = true;

        this.document = emptyDoc;

        setInitialLocation();
        addKeyEventListener();

        createFocusEvent(locationTextFile, LocationTypes.FILE);

        createFocusEvent(locationUrlText, LocationTypes.URL);

        createFocusEvent(locationArchiveShed, LocationTypes.ARCHIVE);
        createFocusEvent(locationArchiveRack, LocationTypes.ARCHIVE);
        createFocusEvent(locationArchiveFolder, LocationTypes.ARCHIVE);
    }
}