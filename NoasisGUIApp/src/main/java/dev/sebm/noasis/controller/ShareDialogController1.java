package dev.sebm.noasis.controller;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class ShareDialogController1 implements Initializable {
    @FXML private ComboBox<String> cmbSearch;
    @FXML private Text hiddenFocusTarget;

    private Thread currentSearchThread;
    private ObservableList<String> searchResults = FXCollections.observableArrayList();
    private List<String> sharedUsers = new ArrayList<>();
    private String currentText = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        pause.setOnFinished(evt -> {
            String newValue = cmbSearch.getEditor().getText();
            System.out.println("Debounced text: " + newValue);
            performSearch(newValue);
        });
        cmbSearch.setPlaceholder(new Text("Search users"));

        TextField cmbSearchEditor = cmbSearch.getEditor();

        cmbSearch.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                cmbSearch.show();
            }
        });

        cmbSearch.setOnAction(event -> {
            int selectedIndex = cmbSearch.getSelectionModel().getSelectedIndex();
            if (selectedIndex != -1 && selectedIndex < searchResults.size()) {
                String selectedItem = searchResults.get(selectedIndex);
                System.out.println("Selected item: " + selectedItem);
                sharedUsers.add(selectedItem);
                Platform.runLater(() -> {
                    searchResults.remove(selectedItem);
                    cmbSearch.getItems().setAll(searchResults);
                    cmbSearch.getSelectionModel().clearSelection();
                    cmbSearchEditor.clear();
                    cmbSearchEditor.textProperty().set(currentText);
                });
            }
            System.out.println("Shared users: " + sharedUsers);
        });

        cmbSearchEditor.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("typed: " + newValue);
            currentText = newValue;
            if (newValue.length() == 0) {
                cmbSearch.setPlaceholder(new Text("Search users"));
            } else {
                cmbSearch.setPlaceholder(new Text("Loading..."));
            }
            if (currentSearchThread != null && currentSearchThread.isAlive()) {
                currentSearchThread.interrupt();
            }
            pause.playFromStart();
        });
    }

    private void performSearch(String query) {
        currentSearchThread = new Thread(() -> {
            try {
                // Simulate a long-running search operation
                searchResults.clear();
                Thread.sleep(500);
                // Check if the thread was interrupted before proceeding
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                searchResults.add("a");
                searchResults.add("b");

                // Replace this with actual search logic

                Platform.runLater(() -> {
                    hiddenFocusTarget.requestFocus();
                    cmbSearch.hide();
                    cmbSearch.getItems().setAll(searchResults);
                    cmbSearch.show();
                    cmbSearch.requestFocus();
                });
            } catch (InterruptedException e) {
                // Thread was interrupted, handle if necessary
                System.out.println("Search thread interrupted");
            }
        });
        currentSearchThread.start();
    }
}
