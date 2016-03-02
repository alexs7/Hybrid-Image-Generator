package com.alexs7;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
    @FXML private BorderPane hybridImageBorderPane;
    @FXML private TextField firstGaussianDeviation;
    @FXML private TextField secondGuassianDeviation;

    private ImageChooser imageChooser;
    private Image firstImage;
    private Image secondImage;
    private Image hybridImage;
    private ImageProcessor imageProcessor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        imageChooser = new ImageChooser();
        firstImage = null;
        secondImage = null;
        imageProcessor = new ImageProcessor();
    }

    public void openImageChooser(ActionEvent event){
        Button source = (Button) event.getSource();
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
        imageView.setImage(image);
        imageBorderPane.setCenter(imageView);
    }

    public void generateHybridImage() {

        String firstDeviation = firstGaussianDeviation.getText();
        String secondDeviation = secondGuassianDeviation.getText();

        if(firstDeviation.equals("") || secondDeviation.equals("")){
            new AlertDialog("Input Error", "Please enter both standard deviations!").show();
            return;
        }

        if(firstImage == null || secondImage == null){
            new AlertDialog("Input Error", "Enter both images to continue").show();
            return;
        }

        //if we have passed checks
        hybridImage = null;
        try {
            hybridImage = imageProcessor.generateHybridImage(firstImage,
                                               secondImage,
                                               Double.parseDouble(firstDeviation),
                                               Double.parseDouble(secondDeviation));
        } catch (InvalidKernelSize invalidKernelSize) {
            new AlertDialog("Input Error", invalidKernelSize.getMessage()).show();
        }

        renderImage(hybridImage,hybridImageBorderPane);
    }

    public void resetApplication(ActionEvent actionEvent) throws Exception {
        Stage primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        new Main().start(primaryStage);
    }

    public void hybridImageZoomIn(ActionEvent event) {
        if(hybridImage != null){
            ImageView imageView = (ImageView) hybridImageBorderPane.getCenter();
            imageView.setFitHeight(imageView.getFitHeight() + imageView.getFitHeight() * 0.1);
            imageView.setFitWidth(imageView.getFitWidth() + imageView.getFitWidth() * 0.1);
        }
    }

    public void hybridImageZoomOut(ActionEvent event) {
        if(hybridImage != null){
            ImageView imageView = (ImageView) hybridImageBorderPane.getCenter();
            imageView.setFitHeight(imageView.getFitHeight() - imageView.getFitHeight() * 0.1);
            imageView.setFitWidth(imageView.getFitWidth() - imageView.getFitWidth() * 0.1);
        }
    }
}
