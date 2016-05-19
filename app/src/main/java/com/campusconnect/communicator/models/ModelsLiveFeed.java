package com.campusconnect.communicator.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rkd on 29/2/16.
 */
public class ModelsLiveFeed {
    @SerializedName("collegeId")
    private String collegeId;
    @SerializedName("description")
    private String description;
    @SerializedName("imageUrl")
    private String imageUrl;
    @SerializedName("messageId")
    private String messageId;
    @SerializedName("name")
    private String name;
    @SerializedName("personPhotoUrl")
    private String personPhotoUrl;
    @SerializedName("tags")
    private String tags;
    @SerializedName("timestamp")
    private String timestamp;

    /**
     *
     * @return
     * The collegeId
     */
    public String getCollegeId() {
        return collegeId;
    }

    /**
     *
     * @param collegeId
     * The collegeId
     */
    public void setCollegeId(String collegeId) {
        this.collegeId = collegeId;
    }

    /**
     *
     * @return
     * The description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     * The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     * The imageUrl
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     *
     * @param imageUrl
     * The imageUrl
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     *
     * @return
     * The messageId
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     *
     * @param messageId
     * The messageId
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The personPhotoUrl
     */
    public String getPersonPhotoUrl() {
        return personPhotoUrl;
    }

    /**
     *
     * @param personPhotoUrl
     * The personPhotoUrl
     */
    public void setPersonPhotoUrl(String personPhotoUrl) {
        this.personPhotoUrl = personPhotoUrl;
    }

    /**
     *
     * @return
     * The tags
     */
    public String getTags() {
        return tags;
    }

    /**
     *
     * @param tags
     * The tags
     */
    public void setTags(String tags) {
        this.tags = tags;
    }

    /**
     *
     * @return
     * The timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     *
     * @param timestamp
     * The timestamp
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
