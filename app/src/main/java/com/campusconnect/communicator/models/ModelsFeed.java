
package com.campusconnect.communicator.models;


import android.os.Parcel;
import android.os.Parcelable;

//import com.activeandroid.Model;
//import com.activeandroid.annotation.Column;
//import com.activeandroid.annotation.Table;
import com.google.gson.annotations.SerializedName;

//@Table(name = "feed")
public class ModelsFeed implements Parcelable {
    //@Column(name = "startDate")
    @SerializedName("startDate")
    private String startDate;

    //@Column(name = "endDate")
    @SerializedName("endDate")
    private String endDate;

    //@Column(name = "photoUrl")
    @SerializedName("photoUrl")
    private String photoUrl;

    //@Column(name = "commentCount")
    @SerializedName("commentCount")
    private String commentCount;

    //@Column(name = "feedId")
    @SerializedName("id")
    private String feedId;

    //@Column(name = "attendees")
    @SerializedName("attendees")
    private String attendees;

    //@Column(name = "clubId")
    @SerializedName("clubId")
    private String clubId;


    //@Column(name = "title")
    @SerializedName("title")
    private String title;


    //@Column(name = "tags")
    @SerializedName("tags")
    private String tags;

    //@Column(name = "clubName")
    @SerializedName("clubName")
    private String clubName;

    //@Column(name = "description")
    @SerializedName("description")
    private String description;


    //@Column(name = "views")
    @SerializedName("views")
    private String views;

    //@Column(name = "timestamp")
    @SerializedName("timestamp")
    private String timestamp;

    //@Column(name = "completed")
    @SerializedName("completed")
    private String completed;


    //@Column(name = "isAttending")
    @SerializedName("isAttending")
    private String isAttending;

    //@Column(name = "startTime")
    @SerializedName("startTime")
    private String startTime;

    //@Column(name = "isAlumni")
    @SerializedName("isAlumni")
    private String isAlumni;

    //@Column(name = "clubphotoUrl")
    @SerializedName("clubphotoUrl")
    private String clubphotoUrl;

    //@Column(name = "contentCreator")
    @SerializedName("contentCreator")
    private String contentCreator;

    //@Column(name = "venue")
    @SerializedName("venue")
    private String venue;

    //@Column(name = "clubabbreviation")
    @SerializedName("clubabbreviation")
    private String clubabbreviation;

    //@Column(name = "endTime")
    @SerializedName("endTime")
    private String endTime;

    //@Column(name = "kind")
    @SerializedName("kind")
    private String kind;

    //@Column(name = "likes")
    @SerializedName("likes")
    private String likes;

    //@Column(name = "hasLiked")
    @SerializedName("hasLiked")
    private String hasLiked;

    //@Column(name = "feedType")
    @SerializedName("feedType")
    private String feedType;

    private String type;

    //@Column(name = "type", index = true)
    private String table;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    /**
     * @return The startDate
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * @param startDate The startDate
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     * @return The endDate
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * @param endDate The endDate
     */
    public void setEndDate(String endDate) {
        this.endDate = endDate;
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
     * @return The commentCount
     */
    public String getCommentCount() {
        return commentCount;
    }

    /**
     * @param commentCount The commentCount
     */
    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    /**
     * @return The id
     */
    public String getFeedId() {
        return feedId;
    }

    /**
     * @param id The id
     */
    public void setFeedId(String id) {
        this.feedId = id;
    }

    /**
     * @return The attendees
     */
    public String getAttendees() {
        return attendees;
    }

    /**
     * @param attendees The attendees
     */
    public void setAttendees(String attendees) {
        this.attendees = attendees;
    }

    /**
     * @return The clubId
     */
    public String getClubId() {
        return clubId;
    }

    /**
     * @param clubId The clubId
     */
    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    /**
     * @return The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return The tags
     */
    public String getTags() {
        return tags;
    }

    /**
     * @param tags The tags
     */
    public void setTags(String tags) {
        this.tags = tags;
    }

    /**
     * @return The clubName
     */
    public String getClubName() {
        return clubName;
    }

    /**
     * @param clubName The clubName
     */
    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    /**
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return The views
     */
    public String getViews() {
        return views;
    }

    /**
     * @param views The views
     */
    public void setViews(String views) {
        this.views = views;
    }

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
     * @return The completed
     */
    public String getCompleted() {
        return completed;
    }

    /**
     * @param completed The completed
     */
    public void setCompleted(String completed) {
        this.completed = completed;
    }

    /**
     * @return The isAttending
     */
    public String getIsAttending() {
        return isAttending;
    }

    /**
     * @param isAttending The isAttending
     */
    public void setIsAttending(String isAttending) {
        this.isAttending = isAttending;
    }

    /**
     * @return The startTime
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * @param startTime The startTime
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * @return The isAlumni
     */
    public String getIsAlumni() {
        return isAlumni;
    }

    /**
     * @param isAlumni The isAlumni
     */
    public void setIsAlumni(String isAlumni) {
        this.isAlumni = isAlumni;
    }

    /**
     * @return The clubphotoUrl
     */
    public String getClubphotoUrl() {
        return clubphotoUrl;
    }

    /**
     * @param clubphotoUrl The clubphotoUrl
     */
    public void setClubphotoUrl(String clubphotoUrl) {
        this.clubphotoUrl = clubphotoUrl;
    }

    /**
     * @return The contentCreator
     */
    public String getContentCreator() {
        return contentCreator;
    }

    /**
     * @param contentCreator The contentCreator
     */
    public void setContentCreator(String contentCreator) {
        this.contentCreator = contentCreator;
    }

    /**
     * @return The venue
     */
    public String getVenue() {
        return venue;
    }

    /**
     * @param venue The venue
     */
    public void setVenue(String venue) {
        this.venue = venue;
    }

    /**
     * @return The clubabbreviation
     */
    public String getClubabbreviation() {
        return clubabbreviation;
    }

    /**
     * @param clubabbreviation The clubabbreviation
     */
    public void setClubabbreviation(String clubabbreviation) {
        this.clubabbreviation = clubabbreviation;
    }

    /**
     * @return The endTime
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * @param endTime The endTime
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
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

    /**
     * @return The likes
     */
    public String getLikes() {
        return likes;
    }

    /**
     * @param likes The likes
     */
    public void setLikes(String likes) {
        this.likes = likes;
    }

    /**
     * @return The hasLiked
     */
    public String getHasLiked() {
        return hasLiked;
    }

    /**
     * @param hasLiked The hasLiked
     */
    public void setHasLiked(String hasLiked) {
        this.hasLiked = hasLiked;
    }


    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getFeedType() {
        return feedType;
    }

    public void setFeedType(String feedType) {
        this.feedType = feedType;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getStartDate());
        dest.writeString(getEndDate());
        dest.writeString(getPhotoUrl());
        dest.writeString(getCommentCount());
        dest.writeString(getFeedId());
        dest.writeString(getAttendees());
        dest.writeString(getClubId());
        dest.writeString(getTitle());
        dest.writeString(getTags());
        dest.writeString(getClubName());
        dest.writeString(getDescription());
        dest.writeString(getViews());
        dest.writeString(getTimestamp());
        dest.writeString(getCompleted());
        dest.writeString(getIsAttending());
        dest.writeString(getStartTime());
        dest.writeString(getIsAlumni());
        dest.writeString(getClubphotoUrl());
        dest.writeString(getContentCreator());
        dest.writeString(getVenue());
        dest.writeString(getClubabbreviation());
        dest.writeString(getEndTime());
        dest.writeString(getKind());
        dest.writeString(getLikes());
        dest.writeString(getHasLiked());
        dest.writeString(getFeedType());
    }

    public static final Creator<ModelsFeed> CREATOR = new Creator<ModelsFeed>() {

        @Override
        public ModelsFeed createFromParcel(Parcel source) {
            ModelsFeed feed = new ModelsFeed();
            feed.setStartDate(source.readString());
            feed.setEndDate(source.readString());
            feed.setPhotoUrl(source.readString());
            feed.setCommentCount(source.readString());
            feed.setFeedId(source.readString());
            feed.setAttendees(source.readString());
            feed.setClubId(source.readString());
            feed.setTitle(source.readString());
            feed.setTags(source.readString());
            feed.setClubName(source.readString());
            feed.setDescription(source.readString());
            feed.setViews(source.readString());
            feed.setTimestamp(source.readString());
            feed.setCompleted(source.readString());
            feed.setIsAttending(source.readString());
            feed.setStartTime(source.readString());
            feed.setIsAlumni(source.readString());
            feed.setClubphotoUrl(source.readString());
            feed.setContentCreator(source.readString());
            feed.setVenue(source.readString());
            feed.setClubabbreviation(source.readString());
            feed.setEndTime(source.readString());
            feed.setKind(source.readString());
            feed.setLikes(source.readString());
            feed.setHasLiked(source.readString());
            feed.setFeedType(source.readString());
            return feed;
        }

        @Override
        public ModelsFeed[] newArray(int size) {
            return new ModelsFeed[size];
        }
    };
}