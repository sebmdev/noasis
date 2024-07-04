package dev.sebm.noasis.controller;

import atlantafx.base.theme.Styles;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sebm.noasis.jsonresponses.ErrorResponse;
import dev.sebm.noasis.jsonresponses.LoginSuccessResponse;
import dev.sebm.noasis.jsonresponses.LogoutSuccessResponse;
import dev.sebm.noasis.util.SpringFXMLLoader;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

@Component
public class DashHomeController implements Initializable {
    @FXML private ImageView menu, home, share, ai, logout;
    @FXML private AnchorPane pane1, pane2, mainAncrhoPane;
    @FXML private VBox menubox;
    @FXML private Button sharedBtn;
    @FXML private Button btnLogout;

    private final SpringFXMLLoader springFXMLLoader;
    private final Preferences preferences;
    private Boolean isAnimationInProgress = false;
    private CustomCookieStore customCookieStore;


    public DashHomeController(Preferences preferences, SpringFXMLLoader springFXMLLoader) {
        this.springFXMLLoader = springFXMLLoader;
        this.preferences = preferences.node("session");
        this.customCookieStore = new CustomCookieStore(this.preferences);
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

        btnLogout.setOnMouseClicked(event -> {
            for (Cookie cookie : customCookieStore.getCookies()) {
                System.out.println(cookie.getName() + ": "+ cookie.getValue());
            }

            try {
                final HttpDelete httpPost = new HttpDelete("http://localhost:3000/logout");

                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");

                CloseableHttpClient httpClient = HttpClientBuilder
                        .create()
                        .setDefaultCookieStore(customCookieStore)
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
                            Stage stage = (Stage)(mainAncrhoPane.getScene().getWindow());
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

    private static class CustomCookieStore implements CookieStore {
        private final BasicCookieStore store;

        public CustomCookieStore(Preferences preferences) {
            this.store = new BasicCookieStore();

            // Load the session cookie from preferences
            String sessionCookieValue = preferences.get("connect.sid", "");
            System.out.println("LOADED SESSION COOKIE: " + sessionCookieValue);
            if (sessionCookieValue != null) {
                BasicClientCookie sessionCookie = new BasicClientCookie("connect.sid", sessionCookieValue);
                sessionCookie.setPath("/");
                sessionCookie.setDomain("localhost");
                store.addCookie(sessionCookie);
            }
        }

        @Override
        public void addCookie(Cookie cookie) {
            store.addCookie(cookie);
        }

        @Override
        public List<Cookie> getCookies() {
            return store.getCookies();
        }

        @Override
        public boolean clearExpired(java.util.Date date) {
            return store.clearExpired(date);
        }

        @Override
        public void clear() {
            store.clear();
        }
    }

//    private void makeImageViewResponsive(ImageView imageView, VBox vbox) {
//        imageView.fitHeightProperty().bind(vbox.heightProperty().multiply(0.1)); // Adjust the multiplier as needed
//        imageView.fitWidthProperty().bind(vbox.widthProperty().multiply(0.8));   // Adjust the multiplier as needed
//
//    }
}


