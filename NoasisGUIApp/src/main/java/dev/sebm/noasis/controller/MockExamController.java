package dev.sebm.noasis.controller;

import dev.sebm.noasis.jsonresponses.models.FlashCard;
import dev.sebm.noasis.util.TestItem;
import dev.sebm.noasis.util.TestItemBuilder;
import dev.sebm.noasis.util.TestItemCard;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.*;

@Component
public class MockExamController implements Initializable {
    private final ApplicationContext applicationContext;
    @FXML private Pane items;
    @FXML private Button btnSubmitContinue, btnCancelRetry;

    public MockExamController(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void setItems(List<FlashCard> flashCards) {
        List<TestItemCard> answers = new ArrayList<>();
        TestItemBuilder tib = new TestItemBuilder(flashCards);

        items.getChildren().clear();
        List<FlashCard> shuffledFlashcards = new ArrayList<>(flashCards);
        Collections.shuffle(shuffledFlashcards);
        for (int i = 0; i < shuffledFlashcards.size(); i++) {
            FlashCard flashCard = shuffledFlashcards.get(i);
            TestItem item = tib.createItem(flashCard);
            TestItemCard card = new TestItemCard(item);
            answers.add(card);
            items.getChildren().add(card);
        }
        btnSubmitContinue.setOnMouseClicked(_ -> {
            answers.forEach(answer -> {
                answer.showResult();
//                answer.loadCard();
            });
//            DashboardLayoutController dashboardLayoutController = applicationContext.getBean(DashboardLayoutController.class);
//            dashboardLayoutController.enableNav();
//            dashboardLayoutController.showFlashCards();

        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnCancelRetry.setOnMouseClicked(_ -> returnToFlashcards());
    }

    private void returnToFlashcards() {
            DashboardLayoutController dashboardLayoutController = applicationContext.getBean(DashboardLayoutController.class);
            dashboardLayoutController.enableNav();
            dashboardLayoutController.showFlashCards();
    }
}
