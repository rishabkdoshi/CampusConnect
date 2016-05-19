package com.campusconnect.communicator.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rkd on 25/2/16.
 */
public class ModelsUnjoinClubMiniForm {

    @SerializedName("clubId")
    private String clubId;
    @SerializedName("fromPid")
    private String fromPid;
    @SerializedName("pid")
    private String pid;

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public String getFromPid() {
        return fromPid;
    }

    public void setFromPid(String fromPid) {
        this.fromPid = fromPid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
}
