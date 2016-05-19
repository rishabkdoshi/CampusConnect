package com.campusconnect.adt;

/**
 * Created by sylumani on 2/23/2016.
 */
public class TrendingTags {
    private String name = "";
    private String timeStamp ;

    public TrendingTags(String name, String timestamp) {
        this.name=name;
        this.timeStamp=timestamp;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    private boolean checked = false;

    public TrendingTags() {
    }

    public TrendingTags(String name) {
        this.name = name;
    }

    public TrendingTags(String name, boolean checked) {
        this.name = name;
        this.checked = checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String toString() {
        return name;
    }

    public void toggleChecked() {
        checked = !checked;
    }
}
