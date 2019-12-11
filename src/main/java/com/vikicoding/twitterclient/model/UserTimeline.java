package com.vikicoding.twitterclient.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UserTimeline {
    private final SimpleStringProperty test_tweet;
    private final ObjectProperty test_user;

    public UserTimeline(String test_tweet, UserInf test_user) {
        this.test_tweet = new SimpleStringProperty(test_tweet);
        this.test_user = new SimpleObjectProperty(test_user);
    }

    /* Tweettext */
    public String getTest_tweet() {
        return test_tweet.get();
    }

    public void setTest_tweet(String testTweet) {
        this.test_tweet.set(testTweet);
    }

    public StringProperty tweetProperty(){
        return test_tweet;
    }

    /* User */
    public Object getTest_user() {
        return test_user.get();
    }

    public void setTest_user(UserInf testUser) {
        this.test_user.set(testUser);
    }

    public ObjectProperty userProperty(){
        return test_user;
    }

}
