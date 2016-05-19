package com.campusconnect.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.campusconnect.R;
import com.campusconnect.adapter.CommentsAdapterActivity;
import com.campusconnect.communicator.CCWebService;
import com.campusconnect.communicator.ServiceGenerator;
import com.campusconnect.communicator.models.ModelsCommentPost;
import com.campusconnect.communicator.models.ModelsComments;
import com.campusconnect.communicator.models.ModelsCommentsListResponse;
import com.campusconnect.constant.AppConstants;
import com.campusconnect.utility.LogUtility;
import com.campusconnect.utility.SharedpreferenceUtility;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by RK on 06/11/2015.
 */
public class CommentsActivity extends ActionBarActivity {

    public static final int PAGE_SIZE = 25;

    @Bind(R.id.rv_comments)
    RecyclerView commentsListRecyclerView;
    @Bind(R.id.cross_button)
    LinearLayout close;
    @Bind(R.id.tv_show_blank)
    TextView mEmptyView;
    @Bind(R.id.tv_comments_title)
    TextView comments_title;
    @Bind(R.id.et_post_comment)
    EditText et_comment;
    @Bind(R.id.comment_icon_container)
    LinearLayout container_comment_icon;

    @Bind(R.id.loading_iv)
    ProgressBar mLoadingImageView;
    @Bind(R.id.error_ll)
    LinearLayout mErrorLinearLayout;
    @Bind(R.id.error_tv)
    TextView mErrorTextView;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    String postId;


    private boolean mIsLastPage = false;
    private int mCurrentPage = 1;
    private boolean mIsLoading = false;

    String entered_comment;
    CommentsAdapterActivity commentsAdapter;

    private LinearLayoutManager mLayoutManager;

    private CCWebService mCCService;

    int i = 0;

    private void loadMoreItems() {
        mIsLoading = true;

        mCurrentPage += 1;
//
        String pageNumber = String.valueOf(new Integer(mCurrentPage));
        Call findFeedCall = mCCService.getComments(this.postId, pageNumber);
        //Timber.d("mCalls.add() : mQuery - " + mQuery);
        //Timber.d("loadMoreItems() : mCalls.add() : mCurrentPage - " + mCurrentPage);

//        mCalls.add(findFeedCall);
        findFeedCall.enqueue(mFindCommentsNextFetchCallback);
    }

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        ButterKnife.bind(this);

        mCCService = ServiceGenerator.createService(
                CCWebService.class,
                CCWebService.BASE_URL);
//        setHasOptionsMenu(true);
        Bundle extras = getIntent().getExtras();
        if (extras.getBoolean("FLAG_POST_COMMENT")) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            et_comment.requestFocus();
        }
        postId = getIntent().getExtras().getString("POST_ID");


        mLayoutManager = new LinearLayoutManager(CommentsActivity.this);
        commentsListRecyclerView.setLayoutManager(mLayoutManager);

        commentsAdapter = new CommentsAdapterActivity(CommentsActivity.this);
        //mFeedAdapter.setOnItemClickListener(this);

        commentsListRecyclerView.setItemAnimator(new SlideInUpAnimator());
//        collegeFeedRecyclerView.setItemAnimator(new DefaultItemAnimator());
        commentsListRecyclerView.setAdapter(commentsAdapter);

        // Pagination
        commentsListRecyclerView.addOnScrollListener(mRecyclerViewOnScrollListener);

        Typeface r_med = Typeface.createFromAsset(getAssets(), "font/Roboto_Medium.ttf");
        Typeface r_reg = Typeface.createFromAsset(getAssets(), "font/Roboto_Regular.ttf");

        comments_title.setTypeface(r_med);
        et_comment.setTypeface(r_reg);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
//                Toast.makeText(getCon(), "Refreshing", Toast.LENGTH_LONG).show();
                commentsAdapter.clear();
                mLoadingImageView.setVisibility(View.VISIBLE);
                mLoadingImageView.getIndeterminateDrawable().setColorFilter(Color.rgb(250, 209, 86), PorterDuff.Mode.MULTIPLY);

                mCurrentPage = 1;

                String pageNumber = String.valueOf(new Integer(mCurrentPage));

                Call findComments = mCCService.getComments(postId, pageNumber);
                findComments.enqueue(findFirstCommentsCallBack);

            }
        });
        String pageNumber = String.valueOf(new Integer(mCurrentPage));

        Call findComments = mCCService.getComments(postId, pageNumber);
        findComments.enqueue(findFirstCommentsCallBack);

        mLoadingImageView.setVisibility(View.VISIBLE);
        mLoadingImageView.getIndeterminateDrawable().setColorFilter(Color.rgb(250, 209, 86), PorterDuff.Mode.MULTIPLY);

        container_comment_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_comment.getText().toString() == null || et_comment.getText().toString().matches("")) {
                    Toast.makeText(CommentsActivity.this, "Please fill in the data.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    ModelsComments modelsComments = new ModelsComments();
                    String pid = SharedpreferenceUtility.getInstance(v.getContext()).getString(AppConstants.PERSON_PID);
                    modelsComments.setCommentBody(et_comment.getText().toString());
                    String pname = SharedpreferenceUtility.getInstance(v.getContext()).getString(AppConstants.PERSON_NAME);
                    modelsComments.setCommentor(pname);
                    String photoUrl = SharedpreferenceUtility.getInstance(v.getContext()).getString(AppConstants.PHOTO_URL);
                    modelsComments.setPhotoUrl(photoUrl);
                    String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

                    modelsComments.setTimestamp(timeStamp);

                    commentsAdapter.addToTop(modelsComments);
                    commentsAdapter.removeLastItem();


                    Date cDate = new Date();
                    String date = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
                    String time = new SimpleDateFormat("HH:mm:ss").format(cDate);

                    ModelsCommentPost modelsCommentPost = new ModelsCommentPost();
                    modelsCommentPost.setPid(pid);
                    modelsCommentPost.setCommentBody(et_comment.getText().toString());
                    modelsCommentPost.setPostId(postId);
                    modelsCommentPost.setDate(date);
                    modelsCommentPost.setTime(time);


                    Call commentPost = mCCService.commentPost(modelsCommentPost);
                    commentPost.enqueue(callBack);

                    et_comment.setText("");
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    mLayoutManager.scrollToPositionWithOffset(0, 0);
                }

            }
        });


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        // put your code here...

    }


    // region Callbacks
    private Callback<Void> callBack = new Callback<Void>() {
        @Override
        public void onResponse(Response<Void> response, Retrofit retrofit) {

        }

        @Override
        public void onFailure(Throwable t) {
            //Timber.d("onFailure() : mQuery - " + mQuery);

        }
    };

    private Callback<ModelsCommentsListResponse> mFindCommentsNextFetchCallback = new Callback<ModelsCommentsListResponse>() {
        @Override
        public void onResponse(Response<ModelsCommentsListResponse> response, Retrofit retrofit) {
            commentsAdapter.removeLoading();
            mIsLoading = false;
            Log.d("findVid", response.raw().toString());
            if (response != null) {
                if (response.isSuccess()) {
                    ModelsCommentsListResponse modelsCommentsListResponse = response.body();
                    if (modelsCommentsListResponse != null) {
                        List<ModelsComments> feedItems = modelsCommentsListResponse.getItems();
                        if (feedItems != null) {
                            commentsAdapter.addAll(feedItems);

                            if (feedItems.size() >= PAGE_SIZE) {
                                commentsAdapter.addLoading();
                            } else {
                                mIsLastPage = true;
                            }
                        }
                    }
                } else {
                    com.squareup.okhttp.Response rawResponse = response.raw();
                    if (rawResponse != null) {
                        LogUtility.logFailedResponse(rawResponse);

                        String message = rawResponse.message();
                        int code = rawResponse.code();
                    }
                }
            }
        }

        @Override
        public void onFailure(Throwable t) {
//            mFeedAdapter.removeLoading();
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

    private Callback<ModelsCommentsListResponse> findFirstCommentsCallBack = new Callback<ModelsCommentsListResponse>() {
        @Override
        public void onResponse(Response<ModelsCommentsListResponse> response, Retrofit retrofit) {
            mLoadingImageView.setVisibility(View.GONE);
            mIsLoading = false;
//            Log.d("findVid", response.body().toString());
            if (response != null) {
                if (response.isSuccess()) {
                    ModelsCommentsListResponse modelsCommentsListResponse = response.body();
                    if (modelsCommentsListResponse != null) {
                        List<ModelsComments> commentItems = modelsCommentsListResponse.getItems();
                        if (commentItems != null || commentItems.size() > 0) {
                            commentsAdapter.addAll(commentItems);

                            if (commentItems.size() >= PAGE_SIZE) {
                                commentsAdapter.addLoading();
                            } else {
                                mIsLastPage = true;
                            }
                        } else {
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
                    mIsLoading = false;
                    mLoadingImageView.setVisibility(View.GONE);

                    mErrorTextView.setText("Can't load data.\nCheck your network connection.");
                    mErrorLinearLayout.setVisibility(View.VISIBLE);
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




