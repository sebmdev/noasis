package dev.sebm.noasis;

import atlantafx.base.theme.PrimerLight;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sebm.noasis.jsonresponses.ErrorResponse;
import dev.sebm.noasis.util.SpringFXMLLoader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
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
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.util.prefs.Preferences;

public class NoasisApplication extends Application {

    private ConfigurableApplicationContext applicationContext;
    private final CookieStore httpCookieStore = new BasicCookieStore();

    @Override
    public void init() {
        applicationContext = new SpringApplicationBuilder(MainApplication.class).run();
    }

    @Override
    public void stop() {
        applicationContext.stop();
        Platform.exit();
    }

    @Override
    public void start(Stage stage) {
        NoasisApplication.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        SpringFXMLLoader springFXMLLoader = applicationContext.getBean(SpringFXMLLoader.class);
        Preferences preferences = applicationContext.getBean(Preferences.class).node("session");
        String sessionCookie = preferences.get("connect.sid", "none");

        if (sessionCookie.equals("none")) {
            System.out.println("No session cookie found. Loading login scene.");
            // Load the login scene
            try {
                Scene scene = new Scene(springFXMLLoader.loadFXML("fxml/login"), 640, 480);
                stage.setScene(scene);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                Scene scene = new Scene(springFXMLLoader.loadFXML("fxml/loading"), 800, 600);
                stage.setScene(scene);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Session cookie found: " + sessionCookie);
            BasicClientCookie sessionCookieObj = new BasicClientCookie("connect.sid", sessionCookie);
            sessionCookieObj.setPath("/");
            sessionCookieObj.setDomain("localhost");
            httpCookieStore.addCookie(sessionCookieObj);
            // Check server if session is valid
            try {
                final HttpGet httpGet = new HttpGet("http://localhost:3000/check-session");

                httpGet.setHeader("Accept", "application/json");

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
                        // Perform log out
                        preferences.remove("connect.sid");
                        Platform.runLater(() -> {
                            try {
                                Scene scene = new Scene(springFXMLLoader.loadFXML("fxml/login"), 640, 480);
                                stage.setScene(scene);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        return null;
                    }
                    if (entity != null) {
                        Platform.runLater(() -> {
                            try {
                                Scene scene = new Scene(springFXMLLoader.loadFXML("fxml/dashboardLayout"), 800, 600);
                                stage.setScene(scene);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
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
                try {
                    Scene scene = new Scene(springFXMLLoader.loadFXML("fxml/login"), 640, 480);
                    stage.setScene(scene);
                } catch (IOException e1) {
                    throw new RuntimeException(e1);
                }
            }
        }

        stage.setTitle("Noasis");
        stage.setHeight(600);
        stage.setWidth(800);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.show();
    }
}
