package dev.sebm.noasis.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class LoginController {
    @FXML private ImageView asideImage;
    @FXML private AnchorPane imageAnchorPane;
    @FXML private TextField tfEmail;
    @FXML private TextField tfPassword;
    @FXML private Button btnSubmit;

    @FXML
    public void initialize() {

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
                try (CloseableHttpClient client = HttpClientBuilder.create().build();
                     CloseableHttpResponse response = client.execute(request)
                ) {
                    System.out.println(EntityUtils.toString(response.getEntity()));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Any other initialization code
    }
}
