package GUI.JavaFx.RenameModal;

import GUI.JavaFx.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RenameModalGUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage stage) throws Exception {

    }

    public String startWithReturn(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("RenameModal.fxml"));
        Parent root = loader.load();

        RenameModalController controller = loader.getController();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Rename block");

        stage.showAndWait();

        return controller.getNewName();

    }
}
