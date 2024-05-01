package com.vadmax.savemedia;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

public class SaveMediaApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SaveMediaApp.class.getResource("main-window.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 700);

        scene.getStylesheets().add(SaveMediaApp.class.getResource("style.css").toExternalForm()); // Add styles for fxml
        Font.loadFont(SaveMediaApp.class.getResourceAsStream("fonts/JetBrainsMono_Medium.ttf"), 20);

        stage.setMinWidth(400);
        stage.setMinHeight(700);

        stage.setTitle("Save Media");

        stage.setScene(scene);
        stage.show();
    }
}