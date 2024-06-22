package dev.sebm.noasis;

import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;

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
        applicationContext.publishEvent(new StageReadyEvent(stage));
    }


    public static class StageReadyEvent extends ApplicationEvent {
        public StageReadyEvent(Stage stage) {
            super(stage);
        }

        public Stage getStage() {
            return ((Stage) getSource());
        }
    }
}
