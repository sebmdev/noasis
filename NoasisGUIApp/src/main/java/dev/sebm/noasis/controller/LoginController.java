package dev.sebm.noasis.controller;

import atlantafx.base.theme.Styles;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sebm.noasis.jsonresponses.ErrorResponse;
import dev.sebm.noasis.jsonresponses.LoginSuccessResponse;
import dev.sebm.noasis.util.SpringFXMLLoader;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
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
    @FXML private Label lblError;
    @FXML private GridPane gridPane;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private Label lblMessage;

    private String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    CookieStore httpCookieStore = new BasicCookieStore();
    private boolean emailInvalid = true;
    private boolean pswdEmpty = true;

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

            tfEmail.pseudoClassStateChanged(Styles.STATE_DANGER, false);
            tfPassword.pseudoClassStateChanged(Styles.STATE_DANGER, false);
            gridPane.setDisable(true);
            progressIndicator.setVisible(true);

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
                        Platform.runLater(() -> {
                            tfEmail.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                            tfPassword.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                            lblError.setVisible(true);
                            lblError.setStyle("-fx-font-size: 10px;");
                            lblError.setText(errorResponse.getError());

                            gridPane.setDisable(false);
                            progressIndicator.setVisible(false);
                        });
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

                        Platform.runLater(() -> {
                            Parent pane;
                            Stage stage = (Stage)(signUpLbl.getScene().getWindow());
                            try {
                                pane = springFXMLLoader.loadFXML("fxml/dashHome");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            stage.getScene().setRoot(pane);
                            tfPassword.setDisable(false);
                            tfEmail.setDisable(false);
                            btnSubmit.setDisable(false);
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
                            lblError.setVisible(true);
                            lblError.setStyle("-fx-font-size: 10px;");
                            lblError.setText("An error occurred. Please try again later.");
                            gridPane.setDisable(false);
                            progressIndicator.setVisible(false);
                        });
                    }
                });
                thread.start();

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

        tfEmail.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (!newPropertyValue)
                {
                    msgOkay(lblError);
                    clearMessage();
                    tfEmail.pseudoClassStateChanged(Styles.STATE_DANGER, false);
                }
            }
        });

        tfPassword.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (!newPropertyValue)
                {
                    msgOkay(lblError);
                    clearMessage();
                    tfPassword.pseudoClassStateChanged(Styles.STATE_DANGER, false);
                }
            }
        });

        tfEmail.setOnMouseClicked(_ -> {
            msgOkay(lblError);
            tfEmail.pseudoClassStateChanged(Styles.STATE_DANGER, false);
        });

        tfPassword.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                pswdEmpty = newValue.isEmpty();
                btnSubmit.setDisable(emailInvalid || pswdEmpty);
            }
        });

        tfEmail.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                // Check if the email matches the regex
                if (!newValue.matches(emailRegex)) {
                    tfEmail.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                    lblError.setVisible(true);
                    lblError.setStyle("-fx-font-size: 10px;");
                    lblError.setText("Invalid email format");
                    emailInvalid = true;
                } else {
                    msgOkay(lblError);
                    tfEmail.pseudoClassStateChanged(Styles.STATE_DANGER, false);
                    emailInvalid = false;
                }
                btnSubmit.setDisable(emailInvalid || pswdEmpty);
            }
        });
        // Any other initialization code

    }

    private void clearMessage() {
        lblMessage.setVisible(false);
        lblMessage.setStyle("-fx-font-size: 10px;");
        lblMessage.setText("");
    }

    public void showNotification(String message) {
        lblMessage.setText(message);
        lblMessage.setStyle("-fx-font-size: 10px;");
        lblMessage.setVisible(true);
    }

    private void msgOkay(Label errorLabel){
        errorLabel.setStyle("-fx-font-size: 1px;");
        errorLabel.setVisible(false);
    }
}
