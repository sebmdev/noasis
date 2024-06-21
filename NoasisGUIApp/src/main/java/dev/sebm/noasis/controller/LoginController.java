package dev.sebm.noasis.controller;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class LoginController {
    @FXML
    private ImageView asideImage;
    @FXML
    private AnchorPane imageAnchorPane;

    @FXML
    public void initialize() {
        // You can now use the imageView object to manipulate the ImageView component
        asideImage.setFitWidth(imageAnchorPane.getWidth());
        asideImage.setFitHeight(imageAnchorPane.getHeight());
        // Any other initialization code
    }
}
