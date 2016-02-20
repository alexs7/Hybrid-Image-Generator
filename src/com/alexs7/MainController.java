package com.alexs7;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javax.xml.transform.Source;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private BorderPane firstImageBorderPane;
    @FXML private BorderPane secondImageBorderPane;
    @FXML private Pane mainPane;

    private ImageChooser imageChooser;
    private Image firstImage;
    private Image secondImage;
    private ImageProcessor imageProcessor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainPane.setPrefWidth(600.0);
        mainPane.setPrefHeight(400.00);
        imageChooser = new ImageChooser();
        firstImage = null;
        secondImage = null;
        imageProcessor = new ImageProcessor();
    }

    public void openImageChooser(MouseEvent event){
        Text source = (Text) event.getSource();
        Scene scene = source.getScene();
        String sourceId = source.getId();
        Stage stageOfEvent = (Stage) scene.getWindow();

        File imageFile = imageChooser.openImageFile(stageOfEvent);

        if(sourceId.equals("firstImageChooser")){
            firstImage = new Image(imageFile.toURI().toString());
            renderImage(firstImage,firstImageBorderPane);
        }else{
            secondImage = new Image(imageFile.toURI().toString());
            renderImage(secondImage,secondImageBorderPane);
        }
    }

    private void renderImage(Image image, BorderPane imageBorderPane) {
        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setImage(image);

        imageView.setFitWidth(image.getWidth());
        imageView.setFitHeight(image.getHeight());

        imageBorderPane.setCenter(imageView);
    }

    public void generateHybridImage(ActionEvent actionEvent) {
        imageProcessor.generateHybridImage(firstImage,secondImage);
    }
}
