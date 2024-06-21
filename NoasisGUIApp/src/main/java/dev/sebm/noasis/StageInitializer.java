package dev.sebm.noasis;

import atlantafx.base.theme.PrimerLight;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationListener;
import dev.sebm.noasis.NoasisApplication.StageReadyEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StageInitializer implements ApplicationListener<NoasisApplication.StageReadyEvent> {
    private static Scene scene;

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        Stage stage = event.getStage();
        try {
            scene = new Scene(loadFXML("fxml/login"), 640, 480);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.setTitle("Noasis");
        stage.setHeight(400);
        stage.setWidth(600);
        stage.setScene(scene);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StageInitializer.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
}
