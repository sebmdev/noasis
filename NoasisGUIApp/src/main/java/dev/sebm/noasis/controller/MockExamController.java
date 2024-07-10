package dev.sebm.noasis.controller;

import atlantafx.base.controls.Card;
import dev.sebm.noasis.jsonresponses.models.FlashCard;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class MockExamController implements Initializable {
    private final ApplicationContext applicationContext;
    @FXML private Pane items;
    @FXML private Button btnSubmit, btnCancel;

    public MockExamController(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void setItems(List<FlashCard> flashCards) {
        items.getChildren().clear();
        Collections.shuffle(flashCards);
        flashCards.forEach(flashCard -> {
            // HBox
            HBox tilePane = new HBox();

            // AnchorPane
            AnchorPane anchorPane = new AnchorPane();

            // GridPane
            GridPane gridPane = new GridPane();
            AnchorPane.setTopAnchor(gridPane, 0.0);
            AnchorPane.setBottomAnchor(gridPane, 0.0);
            AnchorPane.setLeftAnchor(gridPane, 0.0);
            AnchorPane.setRightAnchor(gridPane, 0.0);

            // Column constraints
            ColumnConstraints col1 = new ColumnConstraints();
            col1.setHgrow(Priority.SOMETIMES);

            ColumnConstraints col2 = new ColumnConstraints();
            col2.setHgrow(Priority.SOMETIMES);

            gridPane.getColumnConstraints().addAll(col1, col2);

            // Row constraints
            RowConstraints row1 = new RowConstraints();
            row1.setVgrow(Priority.SOMETIMES);

            RowConstraints row2 = new RowConstraints();
            row2.setVgrow(Priority.SOMETIMES);

            RowConstraints row3 = new RowConstraints();
            row3.setVgrow(Priority.SOMETIMES);

            gridPane.getRowConstraints().addAll(row1, row2, row3);

            // ScrollPane
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToHeight(true);
            scrollPane.setFitToWidth(true);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            GridPane.setColumnSpan(scrollPane, Integer.MAX_VALUE);

            // Inner AnchorPane
            AnchorPane innerAnchorPane = new AnchorPane();
            innerAnchorPane.setPrefSize(862.0, 55.0);

            // TextArea
            TextArea textArea = new TextArea();
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setText(flashCard.getDefinition());
            AnchorPane.setTopAnchor(textArea, 0.0);
            AnchorPane.setBottomAnchor(textArea, 0.0);
            AnchorPane.setLeftAnchor(textArea, 0.0);
            AnchorPane.setRightAnchor(textArea, 0.0);

            innerAnchorPane.getChildren().add(textArea);
            scrollPane.setContent(innerAnchorPane);

            ToggleGroup toggleGroup = new ToggleGroup();

            // RadioButtons
            RadioButton radioButton1 = new RadioButton(flashCard.getTerm());
            radioButton1.setToggleGroup(toggleGroup);
            GridPane.setRowIndex(radioButton1, 1);

            RadioButton radioButton2 = new RadioButton(flashCard.getTerm());
            radioButton2.setToggleGroup(toggleGroup);
            GridPane.setColumnIndex(radioButton2, 1);
            GridPane.setRowIndex(radioButton2, 1);

            RadioButton radioButton3 = new RadioButton(flashCard.getTerm());
            radioButton3.setToggleGroup(toggleGroup);
            GridPane.setRowIndex(radioButton3, 2);

            RadioButton radioButton4 = new RadioButton(flashCard.getTerm());
            radioButton4.setToggleGroup(toggleGroup);
            GridPane.setColumnIndex(radioButton4, 1);
            GridPane.setRowIndex(radioButton4, 2);

            // Add children to GridPane
            gridPane.getChildren().addAll(scrollPane, radioButton1, radioButton2, radioButton3, radioButton4);

            // Add GridPane to AnchorPane
            anchorPane.getChildren().add(gridPane);

            // Add AnchorPane to HBox
            tilePane.getChildren().add(anchorPane);

            Card card = new Card();
            card.setBody(tilePane);
            card.setMaxWidth(600);
            card.setPrefHeight(200);
            items.getChildren().add(card);
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnSubmit.setOnMouseClicked(_ -> {
            DashboardLayoutController dashboardLayoutController = applicationContext.getBean(DashboardLayoutController.class);
            dashboardLayoutController.enableNav();
            dashboardLayoutController.showFlashCards();
        });

        btnCancel.setOnMouseClicked(_ -> {
            DashboardLayoutController dashboardLayoutController = applicationContext.getBean(DashboardLayoutController.class);
            dashboardLayoutController.enableNav();
            dashboardLayoutController.showFlashCards();
        });
    }
}
