package dev.sebm.noasis.controller;

import dev.sebm.noasis.util.SpringFXMLLoader;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Component
public class dashTestController implements Initializable {
    @FXML private ImageView menu, home, share, ai, logout;
    @FXML private AnchorPane pane1, pane2, mainAncrhoPane;
    @FXML private VBox menubox;
    @FXML private Button sharedBtn;

    private final SpringFXMLLoader springFXMLLoader;

    public dashTestController(SpringFXMLLoader springFXMLLoader) {
        this.springFXMLLoader = springFXMLLoader;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        makeImageViewResponsive(home, menubox);
//        makeImageViewResponsive(share, menubox);
//        makeImageViewResponsive(ai, menubox);
//        makeImageViewResponsive(logout, menubox);


        pane1.setVisible(false);

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), pane1);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        fadeTransition.play();

        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), pane2);
        translateTransition.setByX(-600);
        translateTransition.play();

        menu.setOnMouseClicked(event -> {
            pane1.setVisible(true);

            FadeTransition fadeTransition1 = new FadeTransition(Duration.seconds(0.5), pane1);
            fadeTransition1.setFromValue(0);
            fadeTransition1.setToValue(0.15);
            fadeTransition1.play();

            TranslateTransition translateTransition1 = new TranslateTransition(Duration.seconds(0.5), pane2);
            translateTransition1.setByX(+600);
            translateTransition1.play();
        });

        pane1.setOnMouseClicked(event -> {
            FadeTransition fadeTransition1 = new FadeTransition(Duration.seconds(0.5), pane1);
            fadeTransition1.setFromValue(0.15);
            fadeTransition1.setToValue(0);
            fadeTransition1.play();

            fadeTransition1.setOnFinished(event1 -> {
                pane1.setVisible(false);
            });

            TranslateTransition translateTransition1 = new TranslateTransition(Duration.seconds(0.5), pane2);
            translateTransition1.setByX(-600);
            translateTransition1.play();

        });

        sharedBtn.setOnMouseClicked(event -> {
            loadMainContent("fxml/dashShared");
        });

    }

    private void loadMainContent(String fxmlPath){
        try {
            FXMLLoader loader = springFXMLLoader.getLoader(fxmlPath);
            Parent root = loader.load();
            AnchorPane mainContent = (AnchorPane) root.lookup("#sharedAnchorpane");
            mainAncrhoPane.getChildren().setAll(mainContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private void makeImageViewResponsive(ImageView imageView, VBox vbox) {
//        imageView.fitHeightProperty().bind(vbox.heightProperty().multiply(0.1)); // Adjust the multiplier as needed
//        imageView.fitWidthProperty().bind(vbox.widthProperty().multiply(0.8));   // Adjust the multiplier as needed
//
//    }
}


