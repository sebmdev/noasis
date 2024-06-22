package dev.sebm.noasis;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Lazy;

@SpringBootApplication
@Lazy
public class MainApplication {

    public static void main(String[] args) {
//        SpringApplication.run(NoasisApplication.class, args);
        Application.launch(NoasisApplication.class, args);
    }
}
