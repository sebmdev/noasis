package dev.sebm.noasis.controller;

import atlantafx.base.controls.ModalPane;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class FlashCardsController implements Initializable {
    @FXML private TilePane tilePane;
    @FXML private Button btnAddCard;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TextArea textArea1 = new TextArea();
        textArea1.setEditable(false);
        textArea1.setPrefHeight(100.0);
        textArea1.setPrefWidth(200.0);
        textArea1.setWrapText(true);

        // Create the second TextArea and wrap it in an AnchorPane
        TextArea textArea2 = new TextArea("\n");
        textArea2.setEditable(false);
        textArea2.setMaxHeight(Double.MAX_VALUE);
        textArea2.setMaxWidth(Double.MAX_VALUE);
        textArea2.setWrapText(true);

        AnchorPane anchorPane = new AnchorPane(textArea2);
        AnchorPane.setBottomAnchor(textArea2, 0.0);
        AnchorPane.setLeftAnchor(textArea2, 0.0);
        AnchorPane.setRightAnchor(textArea2, 0.0);
        AnchorPane.setTopAnchor(textArea2, 0.0);

        // Create the ScrollPane and add the AnchorPane as its content
        ScrollPane scrollPane = new ScrollPane(anchorPane);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPrefHeight(100);
        scrollPane.setPrefWidth(800);

        // Create the HBox and add the TextArea and ScrollPane
        HBox hbox = new HBox();
        hbox.setPrefHeight(100.0);
        hbox.getChildren().addAll(textArea1, scrollPane);

        tilePane.getChildren().add(hbox);

        btnAddCard.setOnMouseClicked(_ -> {
        });
    }
}
