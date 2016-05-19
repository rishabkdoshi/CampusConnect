package com.campusconnect.communicator.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rkd on 26/2/16.
 */
public class ModelsKMCParent {
    @SerializedName("items")
    private List<ModelsKMC> items = new ArrayList<ModelsKMC>();
    @SerializedName("kind")
    private String kind;
    @SerializedName("etag")
    private String etag;

    /**
     * @return The items
     */
    public List<ModelsKMC> getItems() {
        return items;
    }

    /**
     * @param items The items
     */
    public void setItems(List<ModelsKMC> items) {
        this.items = items;
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
     * @return The etag
     */
    public String getEtag() {
        return etag;
    }

    /**
     * @param etag The etag
     */
    public void setEtag(String etag) {
        this.etag = etag;
    }

}
