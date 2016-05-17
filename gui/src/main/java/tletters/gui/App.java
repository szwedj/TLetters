package tletters.gui;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class App extends Application {
    private Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;

        Parent root = FXMLLoader.load(getClass().getResource("stage_main.fxml"));
        root.getStylesheets().addAll(
                getClass().getResource("light.css").toExternalForm(),
//                getClass().getResource("dark.css").toExternalForm(),
                getClass().getResource("style.css").toExternalForm());
        Scene scene = new Scene(root);

        primaryStage.setTitle("TLetters");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @FXML
    protected void handleChooseFileAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", ".png", ".jpeg", ".gif"),
                new FileChooser.ExtensionFilter("All files", "*.*")
        );
        File file = fileChooser.showOpenDialog(stage);
        // TODO: process file
    }
}
