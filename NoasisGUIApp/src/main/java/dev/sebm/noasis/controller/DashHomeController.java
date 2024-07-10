package dev.sebm.noasis.controller;

import atlantafx.base.controls.Card;
import atlantafx.base.theme.Styles;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sebm.noasis.jsonresponses.CreateFlashcardSuccessResponse;
import dev.sebm.noasis.jsonresponses.ErrorResponse;
import dev.sebm.noasis.jsonresponses.models.StudySet;
import jakarta.annotation.Nullable;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
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
    private final FlashCardsController flashCardsController;
    private final DashboardLayoutController dashboardLayoutController;
    @FXML private ScrollPane scrollPane;
    @FXML private FlowPane flowPane;
    @FXML private Button btnCreateSet;

    private final Preferences preferences;
    private final CookieStore httpCookieStore = new BasicCookieStore();
    private final ApplicationContext applicationContext;

    public DashHomeController(Preferences preferences, ApplicationContext applicationContext, FlashCardsController flashCardsController, FlashCardAddEditController flashCardAddEditController, DashboardLayoutController dashboardLayoutController) {
        this.preferences = preferences.node("session");
        this.applicationContext = applicationContext;
        this.flashCardsController = flashCardsController;
        this.dashboardLayoutController = dashboardLayoutController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        flowPane.prefWidthProperty().bind(scrollPane.widthProperty());
        loadStudySets();

        btnCreateSet.setOnMouseClicked(e -> {
            String title = dashboardLayoutController.showTextInputDialog("New study set", "Title: ", null);
            System.out.println(title);
            if (title == null) return;
            try {
                final HttpPost httpPost = new HttpPost("http://localhost:3000/study-sets/");

                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
                String json = "{\r\n" +
                        String.format("  \"title\": \"%s\"\r\n", title) +
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

//                    content.setDisable(true);
//                    progressIndicator.setVisible(true);

                    if (status >= 400) {
                        ErrorResponse errorResponse = objectMapper.readValue(entity.getContent(), ErrorResponse.class);
                        System.out.println(errorResponse.getError());
                        Platform.runLater(() -> {
//                            content.setDisable(false);
//                            progressIndicator.setVisible(false);
                        });
                        loadStudySets();
                        return null;
                    }

                    if (entity != null) {
                        CreateFlashcardSuccessResponse createFlashcardSuccessResponse = objectMapper
                                .readValue(entity.getContent(), CreateFlashcardSuccessResponse.class);

                        System.out.println(createFlashcardSuccessResponse);

                        Platform.runLater(() -> {
//                            content.setDisable(false);
//                            progressIndicator.setVisible(false);
                            loadStudySets();
                        });
                    }
                    return null;
                };

                Thread thread = new Thread(() -> {
                    try {
                        httpClient.execute(httpPost, responseHandler);
                        httpClient.close();
                    } catch (IOException e1) {
                    }
                });
                thread.start();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        });

   }

   private void loadStudySets() {
       flowPane.getChildren().clear();
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

                           ContextMenu ctxMenu = new ContextMenu();

                           MenuItem editItem = createItem("Edit", FontAwesome.PENCIL_SQUARE_O, null);
                           MenuItem delete = createItem("Delete", FontAwesome.TRASH_O, null);

                           ctxMenu.getItems().addAll(
                                   editItem,
                                   delete
                           );

                           editItem.setOnAction(_ -> {
                               String newTitle = dashboardLayoutController.showTextInputDialog("Edit title", "Title", studySet.getTitle());
                               if (newTitle == null) return;
                               try {
                                   final HttpPut httpPut = new HttpPut("http://localhost:3000/study-sets/" + studySet.getId());

                                   httpPut.setHeader("Accept", "application/json");
                                   httpPut.setHeader("Content-type", "application/json");
                                   String json = "{\r\n" +
                                           String.format("  \"title\": \"%s\"\r\n", newTitle) +
                                           "}";
                                   System.out.println(json);
                                   StringEntity stringEntity = new StringEntity(json);
                                   httpPut.setEntity(stringEntity);

                                   CloseableHttpClient putHttpClient = HttpClientBuilder
                                           .create()
                                           .setDefaultCookieStore(httpCookieStore)
                                           .setDefaultRequestConfig(RequestConfig
                                                   .custom()
                                                   .setCookieSpec(CookieSpecs.STANDARD)
                                                   .build())
                                           .build();

                                   ResponseHandler<String> putResponseHandler = putResponse -> {
                                       int putStatus = putResponse.getStatusLine().getStatusCode();
                                       System.out.println(putResponse.getEntity().toString());
                                       HttpEntity putEntity = putResponse.getEntity();
                                       ObjectMapper putObjectMapper = new ObjectMapper();

//                                       content.setDisable(true);
//                                       progressIndicator.setVisible(true);

                                       if (putStatus >= 400) {
                                           ErrorResponse errorResponse = objectMapper.readValue(entity.getContent(), ErrorResponse.class);
                                           System.out.println(errorResponse.getError());
                                           Platform.runLater(() -> {
                                               loadStudySets();
//                                               content.setDisable(false);
//                                               progressIndicator.setVisible(false);
                                           });
                                           return null;
                                       }

                                       if (putEntity != null) {
                                           Platform.runLater(() -> {
                                               loadStudySets();
//                                               content.setDisable(false);
//                                               progressIndicator.setVisible(false);
                                           });
                                       }
                                       return null;
                                   };

                                   Thread thread = new Thread(() -> {
                                       try {
                                           putHttpClient.execute(httpPut, putResponseHandler);
                                           putHttpClient.close();
                                       } catch (IOException e) {
                                       }
                                   });
                                   thread.start();
                               } catch (Exception e) {
                                   e.printStackTrace();
                               }
                           });

                           delete.setOnAction(_ -> {
                               boolean confirmDelete = dashboardLayoutController.showDeleteDialog("Delete study set", "Are you sure you want to delete this study set?");
                               if (!confirmDelete) return;
                               try {
                                   final HttpDelete httpDelete = new HttpDelete("http://localhost:3000/study-sets/" + studySet.getId());

                                   CloseableHttpClient deleteHttpClient = HttpClientBuilder
                                           .create()
                                           .setDefaultCookieStore(httpCookieStore)
                                           .setDefaultRequestConfig(RequestConfig
                                                   .custom()
                                                   .setCookieSpec(CookieSpecs.STANDARD)
                                                   .build())
                                           .build();

                                   httpDelete.setHeader("Accept", "application/json");
                                   httpDelete.setHeader("Content-type", "application/json");

                                   ResponseHandler<String> delResponseHandler = delResponse -> {
                                       int delStatus = delResponse.getStatusLine().getStatusCode();
                                       System.out.println(delResponse.getEntity().toString());
                                       HttpEntity delEntity = delResponse.getEntity();
                                       ObjectMapper delObjectMapper = new ObjectMapper();

                                       if (delStatus >= 400) {
                                           ErrorResponse errorResponse = delObjectMapper.readValue(delEntity.getContent(), ErrorResponse.class);
                                           System.out.println(errorResponse.getError());
                                           Platform.runLater(() -> {
                                                loadStudySets();
                                           });
                                           return null;
                                       }

                                       if (delEntity != null) {
                                           Platform.runLater(() -> {
                                            loadStudySets();
                                           });
                                       }
                                       return null;
                                   };

                                   Thread thread = new Thread(() -> {
                                       try {
                                           deleteHttpClient.execute(httpDelete, delResponseHandler);
                                           deleteHttpClient.close();
                                       } catch (IOException e) {
                                       }
                                   });
                                   thread.start();
                               } catch (Exception e) {
                                   e.printStackTrace();
                               }
                           });

                           card.setContextMenu(ctxMenu);

                           card.setOnMouseClicked(event -> {
                               if (event.getButton() == MouseButton.PRIMARY) {
                                   flashCardsController.loadFlashCards(studySet.getId());
                                   FlashCardAddEditController flashCardAddEditController = applicationContext.getBean(FlashCardAddEditController.class);
                                   flashCardAddEditController.setStudySetId(studySet.getId());
                                   DashboardLayoutController dashboardLayoutController = applicationContext.getBean(DashboardLayoutController.class);
                                   dashboardLayoutController.showFlashCards();
                               } else if (event.getButton() == MouseButton.SECONDARY) {
                                   // Show context menu if the secondary mouse button (right-click) was pressed
                                   ctxMenu.show(card, event.getScreenX(), event.getScreenY());
                               }
                           });

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


