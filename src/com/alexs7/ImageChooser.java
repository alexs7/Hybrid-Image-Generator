package com.alexs7;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * Created by alex on 20/02/2016.
 */
public class ImageChooser {

    private FileChooser fileChooser;

    public ImageChooser() {
        fileChooser = new FileChooser();
    }

    public File openImageFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose an Image...");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        File file = fileChooser.showOpenDialog(stage);
        if(file !=null){
            return file;
        }
        return null;
    }
}
