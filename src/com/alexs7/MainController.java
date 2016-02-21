package com.alexs7;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private BorderPane firstImageBorderPane;
    @FXML private BorderPane secondImageBorderPane;
    @FXML private Pane mainPane;
    @FXML private TextField templateWidthTextField;
    @FXML private TextField templateHeightTextField;

    private ImageChooser imageChooser;
    private Image firstImage;
    private Image secondImage;
    private ImageProcessor imageProcessor;
    private double heightOffset;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainPane.setPrefWidth(600.0);
        mainPane.setPrefHeight(400.00);
        imageChooser = new ImageChooser();
        firstImage = null;
        secondImage = null;
        imageProcessor = new ImageProcessor();
        heightOffset = 1.2;
    }

    public void openImageChooser(MouseEvent event){
        Text source = (Text) event.getSource();
        Scene scene = source.getScene();
        String sourceId = source.getId();
        Stage stage = (Stage) scene.getWindow();

        File imageFile = imageChooser.openImageFile(stage);

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
        imageView.setFitWidth(imageBorderPane.getWidth());
        imageView.setFitHeight(imageBorderPane.getHeight());
        imageView.setSmooth(true);
        imageView.setImage(image);
        imageBorderPane.setCenter(imageView);
    }

    public void generateHybridImage() {

        String templateWidth = templateWidthTextField.getText();
        String templateHeight = templateHeightTextField.getText();

        if(templateHeight.equals("") || templateWidth.equals("")){
            new AlertDialog("Input Error", "Template width and height cannot be empty.").show();
            return;
        }

        if(firstImage == null || secondImage == null){
            new AlertDialog("Input Error", "Enter both images to continue").show();
            return;
        }

        int templateWidthInt = Integer.parseInt(templateWidth);
        int templateHeightInt = Integer.parseInt(templateHeight);

        if((templateWidthInt % 2 == 0) || (templateHeightInt % 2 == 0) ){
            new AlertDialog("Input Error", "Template width and height cannot be even numbers.").show();
            return;
        }

        //if we have passed checks
        imageProcessor.generateHybridImage(firstImage,secondImage,templateWidthInt,templateHeightInt);
    }

    public void resetApplication(ActionEvent actionEvent) throws Exception {
        Stage primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        new Main().start(primaryStage);
    }
}
