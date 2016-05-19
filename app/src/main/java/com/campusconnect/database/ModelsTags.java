package com.campusconnect.database;

//import com.activeandroid.Model;
//import com.activeandroid.annotation.Column;
//import com.activeandroid.annotation.Table;

/**
 * Created by rkd on 26/2/16.
 */
//@Table(name = "Tags")
public class ModelsTags  {

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

//    @Column(name = "tags")
    private String tags;

}
