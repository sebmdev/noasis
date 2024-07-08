package dev.sebm.noasis.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sebm.noasis.jsonresponses.ErrorResponse;
import dev.sebm.noasis.jsonresponses.models.FlashCard;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

@Component
public class FlashCardsController implements Initializable {
    @FXML private TilePane tilePane;
    @FXML private Button btnAddCard, btnBack;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private AnchorPane content;

    private final CookieStore httpCookieStore = new BasicCookieStore();
    private final ApplicationContext applicationContext;

    public FlashCardsController(Preferences preferences, ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        String sessionCookieValue = preferences.node("session").get("connect.sid", "none");
        if (sessionCookieValue != null) {
            BasicClientCookie sessionCookie = new BasicClientCookie("connect.sid", sessionCookieValue);
            sessionCookie.setPath("/");
            sessionCookie.setDomain("localhost");
            httpCookieStore.addCookie(sessionCookie);
        }
    }

    public void loadFlashCards(String studySetId) {
        tilePane.getChildren().clear();
        progressIndicator.setVisible(true);
        content.setDisable(true);
        try {
            final HttpGet httpGet = new HttpGet("http://localhost:3000/study-sets/" + studySetId);
            System.out.println("Loading study set "+ studySetId);

            httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("Content-type", "application/json");

            CloseableHttpClient httpClient = HttpClientBuilder
                    .create()
                    .setDefaultCookieStore(httpCookieStore)
                    .setDefaultRequestConfig(RequestConfig
                            .custom()
                            .setCookieSpec(CookieSpecs.STANDARD)
                            .build())
                    .build();

            ResponseHandler<String> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                System.out.println(response.getEntity().toString());
                HttpEntity entity = response.getEntity();
                ObjectMapper objectMapper = new ObjectMapper();

                if (status >= 400) {
                    ErrorResponse errorResponse = objectMapper.readValue(entity.getContent(), ErrorResponse.class);
                    System.out.println(errorResponse.getError());
                    return null;
                }
                if (entity != null) {
                    List<FlashCard> flashCards = objectMapper.readValue(EntityUtils.toString(entity),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, FlashCard.class));

                    Platform.runLater(() -> {
                        flashCards.forEach(flashCard -> {
                            TextArea textArea1 = new TextArea();
                            textArea1.setEditable(false);
                            textArea1.setPrefHeight(100.0);
                            textArea1.setPrefWidth(200.0);
                            textArea1.setWrapText(true);
                            textArea1.setText(flashCard.getTerm());

                            // Create the second TextArea and wrap it in an AnchorPane
                            TextArea textArea2 = new TextArea("\n");
                            textArea2.setEditable(false);
                            textArea2.setMaxHeight(Double.MAX_VALUE);
                            textArea2.setMaxWidth(Double.MAX_VALUE);
                            textArea2.setWrapText(true);
                            textArea2.setText(flashCard.getDefinition());

                            AnchorPane anchorPane = new AnchorPane(textArea2);
                            AnchorPane.setBottomAnchor(textArea2, 0.0);
                            AnchorPane.setLeftAnchor(textArea2, 0.0);
                            AnchorPane.setRightAnchor(textArea2, 0.0);
                            AnchorPane.setTopAnchor(textArea2, 0.0);

                            // Create the Button with a FontIcon
                            Button edit = new Button();
                            edit.getStyleClass().add("flat");
                            edit.setGraphic(new FontIcon("fa-pencil-square-o"));

                            Button trash = new Button();
                            trash.getStyleClass().addAll("flat", "danger");
                            trash.setGraphic(new FontIcon("fa-trash-o"));

                            // Set margin for the Button
                            HBox.setMargin(edit, new Insets(0, 0, 0, 10));
                            HBox.setMargin(trash, new Insets(0, 0, 0, 10));

                            // Create the ScrollPane and add the AnchorPane as its content
                            ScrollPane scrollPane = new ScrollPane(anchorPane);
                            scrollPane.setFitToHeight(true);
                            scrollPane.setFitToWidth(true);
                            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                            scrollPane.setPrefHeight(100);
                            scrollPane.setPrefWidth(800);

                            // Create the HBox and add the TextArea and ScrollPane
                            HBox hbox = new HBox();
                            hbox.setPrefHeight(100.0);
                            hbox.getChildren().addAll(textArea1, scrollPane, edit, trash);

                            tilePane.getChildren().add(hbox);
                        });
                        progressIndicator.setVisible(false);
                        content.setDisable(false);
                    });
                }
                return null;
            };

            Thread thread = new Thread(() -> {
                try {
                    httpClient.execute(httpGet, responseHandler);
                    httpClient.close();
                } catch (IOException e) {
                    Platform.runLater(() -> {
                    });
                }
            });
            thread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

        btnBack.setOnMouseClicked(_ -> {
            DashboardLayoutController dashboardLayoutController = applicationContext.getBean(DashboardLayoutController.class);
            dashboardLayoutController.showStudySets();
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnAddCard.setOnMouseClicked(_ -> {
            DashboardLayoutController dashboardLayoutController = applicationContext.getBean(DashboardLayoutController.class);
            dashboardLayoutController.showFlashCardsAdd();
        });
    }
}
