import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;


/**
 * The type Run.
 *
 * @author Leonhard Gahr
 * @author Pascal de Vries
 * @author Marcel Lillig
 */
public class Run extends Application {

    /**
     * Main.
     *
     * @param args the args
     */
    public static void main(String args[]) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL FXMLResource = getClass().getClassLoader().getResource("front.fxml");
        assert FXMLResource != null;
        Parent root = FXMLLoader.load(FXMLResource);

        // set stage parameters
        primaryStage.setTitle("DocMan");

        primaryStage.setScene(new Scene(root));
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(500);
        primaryStage.show();
    }
}
