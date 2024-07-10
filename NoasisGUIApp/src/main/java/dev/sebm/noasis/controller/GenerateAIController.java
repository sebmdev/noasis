package dev.sebm.noasis.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;


@Component
public class GenerateAIController {
    @FXML

    private Label statusLabel;


    @FXML

    private Button uploadButton, btnGenerate;


    private File selectedFile; // Class-level variable to store the selected file


    @FXML

    public void initialize() {

        // Set up drag and drop event handlers

        uploadButton.setOnDragOver(this::handleDragOver);

        uploadButton.setOnDragDropped(this::handleDragDropped);


        btnGenerate.setOnMouseClicked(e -> {

            if (selectedFile != null) {

                try {

                    processPdfFile(selectedFile);

                } catch (IOException ex) {

                    ex.printStackTrace(); // Handle or log the exception as needed

                }

            } else {

                System.err.println("No file selected or invalid file path.");

            }

        });

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

                statusLabel.setText("Invalid File.");

            }

        }

        event.setDropCompleted(success);

        event.consume();

    }


    private void loadPdfFile(File file) {

        // Load and process the PDF file

        statusLabel.setText(file.getName());

        selectedFile = file; // Store the selected file

        // Your code to process the PDF goes here

    }


    private void processPdfFile(File file) throws IOException {

        PdfReader reader = new PdfReader(file.getAbsolutePath());

        int pages = reader.getNumberOfPages();

        StringBuilder text = new StringBuilder();

        for (int i = 1; i <= pages; i++) {

            text.append(PdfTextExtractor.getTextFromPage(reader, i));

        }

        reader.close();
        System.out.println(text);

    }
}