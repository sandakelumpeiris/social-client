package com.vikicoding.twitterclient.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.io.*;
import java.net.URL;
import java.util.Optional;
import java.util.Properties;
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
        String errorMessage = "";

        AccessToken accessToken = getUserProperties();
        if(accessToken != null) {
            twitter.setOAuthAccessToken(accessToken);
        } else {
            try {
                RequestToken requestToken = twitter.getOAuthRequestToken();
                System.out.println("Authorization URL: \n"
                        + requestToken.getAuthorizationURL());
                while (accessToken == null) {
                    try {
                        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
                        Parent loginParent = loginLoader.load();
                        LoginController loginController = loginLoader.getController();
                        loginController.errLbl.setText(errorMessage);
                        loginController.setAuthURL(requestToken.getAuthorizationURL());
                        loginStage.setTitle("Login");
                        loginStage.setScene(new Scene(loginParent, 450, 150));
                        loginStage.setResizable(false);
                        loginStage.showAndWait();

                        String authPin = loginController.pin;
                        accessToken = twitter.getOAuthAccessToken(requestToken, authPin);
                        saveUserProperties(accessToken.getToken(), accessToken.getTokenSecret());
                        System.out.println("Auth pin: " + authPin);
                        System.out.println("Screen Name: " + accessToken.getScreenName());

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
    }

    public void handleClear(ActionEvent actionEvent) {
        newPostTxt.clear();
    }

    public void handlePost(ActionEvent actionEvent) {
        try {
            String postText = newPostTxt.getText();
            twitter.updateStatus(postText);
            System.out.println("New tweet updated successfully !");
            newPostTxt.clear();

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

        if(confirmBox("Are you sure !", "You are going to logout from FX Twitter Client")) {
            try (FileReader reader = new FileReader("user.properties")) {

                Properties prop = new Properties();
                // load a properties file
                prop.load(reader);
                prop.remove( "user.access.token");
                prop.remove("user.access.token.secret");
                OutputStream output = new FileOutputStream("user.properties");
                prop.store(output, null);

                loadLoginAgain();

            } catch (IOException ex) {
                System.out.println("get user properties failed due to "+ex.getMessage());
            }
        }

    }

    public static String getConsumerKey() {
        return CONSUMER_KEY;
    }

    public static String getConsumerKeySecret() {
        return CONSUMER_KEY_SECRET;
    }

    public void saveUserProperties (String accessToken, String accessTokenSecret) {
        try (OutputStream output = new FileOutputStream("user.properties")) {

            Properties prop = new Properties();

            // set the properties value
            prop.setProperty("user.access.token", accessToken);
            prop.setProperty("user.access.token.secret", accessTokenSecret);

            prop.store(output, null);

            System.out.println("Tokens are stored successfully !");

        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public AccessToken getUserProperties () {
        AccessToken accessToken = null;
        try (InputStream input = new FileInputStream("user.properties")) {

            Properties prop = new Properties();
            // load a properties file
            prop.load(input);
            String token = prop.getProperty("user.access.token");
            String secret = prop.getProperty("user.access.token.secret");

            if(token != null && secret != null) {
                accessToken = new AccessToken(token, secret);
            }

        } catch (IOException ex) {
            System.out.println("get user properties failed due to "+ex.getMessage());
        }
        return accessToken;
    }

    private void loadLoginAgain () {
        twitter.setOAuthAccessToken(null);
        Stage loginStage = new Stage();
        loginStage.initModality(Modality.APPLICATION_MODAL);
        String errorMessage = "";

        AccessToken accessToken = null;
        try {
            RequestToken requestToken = twitter.getOAuthRequestToken();
            System.out.println("Authorization URL: \n"
                    + requestToken.getAuthorizationURL());
            while (accessToken == null) {
                try {
                    FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
                    Parent loginParent = loginLoader.load();
                    LoginController loginController = loginLoader.getController();
                    loginController.errLbl.setText(errorMessage);
                    loginController.setAuthURL(requestToken.getAuthorizationURL());
                    loginStage.setTitle("Login");
                    loginStage.setScene(new Scene(loginParent, 450, 150));
                    loginStage.setResizable(false);
                    loginStage.setOnCloseRequest(e->{
                        if(confirmBox("You are going to excit from FX twitter client", "Are you sure you want to exit from FX Twitter Client")) {
                            System.exit(0);
                        }
                    });
                    loginStage.showAndWait();

                    String authPin = loginController.pin;
                    accessToken = twitter.getOAuthAccessToken(requestToken, authPin);
                    saveUserProperties(accessToken.getToken(), accessToken.getTokenSecret());
                    System.out.println("Auth pin: " + authPin);
                    System.out.println("Screen Name: " + accessToken.getScreenName());

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
