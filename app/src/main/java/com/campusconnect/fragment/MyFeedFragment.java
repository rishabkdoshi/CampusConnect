package com.campusconnect.fragment;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

//import com.activeandroid.ActiveAndroid;
//import com.activeandroid.query.Delete;
//import com.activeandroid.query.Select;
import com.campusconnect.R;
import com.campusconnect.activity.FlashActivity;
import com.campusconnect.adapter.CollegeCampusFeedAdapter;
import com.campusconnect.communicator.CCWebService;
import com.campusconnect.communicator.ServiceGenerator;
import com.campusconnect.communicator.models.ModelsClubMiniForm;
import com.campusconnect.communicator.models.ModelsCollegeFeed;
import com.campusconnect.communicator.models.ModelsFeed;
import com.campusconnect.communicator.models.ModelsGetInformation;
import com.campusconnect.constant.AppConstants;
import com.campusconnect.database.DBHandler;
import com.campusconnect.utility.LogUtility;
import com.campusconnect.utility.SharedpreferenceUtility;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import timber.log.Timber;

/**
 * Created by rkd on 20/1/16.
 */
public class MyFeedFragment extends BaseFragment{

    RVPass rvPasser;
    // region Constants
    public static final int PAGE_SIZE = 10;
    // endregion

    // region Member Variables
    @Bind(R.id.rv_college_feed)
    RecyclerView collegeFeedRecyclerView;
    @Bind(R.id.tv_show_blank)
    TextView mEmptyView;
    @Bind(R.id.tv_helper_campus_feed)
    TextView helper_text_feed;
    @Bind(R.id.ib_helper_close)
    ImageButton close;
    @Bind(R.id.helper_text_container)
    LinearLayout helper_campus_feed;
    @Bind(R.id.loading_iv)
    ProgressBar mLoadingImageView;
    @Bind(R.id.error_ll)
    LinearLayout mErrorLinearLayout;
    @Bind(R.id.error_tv)
    TextView mErrorTextView;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private boolean mIsLastPage = false;
    private int mCurrentPage = 1;
    private boolean mIsLoading = false;
    private CollegeCampusFeedAdapter mFeedAdapter;

    private String mQuery;
    private LinearLayoutManager mLayoutManager;
    private CCWebService mCCService;
    // endregion

    DBHandler db;


    // region Listeners
    private RecyclerView.OnScrollListener mRecyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = mLayoutManager.getChildCount();
            int totalItemCount = mLayoutManager.getItemCount();
            int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();

            if (!mIsLoading && !mIsLastPage) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= PAGE_SIZE) {
                    loadMoreItems();
                }
            }
//            else if (mIsLastPage){
//                Toast.makeText(getContext(),"NO MORE DATA",Toast.LENGTH_LONG);
//            }
        }
    };


    private View.OnClickListener mReloadOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCurrentPage = 1;
            mFeedAdapter.clear();
            mLoadingImageView.setVisibility(View.VISIBLE);
            mLoadingImageView.getIndeterminateDrawable().setColorFilter(Color.rgb(250, 209, 86), PorterDuff.Mode.MULTIPLY);

            String collegeId=SharedpreferenceUtility.getInstance(getContext()).getString(AppConstants.COLLEGE_ID);
            String pid=SharedpreferenceUtility.getInstance(getContext()).getString(AppConstants.PERSON_PID);
            String pageNumber=new Integer(mCurrentPage).toString();
            Call findFeedCall = mCCService.myFeed(collegeId,pid,pageNumber);

            mCalls.add(findFeedCall);
            findFeedCall.enqueue(mFindFeedFirstFetchCallback);
        }
    };
    // endregion

    // region Callbacks
    private Callback<ModelsCollegeFeed> mFindFeedFirstFetchCallback = new Callback<ModelsCollegeFeed>() {
        @Override
        public void onResponse(Response<ModelsCollegeFeed> response, Retrofit retrofit) {
            try {
                mLoadingImageView.setVisibility(View.GONE);
            }catch (NullPointerException e){

            }
            mIsLoading = false;
            if (response != null) {
                if (response.isSuccess()) {
                    ModelsCollegeFeed modelsCollegeFeed = response.body();
                    if (modelsCollegeFeed != null) {
                        List<ModelsFeed> feedItems = modelsCollegeFeed.getItems();
                        if (feedItems != null || feedItems.size()>0) {
                            mFeedAdapter.addAll(feedItems);
                            saveToDb(feedItems);

                            if (feedItems.size() >= PAGE_SIZE) {
                                mFeedAdapter.addLoading();
                            }
                            else{
                                mIsLastPage=true;
                            }
                        }else{
                            mEmptyView.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    com.squareup.okhttp.Response rawResponse = response.raw();
                    if (rawResponse != null) {
                        LogUtility.logFailedResponse(rawResponse);

                        int code = rawResponse.code();
                        switch (code) {
                            case 500:
                                mErrorTextView.setText("Can't load data.\nCheck your network connection.");
                                mErrorLinearLayout.setVisibility(View.VISIBLE);
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

    private Callback<ModelsCollegeFeed> mFindFeedNextFetchCallback = new Callback<ModelsCollegeFeed>() {
        @Override
        public void onResponse(Response<ModelsCollegeFeed> response, Retrofit retrofit) {
            mFeedAdapter.removeLoading();
            mIsLoading = false;
            if (response != null) {
                if (response.isSuccess()) {
                    ModelsCollegeFeed modelsCollegeFeed = response.body();
                    if (modelsCollegeFeed != null) {
                        List<ModelsFeed> feedItems = modelsCollegeFeed.getItems();
                        if (feedItems != null) {
                            mFeedAdapter.addAll(feedItems);

                            if(feedItems.size() >= PAGE_SIZE){
                                mFeedAdapter.addLoading();
                            }
                            else{
                                mIsLastPage=true;
                            }
                        }
                    }
                } else {
                    com.squareup.okhttp.Response rawResponse = response.raw();
                    if (rawResponse != null) {
                        LogUtility.logFailedResponse(rawResponse);

                        String message = rawResponse.message();
                        int code = rawResponse.code();

                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                String.format("message - %s : code - %d", message, code),
                                Snackbar.LENGTH_INDEFINITE)
//                                .setAction("Undo", mOnClickListener)
//                                .setActionTextColor(Color.RED)
                                .show();

                        switch (code) {
                            case 500:
                                Timber.e("Display error message in place of load more");
                                mErrorTextView.setText("Can't load data.\nCheck your network connection.");
                                mErrorLinearLayout.setVisibility(View.VISIBLE);
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
            mFeedAdapter.removeLoading();
            if (t != null) {
                String message = t.getMessage();
                //LogUtility.logFailure(t);

                if (t instanceof SocketTimeoutException) {
//                    showReloadSnackbar(String.format("message - %s", message));
                } else if (t instanceof UnknownHostException) {
                    //Timber.e("Timeout occurred");
//                    showReloadSnackbar("Can't load data. Check your network connection.");
                }
            }
        }
    };
    // endregion

    // region Constructors
    public MyFeedFragment() {
    }
    // endregion

    // region Factory Methods
    public static MyFeedFragment newInstance() {
        return new MyFeedFragment();
    }

    public static MyFeedFragment newInstance(Bundle extras) {
        MyFeedFragment fragment = new MyFeedFragment();
        fragment.setArguments(extras);
        return fragment;
    }
    // endregion

    // region Lifecycle Methods

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//
//        if (getArguments() != null) {
//            mQuery = getArguments().getString("query");
//        }
//
//        AccessToken token = PreferencesHelper.getAccessToken(getActivity());
//        mVimeoService = ServiceGenerator.createService(
//                VimeoService.class,
//                VimeoService.BASE_URL,
//                token);

        mCCService = ServiceGenerator.createService(
                CCWebService.class,
                CCWebService.BASE_URL);
//        Log.d("URL BASE",CCWebService.BASE_URL);
        setHasOptionsMenu(true);
        onAttachFragment(getParentFragment());
//        BusProvider.getInstance().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);
        ButterKnife.bind(this, rootView);

        db = new DBHandler(getActivity());


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "Refreshing", Toast.LENGTH_LONG).show();
                mFeedAdapter.clear();
                mLoadingImageView.setVisibility(View.VISIBLE);
                mLoadingImageView.getIndeterminateDrawable().setColorFilter(Color.rgb(250, 209, 86), PorterDuff.Mode.MULTIPLY);

                mCurrentPage = 1;

                SharedpreferenceUtility.getInstance(getContext()).putInt("MyFeedLoad", 0);
                setUpAdapter();
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLayoutManager = new LinearLayoutManager(getActivity());
        collegeFeedRecyclerView.setLayoutManager(mLayoutManager);

        mFeedAdapter = new CollegeCampusFeedAdapter(view.getContext());

        rvPasser.rvPassfromMyFeed(collegeFeedRecyclerView, mFeedAdapter);

        collegeFeedRecyclerView.setItemAnimator(new SlideInUpAnimator());
        collegeFeedRecyclerView.setAdapter(mFeedAdapter);

        collegeFeedRecyclerView.addOnScrollListener(mRecyclerViewOnScrollListener);

        mCurrentPage=1;
        setUpAdapter();

        if (SharedpreferenceUtility.getInstance(getActivity()).getBoolean(AppConstants.IS_FIRST_TIME_MY_FEED)) {
            helper_text_feed.setText("This feed shall serve you with all the posts and events coming from the groups you follow.");
            helper_campus_feed.setVisibility(View.VISIBLE);
        }

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                helper_campus_feed.setVisibility(View.GONE);
                SharedpreferenceUtility.getInstance(getContext()).putBoolean(AppConstants.IS_FIRST_TIME_MY_FEED, Boolean.FALSE);

            }
        });

    }

    private void saveToDb(List<ModelsFeed> feedItems) {
//        ActiveAndroid.beginTransaction();
//        try {
//            for (int i = 0; i < feedItems.size(); i++) {
//                feedItems.get(i).setTable("MyFeed");
//                feedItems.get(i).save();
//            }
//            ActiveAndroid.setTransactionSuccessful();
//        }
//        finally {
//            ActiveAndroid.endTransaction();
//        }
        for (int i = 0; i < feedItems.size(); i++) {
//                feedItems.get(i).setTable("MyFeed");
            db.addMyFeedItem(feedItems.get(i));
            }
    }

    public void setUpAdapter() {

        if (SharedpreferenceUtility.getInstance(getContext()).getInt("MyFeedLoad") % 2 == 0) {

            //delete all myfeed
//            if(listAll().size()>0) {
//                new Delete().from(ModelsFeed.class).where("type = ?", "MyFeed").execute();
//            }
            db.deleteAllMyFeed();
            mCurrentPage=1;

            String collegeId=SharedpreferenceUtility.getInstance(this.getContext()).getString(AppConstants.COLLEGE_ID);
            String pid=SharedpreferenceUtility.getInstance(this.getContext()).getString(AppConstants.PERSON_PID);
            String pageNumber=new Integer(mCurrentPage).toString();
            Call findFeedCall = mCCService.myFeed(collegeId,pid,pageNumber);

            mCalls.add(findFeedCall);
            findFeedCall.enqueue(mFindFeedFirstFetchCallback);

            SharedpreferenceUtility.getInstance(getContext()).putInt("MyFeedLoad", 1);
        } else {

            try {
                mLoadingImageView.setVisibility(View.GONE);
            }catch (NullPointerException e){

            }
            mIsLoading = false;
            List<ModelsFeed> feedItems = listAll();
            mFeedAdapter.addAll(feedItems);

            if (feedItems.size() >= PAGE_SIZE) {
                mFeedAdapter.addLoading();
            } else {
                mIsLastPage = true;
            }
        }
    }

    private List<ModelsFeed> listAll() {
//        return new Select()
//                .from(ModelsFeed.class)
//                .where("type = ?","MyFeed")
//                .orderBy("id ASC")
//                .execute();

        return db.getAllMyFeedItems();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        removeListeners();
        mCurrentPage = 1;
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Unregister Otto Bus
//        BusProvider.getInstance().unregister(this);
    }
    // endregion

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

//        inflater.inflate(R.menu.videos_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                // do nothing
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // region Helper Methods
    private void loadMoreItems() {
        mIsLoading = true;

        mCurrentPage += 1;
//
        String collegeId=SharedpreferenceUtility.getInstance(this.getContext()).getString(AppConstants.COLLEGE_ID);
        String pid=SharedpreferenceUtility.getInstance(this.getContext()).getString(AppConstants.PERSON_PID);
        String pageNumber=new Integer(mCurrentPage).toString();
        Call findFeedCall = mCCService.myFeed(collegeId,pid,pageNumber);
        //Timber.d("mCalls.add() : mQuery - " + mQuery);
        //Timber.d("loadMoreItems() : mCalls.add() : mCurrentPage - " + mCurrentPage);

        mCalls.add(findFeedCall);
        findFeedCall.enqueue(mFindFeedNextFetchCallback);
    }


    public String getQuery() {
        return mQuery;
    }


    private void removeListeners(){
        collegeFeedRecyclerView.removeOnScrollListener(mRecyclerViewOnScrollListener);
    }

    public void onAttachFragment(Fragment fragment)
    {
        try
        {
            rvPasser = (RVPass)fragment;

        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(
                    fragment.toString() + " must implement RVPass");
        }
    }

    public interface  RVPass{
        public void rvPassfromMyFeed(RecyclerView personalFeedRecyclerView, CollegeCampusFeedAdapter mfeedAdapter);
    }
    // endregion
}
