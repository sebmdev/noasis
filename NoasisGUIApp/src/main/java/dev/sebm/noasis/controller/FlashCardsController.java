package dev.sebm.noasis.controller;

import atlantafx.base.controls.Card;
import atlantafx.base.theme.Styles;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sebm.noasis.guicomponent.FlipCard;
import dev.sebm.noasis.jsonresponses.ErrorResponse;
import dev.sebm.noasis.jsonresponses.models.FlashCard;
import javafx.animation.AnimationTimer;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
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
    @FXML private Pane root, flashcardList;
    @FXML private Button btnAddCard, btnBack, btnMockExam;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private AnchorPane content;

    private final CookieStore httpCookieStore = new BasicCookieStore();
    private final Preferences preferences;
    public FlashCardsController(
            Preferences preferences,
            FlashCardAddEditController flashCardAddEditController,
            DashboardLayoutController dashboardLayoutController) {
        this.flashCardAddEditController = flashCardAddEditController;
        this.dashboardLayoutController = dashboardLayoutController;
        this.preferences = preferences.node("session");
    }

    public void loadFlashCards(String studySetId) {
        String sessionCookieValue = preferences.get("connect.sid", "none");
        if (sessionCookieValue != null) {
            BasicClientCookie sessionCookie = new BasicClientCookie("connect.sid", sessionCookieValue);
            sessionCookie.setPath("/");
            sessionCookie.setDomain("localhost");
            httpCookieStore.addCookie(sessionCookie);
        }
        flashcardList.getChildren().clear();
        progressIndicator.setVisible(true);
        content.setDisable(true);
        // Create front and back nodes for the flip card (you can use any Node type)

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

                System.out.println(status);

                if (status >= 400) {
                    ErrorResponse errorResponse = objectMapper.readValue(entity.getContent(), ErrorResponse.class);
                    System.out.println(errorResponse.getError());
                    progressIndicator.setVisible(false);
                    content.setDisable(false);
                    return null;
                }
                if (entity != null) {
                    List<FlashCard> flashCards = objectMapper.readValue(EntityUtils.toString(entity),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, FlashCard.class));

                    Platform.runLater(() -> {
                        btnMockExam.setOnMouseClicked(e -> {
                            if (flashCards.size() < 4) {
                                dashboardLayoutController.showWarningDialog("Warning", "Set should have at least 4 flashcards.");
                                return;
                            }
                            dashboardLayoutController.showMockExam(flashCards);
                            dashboardLayoutController.disableNav();
                        });

                        if (flashCards.size() > 1) {
                            Pagination pg = new Pagination(flashCards.size(), 0);
                            pg.setMaxPageIndicatorCount(1);
                            pg.setPageFactory(index -> {
                                Card front = new Card();
                                front.minHeight(200);;
                                front.minWidth(400);
                                HBox frontContainer = new HBox();
                                frontContainer.setAlignment(Pos.CENTER);
                                frontContainer.getChildren().add(new Text(flashCards.get(index).getTerm()));
                                front.setBody(frontContainer);


                                Card back = new Card();
                                back.minHeight(200);
                                back.minWidth(400);
                                HBox backContainer = new HBox();
                                backContainer.setAlignment(Pos.CENTER);
                                backContainer.getChildren().add(new Text(flashCards.get(index).getDefinition()));
                                back.setBody(backContainer);

                                // Create the flip card
                                FlipCard flipCard = new FlipCard(front, back);
                                flipCard.setMinHeight(200);
                                flipCard.setMinWidth(400);

                                HBox hBox = new HBox();
                                hBox.setAlignment(Pos.CENTER);
                                HBox.setHgrow(hBox, Priority.ALWAYS);
                                hBox.getChildren().add(flipCard);
                                return hBox;
                            });

                            flashcardList.getChildren().add(pg);
                        }

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
                            trash.setOnMouseClicked(_ -> {
                                boolean confirmDelete = dashboardLayoutController.showDeleteDialog("Delete flashcard", "Are you sure you want to delete this flashcard?");
                                if (!confirmDelete) return;
                                try {
                                    final HttpDelete httpDelete = new HttpDelete("http://localhost:3000/flashcard/" + flashCard.getId());

                                    CloseableHttpClient deleteHttpClient = HttpClientBuilder
                                            .create()
                                            .setDefaultCookieStore(httpCookieStore)
                                            .setDefaultRequestConfig(RequestConfig
                                                    .custom()
                                                    .setCookieSpec(CookieSpecs.STANDARD)
                                                    .build())
                                            .build();

                                    httpDelete.setHeader("Accept", "application/json");
                                    httpDelete.setHeader("Content-type", "application/json");

                                    ResponseHandler<String> delResponseHandler = delResponse -> {
                                        int delStatus = delResponse.getStatusLine().getStatusCode();
                                        System.out.println(delResponse.getEntity().toString());
                                        HttpEntity delEntity = delResponse.getEntity();
                                        ObjectMapper delObjectMapper = new ObjectMapper();

                                        content.setDisable(true);
                                        progressIndicator.setVisible(true);

                                        if (delStatus >= 400) {
                                            ErrorResponse errorResponse = delObjectMapper.readValue(delEntity.getContent(), ErrorResponse.class);
                                            System.out.println(errorResponse.getError());
                                            Platform.runLater(() -> {
                                                content.setDisable(false);
                                                progressIndicator.setVisible(false);
                                            });
                                            return null;
                                        }

                                        if (delEntity != null) {
                                            Platform.runLater(() -> {
                                                flashcardList.getChildren().clear();
                                                loadFlashCards(studySetId);
                                            });
                                        }
                                        return null;
                                    };

                                    Thread thread = new Thread(() -> {
                                        try {
                                            deleteHttpClient.execute(httpDelete, delResponseHandler);
                                            deleteHttpClient.close();
                                        } catch (IOException e) {
                                        }
                                    });
                                    thread.start();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });

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
        btnBack.setOnMouseClicked(_ -> {
            dashboardLayoutController.showStudySets();
        });
    }

}
