package dev.sebm.noasis.util;

import atlantafx.base.controls.Card;
import dev.sebm.noasis.jsonresponses.models.FlashCard;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestItemCard extends Card {
    private ToggleGroup toggleGroup;
    private TestItem item;
    private HBox statusContainer;
    private List<FlashCard> choices;

    public void shuffleChoices() {
        choices = new ArrayList<>();
        choices.add(item.getCorrectFlashCard());
        FlashCard[] dummyChoices = item.getDummyFlashCards();
        choices.addAll(Arrays.asList(dummyChoices));
        Collections.shuffle(choices);
    }

    public void loadCard() {
        setHeader(null);
        setFooter(null);
        shuffleChoices();
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

        // TextArea
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setText(item.getCorrectFlashCard().getDefinition());
        AnchorPane.setTopAnchor(textArea, 0.0);
        AnchorPane.setBottomAnchor(textArea, 0.0);
        AnchorPane.setLeftAnchor(textArea, 0.0);
        AnchorPane.setRightAnchor(textArea, 0.0);

        innerAnchorPane.getChildren().add(textArea);
        scrollPane.setContent(innerAnchorPane);

        // RadioButtons
        RadioButton radioButton1 = new RadioButton("A) " + choices.get(0).getTerm());
        radioButton1.setUserData(0);
        radioButton1.setToggleGroup(toggleGroup);
        GridPane.setRowIndex(radioButton1, 1);

        RadioButton radioButton2 = new RadioButton("B) " + choices.get(1).getTerm());
        radioButton2.setUserData(1);
        radioButton2.setToggleGroup(toggleGroup);
        GridPane.setColumnIndex(radioButton2, 1);
        GridPane.setRowIndex(radioButton2, 1);

        RadioButton radioButton3 = new RadioButton("C) " + choices.get(2).getTerm());
        radioButton3.setUserData(2);
        radioButton3.setToggleGroup(toggleGroup);
        GridPane.setRowIndex(radioButton3, 2);

        RadioButton radioButton4 = new RadioButton("D) " + choices.get(3).getTerm());
        radioButton4.setUserData(3);
        radioButton4.setToggleGroup(toggleGroup);
        GridPane.setColumnIndex(radioButton4, 1);
        GridPane.setRowIndex(radioButton4, 2);

        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                RadioButton selectedRadioButton = (RadioButton) newValue;
                int selectedIndex = (int) selectedRadioButton.getUserData();
                item.setAnswer(choices.get(selectedIndex));
                System.out.println("Selected Radio Button Index: " + selectedIndex);
            }
        });

        // Add children to GridPane
        gridPane.getChildren().addAll(scrollPane, radioButton1, radioButton2, radioButton3, radioButton4);

        // Add GridPane to AnchorPane
        anchorPane.getChildren().add(gridPane);

        // Add AnchorPane to HBox
        tilePane.getChildren().add(anchorPane);

        setBody(tilePane);

        statusContainer = new HBox();
        statusContainer.setPadding(new Insets(0, 0, 0, 10));
        statusContainer.setAlignment(Pos.CENTER_LEFT);

        setMaxWidth(600);
        setPrefHeight(200);
    }

    public TestItemCard(TestItem item) {
        this.item = item;
        this.toggleGroup = new ToggleGroup();
        loadCard();
    }

    public boolean getResult() {
        return item.checkAnswer();
    }

    public void showResult() {
        Text status = new Text();
        statusContainer.getChildren().clear();
        if (item.getAnswer() == null) {
            status.setText("No answer!");
            status.setStyle("-fx-fill: -color-danger-fg");
            statusContainer.setStyle("-fx-background-color: -color-danger-subtle");
            String correctLetter = "";
            switch (choices.indexOf(item.getCorrectFlashCard())) {
                case 0 -> correctLetter = "A) ";
                case 1 -> correctLetter = "B) ";
                case 2 -> correctLetter = "C) ";
                case 3 -> correctLetter = "D) ";
            }
            setFooter(new Text("Correct answer: " + correctLetter + item.getCorrectFlashCard().getTerm()));
        } else if (item.checkAnswer()) {
            status.setText("Correct!");
            status.setStyle("-fx-fill: -color-success-fg");
            statusContainer.setStyle("-fx-background-color: -color-success-subtle");
        } else {
            status.setText("Incorrect!");
            status.setStyle("-fx-fill: -color-danger-fg");
            statusContainer.setStyle("-fx-background-color: -color-danger-subtle");
            String correctLetter = "";
            switch (choices.indexOf(item.getCorrectFlashCard())) {
                case 0 -> correctLetter = "A) ";
                case 1 -> correctLetter = "B) ";
                case 2 -> correctLetter = "C) ";
                case 3 -> correctLetter = "D) ";
            }
            setFooter(new Text("Correct answer: " + correctLetter + item.getCorrectFlashCard().getTerm()));
        }
        toggleGroup.getToggles().forEach(toggle -> {
            RadioButton radioButton = (RadioButton) toggle;
            radioButton.setDisable(true);
        });
        statusContainer.getChildren().add(status);
        setHeader(statusContainer);
    }
}