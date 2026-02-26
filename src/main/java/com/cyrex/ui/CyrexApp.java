package com.cyrex.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * CYREX Phase 1 - JavaFX application entry point.
 */
public class CyrexApp extends Application {
    @Override
    public void start(Stage stage) {
        MainView mainView = new MainView();
        Scene scene = new Scene(mainView.getRoot(), 900, 700);
        stage.setTitle("CYREX - Cyber Resilience Evaluation & Stress Testing");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
