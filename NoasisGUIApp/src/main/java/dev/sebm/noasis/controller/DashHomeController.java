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
import javafx.scene.Scene;
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

    private final SpringFXMLLoader springFXMLLoader;
    private final Preferences preferences;
    private Boolean isAnimationInProgress = false;
    private CookieStore httpCookieStore = new BasicCookieStore();

    public DashHomeController(Preferences preferences, SpringFXMLLoader springFXMLLoader) {
        this.springFXMLLoader = springFXMLLoader;
        this.preferences = preferences.node("session");
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        makeImageViewResponsive(home, menubox);
//        makeImageViewResponsive(share, menubox);
//        makeImageViewResponsive(ai, menubox);
//        makeImageViewResponsive(logout, menubox);


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


//        btnLogout.setOnMouseClicked(event -> {
//            for (Cookie cookie : httpCookieStore.getCookies()) {
//                System.out.println(cookie.getName() + ": "+ cookie.getValue());
//            }
//
//            try {
//                final HttpDelete httpPost = new HttpDelete("http://localhost:3000/logout");
//
//                httpPost.setHeader("Accept", "application/json");
//                httpPost.setHeader("Content-type", "application/json");
//
//                CloseableHttpClient httpClient = HttpClientBuilder
//                        .create()
//                        .setDefaultCookieStore(httpCookieStore)
//                        .setDefaultRequestConfig(RequestConfig
//                                .custom()
//                                .setCookieSpec(CookieSpecs.STANDARD)
//                                .build())
//                        .build();
//
//                ResponseHandler<String> responseHandler = response -> {
//                    int status = response.getStatusLine().getStatusCode();
//                    System.out.println(response.getEntity().toString());
//                    HttpEntity entity = response.getEntity();
//                    ObjectMapper objectMapper = new ObjectMapper();
//
//                    if (status >= 400) {
//                        ErrorResponse errorResponse = objectMapper.readValue(entity.getContent(), ErrorResponse.class);
//                        System.out.println(errorResponse.getError());
//                        Platform.runLater(() -> {
//                            Parent pane;
//                            Stage stage = (Stage)(mainAncrhoPane.getScene().getWindow());
//                            try {
//                                pane = springFXMLLoader.loadFXML("fxml/login");
//                            } catch (IOException e) {
//                                throw new RuntimeException(e);
//                            }
//
//                            stage.getScene().setRoot(pane);
//                        });
//                        return null;
//                    }
//                    if (entity != null) {
//                        LogoutSuccessResponse logoutSuccessResponse = objectMapper
//                                .readValue(entity.getContent(), LogoutSuccessResponse.class);
//
//                        System.out.println(logoutSuccessResponse);
//                        System.out.println(logoutSuccessResponse.getMessage());
//                        preferences.remove("connect.sid");
//
//                        Platform.runLater(() -> {
//                            Parent pane;
//                            Stage stage = (Stage)(btnLogout.getScene().getWindow());
//                            try {
//                                pane = springFXMLLoader.loadFXML("fxml/login");
//                            } catch (IOException e) {
//                                throw new RuntimeException(e);
//                            }
//
//                            stage.getScene().setRoot(pane);
//                        });
//                    }
//                    return null;
//                };
//
//                Thread thread = new Thread(() -> {
//                    try {
//                        httpClient.execute(httpPost, responseHandler);
//                        httpClient.close();
//                    } catch (IOException e) {
//                        Platform.runLater(() -> {
//                        });
//                    }
//                });
//                thread.start();
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//
   }

    private void loadMainContent(String fxmlPath) {
        try {
            FXMLLoader loader = springFXMLLoader.getLoader(fxmlPath);
            Parent root = loader.load();
            AnchorPane mainContent = (AnchorPane) root.lookup("#sharedAnchorpane");

            if (mainContent != null) {
                // Clear existing content but keep pane1 and pane2
                mainAncrhoPane.getChildren().clear();
                mainAncrhoPane.getChildren().add(mainContent);

                // Ensure pane1 and pane2 are on top
                mainAncrhoPane.getChildren().add(pane1);
                mainAncrhoPane.getChildren().add(pane2);

                // Set the anchor constraints to ensure the content fills the pane
                AnchorPane.setTopAnchor(mainContent, 0.0);
                AnchorPane.setBottomAnchor(mainContent, 0.0);
                AnchorPane.setLeftAnchor(mainContent, 0.0);
                AnchorPane.setRightAnchor(mainContent, 0.0);

                // Restore pane1 and pane2 to their expected positions
                AnchorPane.setTopAnchor(pane1, 10.0); // Adjust as necessary
                AnchorPane.setLeftAnchor(pane1, 10.0); // Adjust as necessary

                AnchorPane.setTopAnchor(pane2, 10.0); // Adjust as necessary
                AnchorPane.setRightAnchor(pane2, 10.0); // Adjust as necessary
            } else {
                System.out.println("No AnchorPane with id 'sharedAnchorpane' found in the loaded FXML.");
            }
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


