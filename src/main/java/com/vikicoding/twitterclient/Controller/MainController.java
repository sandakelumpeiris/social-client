package com.vikicoding.twitterclient.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {


    public TextArea newPostTxt;
    private final static String CONSUMER_KEY = "3qOgAvwC0lZ9CLILyM03Z2Eyz";
    private final static String CONSUMER_KEY_SECRET = "ZIsxJwtwnMAAZTQqDflC6RAMrV4hd6VGFEP2fHjiJS2ulMpWQ3";
    private AccessToken accessToken = null;

    private Twitter twitter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_KEY_SECRET);
        System.out.println("Testing initialisation");
        Stage loginStage = new Stage();
        loginStage.initModality(Modality.APPLICATION_MODAL);

        AccessToken accessToken = null;
        String errorMessage = "";
        try {
            RequestToken requestToken = twitter.getOAuthRequestToken();
            System.out.println("Authorization URL: \n"
                    + requestToken.getAuthorizationURL());
            //TODO: pass the url
            while (accessToken == null) {
                try {
                    FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
                    Parent loginParent = loginLoader.load();
                    LoginController loginController = loginLoader.getController();
                    loginController.errLbl.setText(errorMessage);
                    loginStage.setTitle("Login");
                    loginStage.setScene(new Scene(loginParent, 450, 150));
                    loginStage.setResizable(false);
                    loginStage.showAndWait();

                    String authPin = loginController.pin;
                    accessToken = twitter.getOAuthAccessToken(requestToken, authPin);

                    System.out.println("Auth pin: " + authPin);

                } catch (Exception e) {
                    errorMessage = "Pin is invalid try again !";
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    public void handleClear(ActionEvent actionEvent) {
        newPostTxt.clear();
    }

    public void handlePost(ActionEvent actionEvent) {
        try {
            String postText = newPostTxt.getText();
            twitter.updateStatus(postText);
            System.out.println("New tweet updated successfully !");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("New tweet updated successfully !");

            alert.showAndWait();
        } catch (TwitterException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Post Error");
            alert.setHeaderText("You new status update failed");
            alert.setContentText("Updating status failed due to "+e.getErrorMessage());
            alert.showAndWait();
        }
    }

    public void handleLogout(ActionEvent actionEvent) {
    }

    public static String getConsumerKey() {
        return CONSUMER_KEY;
    }

    public static String getConsumerKeySecret() {
        return CONSUMER_KEY_SECRET;
    }
}
