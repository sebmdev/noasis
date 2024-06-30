package dev.sebm.noasis.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sebm.noasis.jsonresponses.ErrorResponse;
import dev.sebm.noasis.jsonresponses.LoginSuccessResponse;
import dev.sebm.noasis.util.SpringFXMLLoader;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.prefs.Preferences;

@Component
public class LoginController {
    @FXML private ImageView asideImage;
    @FXML private AnchorPane imageAnchorPane;
    @FXML private TextField tfEmail;
    @FXML private TextField tfPassword;
    @FXML private Button btnSubmit;
    @FXML private Label signUpLbl;
<<<<<<< HEAD
    @FXML private Label errorEmail, errorPswd;
    @FXML private Button btnSubmit1;
    private String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
=======
>>>>>>> 11814b5 (Added login functionality)
    CookieStore httpCookieStore = new BasicCookieStore();

    private final Preferences preferences;
    private final SpringFXMLLoader springFXMLLoader;

    public LoginController(Preferences preferences, SpringFXMLLoader springFXMLLoader) {
        this.preferences = preferences.node("session");
        this.springFXMLLoader = springFXMLLoader;
    }

    public void initialize() {
        System.out.println(this.preferences.get("sessionCookie", "none"));
        imageAnchorPane.heightProperty().addListener((_, _, t1) -> {
            asideImage.setFitHeight(t1.doubleValue());
        });
        imageAnchorPane.widthProperty().addListener((_, _, t1) -> {
            asideImage.setFitWidth(t1.doubleValue());
        });

        btnSubmit.setOnMouseClicked(_ -> {
            String email = tfEmail.getText();
            String password = tfPassword.getText();

            if(!email.isEmpty()&&!password.isEmpty()){
                if (email.matches(emailRegex)) {
                    msgOkay(errorEmail, tfEmail);

                    if(email.equals("Clyde@gmail.com")) {
                        msgError(errorEmail, tfEmail, "Email Does Not Exist");
                        return;
                    }
                    return;
                }
                else{
                    msgError(errorEmail, tfEmail,"Invalid email format");
                }
            }
            else{
                if(email.isEmpty()){
                    msgError(errorEmail, tfEmail, "Hey you, type something");}
                if(password.isEmpty()){
                    msgError(errorPswd, tfPassword, "Hey you, type something");
                }
                return;
            }

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
                        LoginSuccessResponse loginSuccessResponse = objectMapper
                                .readValue(entity.getContent(), LoginSuccessResponse.class);


                        System.out.println(loginSuccessResponse);
                        System.out.println(loginSuccessResponse.getUser().getId());
                        List<Cookie> cookies = httpCookieStore.getCookies();
                        for (Cookie cookie : cookies) {
                            System.out.println(cookie.getName() + ": "+ cookie.getValue());
                            if ("connect.sid".equals(cookie.getName())) {
                                preferences.put("connect.sid", cookie.getValue());
                                break;
                            }
                        }

                        Parent pane;
                        Stage stage = (Stage)(signUpLbl.getScene().getWindow());
                        try {
                            pane = springFXMLLoader.loadFXML("fxml/dashTEST");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        stage.getScene().setRoot(pane);
                    }
                    return null;
                };
                httpClient.execute(httpPost, responseHandler);
                httpClient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        signUpLbl.setOnMouseClicked(_ -> {
            Parent pane;
            Stage stage = (Stage)(signUpLbl.getScene().getWindow());
            try {
                pane = springFXMLLoader.loadFXML("fxml/register");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            stage.getScene().setRoot(pane);
        });

        tfEmail.setOnMouseClicked(_ -> {
            msgOkay(errorEmail, tfEmail);
        });
        tfPassword.setOnMouseClicked(_ -> {
            msgOkay(errorPswd, tfPassword);
        });

        tfEmail.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                // Check if the email matches the regex
                if (!newValue.matches(emailRegex)) {
                    msgError(errorEmail, tfEmail, "Invalid email format");
                    return;
                } else {
                    msgOkay(errorEmail, tfEmail);
                }
            }
        });
        // Any other initialization code

    }
<<<<<<< HEAD

    private void msgError(Label errorLabel, TextField tf, String errorMsg){
        errorLabel.setVisible(true);
        errorLabel.setText(errorMsg);
        errorLabel.setStyle("-fx-font-size: 10px;");
        tf.setStyle("-fx-border-color: red;");
    }

    private void msgOkay(Label errorLabel, TextField tf){
        errorLabel.setStyle("-fx-font-size: 1px;");
        errorLabel.setVisible(false);
        tf.setStyle("-fx-border-color: none;");
    }
=======
>>>>>>> 11814b5 (Added login functionality)
}
