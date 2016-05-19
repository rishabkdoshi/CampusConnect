package com.campusconnect.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//import com.activeandroid.annotation.Column;
import com.campusconnect.bean.GroupBean;
import com.campusconnect.communicator.models.ModelsClubMiniForm;
import com.campusconnect.communicator.models.ModelsFeed;
import com.campusconnect.communicator.models.ModelsHashTag;
import com.campusconnect.communicator.models.ModelsLiveFeed;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rkd on 28/2/16.
 */
public class DBHandler extends SQLiteOpenHelper {
    String Tag = "DatabaseHandler";

    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_LIVE = "live";

    // Database Name
    private static final String DATABASE_NAME = "campusconnect";


    SQLiteDatabase db;

    //table for clubs
    private static final String TABLE_CLUBS = "clubs";

    //table for campusFeed
    private static final String TABLE_CAMPUSFEED = "campusfeed";

    //table for myfeed
    private static final String TABLE_MYFEED = "myfeed";

    //table for hashtags
    private static final String TABLE_HASH_TAGS = "hashtags";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = this.getWritableDatabase();
    }


    public List<ModelsClubMiniForm> getAllGroups() {
        List<ModelsClubMiniForm> modelsClubMiniForms = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();

        String sql = "select * from " + TABLE_CLUBS + " ORDER BY id ASC";

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                ModelsClubMiniForm modelsClubMiniForm = new ModelsClubMiniForm();
                modelsClubMiniForm.setClubId(cursor.getString(1));
                modelsClubMiniForm.setName(cursor.getString(2));
                modelsClubMiniForm.setDescription(cursor.getString(3));
                modelsClubMiniForm.setAbbreviation(cursor.getString(4));
                modelsClubMiniForm.setIsFollower(cursor.getString(5));
                modelsClubMiniForm.setAdminName(cursor.getString(6));
                modelsClubMiniForm.setCollegeName(cursor.getString(7));
                modelsClubMiniForm.setIsMember(cursor.getString(8));
                modelsClubMiniForm.setFollowerCount(cursor.getString(9));
                modelsClubMiniForm.setPhotoUrl(cursor.getString(10));
                modelsClubMiniForm.setMemberCount(cursor.getString(11));

                modelsClubMiniForms.add(modelsClubMiniForm);
            } while (cursor.moveToNext());
        }

        return modelsClubMiniForms;

    }

    public ModelsClubMiniForm getClubByClubId(String clubId) {
        SQLiteDatabase db = this.getReadableDatabase();

        ModelsClubMiniForm modelsClubMiniForm = null;
        String sql = " select * from " + TABLE_CLUBS + " where clubId = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{clubId});


        if (cursor.moveToFirst()) {
            do {

                modelsClubMiniForm = new ModelsClubMiniForm();
                modelsClubMiniForm.setClubId(cursor.getString(1));
                modelsClubMiniForm.setName(cursor.getString(2));
                modelsClubMiniForm.setDescription(cursor.getString(3));
                modelsClubMiniForm.setAbbreviation(cursor.getString(4));
                modelsClubMiniForm.setIsFollower(cursor.getString(5));
                modelsClubMiniForm.setAdminName(cursor.getString(6));
                modelsClubMiniForm.setCollegeName(cursor.getString(7));
                modelsClubMiniForm.setIsMember(cursor.getString(8));
                modelsClubMiniForm.setFollowerCount(cursor.getString(9));
                modelsClubMiniForm.setPhotoUrl(cursor.getString(10));
                modelsClubMiniForm.setMemberCount(cursor.getString(11));

            } while (cursor.moveToNext());
        }
//        Log.d("ever",modelsClubMiniForm.getFollowerCount());

        return modelsClubMiniForm;
    }

    public void deleteAllClubs() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("delete from " + TABLE_CLUBS);

        db.close();
    }

    public void updateFollowUnfollow(String clubId,String followValue){
        ContentValues values = new ContentValues();
        values.put("isFollower", followValue);
        db.update(TABLE_CLUBS, values, "clubId = ?", new String[]{clubId});
    }

    public List<ModelsClubMiniForm> getAllFollowingClubs() {

        SQLiteDatabase db = this.getReadableDatabase();

        List<ModelsClubMiniForm> modelsClubMiniForms = new ArrayList<>();
        String sql = " select * from " + TABLE_CLUBS + " where isFollower = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{"Y"});


        if (cursor.moveToFirst()) {
            do {
                ModelsClubMiniForm modelsClubMiniForm = new ModelsClubMiniForm();
                modelsClubMiniForm.setClubId(cursor.getString(1));
                modelsClubMiniForm.setName(cursor.getString(2));
                modelsClubMiniForm.setDescription(cursor.getString(3));
                modelsClubMiniForm.setAbbreviation(cursor.getString(4));
                modelsClubMiniForm.setIsFollower(cursor.getString(5));
                modelsClubMiniForm.setAdminName(cursor.getString(6));
                modelsClubMiniForm.setCollegeName(cursor.getString(7));
                modelsClubMiniForm.setIsMember(cursor.getString(8));
                modelsClubMiniForm.setFollowerCount(cursor.getString(9));
                modelsClubMiniForm.setPhotoUrl(cursor.getString(10));
                modelsClubMiniForm.setMemberCount(cursor.getString(11));
                modelsClubMiniForms.add(modelsClubMiniForm);
            } while (cursor.moveToNext());
        }

        return modelsClubMiniForms;
    }

    public void addGroupItem(ModelsClubMiniForm modelsClubMiniForm) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = Clubs.saveClub(modelsClubMiniForm);
        // Inserting Row
        db.insert(TABLE_CLUBS, null, values);
        db.close(); // Closing database connection
    }


    public void addMyFeedItem(ModelsFeed modelsFeed) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = Feed.saveFeed(modelsFeed);
        // Inserting Row
        db.insert(TABLE_MYFEED, null, values);
        db.close(); // Closing database connection
    }

    public void addCampusFeedItem(ModelsFeed modelsFeed) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = Feed.saveFeed(modelsFeed);
        // Inserting Row
        db.insert(TABLE_CAMPUSFEED, null, values);
//        db.close(); // Closing database connection
    }

    public void deleteAllMyFeed() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_MYFEED);
//        db.close();
    }

    public void deleteAllCampusFeed() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_CAMPUSFEED);
//        db.close();
    }

    public void deleteAllLive() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_LIVE);
//        db.close();
    }


    public void addLiveItem(ModelsLiveFeed modelsLiveFeed) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = LiveFeed.saveLive(modelsLiveFeed);
        // Inserting Row
        db.insert(TABLE_LIVE, null, values);
//        db.close(); // Closing database connection
    }

    public void addHashTagItem(String tag,String timestamp) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = HashTags.saveTag(tag,timestamp);
        // Inserting Row
        db.insert(TABLE_HASH_TAGS, null, values);
//        db.close(); // Closing database connection
    }


    public List<ModelsHashTag> getAllHashTags(){
        List<ModelsHashTag> tags = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();

        String sql = "select * from " + TABLE_HASH_TAGS + " ORDER BY id ASC";

        Cursor cursor = db.rawQuery(sql, null);


        if (cursor.moveToFirst()) {
            do {
                ModelsHashTag modelsHashTag=new ModelsHashTag();
                modelsHashTag.setStartTime(cursor.getString(1));
                modelsHashTag.setName(cursor.getString(2));

                tags.add(modelsHashTag);
            } while (cursor.moveToNext());
        }

        return tags;

    }


    public void deleteAllTags() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_HASH_TAGS);
//        db.close();
    }

    public List<ModelsLiveFeed> getAllLiveItems() {
        List<ModelsLiveFeed> modelsLiveFeeds = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();

        String sql = "select * from " + TABLE_LIVE + " ORDER BY id ASC";

        Cursor cursor = db.rawQuery(sql, null);


        if (cursor.moveToFirst()) {
            do {
                ModelsLiveFeed modelsLiveFeed = new ModelsLiveFeed();

                modelsLiveFeed.setDescription(cursor.getString(1));
                modelsLiveFeed.setImageUrl(cursor.getString(2));
                modelsLiveFeed.setPersonPhotoUrl(cursor.getString(3));
                modelsLiveFeed.setTags(cursor.getString(4));
                modelsLiveFeed.setTimestamp(cursor.getString(5));
                modelsLiveFeed.setMessageId(cursor.getString(6));
                modelsLiveFeed.setName(cursor.getString(7));

                modelsLiveFeeds.add(modelsLiveFeed);
            } while (cursor.moveToNext());
        }

        return modelsLiveFeeds;

    }



    public List<ModelsFeed> getAllCampusFeedItems() {
        List<ModelsFeed> modelsFeeds = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();

        String sql = "select * from " + TABLE_CAMPUSFEED + " ORDER BY id ASC";

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                ModelsFeed modelsFeed = new ModelsFeed();

                modelsFeed.setClubId(cursor.getString(1));
                modelsFeed.setStartDate(cursor.getString(2));
                modelsFeed.setDescription(cursor.getString(3));
                modelsFeed.setEndDate(cursor.getString(4));
                modelsFeed.setFeedType(cursor.getString(5));
                modelsFeed.setHasLiked(cursor.getString(6));
                modelsFeed.setLikes(cursor.getString(7));
                modelsFeed.setKind(cursor.getString(8));
                modelsFeed.setEndTime(cursor.getString(9));
                modelsFeed.setPhotoUrl(cursor.getString(10));
                modelsFeed.setVenue(cursor.getString(11));
                modelsFeed.setContentCreator(cursor.getString(12));
                modelsFeed.setClubphotoUrl(cursor.getString(13));
                modelsFeed.setIsAlumni(cursor.getString(14));
                modelsFeed.setStartTime(cursor.getString(15));
                modelsFeed.setIsAttending(cursor.getString(16));
                modelsFeed.setCompleted(cursor.getString(17));
                modelsFeed.setTimestamp(cursor.getString(18));
                modelsFeed.setCommentCount(cursor.getString(19));
                modelsFeed.setFeedId(cursor.getString(20));
                modelsFeed.setAttendees(cursor.getString(21));
                modelsFeed.setViews(cursor.getString(22));
                modelsFeed.setClubName(cursor.getString(23));
                modelsFeed.setTitle(cursor.getString(24));
                modelsFeed.setTags(cursor.getString(25));
                modelsFeed.setClubabbreviation(cursor.getString(26));

                modelsFeeds.add(modelsFeed);
            } while (cursor.moveToNext());
        }

        return modelsFeeds;

    }


    public List<ModelsFeed> getAllMyFeedItems() {
        List<ModelsFeed> modelsFeeds = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();

        String sql = "select * from " + TABLE_MYFEED + " ORDER BY id ASC";

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                ModelsFeed modelsFeed = new ModelsFeed();

                modelsFeed.setClubId(cursor.getString(1));
                modelsFeed.setStartDate(cursor.getString(2));
                modelsFeed.setDescription(cursor.getString(3));
                modelsFeed.setEndDate(cursor.getString(4));
                modelsFeed.setFeedType(cursor.getString(5));
                modelsFeed.setHasLiked(cursor.getString(6));
                modelsFeed.setLikes(cursor.getString(7));
                modelsFeed.setKind(cursor.getString(8));
                modelsFeed.setEndTime(cursor.getString(9));
                modelsFeed.setPhotoUrl(cursor.getString(10));
                modelsFeed.setVenue(cursor.getString(11));
                modelsFeed.setContentCreator(cursor.getString(12));
                modelsFeed.setClubphotoUrl(cursor.getString(13));
                modelsFeed.setIsAlumni(cursor.getString(14));
                modelsFeed.setStartTime(cursor.getString(15));
                modelsFeed.setIsAttending(cursor.getString(16));
                modelsFeed.setCompleted(cursor.getString(17));
                modelsFeed.setTimestamp(cursor.getString(18));
                modelsFeed.setCommentCount(cursor.getString(19));
                modelsFeed.setFeedId(cursor.getString(20));
                modelsFeed.setAttendees(cursor.getString(21));
                modelsFeed.setViews(cursor.getString(22));
                modelsFeed.setClubName(cursor.getString(23));
                modelsFeed.setTitle(cursor.getString(24));
                modelsFeed.setTags(cursor.getString(25));
                modelsFeed.setClubabbreviation(cursor.getString(26));

                modelsFeeds.add(modelsFeed);
            } while (cursor.moveToNext());
        }

        return modelsFeeds;

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CLUB_TABLE = Clubs.createClubsTable();
        String CREATE_MY_FEED_TABLE = Feed.createFeedTable(TABLE_MYFEED);
        String CREATE_CAMPUS_FEED_TABLE = Feed.createFeedTable(TABLE_CAMPUSFEED);
        String CREATE_LIVE_TABLE = LiveFeed.createLiveTable();
        String CREATE_HASHTAG_TABLE = HashTags.createTagsTable();

        db.execSQL(CREATE_CLUB_TABLE);
        db.execSQL(CREATE_CAMPUS_FEED_TABLE);
        db.execSQL(CREATE_MY_FEED_TABLE);
        db.execSQL(CREATE_LIVE_TABLE);
        db.execSQL(CREATE_HASHTAG_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLUBS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HASH_TAGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIVE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAMPUSFEED);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MYFEED);

        // Create tables again
        onCreate(db);
    }
}

class Feed {
    //    @SerializedName()
    private static String KEY_START_DATE = "startDate";

    //    @SerializedName("endDate")
    private static String KEY_END_DATE = "endDate";

    //    @SerializedName("photoUrl")
    private static String KEY_PHOTO_URL = "photoUrl";

    //    @Column(name = "commentCount")
//    @SerializedName("commentCount")
    private static String KEY_COMMENT_COUNT = "commentCount";

    //    @Column(name = "feedId")
//    @SerializedName("id")
    private static String KEY_FEED_ID = "feedId";

    //    @Column(name = "attendees")
//    @SerializedName("attendees")
    private static String KEY_ATTENDEES = "attendees";

    //    @Column(name = "clubId")
//    @SerializedName("clubId")
    private static String KEY_CLUBID = "clubId";


    //    @Column(name = "title")
//    @SerializedName("title")
    private static String KEY_TITLE = "title";


    //    @Column(name = "tags")
//    @SerializedName("tags")
    private static String KEY_TAGS = "tags";

    //    @Column(name = "clubName")
//    @SerializedName("clubName")
    private static String KEY_CLUBNAME = "clubName";

    //    @Column(name = "description")
//    @SerializedName("description")
    private static String KEY_DESCRIPTION = "description";


    //    @Column(name = "views")
//    @SerializedName("views")
    private static String KEY_VIEWS = "views";

    //    @Column(name = "timestamp")
//    @SerializedName("timestamp")
    private static String KEY_TIMESTAMP = "timestamp";

    //    @Column(name = "completed")
//    @SerializedName("completed")
    private static String KEY_COMPLETED = "completed";


    //    @Column(name = "isAttending")
//    @SerializedName("isAttending")
    private static String KEY_ISATTENDING = "isAttending";

    //    @Column(name = "startTime")
//    @SerializedName("startTime")
    private static String KEY_STARTTIME = "startTime";

    //    @Column(name = "isAlumni")
//    @SerializedName("isAlumni")
    private static String KEY_ISALUMNI = "isAlumni";

    //    @Column(name = "clubphotoUrl")
//    @SerializedName("clubphotoUrl")
    private static String KEY_CLUB_PHOTO_URL = "clubPhotoUrl";

    //    @Column(name = "contentCreator")
//    @SerializedName("contentCreator")
    private static String KEY_CONTENT_CREATOR = "contentCreator";

    //    @Column(name = "venue")
//    @SerializedName("venue")
    private static String KEY_VENUE = "venue";

    //    @Column(name = "clubabbreviation")
//    @SerializedName("clubabbreviation")
    private static String KEY_CLUB_ABBREVIATION = "clubabbreviation";

    //    @Column(name = "endTime")
//    @SerializedName("endTime")
    private static String KEY_END_TIME = "endTime";

    //    @Column(name = "kind")
//    @SerializedName("kind")
    private static String KEY_KIND = "kind";

    //    @Column(name = "likes")
//    @SerializedName("likes")
    private static String KEY_LIKES = "likes";

    //    @Column(name = "hasLiked")
//    @SerializedName("hasLiked")
    private static String KEY_HASLIKED = "hasLiked";

    //    @Column(name = "feedType")
//    @SerializedName("feedType")
    private static String KEY_FEEDTYPE = "feedType";

    private static String KEY_ID = "id";

    static String createFeedTable(String tableName) {
        String CREATE_FEED_TABLE = "CREATE TABLE IF NOT EXISTS " + tableName + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + KEY_CLUBID + " TEXT,"
                + KEY_START_DATE + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_END_DATE + " TEXT,"
                + KEY_FEEDTYPE + " TEXT,"
                + KEY_HASLIKED + " TEXT,"
                + KEY_LIKES + " TEXT,"
                + KEY_KIND + " TEXT,"
                + KEY_END_TIME + " TEXT,"
                + KEY_PHOTO_URL + " TEXT,"
                + KEY_VENUE + " TEXT,"
                + KEY_CONTENT_CREATOR + " TEXT,"
                + KEY_CLUB_PHOTO_URL + " TEXT,"
                + KEY_ISALUMNI + " TEXT,"
                + KEY_STARTTIME + " TEXT,"
                + KEY_ISATTENDING + " TEXT,"
                + KEY_COMPLETED + " TEXT,"
                + KEY_TIMESTAMP + " TEXT,"
                + KEY_COMMENT_COUNT + " TEXT,"
                + KEY_FEED_ID + " TEXT,"
                + KEY_ATTENDEES + " TEXT,"
                + KEY_VIEWS + " TEXT,"
                + KEY_CLUBNAME + " TEXT,"
                + KEY_TITLE + " TEXT,"
                + KEY_TAGS + " TEXT,"
                + KEY_CLUB_ABBREVIATION + " TEXT" + ")";


        return CREATE_FEED_TABLE;
    }

    public static ContentValues saveFeed(ModelsFeed modelsFeed) {
        ContentValues values = new ContentValues();
        values.put(KEY_ATTENDEES, modelsFeed.getAttendees());//26
        values.put(KEY_CLUB_ABBREVIATION, modelsFeed.getClubabbreviation());//25
        values.put(KEY_CLUB_PHOTO_URL, modelsFeed.getClubphotoUrl());//24
        values.put(KEY_CLUBID, modelsFeed.getClubId());//23
        values.put(KEY_CLUBNAME, modelsFeed.getClubName());//22
        values.put(KEY_COMMENT_COUNT, modelsFeed.getCommentCount());//21
        values.put(KEY_COMPLETED, modelsFeed.getCompleted());//20
        values.put(KEY_CONTENT_CREATOR, modelsFeed.getContentCreator());//19
        values.put(KEY_DESCRIPTION, modelsFeed.getDescription());//18
        values.put(KEY_END_DATE, modelsFeed.getEndDate());//17
        values.put(KEY_END_TIME, modelsFeed.getEndTime());//16
        values.put(KEY_FEED_ID, modelsFeed.getFeedId());//15
        values.put(KEY_FEEDTYPE, modelsFeed.getFeedType());//14
        values.put(KEY_HASLIKED, modelsFeed.getHasLiked());//13
        values.put(KEY_ISALUMNI, modelsFeed.getIsAlumni());//12
        values.put(KEY_ISATTENDING, modelsFeed.getIsAttending());//11
        values.put(KEY_KIND, modelsFeed.getKind());//10
        values.put(KEY_LIKES, modelsFeed.getLikes());//9
        values.put(KEY_PHOTO_URL, modelsFeed.getPhotoUrl());//8
        values.put(KEY_START_DATE, modelsFeed.getStartDate());//7
        values.put(KEY_STARTTIME, modelsFeed.getStartTime());//6
        values.put(KEY_TAGS, modelsFeed.getTags());//5
        values.put(KEY_TIMESTAMP, modelsFeed.getTimestamp());//4
        values.put(KEY_TITLE, modelsFeed.getTitle());//3
        values.put(KEY_VENUE, modelsFeed.getVenue());//2
        values.put(KEY_VIEWS, modelsFeed.getViews());//1

        return values;
    }
}

class Clubs {

    //table for clubs
    private static final String TABLE_CLUBS = "clubs";

    //columns in clubs table
    private static String KEY_FOLLOWERCOUNT = "followerCount";

    private static String KEY_DESCRIPTION = "description";

    private static String KEY_PHOTOURL = "photoUrl";

    private static String KEY_MEMBER_COUNT = "memberCount";

    private static String KEY_COLLEGE_NAME = "collegeName";

    private static String KEY_ADMIN_NAME = "adminName";

    private static String KEY_ABBREVIATION = "abbreviation";

    private static String KEY_ISFOLLOWER = "isFollower";

    private static String KEY_ISMEMBER = "isMember";

    private static String KEY_CLUBID = "clubId";

    private static String KEY_NAME = "name";

    private static String KEY_ID = "id";

    static String createClubsTable() {
        String CREATE_CLUB_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_CLUBS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"//0
                + KEY_CLUBID + " TEXT,"//1
                + KEY_NAME + " TEXT,"//2
                + KEY_DESCRIPTION + " TEXT,"//3
                + KEY_ABBREVIATION + " TEXT,"//4
                + KEY_ISFOLLOWER + " TEXT,"//5
                + KEY_ADMIN_NAME + " TEXT,"//6
                + KEY_COLLEGE_NAME + " TEXT,"//7
                + KEY_ISMEMBER + " TEXT,"//8
                + KEY_FOLLOWERCOUNT + " TEXT,"//9
                + KEY_PHOTOURL + " TEXT,"//10
                + KEY_MEMBER_COUNT + " TEXT" + ")";//11


        return CREATE_CLUB_TABLE;
    }

    public static ContentValues saveClub(ModelsClubMiniForm modelsClubMiniForm) {
        ContentValues values = new ContentValues();

        values.put(KEY_CLUBID, modelsClubMiniForm.getClubId());//2
        values.put(KEY_NAME, modelsClubMiniForm.getName()); //1
        values.put(KEY_DESCRIPTION, modelsClubMiniForm.getDescription());//3
        values.put(KEY_ABBREVIATION, modelsClubMiniForm.getAbbreviation());//5
        values.put(KEY_ISFOLLOWER, modelsClubMiniForm.getIsFollower());//6
        values.put(KEY_ADMIN_NAME, modelsClubMiniForm.getAdminName());//4
        values.put(KEY_COLLEGE_NAME, modelsClubMiniForm.getCollegeName());//11
        values.put(KEY_ISMEMBER, modelsClubMiniForm.getIsMember());//7
        values.put(KEY_FOLLOWERCOUNT, modelsClubMiniForm.getFollowerCount());//9
        values.put(KEY_PHOTOURL, modelsClubMiniForm.getPhotoUrl());//10
        values.put(KEY_MEMBER_COUNT, modelsClubMiniForm.getMemberCount());//8


        return values;
    }

}

class LiveFeed {
    //table for clubs
    private static final String TABLE_LIVE = "live";

    //columns in clubs table

    private static String KEY_DESCRIPTION = "description";

    private static String KEY_PHOTOURL = "photoUrl";

    private static String KEY_PERSON_PHOTO_URL = "personPhotoUrl";

    private static String KEY_HASH_TAGS = "hashTags";

    private static String KEY_TIMESTAMP = "timestamp";

    private static String KEY_MESSAGE_ID = "messageId";

    private static String KEY_NAME = "name";



    private static String KEY_ID = "id";

    static String createLiveTable() {
        String CREATE_CLUB_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_LIVE + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"//0
                + KEY_DESCRIPTION + " TEXT,"//1
                + KEY_PHOTOURL + " TEXT,"//2
                + KEY_PERSON_PHOTO_URL + " TEXT,"//3
                + KEY_HASH_TAGS + " TEXT,"//4
                + KEY_TIMESTAMP + " TEXT,"//5
                + KEY_MESSAGE_ID + " TEXT,"//6
                + KEY_NAME + " TEXT"//6
                + ")";//11


        return CREATE_CLUB_TABLE;
    }

    public static ContentValues saveLive(ModelsLiveFeed modelsLiveFeed) {
        ContentValues values = new ContentValues();

        values.put(KEY_DESCRIPTION, modelsLiveFeed.getDescription());
        values.put(KEY_PHOTOURL, modelsLiveFeed.getImageUrl());
        values.put(KEY_PERSON_PHOTO_URL, modelsLiveFeed.getPersonPhotoUrl());
        values.put(KEY_HASH_TAGS, modelsLiveFeed.getTags());
        values.put(KEY_TIMESTAMP, modelsLiveFeed.getTimestamp());
        values.put(KEY_MESSAGE_ID, modelsLiveFeed.getMessageId());
        values.put(KEY_NAME,modelsLiveFeed.getName());

        return values;


    }
}


class HashTags{
    private static final String TABLE_HASH_TAGS = "hashtags";

    //columns in clubs table

    private static String KEY_HASHTAG = "hashtag";

    private static String KEY_TIMESTAMP = "timestamp";

    private static String KEY_ID = "id";


    static String createTagsTable() {
        String CREATE_CLUB_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_HASH_TAGS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"//0
                + KEY_TIMESTAMP + " TEXT,"//0
                + KEY_HASHTAG + " TEXT"//6
                + ")";//11


        return CREATE_CLUB_TABLE;
    }

    public static ContentValues saveTag(String hashtag,String timestamp) {
        ContentValues values = new ContentValues();
        values.put(KEY_TIMESTAMP,timestamp);
        values.put(KEY_HASHTAG, hashtag);

        return values;


    }
}