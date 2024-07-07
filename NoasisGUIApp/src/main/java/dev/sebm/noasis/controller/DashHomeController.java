package dev.sebm.noasis.controller;

import atlantafx.base.controls.Card;
import atlantafx.base.theme.Styles;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sebm.noasis.jsonresponses.ErrorResponse;
import dev.sebm.noasis.jsonresponses.models.StudySet;
import dev.sebm.noasis.util.SpringFXMLLoader;
import jakarta.annotation.Nullable;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
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
import org.apache.http.util.EntityUtils;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

@Component
public class DashHomeController implements Initializable {
    @FXML private ScrollPane scrollPane;
    @FXML private FlowPane flowPane;
    @FXML private Button btnCreateSet;

    private final SpringFXMLLoader springFXMLLoader;
    private final Preferences preferences;
    private final CookieStore httpCookieStore = new BasicCookieStore();
    private final ApplicationContext applicationContext;

    public DashHomeController(Preferences preferences, SpringFXMLLoader springFXMLLoader, ApplicationContext applicationContext) {
        this.springFXMLLoader = springFXMLLoader;
        this.preferences = preferences.node("session");
        this.applicationContext = applicationContext;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        flowPane.prefWidthProperty().bind(scrollPane.widthProperty());

        String sessionCookieValue = preferences.get("connect.sid", "none");
        System.out.println("LOADED SESSION COOKIE: " + sessionCookieValue);
        if (sessionCookieValue != null) {
            BasicClientCookie sessionCookie = new BasicClientCookie("connect.sid", sessionCookieValue);
            sessionCookie.setPath("/");
            sessionCookie.setDomain("localhost");
            httpCookieStore.addCookie(sessionCookie);
        }

        try {
            final HttpGet httpGet = new HttpGet("http://localhost:3000/study-sets");

            httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("Content-type", "application/json");

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
                    List<StudySet> studySets = objectMapper.readValue(EntityUtils.toString(entity),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, StudySet.class));

                    Platform.runLater(() -> {
                        studySets.forEach(studySet -> {
                            Card card = new Card();
                            card.setMinWidth(300);
                            card.setMinHeight(200);

                            Label title = new Label(studySet.getTitle());
                            title.getStyleClass().add(Styles.TITLE_3);
                            card.setHeader(title);

                            TextFlow text = new TextFlow(new Text(studySet.getId()));
                            text.setMaxWidth(260);
                            card.setBody(text);

                            ContextMenu ctxMenu = new ContextMenu();

                            MenuItem editItem = createItem("Edit", FontAwesome.PENCIL_SQUARE_O, null);
                            MenuItem delete = createItem("Delete", FontAwesome.TRASH_O, null);

                            ctxMenu.getItems().addAll(
                                    editItem,
                                    delete
                            );

                            card.setContextMenu(ctxMenu);
                            flowPane.getChildren().add(card);
                        });
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
        }

        btnCreateSet.setOnMouseClicked(e -> {
            DashboardLayoutController dashboardLayoutController = applicationContext.getBean(DashboardLayoutController.class);
            dashboardLayoutController.showFlashCards();
        });

   }

    private MenuItem createItem(@Nullable String text, @Nullable Ikon graphic, @Nullable KeyCombination accelerator) {
        MenuItem menuItem = new MenuItem(text);
        if (graphic != null) {
            menuItem.setGraphic(new FontIcon(graphic));
        }
        if (accelerator != null) {
            menuItem.setAccelerator(accelerator);
        }

        return menuItem;
    }
}


