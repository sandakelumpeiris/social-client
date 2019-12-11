package com.vikicoding.twitterclient.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class LoginController implements Initializable {


    public Label errLbl;
    public TextField authPin;
    public Hyperlink authUrlLink;
    private boolean isLoggedIn = false;
    public String pin = "";
    public String authURL = "";
//    private String acccessToken = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.isLoggedIn = false;
    }

    public void HandleCancel(ActionEvent actionEvent) {
        if(confirmBox("You are going to excit from FX twitter client", "Are you sure you want to exit from FX Twitter Client")) {
            System.exit(0);
        }
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

    public void handleAuthLink(ActionEvent actionEvent) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI(authURL));
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Post Error");
                alert.setHeaderText("Opening URL failed !");
                alert.setContentText("Please open URL below manually: \n\n"+authURL);
                alert.showAndWait();
            }
        }
    }

    public void setAuthURL(String authURL) {
        this.authURL = authURL;
    }

    private boolean confirmBox(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(title);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            return true;
        } else {
            return false;
        }
    }
}
