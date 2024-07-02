package dev.sebm.noasis.util;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class General {
    public static void msgError(Label errorLabel, TextField tf, String errorMsg){
        errorLabel.setVisible(true);
        errorLabel.setText(errorMsg);
        errorLabel.setStyle("-fx-font-size: 10px;");
        tf.setStyle("-fx-border-color: red;");
    }

    public static void msgOkay(Label errorLabel, TextField tf){
        errorLabel.setStyle("-fx-font-size: 1px;");
        errorLabel.setVisible(false);
        tf.setStyle("-fx-border-color: none;");
    }
}
