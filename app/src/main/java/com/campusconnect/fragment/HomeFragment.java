package com.campusconnect.fragment;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.activeandroid.ActiveAndroid;
//import com.activeandroid.query.Delete;
import com.campusconnect.R;
import com.campusconnect.activity.CreateGroupActivity;
import com.campusconnect.activity.CreatePostActivity;
import com.campusconnect.activity.DirectoryExpandabeListViewActivity;
import com.campusconnect.activity.EditPostActivity;
import com.campusconnect.activity.FAQActivity;
import com.campusconnect.activity.FlashActivity;
import com.campusconnect.activity.GroupPageActivity;
import com.campusconnect.activity.MainActivity;
import com.campusconnect.activity.RequestsSuperAdmin;
import com.campusconnect.activity.SelectAdminClub;
//import com.campusconnect.activity.SlamDunkScorecardActivity;
import com.campusconnect.activity.SlamDunkScorecardActivity;
import com.campusconnect.adapter.CollegeCampusFeedAdapter;
import com.campusconnect.adapter.LiveAdapter;
import com.campusconnect.adapter.MenuListAdapter;
import com.campusconnect.adapter.TagArrayAdapter;
import com.campusconnect.adt.TagViewHolder;
import com.campusconnect.adt.TrendingTags;
import com.campusconnect.bean.GroupBean;
import com.campusconnect.communicator.CCWebService;
import com.campusconnect.communicator.ServiceGenerator;
import com.campusconnect.communicator.WebRequestTask;
import com.campusconnect.communicator.WebServiceDetails;
import com.campusconnect.communicator.models.ModelsAdminStatus;
import com.campusconnect.communicator.models.ModelsClubMiniForm;
import com.campusconnect.communicator.models.ModelsFeed;
import com.campusconnect.communicator.models.ModelsKMC;
import com.campusconnect.communicator.models.ModelsKMCParent;
import com.campusconnect.communicator.models.ModelsLiveFeed;
import com.campusconnect.communicator.models.ModelsUpdateStatus;
import com.campusconnect.constant.AppConstants;
import com.campusconnect.database.DatabaseHandler;
import com.campusconnect.photoFilter.PhotoActivty;
import com.campusconnect.slidingtab.SlidingTabLayout_home;
import com.campusconnect.utility.DividerItemDecoration;
import com.campusconnect.utility.FastBlur;
import com.campusconnect.utility.NetworkAvailablity;
import com.campusconnect.utility.SharedpreferenceUtility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


/**
 * Created by RK on 17-09-2015.
 */
public class HomeFragment extends BaseFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, CampusFeedFragment.RVPass, MyFeedFragment.RVPass, FragmentLive.RVPass {

    boolean is_lite_chkbox_checked = false;

    FloatingActionButton fab_home;
    RelativeLayout fab_event_container, fab_news_container;
    View shadow, shadow_fab_event, shadow_fab_news;
    Boolean is_fab_home_clicked=false;
    Calendar current_date;

    View setting;
    Context context;
    private PopupWindow popupWindow;
    View mRootView;
    //CollegeMyFeedAdapter cf;//change to campusfeed
    CollegeCampusFeedAdapter cf;
    RecyclerView college_feed;
    CollegeCampusFeedAdapter mCollegefeed, mMyFeed;
    LiveAdapter mLiveFeed;
    RecyclerView personal_feed;
    RecyclerView live_feed;

    ImageView blur_rect;

    //Scoreboard related definitions
    TextView score_2015, score_2014, score_2013, score_2012, score_PGI, score_AHS;
    TextView slamdunk_title;

    //Directory related definitions
    RelativeLayout directory_container;
    ImageButton directory;


    FrameLayout tabs_container;
    LinearLayout toolbar;
    public static LinearLayout settings;
    TextView admin;
    //LinearLayout admin;
    //LinearLayout add_post;
    ImageButton i_settings,super_admin, i_icon;
    //ImageButton i_admin;
    //ImageButton i_add_post;
    ViewPager pager;


    ViewPagerAdapter_home adapter;
    public static SlidingTabLayout_home tabs;
    CharSequence Titles[] = {"Live", "CampusFeed", "MyFeed", "Groups"};
    int Numboftabs = 4;

    GoogleApiClient mGoogleApiClient;
    boolean mSignInClicked;

    public HomeFragment() {
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

LinearLayout kmc_score_container;
    CCWebService mCCService;
    View anim_cover;
    Boolean to_create_post_flag = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView != null) {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (parent != null)
                parent.removeView(mRootView);
        }
        try {
            mRootView = inflater.inflate(R.layout.activity_home, container, false);
            context = mRootView.getContext();

            fab_home = (FloatingActionButton) mRootView.findViewById(R.id.fab_home);
            fab_event_container = (RelativeLayout) mRootView.findViewById(R.id.fab_event_container);
            fab_news_container = (RelativeLayout) mRootView.findViewById(R.id.fab_news_container);
            
            //Live edittext related declarations

            
            
            

            admin = (TextView) mRootView.findViewById(R.id.tv_admin);
//            add_post = (LinearLayout) mRootView.findViewById(R.id.plus);
            settings = (LinearLayout) mRootView.findViewById(R.id.settings);
            toolbar = (LinearLayout) mRootView.findViewById(R.id.custom_toolbar);
            tabs_container = (FrameLayout) mRootView.findViewById(R.id.tabs_container);
            shadow = (View) mRootView.findViewById(R.id.shadow_view);
            shadow_fab_event = (View) mRootView.findViewById(R.id.shadow_fab_event);
            shadow_fab_news = (View) mRootView.findViewById(R.id.shadow_fab_news);

            blur_rect = (ImageView) mRootView.findViewById(R.id.blur);

            //Directory related links
            directory_container = (RelativeLayout) mRootView.findViewById(R.id.directory_container);
            directory = (ImageButton) mRootView.findViewById(R.id.ib_directory);

            kmc_score_container=(LinearLayout)mRootView.findViewById(R.id.score_container);

            score_2015 = (TextView) mRootView.findViewById(R.id.tv_2015_score);
            score_2014 = (TextView) mRootView.findViewById(R.id.tv_2014_score);
            score_2013 = (TextView) mRootView.findViewById(R.id.tv_2013_score);
            score_2012 = (TextView) mRootView.findViewById(R.id.tv_2012_score);
            score_PGI = (TextView) mRootView.findViewById(R.id.tv_PGI_score);
            score_AHS = (TextView) mRootView.findViewById(R.id.tv_AHS_score);

            slamdunk_title = (TextView) mRootView.findViewById(R.id.tv_slamdunk_scoreboard_title);

            Typeface scoreboard = Typeface.createFromAsset(getContext().getAssets(), "font/Scoreboard.ttf");
            score_2015.setTypeface(scoreboard);
            score_2014.setTypeface(scoreboard);
            score_2013.setTypeface(scoreboard);
            score_2012.setTypeface(scoreboard);
            score_PGI.setTypeface(scoreboard);
            score_AHS.setTypeface(scoreboard);
            slamdunk_title.setTypeface(scoreboard);


//            i_admin = (ImageButton) mRootView.findViewById(R.id.ib_admin);
//            i_add_post = (ImageButton) mRootView.findViewById(R.id.ib_create_post);
            i_settings = (ImageButton) mRootView.findViewById(R.id.ib_settings);
            i_icon = (ImageButton) mRootView.findViewById(R.id.ib_icon);
            super_admin = (ImageButton) mRootView.findViewById(R.id.ib_super_admin);
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).addApi(Plus.API)
                    .addScope(Plus.SCOPE_PLUS_LOGIN).build();

            pager = (ViewPager) mRootView.findViewById(R.id.pager);
            tabs = (SlidingTabLayout_home) mRootView.findViewById(R.id.tabs);
            adapter = new ViewPagerAdapter_home(getChildFragmentManager(), Titles, Numboftabs, getActivity());
            pager.setAdapter(adapter);
            pager.setCurrentItem(0);
            tabs.setDistributeEvenly(true);
            tabs.setViewPager(pager);

            //HELPER
            if (!SharedpreferenceUtility.getInstance(context).getBoolean(AppConstants.IS_LIVE_FIRST_TIME)){
                SharedpreferenceUtility.getInstance(getContext()).putBoolean(AppConstants.IS_LIVE_FIRST_TIME, Boolean.TRUE);
                UsePhotoFilterDialog confirmDialog = new UsePhotoFilterDialog((Activity) context);
                Window window = confirmDialog.getWindow();
                window.setLayout(450, ViewGroup.LayoutParams.WRAP_CONTENT);
                confirmDialog.show();
            }

            settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    PopupWindow popUp = popupDisplay();
                    popUp.showAsDropDown(v, 0, 0);

                }
            });
            i_settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    PopupWindow popUp = popupDisplay();
                    popUp.showAsDropDown(settings, 0, 0);

                }


            });

            directory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent_temp = new Intent(v.getContext(), DirectoryExpandabeListViewActivity.class);
                    startActivity(intent_temp);

                }
            });
            directory_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent_temp = new Intent(v.getContext(), DirectoryExpandabeListViewActivity.class);
                    startActivity(intent_temp);

                }


            });


//            mCCService = ServiceGenerator.createService(
//                    CCWebService.class,
//                    CCWebService.BASE_URL);


            //function(just get the score from the server and replace the integers with it.) ---- RK
//            scoreboardflipAnim(score_2015, 8712);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    scoreboardflipAnim(score_2014, 8212);
//                }
//            }, 200);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    scoreboardflipAnim(score_2013, 8200);
//                }
//            }, 400);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    scoreboardflipAnim(score_2012, 8200);
//                }
//            }, 600);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    scoreboardflipAnim(score_PGI,8400);
//                }
//            }, 800);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    scoreboardflipAnim(score_AHS, 8782);
//                }
//            }, 1000);
//
//
//

            //FAB_home's intial condition
            fab_home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent_temp = new Intent(v.getContext(), CreatePostActivity.class);
                    startActivity(intent_temp);
                }
            });
     /*       fab_home.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(0, 0, 0)));
            fab_home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (is_fab_home_clicked == false) {
                        FABAnim();
                        is_fab_home_clicked = true;
                    } else {
                        ReverseFABAnim();
                        is_fab_home_clicked = false;
                    }
                }
            });

            fab_event_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent_temp = new Intent(v.getContext(), CreatePostActivity.class);
                    intent_temp.putExtra("FAB_TRIGGER_EVENT",true);
                    is_fab_home_clicked=false;
                    ReverseFABAnim();
                    startActivity(intent_temp);
                }
            });

            fab_news_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent_temp = new Intent(v.getContext(), CreatePostActivity.class);
                    intent_temp.putExtra("FAB_TRIGGER_NEWS",true);
                    is_fab_home_clicked=false;
                    ReverseFABAnim();
                    startActivity(intent_temp);
                }
            });

            shadow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    is_fab_home_clicked=false;
                    ReverseFABAnim();
                }
            });
            shadow_fab_event.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    is_fab_home_clicked=false;
                    ReverseFABAnim();
                }
            });
            shadow_fab_news.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    is_fab_home_clicked=false;
                    ReverseFABAnim();
                }
            });*/

            current_date = Calendar.getInstance();

            //Setting date limit for scoreboards
//            if (SharedpreferenceUtility.getInstance(context).getString(AppConstants.COLLEGE_ID).equals("4847453903781888")){
//                kmc_score_container.setVisibility(View.GONE);
//                if(current_date.get(Calendar.DATE)>=3 && current_date.get(Calendar.DATE)<=6 && current_date.get(Calendar.MONTH)==2 && current_date.get(Calendar.YEAR)==2016){
//                    slamdunk_title.setVisibility(View.VISIBLE);
//                }
//            }else if(SharedpreferenceUtility.getInstance(context).getString(AppConstants.COLLEGE_ID).equals("5749882031702016")){
//                slamdunk_title.setVisibility(View.GONE);
//                if(((current_date.get(Calendar.MONTH)==2)||((current_date.get(Calendar.DATE)<=15)&&current_date.get(Calendar.MONTH)==3))&&(current_date.get(Calendar.YEAR)==2016)){
//                    kmc_score_container.setVisibility(View.VISIBLE);
//                }
//            }

            slamdunk_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent_temp = new Intent(v.getContext(), SlamDunkScorecardActivity.class);
                    startActivity(intent_temp);
                    getActivity().overridePendingTransition(R.anim.fadein_scorecard, 0);
                }
            });

            pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {

                    if(position==0) {
                        fab_home.setVisibility(View.GONE);
                        slamdunk_title.setVisibility(View.GONE);
                        kmc_score_container.setVisibility(View.GONE);

                    }
                    else {
                        fab_home.setVisibility(View.VISIBLE);
                        if (position == 1 || position ==2) {

                            //Setting date limit for scoreboards
                            if (SharedpreferenceUtility.getInstance(context).getString(AppConstants.COLLEGE_ID).equals("4847453903781888")){
                                kmc_score_container.setVisibility(View.GONE);
                                if(current_date.get(Calendar.DATE)>=3 && current_date.get(Calendar.DATE)<=6 && current_date.get(Calendar.MONTH)==2 && current_date.get(Calendar.YEAR)==2016){
                                    slamdunk_title.setVisibility(View.VISIBLE);
                                }
                            }else if(SharedpreferenceUtility.getInstance(context).getString(AppConstants.COLLEGE_ID).equals("5749882031702016")){
                                slamdunk_title.setVisibility(View.GONE);
                                if(((current_date.get(Calendar.MONTH)==2)||((current_date.get(Calendar.DATE)<=15)&&current_date.get(Calendar.MONTH)==3))&&(current_date.get(Calendar.YEAR)==2016)){
                                    kmc_score_container.setVisibility(View.VISIBLE);
                                }
                            }
                            fab_home.setImageResource(R.drawable.plus_post);
                            fab_home.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent_temp = new Intent(v.getContext(), CreatePostActivity.class);
                                    startActivity(intent_temp);
                                }
                            });
                        } else if(position==3) {
                            slamdunk_title.setVisibility(View.GONE);
                            kmc_score_container.setVisibility(View.GONE);
                            fab_home.setImageResource(R.drawable.create_group);
                            fab_home.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent_temp = new Intent(v.getContext(), CreateGroupActivity.class);
                                    startActivity(intent_temp);
                                }
                            });
                        }
                        else{

                        }
                    }

                }

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });

        } catch (InflateException e) {
            e.printStackTrace();
        }


        return mRootView;
    }





    private Callback<ModelsKMCParent> callBack = new Callback<ModelsKMCParent>() {
        @Override
        public void onResponse(Response<ModelsKMCParent> response, Retrofit retrofit) {

            if (response != null) {
                if (response.isSuccess()) {
                    ModelsKMCParent modelsKMCParent = response.body();
                    List<ModelsKMC> kmcScores = modelsKMCParent.getItems();



                    String sc_2015 = "", sc_2014 = "", sc_2013 = "", sc_2012 = "", sc_pgi = "", sc_ahs = "";

                    for (int i = 0; i < kmcScores.size(); i++) {
                        switch (kmcScores.get(i).getBatch()) {
                            case "1":
                                sc_2015 = kmcScores.get(i).getScore();
                                SharedpreferenceUtility.getInstance(getActivity()).putString("sc_2015",sc_2015);
                                break;
                            case "2":
                                sc_2014 = kmcScores.get(i).getScore();
                                SharedpreferenceUtility.getInstance(getActivity()).putString("sc_2014",sc_2014);
                                break;
                            case "3":
                                sc_2013 = kmcScores.get(i).getScore();
                                SharedpreferenceUtility.getInstance(getActivity()).putString("sc_2013",sc_2013);
                                break;
                            case "4":
                                sc_2012 = kmcScores.get(i).getScore();
                                SharedpreferenceUtility.getInstance(getActivity()).putString("sc_2012",sc_2012);
                                break;
                            case "5":
                                sc_pgi = kmcScores.get(i).getScore();
                                SharedpreferenceUtility.getInstance(getActivity()).putString("sc_pgi",sc_pgi);
                                break;
                            case "6":
                                sc_ahs = kmcScores.get(i).getScore();
                                SharedpreferenceUtility.getInstance(getActivity()).putString("sc_ahs",sc_ahs);
                                break;
                        }
                    }

                    scoreboardflipAnim(score_2015, new Integer(sc_2015));
                    final String finalSc_201 = sc_2014;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scoreboardflipAnim(score_2014, new Integer(finalSc_201));
                        }
                    }, 200);
                    final String finalSc_2011 = sc_2013;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scoreboardflipAnim(score_2013, new Integer(finalSc_2011));
                        }
                    }, 400);
                    final String finalSc_2012 = sc_2012;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scoreboardflipAnim(score_2012, new Integer(finalSc_2012));
                        }
                    }, 600);
                    final String finalSc_pgi = sc_pgi;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scoreboardflipAnim(score_PGI, new Integer(finalSc_pgi));
                        }
                    }, 800);
                    final String finalSc_ahs = sc_ahs;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scoreboardflipAnim(score_AHS, new Integer(finalSc_ahs));
                        }
                    }, 1000);


                } else {
                    com.squareup.okhttp.Response rawResponse = response.raw();
                    if (rawResponse != null) {

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

    public void FABAnim() {

        fab_news_container.setVisibility(View.VISIBLE);
        fab_event_container.setVisibility(View.VISIBLE);
        shadow.setVisibility(View.VISIBLE);
        shadow_fab_event.setVisibility(View.VISIBLE);
        shadow_fab_news.setVisibility(View.VISIBLE);

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(800);
        fadeIn.setFillAfter(true);
        shadow.startAnimation(fadeIn);

        AnimationSet animSet_fab_event_appear = new AnimationSet(false);
        Animation fadeIn_event = new AlphaAnimation(0, 1);
        fadeIn_event.setDuration(600);
        fadeIn.setFillAfter(true);
        TranslateAnimation fab_event_appear = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0.0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0f,
                TranslateAnimation.RELATIVE_TO_SELF, 2.8f,
                TranslateAnimation.RELATIVE_TO_SELF, 0.0f);
        fab_event_appear.setDuration(600);
        fab_event_appear.setInterpolator(new AccelerateInterpolator());
        fab_event_appear.setFillAfter(true);
        animSet_fab_event_appear.addAnimation(fab_event_appear);
        animSet_fab_event_appear.addAnimation(fadeIn_event);
        fab_event_container.startAnimation(animSet_fab_event_appear);

        AnimationSet animSet_fab_news_appear = new AnimationSet(false);
        Animation fadeIn_news = new AlphaAnimation(0, 1);
        fadeIn_news.setDuration(600);
        fadeIn.setFillAfter(true);
        TranslateAnimation fab_news_appear = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0.0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0f,
                TranslateAnimation.RELATIVE_TO_SELF, 1.5f,
                TranslateAnimation.RELATIVE_TO_SELF, 0f);
        fab_news_appear.setDuration(600);
        fab_news_appear.setStartOffset(150);
        fadeIn_news.setStartOffset(150);
        animSet_fab_news_appear.addAnimation(fab_news_appear);
        animSet_fab_news_appear.addAnimation(fadeIn_news);
        fab_news_appear.setInterpolator(new AccelerateInterpolator());
        fab_news_appear.setFillAfter(true);
        fab_news_container.startAnimation(animSet_fab_news_appear);
    }

    public void ReverseFABAnim() {

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setDuration(800);
        shadow.startAnimation(fadeOut);
        shadow.setVisibility(View.GONE);

        AnimationSet animSet_fab_event_disappear = new AnimationSet(false);
        Animation fadeOut_event = new AlphaAnimation(1, 0);
        fadeOut_event.setDuration(500);
        TranslateAnimation fab_event_disappear = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0.0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0.0f,
                TranslateAnimation.RELATIVE_TO_SELF, 2.8f);
        fab_event_disappear.setDuration(600);
        fab_event_disappear.setStartOffset(150);
        fadeOut_event.setStartOffset(150);
        animSet_fab_event_disappear.addAnimation(fab_event_disappear);
        animSet_fab_event_disappear.addAnimation(fadeOut_event);
        fab_event_disappear.setInterpolator(new DecelerateInterpolator());
        fab_event_container.startAnimation(animSet_fab_event_disappear);
        fab_event_container.setVisibility(View.GONE);

        AnimationSet animSet_fab_news_disappear = new AnimationSet(false);
        Animation fadeOut_news = new AlphaAnimation(1, 0);
        fadeOut_news.setDuration(500);
        TranslateAnimation fab_news_disappear = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0.0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0.0f,
                TranslateAnimation.RELATIVE_TO_SELF, 1.5f);
        fab_news_disappear.setDuration(600);
        animSet_fab_news_disappear.addAnimation(fab_news_disappear);
        animSet_fab_news_disappear.addAnimation(fadeOut_news);
        fab_news_disappear.setInterpolator(new DecelerateInterpolator());
        fab_news_container.startAnimation(animSet_fab_news_disappear);
        fab_news_container.setVisibility(View.GONE);

        shadow_fab_event.setVisibility(View.GONE);
        shadow_fab_news.setVisibility(View.GONE);
    }

    public void scoreboardflipAnim(final TextView v, final int new_score) {
        ObjectAnimator animRotate = ObjectAnimator.ofFloat(v, "rotationX", 0.0f, -90f);
        animRotate.setDuration(1400);
        animRotate.setInterpolator(new AccelerateInterpolator());
        animRotate.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                v.setText(Integer.toString(new_score));
            }
        }, 1200);

        ObjectAnimator animRotate_appear = ObjectAnimator.ofFloat(v, "rotationX", 90.0f, 0.0f);
        animRotate_appear.setStartDelay(1200);
        animRotate_appear.setDuration(1400);
        animRotate_appear.setInterpolator(new DecelerateInterpolator());
        animRotate_appear.start();
    }

    private PopupWindow popupDisplay() {

        final PopupWindow popupWindow = new PopupWindow(context);

        // inflate your layout or dynamically add view
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.menu_layout, null);


        String[] menu_item = {
                "Lite mode",
                "FAQ",
                "Feedback",
                "Rate Us",
                "Log out"
        };
        ListView menu_list = (ListView) view.findViewById(R.id.lv_menu);
        final MenuListAdapter adapter = new MenuListAdapter(getActivity(), menu_item);

        menu_list.setAdapter(adapter);

        menu_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                if (position == 0) {

                    if (SharedpreferenceUtility.getInstance(getActivity()).getBoolean(AppConstants.IS_LITE_CHECKED)) {
                        SharedpreferenceUtility.getInstance(getActivity()).putBoolean(AppConstants.IS_LITE, Boolean.TRUE);
                        if (pager.getCurrentItem() == 0 || pager.getCurrentItem() == 1) {
                            applyBlur();
                            Animation fadeIn = new AlphaAnimation(0, 1);
                            fadeIn.setDuration(800);
                            blur_rect.setVisibility(View.VISIBLE);
                            blur_rect.startAnimation(fadeIn);
                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                college_feed.setAdapter(mCollegefeed);
                                personal_feed.setAdapter(mMyFeed);
                                if (pager.getCurrentItem() == 0 || pager.getCurrentItem() == 1) {
                                    applyBlur();
                                    Animation fadeOut = new AlphaAnimation(1, 0);
                                    fadeOut.setDuration(800);
                                    fadeOut.setInterpolator(new AccelerateInterpolator());
                                    blur_rect.startAnimation(fadeOut);
                                    blur_rect.setVisibility(View.GONE);
                                }
                            }
                        }, 800);
                    } else {
                        SharedpreferenceUtility.getInstance(getActivity()).putBoolean(AppConstants.IS_LITE, Boolean.FALSE);
                        if (pager.getCurrentItem() == 0 || pager.getCurrentItem() == 1) {
                            applyBlur();
                            Animation fadeIn = new AlphaAnimation(0, 1);
                            fadeIn.setDuration(800);
                            blur_rect.setVisibility(View.VISIBLE);
                            blur_rect.startAnimation(fadeIn);
                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                college_feed.setAdapter(mCollegefeed);
                                personal_feed.setAdapter(mMyFeed);
                                if (pager.getCurrentItem() == 0 || pager.getCurrentItem() == 1) {
                                    applyBlur();
                                    Animation fadeOut = new AlphaAnimation(1, 0);
                                    fadeOut.setDuration(800);
                                    fadeOut.setInterpolator(new AccelerateInterpolator());
                                    blur_rect.startAnimation(fadeOut);
                                    blur_rect.setVisibility(View.GONE);
                                }
                            }
                        }, 800);
                    }
                } else if (position == 1) {
                    Intent intent_temp = new Intent(view.getContext(), FAQActivity.class);
                    startActivity(intent_temp);
                } else if (position == 2) {
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    String email = "contact@campusconnect.cc";
                    sendIntent.setType("plain/text");
                    sendIntent.setData(Uri.parse(email));
                    sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
                    sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                    sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback about CampusConnect");
                    getContext().startActivity(sendIntent);
                } else if (position == 3) {
                    Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);

                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                            Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    try {
                        startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
                    }
                }
                //logout button
                else if (position == 4) {
                    if (mGoogleApiClient.isConnected()) {
                        Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                        mGoogleApiClient.disconnect();
                        //mGoogleApiClient.connect();
                        // updateUI(false);
                        System.err.println("LOG OUT ^^^^^^^^^^^^^^^^^^^^ SUCESS");
                    }
                    DatabaseHandler db = new DatabaseHandler(getContext());
                    db.clearClub();

//                    SQLiteDatabase as = ActiveAndroid.getDatabase();
//                    List<String> tables = new ArrayList<>();
//                    Cursor cursor = as.rawQuery("SELECT * FROM sqlite_master WHERE type='table';", null);
//                    cursor.moveToFirst();
//                    while (!cursor.isAfterLast()) {
//                        String tableName = cursor.getString(1);
//                        if (!tableName.equals("android_metadata") &&
//                                !tableName.equals("sqlite_sequence")) {
//                            tables.add(tableName);
//                        }
//                        cursor.moveToNext();
//                    }
//                    cursor.close();
//                    for (String tableName : tables) {
//                        as.execSQL("DELETE FROM " + tableName);
//                    }


                    SharedpreferenceUtility.getInstance(getActivity()).clearSharedPreference();
                    SharedpreferenceUtility.getInstance(getActivity()).putBoolean(AppConstants.LOG_IN_STATUS, Boolean.FALSE);
                    Intent intent_temp = new Intent(getContext(), FlashActivity.class);
                    getContext().startActivity(intent_temp);
                    getActivity().finish();
                }

            }
        });


        float wt_px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160, getResources().getDisplayMetrics());
        popupWindow.setWidth((int) wt_px);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.rgb(160, 160, 160)));

        popupWindow.setContentView(view);


        return popupWindow;
    }

    private void applyBlur() {
        pager.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        pager.getViewTreeObserver().removeOnPreDrawListener(
                                this);
                        pager.buildDrawingCache();

                        Bitmap bmp = pager.getDrawingCache();
                        blur(bmp, blur_rect);
                        return true;
                    }
                });
    }

    private void blur(Bitmap bkg, ImageView view) {
        long startMs = System.currentTimeMillis();
        float scaleFactor;
        float radius;

        scaleFactor = 10;
        radius = 10;

        Bitmap overlay = Bitmap.createBitmap(
                (int) (view.getMeasuredWidth() / scaleFactor),
                (int) (view.getMeasuredHeight() / scaleFactor),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft() / scaleFactor, -view.getTop()
                / scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);

        overlay = FastBlur.doBlur(overlay, (int) radius, true);

        view.setImageDrawable(new BitmapDrawable(getResources(), overlay));

    }


    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first


    }

//    void anim_create_post(final Context ct) {
//        if (to_create_post_flag == false) {
//            final AnimationSet animSet = new AnimationSet(false);
//
//            Animation zoom = new ScaleAnimation(1, 0.7f, 1, 0.7f, 0.5f, 0.5f);
//
//            Animation fadeOut = new AlphaAnimation(1, 0);
//
//            zoom.setDuration(600);
//            fadeOut.setDuration(600);
//
//
//            zoom.setInterpolator(new AccelerateInterpolator());
//            fadeOut.setInterpolator(new AccelerateInterpolator());
//
//            animSet.addAnimation(zoom);
//            animSet.addAnimation(fadeOut);
//            animSet.setDuration(800);
//            animSet.setFillAfter(true);
//            i_add_post.startAnimation(animSet);
//
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    AnimationSet animSet_disappear_icons = new AnimationSet(false);
//                    TranslateAnimation tanim = new TranslateAnimation(
//                            TranslateAnimation.RELATIVE_TO_SELF, 0.0f,
//                            TranslateAnimation.RELATIVE_TO_SELF, -1f,
//                            TranslateAnimation.RELATIVE_TO_SELF, 0f,
//                            TranslateAnimation.RELATIVE_TO_SELF, 0f);
//                    Animation fadeOut = new AlphaAnimation(1, 0);
//                    fadeOut.setDuration(400);
//                    tanim.setDuration(400);
//                    tanim.setInterpolator(new AccelerateInterpolator());
//                    fadeOut.setInterpolator(new AccelerateInterpolator());
//                    animSet_disappear_icons.addAnimation(tanim);
//                    animSet_disappear_icons.addAnimation(fadeOut);
//                    animSet_disappear_icons.setFillAfter(true);
//                    i_icon.startAnimation(animSet_disappear_icons);
//                    settings.startAnimation(animSet_disappear_icons);
//                }
//            }, 370/* 1sec delay */);
//
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    AnimationSet animSet_disappear_home_tabs = new AnimationSet(false);
//                    TranslateAnimation tanim = new TranslateAnimation(
//                            TranslateAnimation.RELATIVE_TO_SELF, 0.0f,
//                            TranslateAnimation.RELATIVE_TO_SELF, 0.0f,
//                            TranslateAnimation.RELATIVE_TO_SELF, 0f,
//                            TranslateAnimation.RELATIVE_TO_SELF, -1f);
//                    Animation fadeOut = new AlphaAnimation(1, 0);
//                    fadeOut.setDuration(1000);
//                    tanim.setDuration(1000);
//                    animSet_disappear_home_tabs.addAnimation(tanim);
//                    animSet_disappear_home_tabs.addAnimation(fadeOut);
//                    animSet_disappear_home_tabs.setInterpolator(new AccelerateDecelerateInterpolator());
//                    animSet_disappear_home_tabs.setFillAfter(true);
//                    tabs.startAnimation(animSet_disappear_home_tabs);
//                }
//            }, 1000/* 1sec delay */);
//
//            to_create_post_flag = true;
//        } else if (to_create_post_flag == true) {
//
//            to_create_post_flag = false;
//
//
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//
//                }
//            }, 600/* 1sec delay */);
//
//
//        }
//
//    }

    private PopupWindow popupWindowsort() {

        // initialize a pop up window type
        popupWindow = new PopupWindow(context);

        ArrayList<String> sortList = new ArrayList<String>();
        sortList.add("Lite Mode");
        sortList.add("Feedback");
        sortList.add("Rate Us");
        sortList.add("Log out");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.drop_down_text,
                sortList);
        // the drop down list is a list view
        ListView listViewSort = new ListView(context);
        listViewSort.setBackgroundColor(Color.WHITE);

        ColorDrawable divider_color = new ColorDrawable(ContextCompat.getColor(context, R.color.dividerColor));
        listViewSort.setDivider(divider_color);
        listViewSort.setDividerHeight(1);

        // set our adapter and pass our pop up window contents
        listViewSort.setAdapter(adapter);

        // set on item selected
        listViewSort.setOnItemClickListener(onItemClickListener());

        // some other visual settings for popup window
        float wt_px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160, getResources().getDisplayMetrics());
        popupWindow.setFocusable(true);
        popupWindow.setWidth((int) wt_px);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        this.popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        // set the list view as pop up window content
        popupWindow.setContentView(listViewSort);
        return popupWindow;
    }

    private AdapterView.OnItemClickListener onItemClickListener() {
        return new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                //feedback
                if (position == 0) {
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    String email = "contact@campusconnect.cc";
                    sendIntent.setType("plain/text");
                    sendIntent.setData(Uri.parse(email));
                    sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
                    sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                    sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback about CampusConnect");
                    getContext().startActivity(sendIntent);
                } else if (position == 1) {
                    Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);

                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                            Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    try {
                        startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
                    }
                }
                //logout button
                else if (position == 2) {
                    if (mGoogleApiClient.isConnected()) {
                        Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                        mGoogleApiClient.disconnect();
                        //mGoogleApiClient.connect();
                        // updateUI(false);
                        System.err.println("LOG OUT ^^^^^^^^^^^^^^^^^^^^ SUCESS");
                    }
                    DatabaseHandler db = new DatabaseHandler(getContext());
                    db.clearClub();
                    SharedpreferenceUtility.getInstance(getActivity()).clearSharedPreference();
                    SharedpreferenceUtility.getInstance(getActivity()).putBoolean(AppConstants.LOG_IN_STATUS, Boolean.FALSE);
                    Intent intent_temp = new Intent(getContext(), FlashActivity.class);
                    getContext().startActivity(intent_temp);
                    getActivity().finish();
                }
                dismissPopup();
            }

        };
    }

    private void dismissPopup() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        mSignInClicked = false;

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void rvPassfromCampusFeed(RecyclerView collegeFeedRecyclerView, CollegeCampusFeedAdapter mfeedAdapter) {
        college_feed = collegeFeedRecyclerView;
        mCollegefeed = mfeedAdapter;
    }

    @Override
    public void rvPassfromMyFeed(RecyclerView personalFeedRecyclerView, CollegeCampusFeedAdapter mfeedAdapter) {
        personal_feed = personalFeedRecyclerView;
        mMyFeed = mfeedAdapter;
    }

    @Override
    public void passFromLiveFeed(RecyclerView personalFeedRecyclerView, LiveAdapter mLiveFeed) {
        live_feed = personalFeedRecyclerView;
        mLiveFeed = mLiveFeed;
    }


    public class ViewPagerAdapter_home extends FragmentPagerAdapter {


        CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter_home is created
        int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter_home is created

        private Context mContext;


        // Build a Constructor and assign the passed Values to appropriate values in the class
        public ViewPagerAdapter_home(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb, Context context) {
            super(fm);
            this.Titles = mTitles;
            this.NumbOfTabs = mNumbOfTabsumb;
            this.mContext = context;
        }

        // region Callbacks
        private Callback<ModelsAdminStatus> adminStatusCallBack = new Callback<ModelsAdminStatus>() {
            @Override
            public void onResponse(Response<ModelsAdminStatus> response, Retrofit retrofit) {

//            Log.d("findVid", response.body().toString());
                if (response != null) {
                    if (response.isSuccess()) {
                        ModelsAdminStatus modelsAdminStatus = response.body();
                        if (modelsAdminStatus.getIsAdmin().equals("Y")) {
                            SharedpreferenceUtility.getInstance(getActivity()).putBoolean(AppConstants.IS_ADMIN, true);
                            admin.setVisibility(View.VISIBLE);
                            admin.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent_temp = new Intent(v.getContext(), SelectAdminClub.class);
                                    startActivity(intent_temp);
                                }
                            });
                        } else {
                            SharedpreferenceUtility.getInstance(getActivity()).putBoolean(AppConstants.IS_ADMIN, false);
                            admin.setVisibility(View.GONE);
                            admin.setClickable(false);
                        }

                        if (modelsAdminStatus.getIsSuperAdmin().equals("Y")) {
                            SharedpreferenceUtility.getInstance(getActivity()).putBoolean(AppConstants.IS_SUP_ADMIN, true);
                            super_admin.setVisibility(View.VISIBLE);
                            super_admin.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent_temp = new Intent(v.getContext(), RequestsSuperAdmin.class);
                                    startActivity(intent_temp);
                                }
                            });
                        } else {
                            SharedpreferenceUtility.getInstance(getActivity()).putBoolean(AppConstants.IS_SUP_ADMIN, false);
                            super_admin.setVisibility(View.GONE);
                            super_admin.setClickable(false);
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

        private Callback<ModelsUpdateStatus> updateStatusCallBack = new Callback<ModelsUpdateStatus>() {
            @Override
            public void onResponse(Response<ModelsUpdateStatus> response, Retrofit retrofit) {

//            Log.d("findVid", response.body().toString());
                if (response != null) {
                    if (response.isSuccess()) {
                        ModelsUpdateStatus modelsUpdateStatus = response.body();
                        String latestVersionNumber = modelsUpdateStatus.getUpdate();
                        String message = modelsUpdateStatus.getMessage();
                        try {
                            Integer lvn = new Integer(latestVersionNumber);
                            if (lvn > AppConstants.APP_VERSION) {
                                UpdateDialog updateDialog = new UpdateDialog((Activity) mContext,message);
                                Window window = updateDialog.getWindow();
                                window.setLayout(450, ViewGroup.LayoutParams.WRAP_CONTENT);
                                updateDialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ;

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


        public void setupAdminButton() {

            CCWebService mCCService = ServiceGenerator.createService(
                    CCWebService.class,
                    CCWebService.BASE_URL);

            if (SharedpreferenceUtility.getInstance(getContext()).getInt("AdminStatus") % 2 == 0) {


                String pid = SharedpreferenceUtility.getInstance(getContext()).getString(AppConstants.PERSON_PID);

                Call findAdminStatusCall = mCCService.adminStatus(pid);
                mCalls.add(findAdminStatusCall);
                findAdminStatusCall.enqueue(adminStatusCallBack);

                Call findUpdateStatusCall = mCCService.getUpdateStatus();
                mCalls.add(findUpdateStatusCall);
                findUpdateStatusCall.enqueue(updateStatusCallBack);


                //if collegeId is KMC collegeId
                String collegeId=SharedpreferenceUtility.getInstance(getContext()).getString(AppConstants.COLLEGE_ID);
                if(collegeId.equals("5749882031702016")) {
                    Call getKMCScores = mCCService.getKMCScoreBoard();
                    getKMCScores.enqueue(callBack);
                }else{
                    kmc_score_container.setVisibility(View.GONE);
                }
                SharedpreferenceUtility.getInstance(getContext()).putInt("AdminStatus", 1);
            } else {
                if (SharedpreferenceUtility.getInstance(getActivity()).getBoolean(AppConstants.IS_ADMIN)) {
                    admin.setVisibility(View.VISIBLE);
                    admin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent_temp = new Intent(v.getContext(), SelectAdminClub.class);
                            startActivity(intent_temp);
                        }
                    });
                } else {
                    SharedpreferenceUtility.getInstance(getActivity()).putBoolean(AppConstants.IS_ADMIN, false);
                    admin.setVisibility(View.GONE);
                    admin.setClickable(false);
                }

                if (SharedpreferenceUtility.getInstance(getActivity()).getBoolean(AppConstants.IS_SUP_ADMIN)) {
                    //SharedpreferenceUtility.getInstance(getActivity()).putBoolean(AppConstants.IS_SUP_ADMIN,true);
                    super_admin.setVisibility(View.VISIBLE);
                    super_admin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent_temp = new Intent(v.getContext(), RequestsSuperAdmin.class);
                            startActivity(intent_temp);
                        }
                    });
                } else {
                    SharedpreferenceUtility.getInstance(getActivity()).putBoolean(AppConstants.IS_SUP_ADMIN, false);

                    super_admin.setVisibility(View.GONE);
                    super_admin.setClickable(false);
                }

                String collegeId=SharedpreferenceUtility.getInstance(getActivity()).getString(AppConstants.COLLEGE_ID);
                if(collegeId.equals("5749882031702016")) {

                    String sc_2015=SharedpreferenceUtility.getInstance(getActivity()).getString("sc_2015");
                    String sc_2014=SharedpreferenceUtility.getInstance(getActivity()).getString("sc_2014");
                    String sc_2013=SharedpreferenceUtility.getInstance(getActivity()).getString("sc_2013");
                    String sc_2012=SharedpreferenceUtility.getInstance(getActivity()).getString("sc_2012");
                    String sc_pgi=SharedpreferenceUtility.getInstance(getActivity()).getString("sc_pgi");
                    String sc_ahs=SharedpreferenceUtility.getInstance(getActivity()).getString("sc_ahs");

                try {
                    scoreboardflipAnim(score_2015, new Integer(sc_2015));
                    final String finalSc_201 = sc_2014;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scoreboardflipAnim(score_2014, new Integer(finalSc_201));
                        }
                    }, 200);
                    final String finalSc_2011 = sc_2013;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scoreboardflipAnim(score_2013, new Integer(finalSc_2011));
                        }
                    }, 400);
                    final String finalSc_2012 = sc_2012;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scoreboardflipAnim(score_2012, new Integer(finalSc_2012));
                        }
                    }, 600);
                    final String finalSc_pgi = sc_pgi;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scoreboardflipAnim(score_PGI, new Integer(finalSc_pgi));
                        }
                    }, 800);
                    final String finalSc_ahs = sc_ahs;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scoreboardflipAnim(score_AHS, new Integer(finalSc_ahs));
                        }
                    }, 1000);
                }catch(NumberFormatException ne){
                    Call getKMCScores = mCCService.getKMCScoreBoard();
                    getKMCScores.enqueue(callBack);
                }

                }else{
                    kmc_score_container.setVisibility(View.GONE);
                }

            }
        }

        //This method return the fragment for the every position in the View Pager
        @Override
        public Fragment getItem(int position) {

            setupAdminButton();

            Fragment fragment = null;
            if (position == 0) {
                fragment = new FragmentLive();
            } else if (position == 1) {
                if (MainActivity.isLaunch) {
                }
                fragment = new CampusFeedFragment();
            } else if (position == 2) {
                //WebApiGetGroups();
                fragment = new MyFeedFragment();
            }else if (position == 3) {
                //WebApiGetGroups();
                fragment = new GroupsFragment();
            }
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return Titles[position];
        }


        @Override
        public int getCount() {
            return NumbOfTabs;
        }


    }


    public class PopupDialog extends Dialog {

        public Activity c;
        public Dialog d;
        Context context;


        public PopupDialog(Activity a) {
            super(a);
            // TODO Auto-generated constructor stub
            this.c = a;
            this.context = context;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.attending_dialog_box);


        }

    }

    public class UpdateDialog extends Dialog implements
            View.OnClickListener {

        public Activity c;
        public Dialog d;
        public TextView update;
        public TextView later;

        private TextView tv_dialog_info;
        Context context;
        String msg;

        public UpdateDialog(Activity a,String msg) {
            super(a);
            // TODO Auto-generated constructor stub
            this.c = a;
            this.context = context;
            this.msg = msg;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.update_confirmation_dialog);

            update = (TextView) findViewById(R.id.btn_update);
            later = (TextView) findViewById(R.id.btn_later);
            tv_dialog_info = (TextView) findViewById(R.id.tv_dialog_info);

            tv_dialog_info.setText(this.msg);

            update.setOnClickListener(this);
            later.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_update: {
                    Uri uri = Uri.parse("market://details?id=" + v.getContext().getPackageName());
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    // To count with Play market backstack, After pressing back button,
                    // to taken back to our application, we need to add following flags to intent.
                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                            Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    try {
                        startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id=" + v.getContext().getPackageName())));
                    }
                    dismiss();
                    break;
                }
                case R.id.btn_later:
                    dismiss();
                    break;
                default:
                    break;
            }
        }
    }

    public class UsePhotoFilterDialog extends Dialog implements
            View.OnClickListener {

        public Activity c;
        public Dialog d;
        public TextView okay;
        Context context;


        public UsePhotoFilterDialog(Activity a) {
            super(a);
            // TODO Auto-generated constructor stub
            this.c = a;
            this.context = context;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.photo_filter_helper_dialog);

            okay=(TextView)findViewById(R.id.btn_okay);

            okay.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_okay: {
                    dismiss();
                    break;
                }
                default:
                    break;
            }
        }

    }



}