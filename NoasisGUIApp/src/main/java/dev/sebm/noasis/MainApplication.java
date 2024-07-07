package dev.sebm.noasis;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Lazy;

@SpringBootApplication
@Lazy
public class MainApplication{

    public static void main(String[] args) {
//        SpringApplication.run(NoasisApplication.class, args);
        Application.launch(NoasisApplication.class, args);
    }

    double x, y = 0;
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("src/main/resources/dev/sebm/Noasis/fxml/StudySets.fxml"));
        primaryStage.initStyle(StageStyle.UNDECORATED);

        root.setOnMousePressed(event -> {
            x = event.getSceneX();
            y = event.getSceneY();

        });

        root.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - x);
            primaryStage.setY(event.getScreenY() - y);

            primaryStage.setScene(new Scene(root, 800, 500));
            primaryStage.show();
        });


    }
}
