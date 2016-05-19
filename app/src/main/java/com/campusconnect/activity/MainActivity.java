package com.campusconnect.activity;

import android.app.Activity;
import android.app.Service;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

//import com.activeandroid.ActiveAndroid;
//import com.activeandroid.Configuration;
import com.campusconnect.R;
import com.campusconnect.communicator.CCWebService;
import com.campusconnect.communicator.ServiceGenerator;
import com.campusconnect.communicator.models.ModelsHashTag;
import com.campusconnect.communicator.models.ModelsHashTagList;
import com.campusconnect.communicator.models.ModelsProfileMiniForm;
import com.campusconnect.communicator.models.ModelsUpdateStatus;
import com.campusconnect.constant.AppConstants;
import com.campusconnect.database.DBHandler;
import com.campusconnect.fragment.CalenderFragment;
import com.campusconnect.fragment.GuestFragment;
import com.campusconnect.fragment.HomeFragment;
import com.campusconnect.fragment.NotificationFragment;
import com.campusconnect.fragment.ProfilePageFragment;
import com.campusconnect.fragment.SearchFragment;
import com.campusconnect.utility.Analytics;
import com.campusconnect.utility.SharedpreferenceUtility;
import com.flurry.android.FlurryAgent;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by canopus on 24/11/15.
 */
public class MainActivity extends FragmentActivity implements View.OnClickListener {

    @Bind(R.id.ib_search)
    ImageButton search;
    @Bind(R.id.ib_calendar)
    ImageButton calender;
    @Bind(R.id.ib_profile)
    ImageButton profile;
    @Bind(R.id.ib_home)
    ImageButton home;
    @Bind(R.id.ib_notification)
    ImageButton notification;
    @Bind(R.id.lnr_search_line)
    LinearLayout searchLine;
    @Bind(R.id.lnr_cal_line)
    LinearLayout calLine;
    @Bind(R.id.lnr_notification_line)
    LinearLayout notificationLine;
    @Bind(R.id.lnr_profile_line)
    LinearLayout profileLine;
    @Bind(R.id.lnr_home_line)
    LinearLayout homeLine;

    FrameLayout frame;
    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager;

    public static boolean isLaunch = true;

    int fragment;
    public static String frag_loc = "";

    public MainActivity() {
    }

    CCWebService mCCService;

    ModelsProfileMiniForm modelsProfileMiniForm;

    DBHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (SharedpreferenceUtility.getInstance(MainActivity.this).getString(AppConstants.PERSON_TAGS) == null) {
            SharedpreferenceUtility.getInstance(MainActivity.this).putString(AppConstants.PERSON_TAGS, " ;");
        }

        db=new DBHandler(MainActivity.this);

        FlurryAgent.setLogLevel(Log.VERBOSE);
        FlurryAgent.setLogEvents(true);
        FlurryAgent.setLogEnabled(true);
        //FlurryAgent.setLocation(float latitude, float longitude);
        FlurryAgent.init(this, AppConstants.MY_FLURRY_APIKEY);

        isLaunch = true;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            fragment = bundle.getInt("FRAGMENT");
            modelsProfileMiniForm = bundle.getParcelable("profile");
        } else if (savedInstanceState == null) {
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            if(SharedpreferenceUtility.getInstance(MainActivity.this).getBoolean(AppConstants.IS_INCI_GUEST)){
                GuestFragment fragment = new GuestFragment();
                fragmentTransaction.replace(R.id.frame_container, fragment);
            }
            else{
                HomeFragment fragment = new HomeFragment();
                fragmentTransaction.replace(R.id.frame_container, fragment);
            }

            frag_loc = "home";
            fragmentTransaction.commit();
        }

        search.setOnClickListener(this);
        calender.setOnClickListener(this);
        home.setOnClickListener(this);
        profile.setOnClickListener(this);
        notification.setOnClickListener(this);

        if (fragment == 1) {
            search.performClick();
        } else if (fragment == 2) {
            calender.performClick();
        } else if (fragment == 3) {
            home.performClick();
        } else if (fragment == 4) {
            notification.performClick();
        } else if (fragment == 5) {
            profile.performClick();
        }

        //this is to retrieve hashtags and save to DB
        if (SharedpreferenceUtility.getInstance(MainActivity.this).getInt("HashTagsLoad") % 2 == 0) {

            //delete existing tags
            db.deleteAllTags();

            mCCService = ServiceGenerator.createService(CCWebService.class,CCWebService.BASE_URL);
                    String collegeId = SharedpreferenceUtility.getInstance(MainActivity.this).getString(AppConstants.COLLEGE_ID);

            Date date = new Date();
            String timestamp= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);

            Call getHashtags = mCCService.getHashTags(timestamp,collegeId);
            getHashtags.enqueue(hashCallBack);
            SharedpreferenceUtility.getInstance(MainActivity.this).putInt("HashTagsLoad", 1);
        }

        }

    private Callback<ModelsHashTagList> hashCallBack = new Callback<ModelsHashTagList>() {
        @Override
        public void onResponse(Response<ModelsHashTagList> response, Retrofit retrofit) {

            if (response != null) {
                if (response.isSuccess()) {
                    ModelsHashTagList modelsHashTagList = response.body();

                    for(ModelsHashTag modelsHashTag : modelsHashTagList.getItems()){
                        db.addHashTagItem(modelsHashTag.getName(),modelsHashTag.getStartTime());
                    }

                } else {
                    com.squareup.okhttp.Response rawResponse = response.raw();
                    if (rawResponse != null) {
//                            LogUtility.logFailedResponse(rawResponse);

                        int code = rawResponse.code();
                        switch (code) {
                            case 500:
//                                    mErrorTextView.setText("Can't load data.\nCheck your network connection.");
//                                    mErrorLinearLayout.setVisibility(View.VISIBLE);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }

        @Override
        public void onFailure(Throwable t) {
            //Timber.d("onFailure() : mQuery - " + mQuery);
            if (t != null) {
                String message = t.getMessage();
                //LogUtility.logFailure(t);

                if (t instanceof SocketTimeoutException
                        || t instanceof UnknownHostException
                        || t instanceof SocketException) {
                    //Timber.e("Timeout occurred");

//                        mErrorTextView.setText("Can't load data.\nCheck your network connection.");
//                        mErrorLinearLayout.setVisibility(View.VISIBLE);
                } else if (t instanceof IOException) {
                    if (message.equals("Canceled")) {
                        //Timber.e("onFailure() : Canceled");
                    } else {
//                        mIsLoading = false;
//                        mLoadingImageView.setVisibility(View.GONE);
                    }
                }
            }
        }
    };



    //TODO  changes in the listner
    //TODO changes in the bottom bar
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_profile:

                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.pushfront, R.anim.centertoright);
                ProfilePageFragment profileFragment = new ProfilePageFragment();

                if (modelsProfileMiniForm != null) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("profile", modelsProfileMiniForm);
                    profileFragment.setArguments(bundle);
                }
                fragmentTransaction.replace(R.id.frame_container, profileFragment);
                fragmentTransaction.commit();

                UpdateUi("profile");
                // Intent intent_profile = new Intent(v.getContext(), ProfilePageFragment.class);
                // startActivity(intent_profile);
                // finish();
                break;
            case R.id.ib_home:

                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.righttocenter, R.anim.pushback);
                if (SharedpreferenceUtility.getInstance(MainActivity.this).getBoolean(AppConstants.IS_INCI_GUEST)) {
                    GuestFragment guestFragment = new GuestFragment();
                    fragmentTransaction.replace(R.id.frame_container, guestFragment);
                    fragmentTransaction.commit();
                } else {
                    HomeFragment homeFragment = new HomeFragment();
                    fragmentTransaction.replace(R.id.frame_container, homeFragment);
                    fragmentTransaction.commit();
                }

                UpdateUi("home");
             /*   Intent intent_home = new Intent(v.getContext(), HomeFragment.class);
                startActivity(intent_home);
                overridePendingTransition(R.anim.pushfront, R.anim.centertoright);
                finish();*/
                break;
            case R.id.ib_notification:
              /*  Intent intent_noti = new Intent(v.getContext(), NotificationFragment.class);
                startActivity(intent_noti);
                finish();*/
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.pushfront, R.anim.centertoright);
                NotificationFragment notificationFragment = new NotificationFragment();
                fragmentTransaction.replace(R.id.frame_container, notificationFragment);
                fragmentTransaction.commit();

                UpdateUi("notification");
                break;
            case R.id.ib_search:


                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.righttocenter, R.anim.pushback);
                SearchFragment serchFragment = new SearchFragment();
                fragmentTransaction.replace(R.id.frame_container, serchFragment);
                fragmentTransaction.commit();
                UpdateUi("search");
             /*
                Intent intent_cal = new Intent(v.getContext(), SearchFragment.class);
                startActivity(intent_cal);
                finish();*/
                break;
            case R.id.ib_calendar:

                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.righttocenter, R.anim.pushback);
                CalenderFragment calender = new CalenderFragment();
                fragmentTransaction.replace(R.id.frame_container, calender);
                fragmentTransaction.commit();
                UpdateUi("calender");
                break;
        }
    }


    public void UpdateUi(String str) {

        if (fragment != 1 || fragment != 2 || fragment != 3 || fragment != 4 || fragment != 5)
            frag_loc = str;

        if (str.equals("home")) {
            search.setImageResource(R.mipmap.search);
            calender.setImageResource(R.mipmap.calendar);
            home.setImageResource(R.mipmap.home_selected);
            notification.setImageResource(R.mipmap.notification);
            profile.setImageResource(R.mipmap.profile);

            searchLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
            calLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
            notificationLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
            profileLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
            homeLine.setBackgroundColor(getResources().getColor(R.color.yello));

        } else if (str.equals("profile")) {

            search.setImageResource(R.mipmap.search);
            calender.setImageResource(R.mipmap.calendar);
            home.setImageResource(R.mipmap.home);
            notification.setImageResource(R.mipmap.notification);
            profile.setImageResource(R.mipmap.profile_selected);
            searchLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
            calLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
            notificationLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
            profileLine.setBackgroundColor(getResources().getColor(R.color.yello));
            homeLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));

        } else if (str.equals("search")) {

            search.setImageResource(R.mipmap.search_selected);
            calender.setImageResource(R.mipmap.calendar);
            home.setImageResource(R.mipmap.home);
            notification.setImageResource(R.mipmap.notification);
            profile.setImageResource(R.mipmap.profile);

            searchLine.setBackgroundColor(getResources().getColor(R.color.yello));
            calLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
            notificationLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
            profileLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
            homeLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));

        } else if (str.equals("calender")) {


            search.setImageResource(R.mipmap.search);
            calender.setImageResource(R.mipmap.calendar_selected);
            home.setImageResource(R.mipmap.home);
            notification.setImageResource(R.mipmap.notification);
            profile.setImageResource(R.mipmap.profile);

            searchLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
            calLine.setBackgroundColor(getResources().getColor(R.color.yello));
            notificationLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
            profileLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
            homeLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));

        } else if (str.equals("notification")) {

            search.setImageResource(R.mipmap.search);
            calender.setImageResource(R.mipmap.calendar);
            home.setImageResource(R.mipmap.home);
            notification.setImageResource(R.mipmap.notification_selected);
            profile.setImageResource(R.mipmap.profile);

            searchLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
            calLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
            notificationLine.setBackgroundColor(getResources().getColor(R.color.yello));
            profileLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
            homeLine.setBackgroundColor(getResources().getColor(R.color.carbonColor));
        }


    }

    public void onStart() {
        super.onStart();
//        Configuration dbConfiguration = new Configuration.Builder(this).setDatabaseName("campusconnect.db").create();
//        ActiveAndroid.initialize(dbConfiguration);

        HashMap<String, String> flurryParams = new HashMap<>();
        if (SharedpreferenceUtility.getInstance(this).getInt("AdminStatus") % 2 == 0) {
            flurryParams.put("User", SharedpreferenceUtility.getInstance(MainActivity.this).getString(AppConstants.EMAIL_KEY));
            flurryParams.put("EventName", "LogIn");
            Analytics.startSession(this, flurryParams);
        }
    }

    public void onStop() {
        super.onStop();

        HashMap<String, String> flurryParams = new HashMap<>();
        flurryParams.put("EventName", "LogIn");
        Analytics.stopSession(this, flurryParams);

    }


}
