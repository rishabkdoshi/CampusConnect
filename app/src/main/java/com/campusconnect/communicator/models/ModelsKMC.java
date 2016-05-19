package com.campusconnect.communicator.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rkd on 26/2/16.
 */
public class ModelsKMC {

    @SerializedName("score")
    private String score;
    @SerializedName("batch")
    private String batch;
    @SerializedName("kind")
    private String kind;

    /**
     * @return The score
     */
    public String getScore() {
        return score;
    }

    /**
     * @param score The score
     */
    public void setScore(String score) {
        this.score = score;
    }

    /**
     * @return The batch
     */
    public String getBatch() {
        return batch;
    }

    /**
     * @param batch The batch
     */
    public void setBatch(String batch) {
        this.batch = batch;
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
}
