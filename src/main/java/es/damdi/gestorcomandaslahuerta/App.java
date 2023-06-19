package es.damdi.gestorcomandaslahuerta;

import es.damdi.gestorcomandaslahuerta.controllers.MainController;
import es.damdi.gestorcomandaslahuerta.controllers.PrimaryController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static Stage stg;
    private AnchorPane rootLayout;

    @Override
    public void start(Stage stage) throws IOException {
        stg=stage;
        stg.setTitle("LA HUERTA");
        stg.show();

        initLayout();
    }

    public void initLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("main.fxml"));
            rootLayout = (AnchorPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            stg.setScene(scene);

            // Give the controller access to the main app.
            MainController controller = loader.getController();
            controller.setMainApp(this);

            stg.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPrimary() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("primary.fxml"));
            rootLayout = (AnchorPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            stg.setScene(scene);

            // Give the controller access to the main app.
            PrimaryController controller = loader.getController();
            controller.setMainApp(this);

            stg.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public void switchToFXML(String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
            Parent root = loader.load();
            scene = new Scene(root);
            stg.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }

}