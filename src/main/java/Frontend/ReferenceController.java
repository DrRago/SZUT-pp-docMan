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
 * The type Reference controller.
 *
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

    /**
     * Init.
     *
     * @param document the document
     * @param database the database
     */
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

    /**
     * Create focus change events and check if the value has changed on focus out
     *
     * @param textField the textfield
     * @param type      the type of the input reference
     */
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

    /**
     * clear textFields except of the necessary ones for the archive
     */
    private void clearForArchiveInput() {
        locationTextFile.clear();
        locationUrlText.clear();
    }

    /**
     * clear textFields except of the necessary ones for the file
     */
    private void clearForFileInput() {
        locationUrlText.clear();
        locationArchiveFolder.clear();
        locationArchiveRack.clear();
        locationArchiveShed.clear();
    }

    /**
     * clear textFields except of the necessary ones for the url
     */
    private void clearForUrlInput() {
        locationTextFile.clear();
        locationArchiveFolder.clear();
        locationArchiveRack.clear();
        locationArchiveShed.clear();
    }

    /**
     * Set the initial view for to the archive tab
     *
     * @param archive the archive to show
     */
    private void setLocationArchive(Archive archive) {
        locationArchiveShed.setText(archive.getShed());
        locationArchiveRack.setText(archive.getRack());
        locationArchiveFolder.setText(archive.getFolder());

        tabPane.getSelectionModel().select(2);
    }

    /**
     * Set the initial view to the file tab
     *
     * @param file the file to show
     */
    private void setLocationFile(File file) {
        locationTextFile.setText(file.getLocation());

        tabPane.getSelectionModel().select(0);
    }

    /**
     * Set the initial view to the url tab
     *
     * @param url the url to show
     */
    private void setLocationURL(URL url) {
        locationUrlText.setText(url.toString());

        tabPane.getSelectionModel().select(1);
    }

    /**
     * close the window
     */
    @FXML
    private void close() {
        ((Stage) tabPane.getScene().getWindow()).close();
    }

    /**
     * save the changed reference and close the window
     *
     * @throws SQLException the sql exception
     */
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

    /**
     * Open a file open dialog
     */
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

    /**
     * Set the initial view tab for the user to the location type of the document
     */
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

    /**
     * add the key listener to exit the program on escape press
     */
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

    /**
     * Init for selection only.
     *
     * @param emptyDoc the empty document
     */
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