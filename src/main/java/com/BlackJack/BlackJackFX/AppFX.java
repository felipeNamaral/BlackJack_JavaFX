package com.BlackJack.BlackJackFX;

import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class AppFX extends Application {
    private ConfigurableApplicationContext context;

    @Override
    public void init() {
        context  = new SpringApplicationBuilder(BlackJackFxApplication.class).run();
    }

    @Override
    public void start(Stage stage)  {
        MainController controller = context.getBean(MainController.class);
        controller.start(stage);
    }

    @Override
    public void stop() {
        context.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
