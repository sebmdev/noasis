package dev.sebm.noasis.controller;

import atlantafx.base.controls.RingProgressIndicator;
import dev.sebm.noasis.jsonresponses.models.FlashCard;
import dev.sebm.noasis.util.TestItem;
import dev.sebm.noasis.util.TestItemBuilder;
import dev.sebm.noasis.util.TestItemCard;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MockExamController implements Initializable {
    private final ApplicationContext applicationContext;
    @FXML private Pane items;
    @FXML private Button btnSubmitContinue, btnCancelRetry;
    @FXML private Text txtScore;
    @FXML private RingProgressIndicator piScorePercent;
    @FXML private Pane scoreContainer;

    private boolean isShowingResults;
    private List<TestItemCard> answers;

    public MockExamController(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.isShowingResults = false;
    }

    public void setItems(List<FlashCard> flashCards) {
        TestItemBuilder tib = new TestItemBuilder(flashCards);

        shuffleItems(flashCards, tib);
        scoreContainer.setVisible(false);

        btnSubmitContinue.setOnMouseClicked(_ -> {
            if (!isShowingResults) {
                isShowingResults = true;
                btnSubmitContinue.setText("Return");
                btnCancelRetry.setText("Retry");
                AtomicInteger correct = new AtomicInteger();
                answers.forEach(answer -> {
                    answer.showResult();
                    if (answer.getResult()) {
                        correct.getAndAdd(1);
                    }
                });
                txtScore.setText(correct + "/" + answers.size());
                piScorePercent.setProgress((double) correct.get() / answers.size());
                scoreContainer.setVisible(true);
            } else {
                btnSubmitContinue.setText("Submit");
                btnCancelRetry.setText("Cancel");
                isShowingResults = false;
                DashboardLayoutController dashboardLayoutController = applicationContext.getBean(DashboardLayoutController.class);
                dashboardLayoutController.enableNav();
                dashboardLayoutController.showFlashCards();
            }
        });

        btnCancelRetry.setOnMouseClicked(_ -> {
            if (isShowingResults) {
               shuffleItems(flashCards, tib);
               answers.forEach(answer -> {
                   answer.loadCard();
               });
               btnSubmitContinue.setText("Submit");
               btnCancelRetry.setText("Cancel");
                scoreContainer.setVisible(false);
            } else {
               DashboardLayoutController dashboardLayoutController = applicationContext.getBean(DashboardLayoutController.class);
               dashboardLayoutController.enableNav();
               dashboardLayoutController.showFlashCards();
            }
            isShowingResults = false;
        });
    }

    private void shuffleItems(List<FlashCard> flashCards, TestItemBuilder tib) {
        answers = new ArrayList<>();
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
