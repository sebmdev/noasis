package dev.sebm.noasis.controller;

import dev.sebm.noasis.util.SpringFXMLLoader;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
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
public class DashHomeController implements Initializable {
    @FXML private ImageView menu, home, share, ai, logout;
    @FXML private AnchorPane pane1, pane2, mainAncrhoPane;
    @FXML private VBox menubox;
    @FXML private Button sharedBtn;

    private final SpringFXMLLoader springFXMLLoader;
    private Boolean isAnimationInProgress = false;

    public DashHomeController(SpringFXMLLoader springFXMLLoader) {
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
            if(isAnimationInProgress) {
                return;
            }
            if (pane1.isVisible()) {

                // If pane1 is visible, fade it out quickly and slide pane2 to the left
                FadeTransition fadeOutTransition = new FadeTransition(Duration.seconds(0.3), pane1);
                fadeOutTransition.setFromValue(0.15);
                fadeOutTransition.setToValue(0);
                fadeOutTransition.setOnFinished(event1 -> pane1.setVisible(false));

                TranslateTransition slideLeftTransition = new TranslateTransition(Duration.seconds(0.3), pane2);
                slideLeftTransition.setByX(-600); // Adjust slide distance as needed

                // Use parallel transition for simultaneous execution
                ParallelTransition parallelTransition = new ParallelTransition(fadeOutTransition, slideLeftTransition);
                parallelTransition.setOnFinished(event1 -> isAnimationInProgress = false);
                parallelTransition.play();

                isAnimationInProgress = true;
                System.out.print("HELLO");
            } else {


                // If pane1 is not visible, fade it in quickly and slide pane2 to the right
                pane1.setVisible(true);

                FadeTransition fadeInTransition = new FadeTransition(Duration.seconds(0.3), pane1);
                fadeInTransition.setFromValue(0);
                fadeInTransition.setToValue(0.15);

                TranslateTransition slideRightTransition = new TranslateTransition(Duration.seconds(0.3), pane2);
                slideRightTransition.setByX(+600); // Adjust slide distance as needed

                // Use parallel transition for simultaneous execution
                ParallelTransition parallelTransition = new ParallelTransition(fadeInTransition, slideRightTransition);
                parallelTransition.setOnFinished(event1 -> isAnimationInProgress = false);
                parallelTransition.play();

                isAnimationInProgress = true;
                System.out.print("HELLOdfsdfdfs");
            }
        });

//        pane1.setOnMouseClicked(event -> {
//            FadeTransition fadeTransition1 = new FadeTransition(Duration.seconds(0.5), pane1);
//            fadeTransition1.setFromValue(0.15);
//            fadeTransition1.setToValue(0);
//            fadeTransition1.play();
//
//            fadeTransition1.setOnFinished(event1 -> {
//                pane1.setVisible(false);
//            });
//
//            TranslateTransition translateTransition1 = new TranslateTransition(Duration.seconds(0.5), pane2);
//            translateTransition1.setByX(-600);
//            translateTransition1.play();
//
//        });

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


