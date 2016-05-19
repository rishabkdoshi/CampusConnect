package com.campusconnect.communicator.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rkd on 1/3/16.
 */
public class ModelsHashTag {
    @SerializedName("name")
    private String name;
    @SerializedName("startTime")
    private String startTime;
    @SerializedName("collegeId")
    private String collegeId;
    @SerializedName("kind")
    private String kind;

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
     * The startTime
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     *
     * @param startTime
     * The startTime
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

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
     * The kind
     */
    public String getKind() {
        return kind;
    }

    /**
     *
     * @param kind
     * The kind
     */
    public void setKind(String kind) {
        this.kind = kind;
    }

}
