package com.vikicoding.twitterclient.model;

public class UserInf {
    String name;
    String handle;
    String imgUrl;

    public UserInf(){

    }

    public UserInf(String name, String handle,String imgUrl){
        this.name= name;
        this.handle= handle;
        this.imgUrl= imgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHandle() {
        return handle;
    }

    public void setArtist(String handle) {
        this.handle = handle;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
