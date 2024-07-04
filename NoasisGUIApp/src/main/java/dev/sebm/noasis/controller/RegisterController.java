package dev.sebm.noasis.controller;


import atlantafx.base.theme.Styles;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sebm.noasis.jsonresponses.ErrorResponse;
import dev.sebm.noasis.jsonresponses.SignUpSuccessResponse;
import dev.sebm.noasis.util.SpringFXMLLoader;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RegisterController {
    private final LoginController loginController;
    @FXML private ImageView asideImage;
    @FXML private AnchorPane imageAnchorPane;
    @FXML private Button btnSubmit;
    @FXML private Label loginLbl;
    @FXML private TextField tfEmail;
    @FXML private PasswordField tfPassword;
    @FXML private PasswordField tfConfirmPassword;
    @FXML private GridPane gridPane;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private Label lblError;

    private final SpringFXMLLoader springFXMLLoader;

    private String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private boolean emailInvalid = true;
    private boolean pswdEmpty = true;

    public RegisterController(SpringFXMLLoader springFXMLLoader, LoginController loginController) {
        this.springFXMLLoader = springFXMLLoader;
        this.loginController = loginController;
    }

    public void initialize() {
        //ID from fxml

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
                final HttpPost httpPost = new HttpPost("http://localhost:3000/signup");

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
                        .create().build();

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
                            if (status != 409) {
                                tfPassword.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                            }
                            lblError.setVisible(true);
                            lblError.setStyle("-fx-font-size: 10px;");
                            lblError.setText(errorResponse.getError());

                            gridPane.setDisable(false);
                            progressIndicator.setVisible(false);
                        });
                        return null;
                    }

                    if (entity != null) {
                        SignUpSuccessResponse signUpSuccessResponse = objectMapper
                                .readValue(entity.getContent(), SignUpSuccessResponse.class);

                        System.out.println(signUpSuccessResponse.getMessage());
                        Platform.runLater(() -> {
                            Parent pane;
                            Stage stage = (Stage)(loginLbl.getScene().getWindow());
                            try {
                                FXMLLoader loader = springFXMLLoader.getLoader("fxml/login");
                                pane = loader.load();

                                LoginController loginController = loader.getController();
                                loginController.showNotification(signUpSuccessResponse.getMessage());

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

        tfPassword.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                pswdEmpty = newValue.isEmpty();
                boolean matchPassword = newValue.matches(tfConfirmPassword.getText());
                btnSubmit.setDisable(emailInvalid || pswdEmpty || !matchPassword);
                if (!matchPassword) {
                    tfPassword.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                    tfConfirmPassword.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                    lblError.setVisible(true);
                    lblError.setStyle("-fx-font-size: 10px;");
                    lblError.setText("Passwords do not match.");
                } else {
                    msgOkay(lblError);
                    tfPassword.pseudoClassStateChanged(Styles.STATE_DANGER, false);
                    tfConfirmPassword.pseudoClassStateChanged(Styles.STATE_DANGER, false);
                }
            }
        });

        tfConfirmPassword.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                pswdEmpty = newValue.isEmpty();
                boolean matchPassword = newValue.matches(tfPassword.getText());
                btnSubmit.setDisable(emailInvalid || pswdEmpty || !matchPassword);
                if (!matchPassword) {
                    tfPassword.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                    tfConfirmPassword.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                    lblError.setVisible(true);
                    lblError.setStyle("-fx-font-size: 10px;");
                    lblError.setText("Passwords do not match.");
                } else {
                    msgOkay(lblError);
                    tfPassword.pseudoClassStateChanged(Styles.STATE_DANGER, false);
                    tfConfirmPassword.pseudoClassStateChanged(Styles.STATE_DANGER, false);
                }
            }
        });

        loginLbl.setOnMouseClicked(_ -> {
            Parent pane;
            Stage stage = (Stage)(loginLbl.getScene().getWindow());
            try {
                pane = springFXMLLoader.loadFXML("fxml/login");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            stage.getScene().setRoot(pane);
        });
    }

    private void msgOkay(Label errorLabel){
        errorLabel.setStyle("-fx-font-size: 1px;");
        errorLabel.setVisible(false);
    }
}
