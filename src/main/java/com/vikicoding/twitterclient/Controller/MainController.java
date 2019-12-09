package com.vikicoding.twitterclient.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {


    public TextArea newPostTxt;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Testing initialisation");
        Stage loginStage = new Stage();
        loginStage.initModality(Modality.APPLICATION_MODAL);

        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
        try {
            Parent loginParent = loginLoader.load();
            loginStage.setTitle("Login");
            loginStage.setScene(new Scene(loginParent, 400, 150));
            loginStage.setResizable(false);
            loginStage.showAndWait();

            LoginController loginController = loginLoader.getController();
            System.out.println(loginController.isLoggedIn());

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        loginLoader.getController();
    }

    public void handleClear(ActionEvent actionEvent) {
        newPostTxt.clear();
    }

    public void handlePost(ActionEvent actionEvent) {
    }

    public void handleLogout(ActionEvent actionEvent) {
    }
}
