package dev.sebm.noasis.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sebm.noasis.jsonresponses.ErrorResponse;
import dev.sebm.noasis.jsonresponses.LogoutSuccessResponse;
import dev.sebm.noasis.util.SpringFXMLLoader;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

@Component
public class DashboardLayoutController implements Initializable {
    @FXML private Button btnToggleNav;
    @FXML private VBox flashCardsAdd;
    @FXML private AnchorPane nav, dashHome, dashShared, flashCards;
    @FXML private BorderPane borderPane;
    @FXML private StackPane centerPane;

    @FXML private Button btnStudySets;
    @FXML private Button btnSharedSets;
    @FXML private Button btnGenerateWithAI;
    @FXML private Button btnLogout;

    private final CookieStore httpCookieStore = new BasicCookieStore();
    private final SpringFXMLLoader springFXMLLoader;
    private final Preferences preferences;


    public DashboardLayoutController(Preferences preferences, SpringFXMLLoader springFXMLLoader) {
        this.springFXMLLoader = springFXMLLoader;
        this.preferences = preferences.node("session");
    }

    private boolean navOpened = true;
    private int navWidth;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        String sessionCookieValue = preferences.get("connect.sid", "none");
        System.out.println("LOADED SESSION COOKIE: " + sessionCookieValue);
        if (sessionCookieValue != null) {
            BasicClientCookie sessionCookie = new BasicClientCookie("connect.sid", sessionCookieValue);
            sessionCookie.setPath("/");
            sessionCookie.setDomain("localhost");
            httpCookieStore.addCookie(sessionCookie);
        }

        navWidth = (int) nav.getPrefWidth();
        dashHome.prefWidthProperty().bind(centerPane.widthProperty());
        dashHome.prefHeightProperty().bind(centerPane.heightProperty());
        dashShared.prefWidthProperty().bind(centerPane.widthProperty());
        dashShared.prefHeightProperty().bind(centerPane.heightProperty());

        btnToggleNav.setOnMouseClicked(e -> {
            TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.4));
            slide.setNode(nav);

            navOpened = !navOpened;
            adjustCenterPane();
        });

        btnStudySets.setOnMouseClicked(e->{
            showPage(dashHome);

        });

        btnSharedSets.setOnMouseClicked(e->{
            showPage(dashShared);
        });

        btnLogout.setOnMouseClicked(event -> {
            for (Cookie cookie : httpCookieStore.getCookies()) {
                System.out.println(cookie.getName() + ": "+ cookie.getValue());
            }

            try {
                final HttpDelete httpPost = new HttpDelete("http://localhost:3000/logout");

                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");

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
                        Platform.runLater(() -> {
                            Parent pane;
                            Stage stage = (Stage)(nav.getScene().getWindow());
                            try {
                                pane = springFXMLLoader.loadFXML("fxml/login");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                            stage.getScene().setRoot(pane);
                        });
                        return null;
                    }
                    if (entity != null) {
                        LogoutSuccessResponse logoutSuccessResponse = objectMapper
                                .readValue(entity.getContent(), LogoutSuccessResponse.class);

                        System.out.println(logoutSuccessResponse);
                        System.out.println(logoutSuccessResponse.getMessage());
                        preferences.remove("connect.sid");

                        Platform.runLater(() -> {
                            Parent pane;
                            Stage stage = (Stage)(btnLogout.getScene().getWindow());
                            try {
                                pane = springFXMLLoader.loadFXML("fxml/login");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                            stage.getScene().setRoot(pane);
                        });
                    }
                    return null;
                };

                Thread thread = new Thread(() -> {
                    try {
                        httpClient.execute(httpPost, responseHandler);
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

    private void showPage(Node node) {
        dashHome.setVisible(false);
        dashShared.setVisible(false);
        flashCards.setVisible(false);
        flashCardsAdd.setVisible(false);
        node.toFront();
        node.setVisible(true);
    }

    public void showFlashCards() {
        showPage(flashCards);
    }

    public void showFlashCardsAdd() {
        showPage(flashCardsAdd);
    }
}
