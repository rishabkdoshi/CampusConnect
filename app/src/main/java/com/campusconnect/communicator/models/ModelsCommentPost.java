package com.campusconnect.communicator.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rkd on 29/1/16.
 */
public class ModelsCommentPost {
    @SerializedName("commentBody")
    private String commentBody;
    @SerializedName("date")
    private String date;
    @SerializedName("pid")
    private String pid;
    @SerializedName("postId")
    private String postId;
    @SerializedName("time")
    private String time;

    /**
     *
     * @return
     * The commentBody
     */
    public String getCommentBody() {
        return commentBody;
    }

    /**
     *
     * @param commentBody
     * The commentBody
     */
    public void setCommentBody(String commentBody) {
        this.commentBody = commentBody;
    }

    /**
     *
     * @return
     * The date
     */
    public String getDate() {
        return date;
    }

    /**
     *
     * @param date
     * The date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     *
     * @return
     * The pid
     */
    public String getPid() {
        return pid;
    }

    /**
     *
     * @param pid
     * The pid
     */
    public void setPid(String pid) {
        this.pid = pid;
    }

    /**
     *
     * @return
     * The postId
     */
    public String getPostId() {
        return postId;
    }

    /**
     *
     * @param postId
     * The postId
     */
    public void setPostId(String postId) {
        this.postId = postId;
    }

    /**
     *
     * @return
     * The time
     */
    public String getTime() {
        return time;
    }

    /**
     *
     * @param time
     * The time
     */
    public void setTime(String time) {
        this.time = time;
    }

}
