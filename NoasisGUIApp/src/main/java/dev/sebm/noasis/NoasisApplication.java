package dev.sebm.noasis;

import atlantafx.base.theme.PrimerLight;
import dev.sebm.noasis.util.SpringFXMLLoader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.util.Arrays;

public class NoasisApplication extends Application {

    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init() {
        applicationContext = new SpringApplicationBuilder(MainApplication.class).run();
        Arrays.asList(applicationContext.getBeanDefinitionNames()).forEach(System.out::println);
    }

    @Override
    public void stop() {
        applicationContext.stop();
        Platform.exit();
    }

    @Override
    public void start(Stage stage) {
        NoasisApplication.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        Scene scene;
        SpringFXMLLoader springFXMLLoader = applicationContext.getBean(SpringFXMLLoader.class);
        try {
            scene = new Scene(springFXMLLoader.loadFXML("fxml/dashTEST"), 640, 480);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        stage.setTitle("Noasis");
        stage.setHeight(600);
        stage.setWidth(800);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.show();
    }
}
