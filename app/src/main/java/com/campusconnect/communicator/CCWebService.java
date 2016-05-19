package com.campusconnect.communicator;


import com.campusconnect.communicator.models.ModelsAddCollege;
import com.campusconnect.communicator.models.ModelsAdminFeed;
import com.campusconnect.communicator.models.ModelsAdminStatus;
import com.campusconnect.communicator.models.ModelsClubListResponse;
import com.campusconnect.communicator.models.ModelsClubMiniForm;
import com.campusconnect.communicator.models.ModelsClubRetrievalMiniForm;
import com.campusconnect.communicator.models.ModelsCollegeFeed;
import com.campusconnect.communicator.models.ModelsColleges;
import com.campusconnect.communicator.models.ModelsCommentPost;
import com.campusconnect.communicator.models.ModelsCommentsForm;
import com.campusconnect.communicator.models.ModelsCommentsListResponse;
import com.campusconnect.communicator.models.ModelsEvents;
import com.campusconnect.communicator.models.ModelsFollowClubMiniForm;
import com.campusconnect.communicator.models.ModelsGetInformation;
import com.campusconnect.communicator.models.ModelsHashTagList;
import com.campusconnect.communicator.models.ModelsJoinClubMiniForm;
import com.campusconnect.communicator.models.ModelsKMCParent;
import com.campusconnect.communicator.models.ModelsLikePost;
import com.campusconnect.communicator.models.ModelsLiveFeed;
import com.campusconnect.communicator.models.ModelsLiveResponse;
import com.campusconnect.communicator.models.ModelsMessageResponse;
import com.campusconnect.communicator.models.ModelsModifyEvent;
import com.campusconnect.communicator.models.ModelsNotificationList;
import com.campusconnect.communicator.models.ModelsNotificationMiniForm;
import com.campusconnect.communicator.models.ModelsPersonalInfoResponse;
import com.campusconnect.communicator.models.ModelsPersonalResponse;
import com.campusconnect.communicator.models.ModelsPosts;
import com.campusconnect.communicator.models.ModelsProfileMiniForm;
import com.campusconnect.communicator.models.ModelsProfileResponse;
import com.campusconnect.communicator.models.ModelsProfileRetrievalMiniForm;
import com.campusconnect.communicator.models.ModelsReportContent;
import com.campusconnect.communicator.models.ModelsRequestMiniForm;
import com.campusconnect.communicator.models.ModelsScoreBoardList;
import com.campusconnect.communicator.models.ModelsSubscribeMatch;
import com.campusconnect.communicator.models.ModelsSuperAdminFeedResponse;
import com.campusconnect.communicator.models.ModelsUnjoinClubMiniForm;
import com.campusconnect.communicator.models.ModelsUpdateStatus;

import retrofit.Call;
//import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by rkd on 18/1/16.
 */
public interface CCWebService {
    public static final String DEFAULT_ROOT_URL = "https://campus-connect-2015.appspot.com/_ah/api/";
    public static final String DEFAULT_SERVICE_PATH = "campusConnectApis/v1/";

    public static final String BASE_URL = DEFAULT_ROOT_URL + DEFAULT_SERVICE_PATH;

    @POST("subscribeMatch")
    Call<Void> subscribeToMatch(@Body ModelsSubscribeMatch modelsSubscribeMatch);

    @POST("unsubscribeMatch")
    Call<Void> unSubscribeToMatch(@Body ModelsSubscribeMatch modelsSubscribeMatch);

    @POST("reportApi")
    Call<Void> reportPost(@Body ModelsReportContent modelsReportContent);

    @GET("scoreBoard")
    Call<ModelsScoreBoardList> getScoreBoard();

    @GET("getEventsES")
    Call<ModelsHashTagList> getHashTags(@Query("timestamp")String timestamp,@Query("collegeId")String collegeId);

    @GET("mainFeed")
    Call<ModelsCollegeFeed> collegeFeed(@Query("collegeId") String collegeId, @Query("pid") String pid, @Query("pageNumber") String pageNumber);

    @GET("mainFeed")
    Call<ModelsCollegeFeed> clubFeed(@Query("clubId") String clubId, @Query("pid") String pid);

    @GET("mainFeed")
    Call<ModelsCollegeFeed> inciFeed(@Query("clubId") String clubId, @Query("pid") String pid,@Query("pageNumber") String pageNumber);

    @GET("myFeed")
    Call<ModelsCollegeFeed> myFeed(@Query("collegeId") String collegeId, @Query("pid") String pid, @Query("pageNumber") String pageNumber);

    @GET("adminStatus")
    Call<ModelsAdminStatus> adminStatus(@Query("pid") String pid);

    @POST("followClub")
    Call<Void> followClub(@Body ModelsFollowClubMiniForm modelsFollowClubMiniForm);

    @POST("unfollowclub")
    Call<Void> unfollowClub(@Body ModelsFollowClubMiniForm modelsFollowClubMiniForm);

    @GET("getClubList")
    Call<ModelsClubListResponse> getClubList(@Query("collegeId") String collegeId, @Query("pid") String pid);

    @POST("attendEvent")
    Call<Void> attendEvent(@Body ModelsModifyEvent modelsModifyEvent);

    @GET("superAdminFeed")
    Call<ModelsSuperAdminFeedResponse> superAdminFeed(@Query("collegeId") String collegeId, @Query("pid") String pid);

    @GET("getClubListofAdmin")
    Call<ModelsClubListResponse> getAdminClubs(@Query("pid") String pid);

    @GET("getClubMembers")
    Call<ModelsPersonalInfoResponse> getClubMembers(@Query("clubId") String clubId);

    @GET("getClub")
    Call<ModelsClubMiniForm> getClub(@Query("clubId") String clubId, @Query("pid") String pid);

    @GET("adminFeed")
    Call<ModelsAdminFeed> getAdminFeed(@Query("clubId") String clubId, @Query("pid") String pid);

    @GET("superAdminFeed")
    Call<ModelsSuperAdminFeedResponse> getSuperAdminFeed(@Query("collegeId") String collegeId, @Query("pid") String pid);

    @GET("getColleges")
    Call<ModelsColleges> getColleges();

    @POST("profileGCM")
    Call<ModelsProfileResponse> profileGCM(@Body ModelsProfileRetrievalMiniForm modelsProfileRetrievalMiniForm);

    @GET("getEvents")
    Call<ModelsEvents> getClubEvents(@Query("clubId") String clubId);

    @POST("approveclubreq")
    Call<Void> acceptRejectClubReq(@Body ModelsRequestMiniForm modelsRequestMiniForm);

    @POST("joinClub")
    Call<Void> joinClub(@Body ModelsJoinClubMiniForm modelsJoinClubMiniForm);

    @POST("joinClubApproval")
    Call<Void> acceptRejectJoinReq(@Body ModelsRequestMiniForm modelsRequestMiniForm);

    @POST("myNotificationsMod")
    Call<ModelsNotificationList> getMyNotifications(@Body ModelsNotificationMiniForm modelsNotificationMiniForm);

    @GET("getAttendeeDetails")
    Call<ModelsPersonalInfoResponse> getAttendeeList(@Query("eventId") String eventId);

    @POST("unAttendEvent")
    Call<Void> unattendEvent(@Body ModelsLikePost modelsLikePost);

    @POST("comment")
    Call<Void> commentPost(@Body ModelsCommentPost modelsCommentPost);

    @GET("getComments")
    Call<ModelsCommentsListResponse> getComments(@Query("postId") String postId, @Query("pageNumber") String pageNumber);

    @GET("updateStatus")
    Call<ModelsUpdateStatus> getUpdateStatus();

    @GET("getEvents")
    Call<ModelsCollegeFeed> getCalendarEvents(@Query("pid") String pid,@Query("collegeId") String collegeId,@Query("futureDate") String futureDate);

    @GET("getEvents")
    Call<ModelsCollegeFeed> getClubEvents(@Query("pid") String pid,@Query("clubId") String clubId);


    @GET("getEvents")
    Call<ModelsCollegeFeed> getSpecificEvent(@Query("pid") String pid,@Query("postId") String postId);


    @GET("getPosts")
    Call<ModelsCollegeFeed> getClubPosts(@Query("pid")String pid,@Query("clubId") String clubId);

    @GET("getPosts")
    Call<ModelsCollegeFeed> getSpecificPost(@Query("pid")String pid,@Query("postId") String postId);

    @GET("profile")
    Call<ModelsProfileResponse> getProfile(@Query("pid")String pid);

    @DELETE("deletePost")
    Call<Void> deletePost(@Query("postId") String postId,@Query("fromPid") String fromPid);

    @DELETE("deleteEvent")
    Call<Void> deleteEvent(@Query("eventId") String postId,@Query("fromPid") String fromPid);

    @POST("unJoinClub")
    Call<Void> unjoinClub(@Body ModelsUnjoinClubMiniForm modelsUnjoinClubMiniForm);

    @GET("kmcScoreQuery")
    Call<ModelsKMCParent> getKMCScoreBoard();

    @POST("profile")
    Call<ModelsProfileMiniForm> createProfile(@Body ModelsProfileMiniForm modelsProfileMiniForm);

    @POST("addCollege")
    Call<ModelsMessageResponse> addCollege(@Body ModelsAddCollege modelsAddCollege);

    @GET("liveCommentsFeed")
    Call<ModelsLiveResponse> inciLive(@Query("collegeId")String collegeId,@Query("pageNumber")String pageNumber);

    @POST("liveComments")
    Call<Void> liveComments(@Body ModelsLiveFeed modelsLiveFeed);
}
