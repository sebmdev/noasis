package dev.sebm.noasis;

import atlantafx.base.theme.PrimerLight;
import atlantafx.base.theme.Styles;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sebm.noasis.jsonresponses.ErrorResponse;
import dev.sebm.noasis.jsonresponses.LoginSuccessResponse;
import dev.sebm.noasis.util.SpringFXMLLoader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;

public class NoasisApplication extends Application {

    private ConfigurableApplicationContext applicationContext;
    private final CookieStore httpCookieStore = new BasicCookieStore();

    @Override
    public void init() {
        applicationContext = new SpringApplicationBuilder(MainApplication.class).run();
        Arrays.asList(applicationContext.getBeanDefinitionNames()).forEach(System.out::println);
    }

    @Override
    public void stop() {
        applicationContext.stop();
        Platform.exit();
    }

    @Override
    public void start(Stage stage) {
        NoasisApplication.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        Scene scene;
        SpringFXMLLoader springFXMLLoader = applicationContext.getBean(SpringFXMLLoader.class);
        Preferences preferences = applicationContext.getBean(Preferences.class).node("session");
        String sessionCookie = preferences.get("connect.sid", "none");

        if (sessionCookie.equals("none")) {
            System.out.println("No session cookie found. Loading login scene.");
            // Load the login scene
            try {
                scene = new Scene(springFXMLLoader.loadFXML("fxml/login"), 640, 480);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Session cookie found: " + sessionCookie);
            // Check server if session is valid
//            try {
//                final HttpGet httpGet = new HttpGet("http://localhost:3000/check-session");
//
//                httpGet.setHeader("Accept", "application/json");
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
//                            tfEmail.pseudoClassStateChanged(Styles.STATE_DANGER, true);
//                            tfPassword.pseudoClassStateChanged(Styles.STATE_DANGER, true);
//                            lblError.setVisible(true);
//                            lblError.setStyle("-fx-font-size: 10px;");
//                            lblError.setText(errorResponse.getError());
//
////                            gridPane.setDisable(false);
////                            progressIndicator.setVisible(false);
//                        });
//                        return null;
//                    }
//                    if (entity != null) {
//                        LoginSuccessResponse loginSuccessResponse = objectMapper
//                                .readValue(entity.getContent(), LoginSuccessResponse.class);
//
//                        System.out.println(loginSuccessResponse);
//                        System.out.println(loginSuccessResponse.getUser().getId());
//                        List<Cookie> cookies = httpCookieStore.getCookies();
//                        for (Cookie cookie : cookies) {
//                            System.out.println(cookie.getName() + ": "+ cookie.getValue());
//                            if ("connect.sid".equals(cookie.getName())) {
//                                preferences.put("connect.sid", cookie.getValue());
//                                break;
//                            }
//                        }
//
//                        Platform.runLater(() -> {
//                            Parent pane;
//                            Stage stage = (Stage)(signUpLbl.getScene().getWindow());
//                            try {
//                                pane = springFXMLLoader.loadFXML("fxml/dashHome");
//                            } catch (IOException e) {
//                                throw new RuntimeException(e);
//                            }
//                            stage.getScene().setRoot(pane);
//                            tfPassword.setDisable(false);
//                            tfEmail.setDisable(false);
//                            btnSubmit.setDisable(false);
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
//                            lblError.setVisible(true);
//                            lblError.setStyle("-fx-font-size: 10px;");
//                            lblError.setText("An error occurred. Please try again later.");
//                            gridPane.setDisable(false);
//                            progressIndicator.setVisible(false);
//                        });
//                    }
//                });
//                thread.start();
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            try {
                scene = new Scene(springFXMLLoader.loadFXML("fxml/dashboardLayout"), 800, 600);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        stage.setTitle("Noasis");
        stage.setHeight(600);
        stage.setWidth(800);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.show();
    }
}
