package com.campusconnect.adt;

/**
 * Created by GleasonK on 7/11/15.
 * <p/>
 * ChatMessage is used to hold information that is transmitted using PubNub.
 * A message in this app has a username, message, and timestamp.
 */
public class ChatMessage {
    private String username;
    private String message;
    private String tag;
    private String imageURL;
    private int upvote;
    private int downvote;

    private long timeStamp;

    public ChatMessage(String username, String message, long timeStamp) {
        this.username = username;
        this.message = message;
        this.timeStamp = timeStamp;
    }

    public ChatMessage(String username, String message, String tag, String imageURL, int upvote, int downvote, long timeStamp) {
        this.username = username;
        this.message = message;
        this.tag = tag;
        this.imageURL = imageURL;
        this.upvote = upvote;
        this.downvote = downvote;
        this.timeStamp = timeStamp;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public int getUpvote() {
        return upvote;
    }

    public void setUpvote(int upvote) {
        this.upvote = upvote;
    }

    public int getDownvote() {
        return downvote;
    }

    public void setDownvote(int downvote) {
        this.downvote = downvote;
    }


}
