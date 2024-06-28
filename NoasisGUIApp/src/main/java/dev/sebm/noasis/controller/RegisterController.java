package dev.sebm.noasis.controller;


import dev.sebm.noasis.util.SpringFXMLLoader;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.springframework.stereotype.Component;


import java.io.IOException;

@Component
public class RegisterController {
    @FXML private ImageView asideImage;
    @FXML private AnchorPane imageAnchorPane;
    @FXML private Button signUp;

    private final SpringFXMLLoader springFXMLLoader;

    public RegisterController(SpringFXMLLoader springFXMLLoader) {
        this.springFXMLLoader = springFXMLLoader;
    }

    public void initialize() {
        //ID from fxml

        imageAnchorPane.heightProperty().addListener((_, _, t1) -> {
            asideImage.setFitHeight(t1.doubleValue());
        });
        imageAnchorPane.widthProperty().addListener((_, _, t1) -> {
            asideImage.setFitWidth(t1.doubleValue());
        });

        signUp.setOnMouseClicked(_ -> {
            Parent pane;
            Stage stage = (Stage)(signUp.getScene().getWindow());

            try {
                pane = springFXMLLoader.loadFXML("fxml/dashTEST");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            stage.getScene().setRoot(pane);
        });
    }
}
