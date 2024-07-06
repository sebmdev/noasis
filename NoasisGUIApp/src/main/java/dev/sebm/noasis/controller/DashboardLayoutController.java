package dev.sebm.noasis.controller;

import atlantafx.base.theme.Styles;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class DashboardLayoutController implements Initializable {
    @FXML private Button btnToggleNav;
    @FXML private AnchorPane nav;

    private boolean navOpened = true;
    private int navWidth;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        navWidth = (int) nav.getPrefWidth();

        btnToggleNav.setOnMouseClicked(e -> {
            TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.4));
            slide.setNode(nav);

            if (navOpened) {
                slide.setToX(-navWidth);
                slide.play();
                nav.setTranslateX(0);
            } else {
                slide.setToX(0);
                slide.play();
                nav.setTranslateX(-navWidth);
            }
            navOpened = !navOpened;
        });
    }
}
