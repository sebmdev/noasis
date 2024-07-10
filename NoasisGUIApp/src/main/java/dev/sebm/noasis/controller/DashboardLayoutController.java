package dev.sebm.noasis.controller;

import atlantafx.base.controls.ModalPane;
import atlantafx.base.theme.PrimerLight;
import atlantafx.base.theme.Styles;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sebm.noasis.jsonresponses.ErrorResponse;
import dev.sebm.noasis.jsonresponses.LogoutSuccessResponse;
import dev.sebm.noasis.jsonresponses.models.FlashCard;
import dev.sebm.noasis.util.SpringFXMLLoader;
import jakarta.annotation.Nullable;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

@Component
public class DashboardLayoutController implements Initializable {
    private final MockExamController mockExamController;
    @FXML private Button btnToggleNav;
    @FXML private Pane root, nav, dashHome, dashShared, flashCards, centerPane, flashCardsAdd,generateAI, mockExam;

    @FXML private Button btnStudySets;
    @FXML private Button btnSharedSets;
    @FXML private Button btnGenerateWithAI;
    @FXML private Button btnLogout;

    @FXML private ModalPane modalPane;

    private final CookieStore httpCookieStore = new BasicCookieStore();
    private final SpringFXMLLoader springFXMLLoader;
    private final Preferences preferences;
    private final FlashCardAddEditController flashCardAddEditController;

    public DashboardLayoutController(
            Preferences preferences,
            SpringFXMLLoader springFXMLLoader,
            FlashCardAddEditController flashCardAddEditController, MockExamController mockExamController) {
        this.springFXMLLoader = springFXMLLoader;
        this.preferences = preferences.node("session");
        this.flashCardAddEditController = flashCardAddEditController;
        this.mockExamController = mockExamController;
    }

    private boolean navOpened = true;
    private int navWidth;

    public boolean showDeleteDialog(String title, String content) {
        var alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(content);
        ButtonType yesBtn = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(yesBtn, cancel);
        alert.initOwner(root.getScene().getWindow());
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == yesBtn;
    }

    public void showWarningDialog(String title, String content) {
        var alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.setHeaderText(null);
        ButtonType okay = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);

        alert.getButtonTypes().setAll(okay);
        alert.initOwner(root.getScene().getWindow());

        alert.getDialogPane().getStylesheets().add(new PrimerLight().getUserAgentStylesheet());
        alert.showAndWait();
    }

    public String showTextInputDialog(String title, String content, @Nullable String defaultText) {
        if (defaultText == null) defaultText = "";
        var dialog = new TextInputDialog(defaultText);
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(content);
        dialog.initOwner(root.getScene().getWindow());

        Optional<String> result = dialog.showAndWait();

        // If the dialog was canceled, return null or handle appropriately
        if (result.isEmpty()) {
            return null; // or handle the cancel case as needed
        }

        String enteredText = result.get();
        if (enteredText.isEmpty()) {
            return "Untitled";
        }

        return enteredText;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        String sessionCookieValue = preferences.get("connect.sid", "none");
        System.out.println("LOADED SESSION COOKIE: " + sessionCookieValue);
        if (sessionCookieValue != null) {
            BasicClientCookie sessionCookie = new BasicClientCookie("connect.sid", sessionCookieValue);
            sessionCookie.setPath("/");
            sessionCookie.setDomain("localhost");
            httpCookieStore.addCookie(sessionCookie);
        }

        navWidth = (int) nav.getPrefWidth();
        initializeContentSize();


        btnToggleNav.setOnMouseClicked(e -> {
            TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.4));
            slide.setNode(nav);

            navOpened = !navOpened;
            adjustCenterPane();
        });

        btnStudySets.setOnMouseClicked(e->{
            initializeContentSize();
            showPage(dashHome);

        });

        btnSharedSets.setOnMouseClicked(e->{
            initializeContentSize();
            showPage(dashShared);
        });

        btnGenerateWithAI.setOnMouseClicked(e ->{
            initializeContentSize();
            showPage(generateAI);
        });

        btnLogout.setOnMouseClicked(event -> {
            for (Cookie cookie : httpCookieStore.getCookies()) {
                System.out.println(cookie.getName() + ": "+ cookie.getValue());
            }

            try {
                final HttpDelete httpPost = new HttpDelete("http://localhost:3000/logout");

                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");

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
                            Parent pane;
                            Stage stage = (Stage)(nav.getScene().getWindow());
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

                        Platform.runLater(() -> {
                            Parent pane;
                            Stage stage = (Stage)(btnLogout.getScene().getWindow());
                            try {
                                pane = springFXMLLoader.loadFXML("fxml/login");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                            stage.getScene().setRoot(pane);
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
                        });
                    }
                });
                thread.start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void adjustCenterPane() {
        if (navOpened) {
            DoubleProperty centerWidth = centerPane.maxWidthProperty();
            DoubleProperty navMaxWidth = nav.maxWidthProperty();

            KeyValue kvCenterStart = new KeyValue(centerWidth, centerPane.getScene().getWindow().getWidth());
            KeyValue kvCenterEnd = new KeyValue(centerWidth, centerPane.getWidth());
            KeyFrame kfCenterStart = new KeyFrame(Duration.ZERO, kvCenterStart);
            KeyFrame kfCenterEnd = new KeyFrame(Duration.millis(400), kvCenterEnd);

            KeyValue kvNavStart = new KeyValue(navMaxWidth, 0);
            KeyValue kvNavEnd = new KeyValue(navMaxWidth, navWidth);
            KeyFrame kfNavStart = new KeyFrame(Duration.ZERO, kvNavStart);
            KeyFrame kfNavEnd = new KeyFrame(Duration.millis(400), kvNavEnd);

            Timeline tl = new Timeline(kfCenterStart, kfCenterEnd, kfNavStart, kfNavEnd);
            tl.play();
            btnStudySets.setAlignment(Pos.BASELINE_LEFT);
            btnSharedSets.setAlignment(Pos.BASELINE_LEFT);
            btnGenerateWithAI.setAlignment(Pos.BASELINE_LEFT);
            btnLogout.setAlignment(Pos.BASELINE_LEFT);
            tl.setOnFinished(_ -> {
                btnStudySets.setContentDisplay(ContentDisplay.LEFT);
                btnSharedSets.setContentDisplay(ContentDisplay.LEFT);
                btnGenerateWithAI.setContentDisplay(ContentDisplay.LEFT);
                btnLogout.setContentDisplay(ContentDisplay.LEFT);
            });
        } else {
            DoubleProperty centerWidth = centerPane.maxWidthProperty();
            DoubleProperty navMaxWidth = nav.maxWidthProperty();

            KeyValue kvCenterStart = new KeyValue(centerWidth, centerPane.getWidth());
            KeyValue kvCenterEnd = new KeyValue(centerWidth, centerPane.getScene().getWindow().getWidth());
            KeyFrame kfCenterStart = new KeyFrame(Duration.ZERO, kvCenterStart);
            KeyFrame kfCenterEnd = new KeyFrame(Duration.millis(400), kvCenterEnd);

            KeyValue kvNavStart = new KeyValue(navMaxWidth, navWidth);
            KeyValue kvNavEnd = new KeyValue(navMaxWidth, 0);
            KeyFrame kfNavStart = new KeyFrame(Duration.ZERO, kvNavStart);
            KeyFrame kfNavEnd = new KeyFrame(Duration.millis(400), kvNavEnd);

            Timeline tl = new Timeline(kfCenterStart, kfCenterEnd, kfNavStart, kfNavEnd);
            tl.play();
            btnStudySets.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            btnSharedSets.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            btnGenerateWithAI.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            btnLogout.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            tl.setOnFinished(_ -> {
                btnStudySets.setAlignment(Pos.BASELINE_CENTER);
                btnGenerateWithAI.setAlignment(Pos.BASELINE_CENTER);
                btnSharedSets.setAlignment(Pos.BASELINE_CENTER);
                btnLogout.setAlignment(Pos.BASELINE_CENTER);
            });
        }
    }

    private void showPage(Node node) {
        dashHome.setVisible(false);
        dashShared.setVisible(false);
        flashCards.setVisible(false);
        flashCardsAdd.setVisible(false);
        generateAI.setVisible(false);
        mockExam.setVisible(false);
        node.toFront();
        node.setVisible(true);
    }
    private void initializeContentSize(){
        dashHome.prefWidthProperty().bind(centerPane.widthProperty());
        dashHome.prefHeightProperty().bind(centerPane.heightProperty());
        dashShared.prefWidthProperty().bind(centerPane.widthProperty());
        dashShared.prefHeightProperty().bind(centerPane.heightProperty());
        generateAI.prefWidthProperty().bind(centerPane.widthProperty());
        generateAI.prefHeightProperty().bind(centerPane.heightProperty());
        flashCards.prefWidthProperty().bind(centerPane.widthProperty());
        flashCards.prefHeightProperty().bind(centerPane.heightProperty());
        flashCardsAdd.prefWidthProperty().bind(centerPane.widthProperty());
        flashCardsAdd.prefHeightProperty().bind(centerPane.heightProperty());
        mockExam.prefWidthProperty().bind(centerPane.widthProperty());
        mockExam.prefHeightProperty().bind(centerPane.heightProperty());
    }

    public void showFlashCards() {
        showPage(flashCards);
    }

    public void showFlashCardsAdd() {
        showPage(flashCardsAdd);
        flashCardAddEditController.setSaveActionForNewFlashcard();
    }

    public void showFlashCardsEdit(FlashCard flashCard) {
        showPage(flashCardsAdd);
        flashCardAddEditController.setSaveActionForEditFlashcard(flashCard);
    }

    public void showStudySets() {
        showPage(dashHome);
    }

    public void showMockExam(List<FlashCard> flashCards){
        showPage(mockExam);
        mockExamController.setItems(flashCards);
    }

    public void disableNav() {
        nav.setDisable(true);
    }

    public void enableNav() {
        nav.setDisable(false);
    }


}
