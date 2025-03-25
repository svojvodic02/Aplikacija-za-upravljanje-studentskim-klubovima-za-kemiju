package com.example.zavrsniprojektvojvodic;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    public static Stage mainStage;
    @Override
    public void start(Stage stage) throws IOException {
        mainStage=stage;
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-page.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Welcome!");
        stage.setScene(scene);
        stage.show();
    }
    public static Stage getMainStage(){
        return mainStage;
    }

    public static void main(String[] args) {
        launch();
    }
}