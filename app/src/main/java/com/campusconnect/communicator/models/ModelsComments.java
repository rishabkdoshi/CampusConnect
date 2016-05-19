package com.campusconnect.communicator.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rkd on 29/1/16.
 */
public class ModelsComments {
    @SerializedName("timestamp")
    private String timestamp;
    @SerializedName("commentBody")
    private String commentBody;
    @SerializedName("commentor")
    private String commentor;
    @SerializedName("photoUrl")
    private String photoUrl;
    @SerializedName("kind")
    private String kind;

    /**
     * @return The timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp The timestamp
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return The commentBody
     */
    public String getCommentBody() {
        return commentBody;
    }

    /**
     * @param commentBody The commentBody
     */
    public void setCommentBody(String commentBody) {
        this.commentBody = commentBody;
    }

    /**
     * @return The commentor
     */
    public String getCommentor() {
        return commentor;
    }

    /**
     * @param commentor The commentor
     */
    public void setCommentor(String commentor) {
        this.commentor = commentor;
    }

    /**
     * @return The photoUrl
     */
    public String getPhotoUrl() {
        return photoUrl;
    }

    /**
     * @param photoUrl The photoUrl
     */
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    /**
     * @return The kind
     */
    public String getKind() {
        return kind;
    }

    /**
     * @param kind The kind
     */
    public void setKind(String kind) {
        this.kind = kind;
    }

}
