package dev.sebm.noasis.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.springframework.stereotype.Component;

import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;



@Component
public class testController {
    @FXML
    private Label statusLabel;

    @FXML
    private Button uploadButton;

    @FXML
    public void initialize() {
        // Set up drag and drop event handlers
        uploadButton.setOnDragOver(this::handleDragOver);
        uploadButton.setOnDragDropped(this::handleDragDropped);
    }

    @FXML
    private void handleUploadButtonAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            loadPdfFile(file);
        }
    }

    private void handleDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

    private void handleDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasFiles()) {
            success = true;
            File file = db.getFiles().get(0);
            if (file.getName().endsWith(".pdf")) {
                loadPdfFile(file);
            } else {
                statusLabel.setText("Not a PDF file.");
            }
        }
        event.setDropCompleted(success);
        event.consume();
    }

    private void loadPdfFile(File file) {
        // Load and process the PDF file
        statusLabel.setText("Loaded: " + file.getName());
        // Your code to process the PDF goes here
    }
}