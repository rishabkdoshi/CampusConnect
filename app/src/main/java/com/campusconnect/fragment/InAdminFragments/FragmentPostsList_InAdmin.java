package com.campusconnect.fragment.InAdminFragments;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.campusconnect.R;
import com.campusconnect.adapter.PostsForEditAdapter;
import com.campusconnect.communicator.CCWebService;
import com.campusconnect.communicator.ServiceGenerator;
import com.campusconnect.communicator.models.ModelsClubMiniForm;
import com.campusconnect.communicator.models.ModelsCollegeFeed;
import com.campusconnect.communicator.models.ModelsFeed;
import com.campusconnect.constant.AppConstants;
import com.campusconnect.fragment.BaseFragment;
import com.campusconnect.utility.LogUtility;
import com.campusconnect.utility.SharedpreferenceUtility;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import timber.log.Timber;

/**
 * Created by RK on 04/02/2016.
 */
public class FragmentPostsList_InAdmin extends BaseFragment {

    // region Constants
    public static final int PAGE_SIZE = 10;
    // endregion

    // region Member Variables
    @Bind(R.id.rv_college_feed)
    public RecyclerView collegeFeedRecyclerView;
    @Bind(R.id.tv_show_blank)
    TextView mEmptyView;
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
    private PostsForEditAdapter mFeedAdapter;

    private String mQuery;
    private LinearLayoutManager mLayoutManager;
    private CCWebService mCCService;
    // endregion

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
        }
    };

//    @OnClick(R.id.reload_btn)
//    public void onReloadButtonClicked() {
//        mErrorLinearLayout.setVisibility(View.GONE);
//        mLoadingImageView.setVisibility(View.VISIBLE);
//        mLoadingImageView.getIndeterminateDrawable().setColorFilter(Color.rgb(250, 209, 86), PorterDuff.Mode.MULTIPLY);
//
//
//        String collegeId= SharedpreferenceUtility.getInstance(this.getContext()).getString(AppConstants.COLLEGE_ID);
//        String pid= SharedpreferenceUtility.getInstance(this.getContext()).getString(AppConstants.PERSON_PID);
//        String pageNumber=new Integer(mCurrentPage).toString();
//        Call findFeedCall = mCCService.collegeFeed(collegeId,pid,pageNumber);
//        mCalls.add(findFeedCall);
//        findFeedCall.enqueue(mFindFeedFirstFetchCallback);
//    }

    private View.OnClickListener mReloadOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCurrentPage -= 1;
            mFeedAdapter.addLoading();
            loadMoreItems();
        }
    };
    // endregion

    // region Callbacks
    private Callback<ModelsCollegeFeed> mFindFeedFirstFetchCallback = new Callback<ModelsCollegeFeed>() {
        @Override
        public void onResponse(Response<ModelsCollegeFeed> response, Retrofit retrofit) {
            mLoadingImageView.setVisibility(View.GONE);
            mIsLoading = false;

            if (response != null) {
                if (response.isSuccess()) {
                    ModelsCollegeFeed modelsCollegeFeed = response.body();
                    if (modelsCollegeFeed != null) {
                        List<ModelsFeed> feedItems = modelsCollegeFeed.getItems();
                        if (feedItems != null || feedItems.size()>0) {
                            mFeedAdapter.addAll(feedItems);

                            if (feedItems.size() >= PAGE_SIZE) {
                                mFeedAdapter.addLoading();
                            } else {
                                mIsLastPage = true;
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

    // endregion

    // region Constructors
    public FragmentPostsList_InAdmin() {
    }
    // endregion

    // region Factory Methods
    public static FragmentPostsList_InAdmin newInstance() {
        return new FragmentPostsList_InAdmin();
    }

    public static FragmentPostsList_InAdmin newInstance(Bundle extras) {
        FragmentPostsList_InAdmin fragment = new FragmentPostsList_InAdmin();
        fragment.setArguments(extras);
        return fragment;
    }
    // endregion
    ModelsClubMiniForm club;
    // region Lifecycle Methods

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        mCCService = ServiceGenerator.createService(
                CCWebService.class,
                CCWebService.BASE_URL);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);
        ButterKnife.bind(this, rootView);

        Bundle bundle = getArguments();

        club = bundle.getParcelable("club");

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "Refreshing", Toast.LENGTH_LONG).show();
                mFeedAdapter.clear();
                mLoadingImageView.setVisibility(View.VISIBLE);
                mLoadingImageView.getIndeterminateDrawable().setColorFilter(Color.rgb(250, 209, 86), PorterDuff.Mode.MULTIPLY);

                mCurrentPage = 1;

                String clubId;
                clubId = club.getClubId();
                String pid = SharedpreferenceUtility.getInstance(getContext()).getString(AppConstants.PERSON_PID);
                String pageNumber = new Integer(mCurrentPage).toString();
                Call findFeedCall = mCCService.clubFeed(clubId, pid);
//        Timber.d("mCalls.add() : mQuery - " + mQuery);
//        Timber.d("onViewCreated() : mCalls.add() : mCurrentPage - " + mCurrentPage);

                mCalls.add(findFeedCall);
                findFeedCall.enqueue(mFindFeedFirstFetchCallback);

            }
        });


        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(mQuery);

        mLayoutManager = new LinearLayoutManager(getActivity());
        collegeFeedRecyclerView.setLayoutManager(mLayoutManager);

        mFeedAdapter = new PostsForEditAdapter(view.getContext());
        //mFeedAdapter.setOnItemClickListener(this);

        collegeFeedRecyclerView.setItemAnimator(new SlideInUpAnimator());
//        collegeFeedRecyclerView.setItemAnimator(new DefaultItemAnimator());
        collegeFeedRecyclerView.setAdapter(mFeedAdapter);

        // Pagination
        collegeFeedRecyclerView.addOnScrollListener(mRecyclerViewOnScrollListener);
//
        String clubId;
        clubId = club.getClubId();
        String pid = SharedpreferenceUtility.getInstance(getContext()).getString(AppConstants.PERSON_PID);
        String pageNumber = new Integer(mCurrentPage).toString();
        Call findFeedCall = mCCService.clubFeed(clubId, pid);
//        Timber.d("mCalls.add() : mQuery - " + mQuery);
//        Timber.d("onViewCreated() : mCalls.add() : mCurrentPage - " + mCurrentPage);

        mCalls.add(findFeedCall);
        findFeedCall.enqueue(mFindFeedFirstFetchCallback);
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
//            case R.id.sort:
//                showSortDialog();
//                break;
            default:
                // do nothing
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    // region Helper Methods
    private void loadMoreItems() {
//        mIsLoading = true;
//
//        mCurrentPage += 1;
////
//        String collegeId= SharedpreferenceUtility.getInstance(this.getContext()).getString(AppConstants.COLLEGE_ID);
//        String pid= SharedpreferenceUtility.getInstance(this.getContext()).getString(AppConstants.PERSON_PID);
//        String pageNumber=new Integer(mCurrentPage).toString();
//        Call findFeedCall = mCCService.collegeFeed(collegeId,pid,pageNumber);
//
//        mCalls.add(findFeedCall);
//        findFeedCall.enqueue(mFindFeedNextFetchCallback);
    }


    public String getQuery() {
        return mQuery;
    }


    private void removeListeners(){
        collegeFeedRecyclerView.removeOnScrollListener(mRecyclerViewOnScrollListener);
    }

    // endregion

}