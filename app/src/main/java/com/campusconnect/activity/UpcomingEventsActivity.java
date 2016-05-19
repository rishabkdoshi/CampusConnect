package com.campusconnect.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.campusconnect.R;
import com.campusconnect.adapter.CalendarAdapter;
import com.campusconnect.adapter.CollegeCampusFeedAdapter;
import com.campusconnect.communicator.CCWebService;
import com.campusconnect.communicator.ServiceGenerator;
import com.campusconnect.communicator.models.ModelsCollegeFeed;
import com.campusconnect.communicator.models.ModelsEventForm;
import com.campusconnect.communicator.models.ModelsEvents;
import com.campusconnect.communicator.models.ModelsFeed;
import com.campusconnect.constant.AppConstants;
import com.campusconnect.utility.LogUtility;
import com.campusconnect.utility.SharedpreferenceUtility;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by RK on 05/11/2015.
 */
public class UpcomingEventsActivity extends BaseActivity {


    @Bind(R.id.rv_upcoming_events)
    RecyclerView upcoming_events;
    @Bind(R.id.cross_button)
    LinearLayout close;
    @Bind(R.id.tv_upcoming_events)
    TextView upcoming_events_text;

    private boolean mIsLoading = false;

    CCWebService mCCService;

    ProgressBar mLoadingImageView;

    Typeface r_med;
    CollegeCampusFeedAdapter adapter;

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_upcoming_events);
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);


//        WebApiGetUpComingEvent(clubId);
        mLoadingImageView = (ProgressBar) findViewById(R.id.loading_iv);


        r_med = Typeface.createFromAsset(getAssets(), "font/Roboto_Medium.ttf");
        upcoming_events_text.setTypeface(r_med);

        upcoming_events.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        upcoming_events.setLayoutManager(llm);
        upcoming_events.setItemAnimator(new DefaultItemAnimator());
        adapter = new CollegeCampusFeedAdapter(this);
        upcoming_events.setAdapter(adapter);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mCCService = ServiceGenerator.createService(
                CCWebService.class,
                CCWebService.BASE_URL);

        String clubId = getIntent().getStringExtra("clubId");
        String pid = SharedpreferenceUtility.getInstance(this).getString(AppConstants.PERSON_PID);
        Call findCalendar = mCCService.getClubEvents(pid, clubId);
        findCalendar.enqueue(findCalendarCallback);

        String frag_location = MainActivity.frag_loc;
        UpdateUi(frag_location);

    }

    private Callback<ModelsCollegeFeed> findCalendarCallback = new Callback<ModelsCollegeFeed>() {
        @Override
        public void onResponse(Response<ModelsCollegeFeed> response, Retrofit retrofit) {
            mLoadingImageView.setVisibility(View.GONE);
            mIsLoading = false;
//            Log.d("findVid", response.body().toString());
            if (response != null) {
                if (response.isSuccess()) {
                    ModelsCollegeFeed modelsCollegeFeed = response.body();
                    if (modelsCollegeFeed != null) {
                        List<ModelsFeed> feedItems = modelsCollegeFeed.getItems();
                        if (feedItems != null || feedItems.size() > 0) {
                            adapter.addAll(feedItems);


                        } else {
//                            mEmptyView.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    com.squareup.okhttp.Response rawResponse = response.raw();
                    if (rawResponse != null) {
                        LogUtility.logFailedResponse(rawResponse);

                        int code = rawResponse.code();
                        switch (code) {
                            case 500:
//                                mErrorTextView.setText("Can't load data.\nCheck your network connection.");
//                                mErrorLinearLayout.setVisibility(View.VISIBLE);
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
            //Timber.r.d("onFailure() : mQuery - " + mQuery);
            if (t != null) {
                String message = t.getMessage();
                //LogUtility.logFailure(t);

                if (t instanceof SocketTimeoutException
                        || t instanceof UnknownHostException
                        || t instanceof SocketException) {
                    //Timber.e("Timeout occurred");
//                    mIsLoading = false;
//                    mLoadingImageView.setVisibility(View.GONE);
//
//                    mErrorTextView.setText("Can't load data.\nCheck your network connection.");
//                    mErrorLinearLayout.setVisibility(View.VISIBLE);
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

}

