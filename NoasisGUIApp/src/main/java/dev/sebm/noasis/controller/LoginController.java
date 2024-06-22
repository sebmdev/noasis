package dev.sebm.noasis.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.apache.http.Header;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.util.prefs.Preferences;

@Component
public class LoginController {
    @FXML private ImageView asideImage;
    @FXML private AnchorPane imageAnchorPane;
    @FXML private TextField tfEmail;
    @FXML private TextField tfPassword;
    @FXML private Button btnSubmit;
    @FXML private Button btnSubmit1;
    CookieStore httpCookieStore = new BasicCookieStore();

    private Preferences preferences;

    public LoginController(Preferences preferences) {
        this.preferences = preferences.node("auth");
//        this.preferences = preferences;
    }

    public void initialize() {
        this.preferences.put("session_cookie", "abc123");
        imageAnchorPane.heightProperty().addListener((_, _, t1) -> {
            asideImage.setFitHeight(t1.doubleValue());
        });
        imageAnchorPane.widthProperty().addListener((_, _, t1) -> {
            asideImage.setFitWidth(t1.doubleValue());
        });

        btnSubmit.setOnMouseClicked(_ -> {
            String email = tfEmail.getText();
            String password = tfPassword.getText();

            System.out.println(email);
            System.out.println(password);

            try {
                final HttpGet request = new HttpGet("http://localhost:3000");
                try (CloseableHttpClient client = HttpClientBuilder.
                        create()
                        .setDefaultRequestConfig(
                                RequestConfig
                                        .custom()
                                        .setCookieSpec(CookieSpecs.STANDARD)
                                        .build()
                        )
                        .setDefaultCookieStore(httpCookieStore)
                        .build();
                     CloseableHttpResponse response = client.execute(request)
                ) {
                    System.out.println(EntityUtils.toString(response.getEntity()));
                    Header[] headers = response.getHeaders("Set-Cookie");
                    for (Header h : headers) {
                        System.out.println(h.getValue().toString());
                    }
                    for (Cookie cookie : httpCookieStore.getCookies()) {
                        System.out.println("WOW");
                        System.out.println(cookie.getName() + ": " + cookie.getValue());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btnSubmit1.setOnMouseClicked(_ -> {
            String email = tfEmail.getText();
            String password = tfPassword.getText();

            System.out.println(email);
            System.out.println(password);

            try {
                final HttpGet request = new HttpGet("http://localhost:3000");
                try (CloseableHttpClient client = HttpClientBuilder.
                        create()
                        .setDefaultRequestConfig(
                                RequestConfig
                                        .custom()
                                        .setCookieSpec(CookieSpecs.STANDARD)
                                        .build()
                        )
                        .setDefaultCookieStore(httpCookieStore)
                        .build();
                     CloseableHttpResponse response = client.execute(request)
                ) {
                    System.out.println(EntityUtils.toString(response.getEntity()));
                    Header[] headers = response.getHeaders("Set-Cookie");
                    for (Header h : headers) {
                        System.out.println(h.getValue().toString());
                    }
                    for (Cookie cookie : httpCookieStore.getCookies()) {
                        System.out.println("WOW");
                        System.out.println(cookie.getName() + ": " + cookie.getValue());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        // Any other initialization code
    }
}
