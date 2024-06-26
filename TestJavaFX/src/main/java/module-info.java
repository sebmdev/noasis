module dev.sebm {
    requires javafx.controls;
    requires javafx.fxml;
    requires atlantafx.base;
    requires spring.context;

    opens dev.sebm to javafx.fxml;
    exports dev.sebm;
}
