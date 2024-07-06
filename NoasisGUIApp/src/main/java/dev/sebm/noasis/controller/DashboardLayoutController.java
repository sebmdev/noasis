package dev.sebm.noasis.controller;

import atlantafx.base.theme.Styles;
import javafx.animation.*;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class DashboardLayoutController implements Initializable {
    @FXML private Button btnToggleNav;
    @FXML private AnchorPane nav;
    @FXML private BorderPane borderPane;
    @FXML private AnchorPane centerPane;

    @FXML private Button btnStudySets;
    @FXML private Button btnSharedSets;
    @FXML private Button btnGenerateWithAI;
    @FXML private Button btnLogout;

    private boolean navOpened = true;
    private int navWidth;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        navWidth = (int) nav.getPrefWidth();

        btnToggleNav.setOnMouseClicked(e -> {
            TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.4));
            slide.setNode(nav);

            navOpened = !navOpened;
            adjustCenterPane();
        });
    }

    private void adjustCenterPane() {
        if (navOpened) {
            DoubleProperty centerWidth = centerPane.maxWidthProperty();
            DoubleProperty navMaxWidth = nav.maxWidthProperty();

            KeyValue kvCenterStart = new KeyValue(centerWidth, centerPane.getScene().getWindow().getWidth());
            KeyValue kvCenterEnd = new KeyValue(centerWidth, centerPane.getWidth());
            KeyFrame kfCenterStart = new KeyFrame(Duration.ZERO, kvCenterStart);
            KeyFrame kfCenterEnd = new KeyFrame(Duration.millis(400), kvCenterEnd);

            KeyValue kvNavStart = new KeyValue(navMaxWidth, 0);
            KeyValue kvNavEnd = new KeyValue(navMaxWidth, navWidth);
            KeyFrame kfNavStart = new KeyFrame(Duration.ZERO, kvNavStart);
            KeyFrame kfNavEnd = new KeyFrame(Duration.millis(400), kvNavEnd);

            Timeline tl = new Timeline(kfCenterStart, kfCenterEnd, kfNavStart, kfNavEnd);
            tl.play();
            btnStudySets.setAlignment(Pos.BASELINE_LEFT);
            btnSharedSets.setAlignment(Pos.BASELINE_LEFT);
            btnGenerateWithAI.setAlignment(Pos.BASELINE_LEFT);
            btnLogout.setAlignment(Pos.BASELINE_LEFT);
            tl.setOnFinished(_ -> {
                btnStudySets.setContentDisplay(ContentDisplay.LEFT);
                btnSharedSets.setContentDisplay(ContentDisplay.LEFT);
                btnGenerateWithAI.setContentDisplay(ContentDisplay.LEFT);
                btnLogout.setContentDisplay(ContentDisplay.LEFT);
            });
        } else {
            DoubleProperty centerWidth = centerPane.maxWidthProperty();
            DoubleProperty navMaxWidth = nav.maxWidthProperty();

            KeyValue kvCenterStart = new KeyValue(centerWidth, centerPane.getWidth());
            KeyValue kvCenterEnd = new KeyValue(centerWidth, centerPane.getScene().getWindow().getWidth());
            KeyFrame kfCenterStart = new KeyFrame(Duration.ZERO, kvCenterStart);
            KeyFrame kfCenterEnd = new KeyFrame(Duration.millis(400), kvCenterEnd);

            KeyValue kvNavStart = new KeyValue(navMaxWidth, navWidth);
            KeyValue kvNavEnd = new KeyValue(navMaxWidth, 0);
            KeyFrame kfNavStart = new KeyFrame(Duration.ZERO, kvNavStart);
            KeyFrame kfNavEnd = new KeyFrame(Duration.millis(400), kvNavEnd);

            Timeline tl = new Timeline(kfCenterStart, kfCenterEnd, kfNavStart, kfNavEnd);
            tl.play();
            btnStudySets.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            btnSharedSets.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            btnGenerateWithAI.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            btnLogout.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            tl.setOnFinished(_ -> {
                btnStudySets.setAlignment(Pos.BASELINE_CENTER);
                btnGenerateWithAI.setAlignment(Pos.BASELINE_CENTER);
                btnSharedSets.setAlignment(Pos.BASELINE_CENTER);
                btnLogout.setAlignment(Pos.BASELINE_CENTER);
            });
        }


    }
}
