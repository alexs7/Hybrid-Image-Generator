package com.alexs7;

import javafx.scene.control.Alert;

/**
 * Created by alex on 21/02/2016.
 */
public class AlertDialog {

    private String title;
    private String contentText;

    public AlertDialog(String title, String contentText) {
        this.title = title;
        this.contentText = contentText;
    }

    public void show(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
}
