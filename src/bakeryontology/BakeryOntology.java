package bakeryontology;

import static bakeryontology.BakeryFXMLController.connection;
import static bakeryontology.BakeryFXMLController.isChanged;
import bakeryontology.utils.Connection;
import java.io.File;
import java.util.Optional;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

/**
 * @author Adrian Wilk
 * @since 15.05.2017
 */
public class BakeryOntology extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("BakeryFXML.fxml"));
        stage.setTitle("Bakery");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);

        stage.setOnCloseRequest(f -> {
            if(isChanged==true){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("CONFIRMATION");
                alert.setHeaderText("No saved files.");
                alert.setContentText("Do you want to save?");
                Optional<ButtonType> result = alert.showAndWait();

                if (result.get() == ButtonType.OK){
                   connection.saveConnection(null);
                }else{
                    Optional<File> optional = Optional.ofNullable(Connection.temporaryData);
                    optional.ifPresent(file -> Connection.temporaryData.delete());
                    System.exit(0);
                }
            }else{
                Optional<File> optional = Optional.ofNullable(Connection.temporaryData);
                optional.ifPresent(file -> Connection.temporaryData.delete());
                System.exit(0);
            }

        });
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
