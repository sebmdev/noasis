package dev.sebm.noasis.controller;

import atlantafx.base.controls.Card;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sebm.noasis.guicomponent.FlipCard;
import dev.sebm.noasis.jsonresponses.ErrorResponse;
import dev.sebm.noasis.jsonresponses.models.FlashCard;
import dev.sebm.noasis.jsonresponses.models.User;
import dev.sebm.noasis.util.SpringFXMLLoader;
import impl.org.controlsfx.skin.AutoCompletePopup;
import impl.org.controlsfx.skin.AutoCompletePopupSkin;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

@Component
public class ShareDialogController implements Initializable {
    @FXML private TextField search;

    private AutoCompletionBinding<User> autoCompletionBinding;
    private final List<User> selectedUsers = new ArrayList<>();

    private final CookieStore httpCookieStore = new BasicCookieStore();
    private final Preferences preferences;

    public ShareDialogController(
            Preferences preferences,
            SpringFXMLLoader springFXMLLoader) {
        this.preferences = preferences.node("session");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        autoCompletionBinding = TextFields.bindAutoCompletion(search, param -> fetchUsers(param.getUserText()));
        autoCompletionBinding.setOnAutoCompleted(event -> {
            // Prevent text field from updating
            Platform.runLater(() -> search.setText(""));
            // Add selected item to list
            if (!selectedUsers.contains(event.getCompletion())) {
                selectedUsers.add(event.getCompletion());
            }
            System.out.println(selectedUsers);
        });

        autoCompletionBinding.setDelay(500);
        AutoCompletePopup<User> colorCompletionPopup = autoCompletionBinding.getAutoCompletionPopup();
        colorCompletionPopup.setSkin(new AutoCompletePopupSkin<>(colorCompletionPopup, param -> new CustomListCell()));
    }

    private void updateSuggestions(String query) {
        List<User> allSuggestions = fetchUsers(query);
        List<User> filteredSuggestions = allSuggestions.stream()
                .filter(user -> !selectedUsers.contains(user))
                .collect(Collectors.toList());
        autoCompletionBinding.getAutoCompletionPopup().getSuggestions().setAll(filteredSuggestions);
    }

    private List<User> fetchUsers(String query) {
        CompletableFuture<List<User>> future = CompletableFuture.supplyAsync(() -> {
            String sessionCookieValue = preferences.get("connect.sid", "none");
            if (sessionCookieValue != null) {
                BasicClientCookie sessionCookie = new BasicClientCookie("connect.sid", sessionCookieValue);
                sessionCookie.setPath("/");
                sessionCookie.setDomain("localhost");
                httpCookieStore.addCookie(sessionCookie);
            }
            final HttpGet httpGet;
            try {
                String encodedInput = URLEncoder.encode(query, StandardCharsets.UTF_8);
                String queryParam = "q=" + encodedInput;
                URI uri = new URI("http", "localhost:3000", "/users", queryParam, null);
                String q = uri.toASCIIString();
                httpGet = new HttpGet(q);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }

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

            ResponseHandler<List<User>> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                ObjectMapper objectMapper = new ObjectMapper();

                if (status >= 400) {
                    ErrorResponse errorResponse = objectMapper.readValue(entity.getContent(), ErrorResponse.class);
                    System.out.println(errorResponse.getError());
                    return List.of();
                }
                if (entity != null) {
                    return objectMapper.readValue(EntityUtils.toString(entity),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, User.class));
                }
                return List.of();
            };

            try {
                return httpClient.execute(httpGet, responseHandler);
            } catch (IOException e) {
                e.printStackTrace();
                return List.of();
            } finally {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            return future.get(); // Block and get the result
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }


    private class CustomListCell extends ListCell<User> {
        @Override
        public void updateItem(User item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
                setStyle("");
            } else {
                setText(item.getEmail());
                if (isSelected()) {
                    setStyle("-fx-background-color: #0078d7; -fx-text-fill: white;");
                } else {
                    setStyle("-fx-background-color: white; -fx-text-fill: black;");
                }
            }
        }

        @Override
        public void updateSelected(boolean selected) {
            super.updateSelected(selected);
            if (selected) {
                setStyle("-fx-background-color: #0078d7; -fx-text-fill: white;");
            } else {
                setStyle("-fx-background-color: white; -fx-text-fill: black;");
            }
        }
    }
}
