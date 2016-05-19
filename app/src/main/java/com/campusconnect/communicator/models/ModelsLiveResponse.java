package com.campusconnect.communicator.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rkd on 29/2/16.
 */
public class ModelsLiveResponse {

    @SerializedName("completed")
    private String completed;
    @SerializedName("items")
    private List<ModelsLiveFeed> items = new ArrayList<ModelsLiveFeed>();

    public String getCompleted() {
        return completed;
    }

    /**
     *
     * @param completed
     * The completed
     */
    public void setCompleted(String completed) {
        this.completed = completed;
    }

    /**
     *
     * @return
     * The items
     */
    public List<ModelsLiveFeed> getItems() {
        return items;
    }

    /**
     *
     * @param items
     * The items
     */
    public void setItems(List<ModelsLiveFeed> items) {
        this.items = items;
    }

}
