package dev.sebm.noasis.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sebm.noasis.jsonresponses.CreateFlashcardSuccessResponse;
import dev.sebm.noasis.jsonresponses.ErrorResponse;
import dev.sebm.noasis.jsonresponses.models.FlashCard;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

@Component
public class FlashCardAddEditController implements Initializable {
    @FXML private TextArea taTerm, taDefinition;
    @FXML private Button btnSave, btnCancel;
    @FXML private VBox content;
    @FXML private ProgressIndicator progressIndicator;

    private final ApplicationContext applicationContext;
    private final Preferences preferences;

    @Getter
    @Setter
    private String studySetId;

    public FlashCardAddEditController(Preferences preferences, ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.preferences = preferences.node("session");
    }

    public void setSaveActionForEditFlashcard(FlashCard flashCard) {
        taTerm.setText(flashCard.getTerm());
        taDefinition.setText(flashCard.getDefinition());

        btnSave.setOnMouseClicked(_ -> {
            CookieStore httpCookieStore = new BasicCookieStore();
            String sessionCookieValue = preferences.get("connect.sid", "none");
            if (sessionCookieValue != null) {
                BasicClientCookie sessionCookie = new BasicClientCookie("connect.sid", sessionCookieValue);
                sessionCookie.setPath("/");
                sessionCookie.setDomain("localhost");
                httpCookieStore.addCookie(sessionCookie);
            }
            String term = taTerm.getText();
            String definition = taDefinition.getText();
            String escapedTerm = term.replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
            String escapedDefinition = definition.replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
            System.out.println("Edited flashcard");

            content.setDisable(true);
            progressIndicator.setVisible(true);
            try {
                final HttpPut httpPut = new HttpPut("http://localhost:3000/flashcard/" + flashCard.getId());

                httpPut.setHeader("Accept", "application/json");
                httpPut.setHeader("Content-type", "application/json");
                String json = "{\r\n" +
                        String.format("  \"term\": \"%s\",\r\n", escapedTerm) +
                        String.format("  \"definition\": \"%s\"\r\n", escapedDefinition) +
                        "}";
                System.out.println(json);
                StringEntity stringEntity = new StringEntity(json);
                httpPut.setEntity(stringEntity);

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

                    content.setDisable(true);
                    progressIndicator.setVisible(true);
                    System.out.println(status);
                    if (status >= 400) {
                        ErrorResponse errorResponse = objectMapper.readValue(entity.getContent(), ErrorResponse.class);
                        System.out.println(errorResponse.getError());
                        Platform.runLater(() -> {
                            content.setDisable(false);
                            progressIndicator.setVisible(false);
                        });
                        return null;
                    }

                    if (entity != null) {
                        Platform.runLater(() -> {
                            FlashCardsController flashCardsController = applicationContext.getBean(FlashCardsController.class);
                            flashCardsController.loadFlashCards(studySetId);
                            DashboardLayoutController dashboardLayoutController = applicationContext.getBean(DashboardLayoutController.class);
                            dashboardLayoutController.showFlashCards();
                            content.setDisable(false);
                            progressIndicator.setVisible(false);
                        });
                    }
                    return null;
                };

                Thread thread = new Thread(() -> {
                    try {
                        httpClient.execute(httpPut, responseHandler);
                        httpClient.close();
                    } catch (IOException e) {
                    }
                });
                thread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void setSaveActionForNewFlashcard() {
        btnSave.setOnMouseClicked(_ -> {
            CookieStore httpCookieStore = new BasicCookieStore();
            String sessionCookieValue = preferences.get("connect.sid", "none");
            if (sessionCookieValue != null) {
                BasicClientCookie sessionCookie = new BasicClientCookie("connect.sid", sessionCookieValue);
                sessionCookie.setPath("/");
                sessionCookie.setDomain("localhost");
                httpCookieStore.addCookie(sessionCookie);
            }
            String term = taTerm.getText();
            String definition = taDefinition.getText();
            String escapedTerm = term.replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
            String escapedDefinition = definition.replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");


            content.setDisable(true);
            progressIndicator.setVisible(true);

            try {
                final HttpPost httpPost = new HttpPost("http://localhost:3000/study-sets/" + studySetId);

                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
                String json = "{\r\n" +
                        String.format("  \"term\": \"%s\",\r\n", escapedTerm) +
                        String.format("  \"definition\": \"%s\"\r\n", escapedDefinition) +
                        "}";
                System.out.println(json);
                StringEntity stringEntity = new StringEntity(json);
                httpPost.setEntity(stringEntity);

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

                    content.setDisable(true);
                    progressIndicator.setVisible(true);

                    if (status >= 400) {
                        ErrorResponse errorResponse = objectMapper.readValue(entity.getContent(), ErrorResponse.class);
                        System.out.println(errorResponse.getError());
                        Platform.runLater(() -> {
                            content.setDisable(false);
                            progressIndicator.setVisible(false);
                        });
                        return null;
                    }

                    if (entity != null) {
                        CreateFlashcardSuccessResponse createFlashcardSuccessResponse = objectMapper
                                .readValue(entity.getContent(), CreateFlashcardSuccessResponse.class);

                        System.out.println(createFlashcardSuccessResponse);

                        Platform.runLater(() -> {
                            FlashCardsController flashCardsController = applicationContext.getBean(FlashCardsController.class);
                            flashCardsController.loadFlashCards(studySetId);
                            DashboardLayoutController dashboardLayoutController = applicationContext.getBean(DashboardLayoutController.class);
                            dashboardLayoutController.showFlashCards();
                            content.setDisable(false);
                            progressIndicator.setVisible(false);
                        });
                    }
                    return null;
                };

                Thread thread = new Thread(() -> {
                    try {
                        httpClient.execute(httpPost, responseHandler);
                        httpClient.close();
                    } catch (IOException e) {
                    }
                });
                thread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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
    }
}
