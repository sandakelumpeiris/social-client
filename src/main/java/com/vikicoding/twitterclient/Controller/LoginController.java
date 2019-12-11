package com.vikicoding.twitterclient.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {


    public Label errLbl;
    public TextField authPin;
    private boolean isLoggedIn = false;
    public String pin = "";
//    private String acccessToken = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.isLoggedIn = false;
    }

    public void HandleCancel(ActionEvent actionEvent) {
        System.exit(0);
    }

    public void handleLogin(ActionEvent actionEvent) {
        if(authPin.getText() != null) {
            this.pin = authPin.getText();
        } else
            this.pin = "";

        Parent source = (Parent) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }
}
