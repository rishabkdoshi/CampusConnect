package com.campusconnect.communicator.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rkd on 1/3/16.
 */
public class ModelsScoreBoard {
    @SerializedName("gender")
    private String gender;
    @SerializedName("completed")
    private String completed;
    @SerializedName("matchId")
    private String matchId;
    @SerializedName("team1")
    private String team1;
    @SerializedName("team2")
    private String team2;
    @SerializedName("score1")
    private String score1;
    @SerializedName("score2")
    private String score2;
    @SerializedName("quarter")
    private String quarter;
    @SerializedName("round")
    private String round;
    @SerializedName("kind")
    private String kind;

    /**
     *
     * @return
     * The gender
     */
    public String getGender() {
        return gender;
    }

    /**
     *
     * @param gender
     * The gender
     */
    public void setGender(String gender) {
        this.gender = gender;
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
     * The matchId
     */
    public String getMatchId() {
        return matchId;
    }

    /**
     *
     * @param matchId
     * The matchId
     */
    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    /**
     *
     * @return
     * The team1
     */
    public String getTeam1() {
        return team1;
    }

    /**
     *
     * @param team1
     * The team1
     */
    public void setTeam1(String team1) {
        this.team1 = team1;
    }

    /**
     *
     * @return
     * The team2
     */
    public String getTeam2() {
        return team2;
    }

    /**
     *
     * @param team2
     * The team2
     */
    public void setTeam2(String team2) {
        this.team2 = team2;
    }

    /**
     *
     * @return
     * The score1
     */
    public String getScore1() {
        return score1;
    }

    /**
     *
     * @param score1
     * The score1
     */
    public void setScore1(String score1) {
        this.score1 = score1;
    }

    /**
     *
     * @return
     * The score2
     */
    public String getScore2() {
        return score2;
    }

    /**
     *
     * @param score2
     * The score2
     */
    public void setScore2(String score2) {
        this.score2 = score2;
    }

    /**
     *
     * @return
     * The quarter
     */
    public String getQuarter() {
        return quarter;
    }

    /**
     *
     * @param quarter
     * The quarter
     */
    public void setQuarter(String quarter) {
        this.quarter = quarter;
    }

    /**
     *
     * @return
     * The round
     */
    public String getRound() {
        return round;
    }

    /**
     *
     * @param round
     * The round
     */
    public void setRound(String round) {
        this.round = round;
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
