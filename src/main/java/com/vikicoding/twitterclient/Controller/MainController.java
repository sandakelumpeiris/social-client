package com.vikicoding.twitterclient.Controller;

import com.vikicoding.twitterclient.model.UserInf;
import com.vikicoding.twitterclient.model.UserTimeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;

public class MainController implements Initializable {


    public TextArea newPostTxt;
    private final static String CONSUMER_KEY = "3qOgAvwC0lZ9CLILyM03Z2Eyz";
    private final static String CONSUMER_KEY_SECRET = "ZIsxJwtwnMAAZTQqDflC6RAMrV4hd6VGFEP2fHjiJS2ulMpWQ3";

    public AnchorPane centerAnchorPane;
    public ToolBar centerToolBar;
    public VBox centerVBox;
    public AnchorPane searchAnchorPane;
    public TextField searchTxt;

    public TableView<UserTimeline> timelineTbl;
    public TableColumn<UserTimeline, UserInf> userCol;
    public TableColumn<UserTimeline,String> textCol;
    private final ObservableList<UserTimeline> data = FXCollections.observableArrayList();
    private String tweetHandle,tweetText,tweetImage,tweetName;

    public ListView<UserInf> followingList;
    ObservableList<UserInf> followingData = FXCollections.observableArrayList();

    public ListView<UserInf> followerList;
    ObservableList<UserInf> followerData = FXCollections.observableArrayList();

    private AccessToken accessToken = null;

    private Twitter twitter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //Setup layout
        centerAnchorPane.prefHeightProperty().bind(centerVBox.heightProperty());
        centerAnchorPane.prefWidthProperty().bind(centerVBox.widthProperty());
        searchTxt.prefWidthProperty().bind(centerToolBar.widthProperty().add(-170));
        textCol.prefWidthProperty().bind(timelineTbl.widthProperty().subtract(userCol.prefWidthProperty()));

        //        Table setup
        setupTweetColumn();
        setupUserColumn();
        setupFollowingList();
        setupFollowerList();

        //Setup twitter
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

//        After all authentication finished
        boolean isLoading = true;

        while (isLoading) {
            try {
                loadHomeTimeline();
                loadFollowers();
                loadFollowings();
                timelineTbl.setItems(data);
                followingList.setItems(followingData);
                followerList.setItems(followerData);
                isLoading = false;
            } catch (Exception e) {
                e.printStackTrace();
                if(!confirmBox("Data loading failed !", "Loading data from twitter is failed due to "+e.getMessage()+". Would you like to try again !")) {
                    System.exit(0);
                }
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

    private void loadHomeTimeline () throws Exception{
        List<Status> statuses = twitter.getHomeTimeline();
        data.clear();
        System.out.println("Number of loaded timeline status: "+statuses.size());
        for (Status status : statuses) {

            tweetName = status.getUser().getName();
            tweetHandle="@"+status.getUser().getScreenName();
            tweetText=status.getText();
            tweetImage   =status.getUser().getProfileImageURL();

            UserInf user = new UserInf(tweetName,tweetHandle,tweetImage);
            UserTimeline timeline = new UserTimeline(tweetText, user);

            data.add(timeline);
        }
    }

    private void setupUserColumn () {
        userCol.setCellFactory((TableColumn<UserTimeline, UserInf> param) -> new TableCell<UserTimeline,UserInf>(){
            @Override
            public void updateItem(UserInf item, boolean empty) {
                if(item!=null){

                    VBox vbox = new VBox();

                    ImageView imageview = new ImageView();
                    imageview.setFitHeight(40);
                    imageview.setFitWidth(40);
                    imageview.setImage(new Image(item.getImgUrl()));

                    vbox.getChildren().add(new Label(item.getHandle()));
                    vbox.getChildren().add(imageview);
                    vbox.getChildren().add(new Label(item.getName()));
                    vbox.setAlignment(Pos.CENTER);
//                        vbox.getStyleClass().add("vboxTimeline");
                    setGraphic(vbox);
                }
            }
        });
    }

    private void setupTweetColumn () {
        textCol.setCellFactory((TableColumn<UserTimeline, String> param) -> new TableCell<UserTimeline,String>(){
            private Text tweet;
            @Override
            public void updateItem(String item, boolean empty) {
                if (item != null) {
                    tweet = new Text(item);
                    tweet.setWrappingWidth(340);
                    setGraphic(tweet);
                }
            }
        });
    }

    public void handleSearch() {

        System.out.println("Search Triggered");
        String searchedTxt = searchTxt.getText();
        if(searchedTxt == null || searchedTxt.equals("")) {
            try {
                loadHomeTimeline();
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Loading timeline Error");
                alert.setHeaderText("You new status update failed");
                alert.setContentText("Loading timeline failed due to "+e.getMessage());
                alert.showAndWait();
            }
        } else {
            try {
                Query query = new Query(searchedTxt);
                query.setCount(15);
                QueryResult result = twitter.search(query);
                data.clear();
                for (Status status : result.getTweets()) {
                    tweetName = status.getUser().getName();
                    tweetHandle="@"+status.getUser().getScreenName();
                    tweetText=status.getText();
                    tweetImage   =status.getUser().getProfileImageURL();

                    UserInf user = new UserInf(tweetName,tweetHandle,tweetImage);
                    UserTimeline timeline = new UserTimeline(tweetText, user);

                    data.add(timeline);
                }
            } catch (TwitterException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Loading timeline Error");
                alert.setHeaderText("You new status update failed");
                alert.setContentText("Loading result failed due to "+e.getMessage());
                alert.showAndWait();
            }
        }
    }

    public void handleSearchBtn(ActionEvent actionEvent) {
        handleSearch();
    }

    public void handleHomeBtn(ActionEvent actionEvent) {
        try {
            loadHomeTimeline();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Loading timeline Error");
            alert.setHeaderText("You new status update failed");
            alert.setContentText("Loading timeline failed due to "+e.getMessage());
            alert.showAndWait();
        }
    }

    private void loadFollowers() throws Exception{
        List<User> followers = twitter.getFollowersList(twitter.getId(), -1);
        System.out.println("Number of followers: "+followers.size());
        for(User user: followers) {
            tweetName = user.getName();
            tweetHandle = "@" + user.getScreenName();
            tweetImage = user.getProfileImageURL();

            UserInf userInf = new UserInf(tweetName, tweetHandle, tweetImage);
            followerData.add(userInf);
        }
    }

    private void loadFollowings() throws Exception{
        List<User> followers = twitter.getFriendsList(twitter.getId(), -1);
        System.out.println("Number of Friends: "+followers.size());
        for(User user: followers) {
            tweetName = user.getName();
            tweetHandle = "@" + user.getScreenName();
            tweetImage = user.getProfileImageURL();

            UserInf userInf = new UserInf(tweetName, tweetHandle, tweetImage);
            followingData.add(userInf);
        }
    }

    public void setupFollowingList () {
        followingList.setCellFactory(new Callback<ListView<UserInf>, ListCell<UserInf>>(){

            @Override
            public ListCell<UserInf> call(ListView<UserInf> p) {

                return new ListCell<UserInf>(){

                    @Override
                    protected void updateItem(UserInf t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                            HBox hBox = new HBox();
                            VBox vBox = new VBox();

                            Label nameLbl = new Label(t.getName());
                            nameLbl.setPadding(new Insets(0,0,0,10));
                            nameLbl.setStyle("-fx-font-weight: bold");

                            Label handleLbl = new Label(t.getHandle());
                            handleLbl.setPadding(new Insets(0,0,0,10));
                            handleLbl.setTextFill(Color.web("#0076a3"));

                            vBox.getChildren().add(nameLbl);
                            vBox.getChildren().add(handleLbl);

                            ImageView imageview = new ImageView();
                            imageview.setFitHeight(40);
                            imageview.setFitWidth(40);
                            imageview.setImage(new Image(t.getImgUrl()));

                            hBox.getChildren().add(imageview);
                            hBox.getChildren().add(vBox);

                            setGraphic(hBox);
                        }
                    }

                };
            }
        });
    }

    public void setupFollowerList () {
        followerList.setCellFactory(new Callback<ListView<UserInf>, ListCell<UserInf>>(){

            @Override
            public ListCell<UserInf> call(ListView<UserInf> p) {

                return new ListCell<UserInf>(){

                    @Override
                    protected void updateItem(UserInf t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                            HBox hBox = new HBox();
                            VBox vBox = new VBox();

                            Label nameLbl = new Label(t.getName());
                            nameLbl.setPadding(new Insets(0,0,0,10));
                            nameLbl.setStyle("-fx-font-weight: bold");

                            Label handleLbl = new Label(t.getHandle());
                            handleLbl.setPadding(new Insets(0,0,0,10));
                            handleLbl.setTextFill(Color.web("#0076a3"));

                            vBox.getChildren().add(nameLbl);
                            vBox.getChildren().add(handleLbl);

                            ImageView imageview = new ImageView();
                            imageview.setFitHeight(40);
                            imageview.setFitWidth(40);
                            imageview.setImage(new Image(t.getImgUrl()));

                            hBox.getChildren().add(imageview);
                            hBox.getChildren().add(vBox);

                            setGraphic(hBox);
                        }
                    }

                };
            }
        });
    }

    public void loadProfileDetails(ActionEvent actionEvent) {

        try {
            long userId = twitter.getId();
            User user = twitter.showUser(userId);
            Stage profileStage = new Stage();
            profileStage.initModality(Modality.APPLICATION_MODAL);

            FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/view/profile.fxml"));
            Parent loginParent = loginLoader.load();
            ProfileController profileController = loginLoader.getController();
            profileController.setUser(user);

            profileStage.setTitle("Login");
            profileStage.setScene(new Scene(loginParent, 600, 218));
            profileStage.setResizable(false);
            profileStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Loading  error");
            alert.setHeaderText("Your profile loading failed");
            alert.setContentText("Loading profile failed due to "+e.getMessage());
            alert.showAndWait();
        }
    }
}
