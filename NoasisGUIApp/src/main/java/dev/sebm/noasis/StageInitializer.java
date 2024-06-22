package dev.sebm.noasis;

import dev.sebm.noasis.NoasisApplication.StageReadyEvent;
import dev.sebm.noasis.util.SpringFXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StageInitializer implements ApplicationListener<NoasisApplication.StageReadyEvent> {
    private final SpringFXMLLoader loader;

    public StageInitializer(SpringFXMLLoader loader) {
        this.loader = loader;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        Stage stage = event.getStage();
        Scene scene;

        try {
            scene = new Scene(loader.loadFXML("fxml/login"), 640, 480);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        stage.setTitle("Noasis");
        stage.setHeight(400);
        stage.setWidth(600);
        stage.setMinWidth(600);
        stage.setMinHeight(400);
        stage.setScene(scene);
        stage.show();
    }
}
