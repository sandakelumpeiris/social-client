package com.vikicoding.twitterclient.Controller;

import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import twitter4j.User;

import java.net.URL;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {
    public Label profileName;
    public Label tweetHandle;
    public Label email;
    public TextArea description;
    public Label favouritCount;
    public Label followingCount;
    public Label followerCount;
    public ImageView profImg;

    private User user;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setUser(User user) {
        this.user = user;
        profImg.setImage(new Image(user.get400x400ProfileImageURL()));
        profileName.setText(user.getName());
        tweetHandle.setText("@"+user.getScreenName());
        email.setText(user.getEmail());
        description.setText(user.getDescription());

        favouritCount.setText(String.valueOf(user.getFavouritesCount()));
        followerCount.setText(String.valueOf(user.getFollowersCount()));
        followingCount.setText(String.valueOf(user.getFriendsCount()));
    }
}
