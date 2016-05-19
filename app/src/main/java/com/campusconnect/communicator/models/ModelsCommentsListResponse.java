package com.campusconnect.communicator.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rkd on 29/1/16.
 */
public class ModelsCommentsListResponse {

    @SerializedName("items")
    private List<ModelsComments> items = new ArrayList<ModelsComments>();
    @SerializedName("completed")
    private String completed;
    @SerializedName("kind")
    private String kind;
    @SerializedName("etag")
    private String etag;

    /**
     *
     * @return
     * The items
     */
    public List<ModelsComments> getItems() {
        return items;
    }

    /**
     *
     * @param items
     * The items
     */
    public void setItems(List<ModelsComments> items) {
        this.items = items;
    }

    /**
     *
     * @return
     * The completed
     */
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

    /**
     *
     * @return
     * The etag
     */
    public String getEtag() {
        return etag;
    }

    /**
     *
     * @param etag
     * The etag
     */
    public void setEtag(String etag) {
        this.etag = etag;
    }


}
