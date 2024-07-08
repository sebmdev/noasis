package dev.sebm.noasis.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

@Component
public class FlashCardAddEditController implements Initializable {
    @FXML private TextArea taTerm, taDefinition;
    @FXML private Button btnSave, btnCancel;
    @FXML private VBox content;
    @FXML private ProgressIndicator progressIndicator;

    private final CookieStore httpCookieStore = new BasicCookieStore();
    private final ApplicationContext applicationContext;

    @Getter
    @Setter
    private String studySetId;

    public FlashCardAddEditController(Preferences preferences, ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        String sessionCookieValue = preferences.node("session").get("connect.sid", "none");
        if (sessionCookieValue != null) {
            BasicClientCookie sessionCookie = new BasicClientCookie("connect.sid", sessionCookieValue);
            sessionCookie.setPath("/");
            sessionCookie.setDomain("localhost");
            httpCookieStore.addCookie(sessionCookie);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        content.setDisable(false);
        progressIndicator.setVisible(false);

        btnCancel.setOnMouseClicked(_ -> {
            FlashCardsController flashCardsController = applicationContext.getBean(FlashCardsController.class);
            flashCardsController.loadFlashCards(studySetId);
            DashboardLayoutController dashboardLayoutController = applicationContext.getBean(DashboardLayoutController.class);
            dashboardLayoutController.showFlashCards();
            System.out.println("Cancelled");
        });

        btnSave.setOnMouseClicked(_ -> {
            String term = taTerm.getText();
            String definition = taDefinition.getText();

            System.out.println(term);
            System.out.println(definition);
            content.setDisable(true);
            progressIndicator.setVisible(true);
        });
    }
}
