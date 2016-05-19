package com.campusconnect.communicator.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rkd on 30/1/16.
 */
public class ModelsUpdateStatus {
    @SerializedName("update")
    private String update;

    @SerializedName("message")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @SerializedName("kind")
    private String kind;
    @SerializedName("etag")
    private String etag;

    /**
     * @return The update
     */
    public String getUpdate() {
        return update;
    }

    /**
     * @param update The update
     */
    public void setUpdate(String update) {
        this.update = update;
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
