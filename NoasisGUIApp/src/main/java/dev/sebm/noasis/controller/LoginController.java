package dev.sebm.noasis.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sebm.noasis.jsonresponses.ErrorResponse;
import dev.sebm.noasis.jsonresponses.LoginSuccessResponse;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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
                final HttpPost httpPost = new HttpPost("http://localhost:3000/login");

                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
                String json = "{\r\n" +
                        String.format("  \"email\": \"%s\",\r\n", email) +
                        String.format("  \"password\": \"%s\"\r\n", password) +
                        "}";
                System.out.println(json);
                StringEntity stringEntity = new StringEntity(json);
                httpPost.setEntity(stringEntity);

                CloseableHttpClient httpClient = HttpClients.createDefault();

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
                        LoginSuccessResponse loginSuccessResponse = objectMapper
                                .readValue(entity.getContent(), LoginSuccessResponse.class);

                        System.out.println(loginSuccessResponse);
                        System.out.println(loginSuccessResponse.getUser().getId());
                    }
                    return null;
                };
                httpClient.execute(httpPost, responseHandler);
                httpClient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        // Any other initialization code
    }
}
