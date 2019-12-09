package com.vikicoding.twitterclient.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    public TextField usernameTxt;
    public TextField passwordTxt;
    public Label errLbl;
    private boolean isLoggedIn = false;
    private String asccessToken = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.isLoggedIn = false;
    }

    public void HandleCancel(ActionEvent actionEvent) {
        System.exit(0);
    }

    public void handleLogin(ActionEvent actionEvent) {
        String username = usernameTxt.getText();
        String password = passwordTxt.getText();

        if(username.equals("admin") && password.equals("abc@123")) {
            this.isLoggedIn = true;
            Node source = (Node) actionEvent.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
        } else {
            errLbl.setText("Incorrect username of Password, Please try again !");
        }
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }
}
