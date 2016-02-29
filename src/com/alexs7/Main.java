package com.alexs7;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        bootstrapApp(primaryStage);
    }

    private void bootstrapApp(Stage stage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent root = loader.load();

        stage.setTitle("Hybrid Image Generator");
        stage.setScene(new Scene(root, 700, 400));
        stage.show();
    }
}
