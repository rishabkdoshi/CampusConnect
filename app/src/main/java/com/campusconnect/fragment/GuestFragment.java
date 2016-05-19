package com.campusconnect.fragment;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.campusconnect.R;
import com.campusconnect.activity.DirectoryExpandabeListViewActivity;
import com.campusconnect.activity.FAQActivity;
import com.campusconnect.activity.FlashActivity;
import com.campusconnect.activity.MainActivity;
import com.campusconnect.activity.SlamDunkScorecardActivity;
import com.campusconnect.adapter.CollegeCampusFeedAdapter;
import com.campusconnect.adapter.LiveAdapter;
import com.campusconnect.adapter.MenuListAdapter;
import com.campusconnect.constant.AppConstants;
import com.campusconnect.database.DatabaseHandler;
import com.campusconnect.slidingtab.SlidingTabLayout_home;
import com.campusconnect.utility.FastBlur;
import com.campusconnect.utility.SharedpreferenceUtility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by RK on 28/02/2016.
 */
public class GuestFragment extends BaseFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, IncidentFragment.RVPass, FragmentLive.RVPass {

    View setting;
    Context context;
    private PopupWindow popupWindow;
    View mRootView;
    //CollegeMyFeedAdapter cf;//change to campusfeed
    RecyclerView college_feed;
    CollegeCampusFeedAdapter mCollegefeed;
    LiveAdapter mLiveFeed;
    RecyclerView live_feed;

    //new
    ImageView blur_rect;
    TextView slamdunk_title;
    Calendar current_date;

    FrameLayout tabs_container;
    LinearLayout toolbar;
    public static LinearLayout settings;

    ImageButton i_settings, i_icon;
    ViewPager pager;

    //Directory related definitions
    RelativeLayout directory_container;
    ImageButton directory;

    ViewPagerAdapter_home adapter;
    public static SlidingTabLayout_home tabs;
    CharSequence Titles[] = {"Live", "Incident"};
    int Numboftabs = 2;

    GoogleApiClient mGoogleApiClient;
    boolean mSignInClicked;

    public GuestFragment() {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView != null) {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (parent != null)
                parent.removeView(mRootView);
        }
        try {
            mRootView = inflater.inflate(R.layout.activity_home_guest, container, false);
            context = mRootView.getContext();

            settings = (LinearLayout) mRootView.findViewById(R.id.settings);
            toolbar = (LinearLayout) mRootView.findViewById(R.id.custom_toolbar);
            tabs_container = (FrameLayout) mRootView.findViewById(R.id.tabs_container);
            blur_rect = (ImageView) mRootView.findViewById(R.id.blur);
            slamdunk_title = (TextView) mRootView.findViewById(R.id.tv_slamdunk_scoreboard_title);

            i_settings = (ImageButton) mRootView.findViewById(R.id.ib_settings);
            i_icon = (ImageButton) mRootView.findViewById(R.id.ib_icon);

            //Directory related links
            directory_container = (RelativeLayout) mRootView.findViewById(R.id.directory_container);
            directory = (ImageButton) mRootView.findViewById(R.id.ib_directory);

            Typeface scoreboard = Typeface.createFromAsset(getContext().getAssets(), "font/Scoreboard.ttf");
            slamdunk_title.setTypeface(scoreboard);

            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).addApi(Plus.API)
                    .addScope(Plus.SCOPE_PLUS_LOGIN).build();

            pager = (ViewPager) mRootView.findViewById(R.id.pager);
            tabs = (SlidingTabLayout_home) mRootView.findViewById(R.id.tabs);
            adapter = new ViewPagerAdapter_home(getChildFragmentManager(), Titles, Numboftabs, getActivity());
            pager.setAdapter(adapter);
            pager.setCurrentItem(1);
            tabs.setDistributeEvenly(true);
            tabs.setViewPager(pager);


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

            slamdunk_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent_temp = new Intent(v.getContext(), SlamDunkScorecardActivity.class);
                    startActivity(intent_temp);
                    getActivity().overridePendingTransition(R.anim.fadein_scorecard, 0);
                }
            });

            current_date = Calendar.getInstance();

            if(current_date.get(Calendar.DATE)>=3 && current_date.get(Calendar.DATE)<=6 && current_date.get(Calendar.MONTH)==2 && current_date.get(Calendar.YEAR)==2016){

                slamdunk_title.setVisibility(View.VISIBLE);
            }

            pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {

                    if(position==0)
                        slamdunk_title.setVisibility(View.GONE);
                    else if(position==1){
                        current_date = Calendar.getInstance();

                        if(current_date.get(Calendar.DATE)>=3 && current_date.get(Calendar.DATE)<=6 && current_date.get(Calendar.MONTH)==2 && current_date.get(Calendar.YEAR)==2016){

                            slamdunk_title.setVisibility(View.VISIBLE);
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
                                if (pager.getCurrentItem() == 1) {
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

                    //SQLiteDatabase as = ActiveAndroid.getDatabase();
                    List<String> tables = new ArrayList<>();
                    //Cursor cursor = as.rawQuery("SELECT * FROM sqlite_master WHERE type='table';", null);
                    /*cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        String tableName = cursor.getString(1);
                        if (!tableName.equals("android_metadata") &&
                                !tableName.equals("sqlite_sequence")) {
                            tables.add(tableName);
                        }
                        cursor.moveToNext();
                    }
                    cursor.close();
                    for (String tableName : tables) {
                        as.execSQL("DELETE FROM " + tableName);
                    }*/


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
    public void rvPassfromIncidentFeed(RecyclerView collegeFeedRecyclerView, CollegeCampusFeedAdapter mfeedAdapter) {
        college_feed = collegeFeedRecyclerView;
        mCollegefeed = mfeedAdapter;
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

        //This method return the fragment for the every position in the View Pager
        @Override
        public Fragment getItem(int position) {

            Fragment fragment = null;
            if (position == 0) {
                fragment = new FragmentLive();
            } else if (position == 1) {
                if (MainActivity.isLaunch) {
                }
                fragment = new IncidentFragment();
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

}
