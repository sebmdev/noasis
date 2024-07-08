package dev.sebm.noasis.controller;

import atlantafx.base.controls.Card;
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
import javafx.scene.layout.*;
import javafx.scene.text.Text;
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
    private final FlashCardAddEditController flashCardAddEditController;
    private final DashboardLayoutController dashboardLayoutController;
    @FXML private Pane flashcardList;
    @FXML private Button btnAddCard, btnBack, btnMockExam;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private AnchorPane content;

    private final CookieStore httpCookieStore = new BasicCookieStore();

    public FlashCardsController(
            Preferences preferences,
            ApplicationContext applicationContext,
            FlashCardAddEditController flashCardAddEditController,
            DashboardLayoutController dashboardLayoutController) {
        String sessionCookieValue = preferences.node("session").get("connect.sid", "none");
        if (sessionCookieValue != null) {
            BasicClientCookie sessionCookie = new BasicClientCookie("connect.sid", sessionCookieValue);
            sessionCookie.setPath("/");
            sessionCookie.setDomain("localhost");
            httpCookieStore.addCookie(sessionCookie);
        }
        this.flashCardAddEditController = flashCardAddEditController;
        this.dashboardLayoutController = dashboardLayoutController;
    }

    public void loadFlashCards(String studySetId) {
        flashcardList.getChildren().clear();
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
                            Card card = new Card();
                            card.setMaxWidth(600);
                            card.setPrefHeight(150);
                            HBox headerContent = new HBox();
                            headerContent.getChildren().add(new Text(flashCard.getTerm()));

                            Region region = new Region();

                            HBox.setHgrow(region, Priority.ALWAYS);

                            Button edit = new Button();
                            edit.getStyleClass().add("flat");
                            edit.setGraphic(new FontIcon("fa-pencil-square-o"));

                            edit.setOnMouseClicked(_ -> {
                                System.out.println("Editing " + flashCard.getId());
                                dashboardLayoutController.showFlashCardsEdit(flashCard);
                            });

                            Button trash = new Button();
                            trash.getStyleClass().addAll("flat", "danger");
                            trash.setGraphic(new FontIcon("fa-trash-o"));
                            headerContent.getChildren().addAll(region, edit, trash);
                            card.setHeader(headerContent);

                            TextArea textArea2 = new TextArea("\n");
                            textArea2.setEditable(false);
                            textArea2.setMaxHeight(Double.MAX_VALUE);
                            textArea2.setMaxWidth(Double.MAX_VALUE);
                            textArea2.setWrapText(true);
                            textArea2.setText(flashCard.getDefinition());

                            card.setBody(textArea2);
                            flashcardList.getChildren().add(card);
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

        btnAddCard.setOnMouseClicked(_ -> {
            dashboardLayoutController.showFlashCardsAdd();
            flashCardAddEditController.setStudySetId(studySetId);
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnMockExam.setOnMouseClicked(e -> {
            dashboardLayoutController.showMockExam();
        });
        btnBack.setOnMouseClicked(_ -> {
            dashboardLayoutController.showStudySets();
        });
    }
}
