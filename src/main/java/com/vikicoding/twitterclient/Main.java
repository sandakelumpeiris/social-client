package com.vikicoding.twitterclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/main.fxml"));
        primaryStage.setTitle("Twitter Client FX");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }
}
