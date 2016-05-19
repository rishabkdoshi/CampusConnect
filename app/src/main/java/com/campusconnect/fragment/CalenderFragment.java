package com.campusconnect.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.campusconnect.R;
import com.campusconnect.adapter.CalendarAdapter;
import com.campusconnect.communicator.CCWebService;
import com.campusconnect.communicator.ServiceGenerator;
import com.campusconnect.communicator.models.ModelsCollegeFeed;
import com.campusconnect.communicator.models.ModelsFeed;
import com.campusconnect.constant.AppConstants;
import com.campusconnect.utility.LogUtility;
import com.campusconnect.utility.SharedpreferenceUtility;
import com.campusconnect.utility.StickyHeaderRecyclerDecor;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class CalenderFragment extends BaseFragment {
    public static Typeface r_med, r_reg;
    TextView calendar_title;
    LinearLayout toolbar;
    CalendarAdapter adapter;

    RecyclerView calendar;
    View mRootView;
    StickyHeaderRecyclerDecor headersDecor;
    private boolean mIsLoading = false;

    CCWebService mCCService;
    ProgressBar mLoadingImageView;
    Context context;

    TextView mEmptyView;
    @Override
    public void onResume() {
        super.onResume();

        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }

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
            mRootView = inflater.inflate(R.layout.activity_calendar_design_two, container, false);

            calendar_title = (TextView) mRootView.findViewById(R.id.tv_calendar);
            Typeface r_med = Typeface.createFromAsset(mRootView.getContext().getAssets(), "font/Roboto_Medium.ttf");
            calendar_title.setTypeface(r_med);
            mLoadingImageView = (ProgressBar)mRootView.findViewById(R.id.loading_iv);
            toolbar = (LinearLayout) mRootView.findViewById(R.id.custom_toolbar);
            calendar = (RecyclerView) mRootView.findViewById(R.id.rv_calendar);

            mEmptyView=(TextView)mRootView.findViewById(R.id.tv_show_blank);

            context=mRootView.getContext();
            mCCService = ServiceGenerator.createService(
                    CCWebService.class,
                    CCWebService.BASE_URL);
            Date cDate = new Date();
            String date = new SimpleDateFormat("yyyy-MM-dd").format(cDate);

            adapter = new CalendarAdapter(getActivity());
            calendar.setAdapter(adapter);
            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            calendar.setLayoutManager(llm);
            // Add the sticky headers decoration
            headersDecor = new StickyHeaderRecyclerDecor(adapter);
            calendar.addItemDecoration(headersDecor);
            String pid=SharedpreferenceUtility.getInstance(mRootView.getContext()).getString(AppConstants.PERSON_PID);
            String collegeId=SharedpreferenceUtility.getInstance(mRootView.getContext()).getString(AppConstants.COLLEGE_ID);
            Call findCalendar=mCCService.getCalendarEvents(pid,collegeId, date);
            findCalendar.enqueue(findCalendarCallback);


        } catch (InflateException e) {
            e.printStackTrace();
        }
        return mRootView;
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
                        if (feedItems != null || feedItems.size()>0) {
                            adapter.addAll(feedItems);
                            Log.d("aasf","hr");
                        }else{
                            mEmptyView.setVisibility(View.VISIBLE);
                            Log.d("aasf", "fdgsdfg");

                            Toast.makeText(context,"No upcoming events",Toast.LENGTH_LONG);
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


    RecyclerView.AdapterDataObserver dataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            headersDecor.invalidateHeaders();
        }
    };

}
