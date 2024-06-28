package dev.sebm.noasis.controller;


import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
public class RegisterController {
    @FXML private ImageView asideImage;
    @FXML private AnchorPane imageAnchorPane;
    public void initialize() {
        //ID from fxml

        imageAnchorPane.heightProperty().addListener((_, _, t1) -> {
            asideImage.setFitHeight(t1.doubleValue());
        });
        imageAnchorPane.widthProperty().addListener((_, _, t1) -> {
            asideImage.setFitWidth(t1.doubleValue());
        });
    }
}
