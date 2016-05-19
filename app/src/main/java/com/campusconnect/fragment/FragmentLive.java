package com.campusconnect.fragment;

/**
 * Created by RK on 17-09-2015.
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.campusconnect.adapter.ChatAdapter;
import com.campusconnect.adapter.LiveAdapter;
import com.campusconnect.adapter.TagArrayAdapter;
import com.campusconnect.adapter.TagListAdapterActivity;
import com.campusconnect.adt.ChatMessage;
import com.campusconnect.adt.TagViewHolder;
import com.campusconnect.adt.TrendingTags;

import com.campusconnect.R;
import com.campusconnect.communicator.CCWebService;
import com.campusconnect.communicator.ServiceGenerator;
import com.campusconnect.communicator.models.ModelsFeed;
import com.campusconnect.communicator.models.ModelsHashTag;
import com.campusconnect.communicator.models.ModelsHashTagList;
import com.campusconnect.communicator.models.ModelsLiveFeed;
import com.campusconnect.communicator.models.ModelsLiveResponse;
import com.campusconnect.constant.AppConstants;
import com.campusconnect.database.DBHandler;
import com.campusconnect.photoFilter.PhotoActivty;
import com.campusconnect.supportClasses.MyScrollListenerLive;
import com.campusconnect.utility.LogUtility;
import com.campusconnect.utility.SharedpreferenceUtility;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
 * Created by hp1 on 21-01-2015.
 */
public class FragmentLive extends BaseFragment {


    RecyclerView tagList;
    List<TrendingTags> selectedTags_fromAdapter;

    RVPass rvPasser;
    private Button mChannelView;

    List<ChatMessage> chatMsgs;
    SwipeRefreshLayout swipeRefreshLayout;

    RecyclerView LiveFeedRecyclerView;

    private RelativeLayout message_box;
    private ImageButton send_message, mAddPic, mAddTag;
    View camera_divider, divider_msg_box;


    private EditText mMessageET;

    //tags shit
    private String sendTags="";
    private TrendingTags[] tags;


    private ArrayAdapter<TrendingTags> tagListAdapter;
    private ArrayList<TrendingTags> currentTags;
    private ArrayList<TrendingTags> filterTags;
    private ArrayList<ChatMessage> filterResult;
    //tags shit

    List<ModelsLiveFeed> modelsLiveFeeds;

    private static List<String> tagsList_rkd;
    private static List<TrendingTags> tagAdapter;

    private boolean mIsLastPage = false;
    TagListAdapterActivity tagListAdapter1;

    private int mCurrentPage = 1;
    private boolean mIsLoading = false;
    private LiveAdapter mLiveAdapter;


    private ChatAdapter mChatAdapter;
    private int search=0;

    private LinearLayoutManager mLayoutManager;
    ProgressBar mLoadingImageView;
    Context context;

    public static final int PAGE_SIZE = 150;

    DBHandler db;
    CCWebService mCCService;


    String DEFAULT_ROOT_URL = "https://campus-connect-live.appspot.com/_ah/api/";
    String DEFAULT_SERVICE_PATH = "campusConnectApis/v1/";
    String BASE_URL = DEFAULT_ROOT_URL + DEFAULT_SERVICE_PATH;



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
////                Toast.makeText(getContext(),"NO MORE DATA",Toast.LENGTH_LONG);
//            }
        }
    };


    private View.OnClickListener mReloadOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCurrentPage = 1;
            mLiveAdapter.clear();
            mLoadingImageView.setVisibility(View.VISIBLE);
            mLoadingImageView.getIndeterminateDrawable().setColorFilter(Color.rgb(250, 209, 86), PorterDuff.Mode.MULTIPLY);

            String collegeId=SharedpreferenceUtility.getInstance(getContext()).getString(AppConstants.COLLEGE_ID);
            String pageNumber=new Integer(mCurrentPage).toString();
            Call findFeedCall = mCCService.inciLive(collegeId, pageNumber);

            mCalls.add(findFeedCall);
            findFeedCall.enqueue(findFirstLiveCallBack);
        }
    };


    private Callback<ModelsLiveResponse> findFirstLiveCallBack = new Callback<ModelsLiveResponse>() {
        @Override
        public void onResponse(Response<ModelsLiveResponse> response, Retrofit retrofit) {
            try {
                mLoadingImageView.setVisibility(View.GONE);
            }catch (NullPointerException e){

            }
            mIsLoading = false;
//            Log.d("findVid", response.body().toString());
            if (response != null) {
                if (response.isSuccess()) {
                    ModelsLiveResponse modelsLiveResponse = response.body();
                    if (modelsLiveResponse != null) {
                        List<ModelsLiveFeed> liveFeedItems = modelsLiveResponse.getItems();
                        if (liveFeedItems != null) {
                            mLiveAdapter.addAll(liveFeedItems);
                            saveToDb(liveFeedItems);
                            modelsLiveFeeds=liveFeedItems;

                            if (liveFeedItems.size() >= PAGE_SIZE) {
//                                mLiveAdapter.addLoading();
                            } else {
                                mIsLastPage = true;
                            }
                        }else{
//                            mEmptyView.setVisibility(View.VISIBLE);
                        }
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
//                        mLoadingImageView.setVisibility(View.GONE);
                    }
                }
            }
        }
    };


    private Callback<ModelsLiveResponse> mFindLiveNextFetchCallback = new Callback<ModelsLiveResponse>() {
        @Override
        public void onResponse(Response<ModelsLiveResponse> response, Retrofit retrofit) {
            mLiveAdapter.removeLoading();
            mIsLoading = false;

            if (response != null) {
                if (response.isSuccess()) {
                    ModelsLiveResponse modelsLiveResponse = response.body();
                    if (modelsLiveResponse != null) {
                        List<ModelsLiveFeed> liveFeedItems = modelsLiveResponse.getItems();
                        if (liveFeedItems != null) {
                            mLiveAdapter.addAll(liveFeedItems);

                            if (liveFeedItems.size() >= PAGE_SIZE) {
//                                mLiveAdapter.addLoading();
                            } else {
                                mIsLastPage = true;
                            }
                        }
                    }
                } else {
                    com.squareup.okhttp.Response rawResponse = response.raw();
                    if (rawResponse != null) {
                        LogUtility.logFailedResponse(rawResponse);

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



    // region Constructors
    public FragmentLive() {
    }
    // endregion

    // region Factory Methods
    public static FragmentLive newInstance() {
        return new FragmentLive();
    }

    public static FragmentLive newInstance(Bundle extras) {
        FragmentLive fragment = new FragmentLive();
        fragment.setArguments(extras);
        return fragment;
    }
    // endregion


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCCService = ServiceGenerator.createService(CCWebService.class, BASE_URL);


        setHasOptionsMenu(true);
//        setHasOptionsMenu(true);
        onAttachFragment(getParentFragment());

    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.sylu_activity_main,container,false);
        context = v.getContext();


        //resultPic = (ImageView) v.findViewById(R.id.image1);


        mChannelView = (Button) v.findViewById(R.id.channel_bar);
        mLoadingImageView = (ProgressBar)v.findViewById(R.id.loading_iv);
        swipeRefreshLayout= (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        LiveFeedRecyclerView = (RecyclerView) v.findViewById(R.id.rv_live_feed);

        db = new DBHandler(getActivity());


        mChannelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFilter(v);
            }
        });

//        send_message.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                sendMessage(v);
//            }
//        });

        message_box = (RelativeLayout) v.findViewById(R.id.msg_box);
        mMessageET = (EditText) v.findViewById(R.id.message_et);
        mAddTag = (ImageButton) v.findViewById(R.id.add_tag);
        send_message = (ImageButton) v.findViewById(R.id.ib_sendmessage);
        mAddPic = (ImageButton) v.findViewById(R.id.add_image);
        camera_divider = (View) v.findViewById(R.id.comment_camera_divider);
        divider_msg_box = (View) v.findViewById(R.id.divider_above_msg_box);
        mLayoutManager = new LinearLayoutManager(getActivity());
        ArrayList<TrendingTags> ttList = new ArrayList<TrendingTags>();
        tagListAdapter = new TagArrayAdapter(context.getApplicationContext(), ttList);

        tagAdapter = new ArrayList<>();
        tagsList_rkd = new ArrayList<>();


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "Refreshing", Toast.LENGTH_LONG).show();
                mLiveAdapter.clear();
                mLoadingImageView.setVisibility(View.VISIBLE);
                mLoadingImageView.getIndeterminateDrawable().setColorFilter(Color.rgb(250, 209, 86), PorterDuff.Mode.MULTIPLY);

                mCurrentPage = 1;

                SharedpreferenceUtility.getInstance(getContext()).putInt("LiveLoad", 0);
                setUpAdapter();

            }
        });


        return v;
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentTags = new ArrayList<>();
        filterResult = new ArrayList<>();
        filterTags = new ArrayList<>();

        mLayoutManager = new LinearLayoutManager(getActivity());
        LiveFeedRecyclerView.setLayoutManager(mLayoutManager);


        mLiveAdapter = new LiveAdapter(view.getContext());

        rvPasser.passFromLiveFeed(LiveFeedRecyclerView, mLiveAdapter);

        LiveFeedRecyclerView.setItemAnimator(new SlideInUpAnimator());
        LiveFeedRecyclerView.setAdapter(mLiveAdapter);


//        LiveFeedRecyclerView.addOnScrollListener(mRecyclerViewOnScrollListener);

        LiveFeedRecyclerView.addOnScrollListener(new MyScrollListenerLive(context) {
            @Override
            public void onMoved(int distance) {
                message_box.setTranslationY(distance);
                divider_msg_box.setTranslationY(distance);
            }
        });

        mAddPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_temp = new Intent(v.getContext(), PhotoActivty.class);
                Log.d("tags length", new Integer(currentTags.size()).toString());

                ArrayList<String> tag_to_send = new ArrayList<String>();
                for (TrendingTags ct : currentTags) {
                    tag_to_send.add(ct.getName());
                }

//                intent_temp.putStringArrayListExtra("hashtags", tag_to_send);

                v.getContext().startActivity(intent_temp);

//                takePicture();
            }
        });


        mAddTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTagToPost(v);
            }
        });

        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mMessageET.getText().toString() == null || mMessageET.getText().toString().matches("")) {
                    Toast.makeText(getActivity(), "Please fill in the data.",
                            Toast.LENGTH_SHORT).show();
                } else {

                    ModelsLiveFeed modelsLiveFeed = new ModelsLiveFeed();


                    String collegeId = SharedpreferenceUtility.getInstance(v.getContext()).getString(AppConstants.COLLEGE_ID);
                    modelsLiveFeed.setCollegeId(collegeId);

                    Date cDate = new Date();
                    String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cDate);

                    modelsLiveFeed.setTimestamp(timestamp);


                    modelsLiveFeed.setImageUrl("");

                    String personUrl = SharedpreferenceUtility.getInstance(v.getContext()).getString(AppConstants.PHOTO_URL);
                    modelsLiveFeed.setPersonPhotoUrl(personUrl);


                    modelsLiveFeed.setTags(sendTags);
                    modelsLiveFeed.setDescription(mMessageET.getText().toString());

                    modelsLiveFeed.setName(SharedpreferenceUtility.getInstance(v.getContext()).getString(AppConstants.PERSON_NAME));


                    Call commentPost = mCCService.liveComments(modelsLiveFeed);
                    commentPost.enqueue(callBack);


                    mLiveAdapter.addToTop(modelsLiveFeed);
                    mLiveAdapter.removeLastItem();

                    mMessageET.setText("");
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    mLayoutManager.scrollToPositionWithOffset(0, 0);
                }

            }
        });



        mMessageET.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mMessageET.getText().length() > 0) {
                    mAddPic.setVisibility(View.GONE);
                    camera_divider.setVisibility(View.GONE);
                } else {
                    mAddPic.setVisibility(View.VISIBLE);
                    camera_divider.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mCurrentPage=1;
        setUpAdapter();
//        setUpHashTags();
    }

//    private void setUpHashTags(){
//        CCWebService mCCService = ServiceGenerator.createService(CCWebService.class,CCWebService.BASE_URL);
//
//        String collegeId = SharedpreferenceUtility.getInstance(getContext()).getString(AppConstants.COLLEGE_ID);
//
//        Date date = new Date();
//        String timestamp= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
//
//        Call getHashtags = mCCService.getHashTags(timestamp,collegeId);
//        mCalls.add(getHashtags);
//        getHashtags.enqueue(hashCallBack);
//    }



    private void saveToDb(List<ModelsLiveFeed> feedItems) {

        for (int i = 0; i < feedItems.size(); i++) {
//                feedItems.get(i).setTable("MyFeed");
            db.addLiveItem(feedItems.get(i));
        }
    }

    public void setUpAdapter() {

        if (SharedpreferenceUtility.getInstance(getContext()).getInt("LiveLoad") % 2 == 0) {

            //delete all myfeed items
            db.deleteAllLive();

            mCurrentPage=1;

            String collegeId = SharedpreferenceUtility.getInstance(getContext()).getString(AppConstants.COLLEGE_ID);
            String pageNumber = new Integer(mCurrentPage).toString();

            Call inciLive = mCCService.inciLive(collegeId,pageNumber);
            mCalls.add(inciLive);
            inciLive.enqueue(findFirstLiveCallBack);

            SharedpreferenceUtility.getInstance(getContext()).putInt("LiveLoad", 1);

        } else {
            try {
                mLoadingImageView.setVisibility(View.GONE);
            }catch (NullPointerException e){

            }
            mIsLoading = false;
            List<ModelsLiveFeed> liveItems = listAll();
            mLiveAdapter.addAll(liveItems);

            if (liveItems.size() >= PAGE_SIZE) {
//                mLiveAdapter.addLoading();
            } else {
                mIsLastPage = true;
            }
        }
    }


    private List<ModelsLiveFeed> listAll() {
        // This is how you execute a query
//        return new Select()
//                    .from(ModelsClubMiniForm.class)
//                    .orderBy("id ASC")
//                    .execute();
//    return null;
        return db.getAllLiveItems();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        removeListeners();
        mCurrentPage = 1;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Unregister Otto Bus
//        BusProvider.getInstance().unregister(this);
    }


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


    private void loadMoreItems() {
        mIsLoading = true;

        mCurrentPage += 1;
//
        String collegeId = SharedpreferenceUtility.getInstance(getContext()).getString(AppConstants.COLLEGE_ID);
        String pageNumber = String.valueOf(new Integer(mCurrentPage));
        Call findFeedCall = mCCService.inciLive(collegeId, pageNumber);

        mCalls.add(findFeedCall);
        findFeedCall.enqueue(mFindLiveNextFetchCallback);
    }



    @Override
    public void onStop() {
        super.onStop();
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


    @Override
    public void onResume() {
        super.onResume();

    }

    public void addTagToPost(View view) {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.tag_dialog, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);

        final TextView title = (TextView) promptsView.findViewById(R.id.tag1);
        title.setText("Trending Tags");
        final ListView filterList = (ListView) promptsView.findViewById(R.id.tag_list);

        // When item is tapped, toggle checked properties of CheckBox and tags.
        filterList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View item,
                                    int position, long id) {
                TrendingTags tag = tagListAdapter.getItem(position);
                tag.toggleChecked();
                TagViewHolder viewHolder = (TagViewHolder) item.getTag();
                viewHolder.getCheckBox().setChecked(tag.isChecked());
            }
        });


        // Create and populate tags..
        tags = (TrendingTags[]) getActivity().getLastNonConfigurationInstance();
        if (tags == null) {

            List<ModelsHashTag> getFromDb=db.getAllHashTags();
//
//            getFromDb.add("1");
//            getFromDb.add("2");
//            getFromDb.add("4");
//            getFromDb.add("3");

            tags = new TrendingTags[getFromDb.size()];

            for(int i=getFromDb.size()-1;i>=0;i--){
                String name=getFromDb.get(i).getName().replace(" ","");
                String timestamp = getFromDb.get(i).getStartTime();
                tags[getFromDb.size()-i-1]=new TrendingTags(name,timestamp);
            }


        }

        ArrayList<TrendingTags> ttList = new ArrayList<TrendingTags>();
        ttList.addAll(Arrays.asList(tags));

        // Set our custom array adapter as the ListView's adapter.
        tagListAdapter = new

                TagArrayAdapter(context.getApplicationContext(), ttList);
        filterList.setAdapter(tagListAdapter);

        //subscribeWithPresence();
        //history();

        currentTags.clear();
        sendTags="";

        if(tags.length>0) {
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    int count = 0;

                                    for (int j = 0; j < tags.length; j++) {
                                        if (tags[j].isChecked() == true) {
                                            count++;
                                            currentTags.add(tags[j]);
                                            sendTags = sendTags + "#" + tags[j];


                                            String tag = "";
                                            ArrayList<TrendingTags> copyCurrentTemp = new ArrayList<TrendingTags>();
                                            copyCurrentTemp = currentTags;
                                            if (copyCurrentTemp != null && copyCurrentTemp.size() != 0) {
                                                for (int i = 0; i < copyCurrentTemp.size(); i++) {
                                                    tag += ("#" + copyCurrentTemp.get(i).getName());
                                                }

                                                String intermediateMsg = mMessageET.getText().toString();
                                                intermediateMsg += " " + tag + " ";
                                                mMessageET.setText(intermediateMsg);
                                                mMessageET.setSelection(mMessageET.getText().toString().length());
                                                copyCurrentTemp.clear();
                                            }
                                        }

                                    }
                                }
                            })

                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }else{
            Toast.makeText(view.getContext(),"Start a trend",Toast.LENGTH_LONG).show();
        }
    }





    /**
     * Create an alert dialog with a text view to enter a new channel to join. If the channel is
     * not empty, unsubscribe from the current channel and join the new one.
     * Then, get messages from history and update the channelView which displays current channel.
     *
     * @param view
     */
    public void changeFilter(View view) {
        LayoutInflater li = LayoutInflater.from(context);
        final View promptsView = li.inflate(R.layout.tag_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);

        final TextView title = (TextView) promptsView.findViewById(R.id.tag1);
        title.setText("Selective Search");
        final ListView filterList = (ListView) promptsView.findViewById(R.id.tag_list);

        // When item is tapped, toggle checked properties of CheckBox and tags.
        filterList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View item,
                                    int position, long id) {
                TrendingTags tag = tagListAdapter.getItem(position);
                tag.toggleChecked();
                TagViewHolder viewHolder = (TagViewHolder) item.getTag();
                viewHolder.getCheckBox().setChecked(tag.isChecked());
            }
        });


        // Create and populate tags..
        tags = (TrendingTags[]) getActivity().getLastNonConfigurationInstance();
        if (tags == null) {
            List<ModelsHashTag> getFromDb=db.getAllHashTags();
//
//            getFromDb.add("1");
//            getFromDb.add("2");
//            getFromDb.add("4");
//            getFromDb.add("3");

            tags = new TrendingTags[getFromDb.size()];

            for(int i=getFromDb.size()-1;i>=0;i--){
                String name=getFromDb.get(i).getName().replace(" ", "");
                String timestamp = getFromDb.get(i).getStartTime();
                tags[getFromDb.size()-i-1]=new TrendingTags(name,timestamp);
            }
        }

        ArrayList<TrendingTags> ttList = new ArrayList<TrendingTags>();
        ttList.addAll(Arrays.asList(tags));

        // Set our custom array adapter as the ListView's adapter.
        tagListAdapter = new

                TagArrayAdapter(context.getApplicationContext(), ttList);
        filterList.setAdapter(tagListAdapter);

        //subscribeWithPresence();
        //history();

        filterResult.clear();
        filterTags.clear();

        if(tags.length>0){
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                search = 1;
                                for (int j = 0; j < tags.length; j++) {
                                    if (tags[j].isChecked() == true) filterTags.add(tags[j]);
                                }
                                if (filterTags.size() == 0) {

                                    mLiveAdapter.clear();
                                    mLiveAdapter.addAll(modelsLiveFeeds);
//                                    mChatAdapter.setMessages(chatMsgs);
                                } else {
                                    List<ModelsLiveFeed> modelsLiveFeeds = new ArrayList<ModelsLiveFeed>();


//                                    for (int k = 0; k < filterTags.size(); k++) {
//                                        for (int i = 0; i < mLiveAdapter.getItemCount(); i++) {
//                                            Log.d("i k", new Integer(mLiveAdapter.getItemCount()).toString() + " " + new Integer(filterTags.size()).toString());
//
//                                            if (mLiveAdapter.getItem(i).getTags().contains(filterTags.get(k).getName())) {
//                                                modelsLiveFeeds.add(mLiveAdapter.getItem(i));
////                                                filterResult.add(chatMsgs.get(i));
////                                                mChatAdapter.setMessages(filterResult);
//                                            }
//                                        }
//                                    }
//                                    mLiveAdapter.clear();
                                    for (int i = 0; i < mLiveAdapter.getItemCount(); i++) {
                                        Log.d("clear", mLiveAdapter.getItem(i).getDescription());
                                        Log.d("i k", new Integer(i).toString() + " " + new Integer(filterTags.size()).toString());
                                        for (int k = 0; k < filterTags.size(); k++)
                                            if (mLiveAdapter.getItem(i).getTags().contains(filterTags.get(k).getName())) {
                                                modelsLiveFeeds.add(mLiveAdapter.getItem(i));
//                                                filterResult.add(chatMsgs.get(i));
//                                                mChatAdapter.setMessages(filterResult);
                                            }
                                        //'


                                    }


                                    mLiveAdapter.clear();
                                    ;
                                    mLiveAdapter.addAll(modelsLiveFeeds);
//
                                }

                            }
                        })

                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                search = 0;
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    } else{
            Toast.makeText(view.getContext(),"No events currently happening.",Toast.LENGTH_LONG).show();
        }
    }


    public Object onRetainNonConfigurationInstance() {
        return tags;
    }




    //Calling camera intent and allowing the user to take pictures
    private void takePicture() {

        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);


        Bitmap bp = (Bitmap) data.getExtras().get("data");
        //resultPic.setImageBitmap(bp);
    }


    public interface  RVPass{
        public void passFromLiveFeed(RecyclerView personalFeedRecyclerView, LiveAdapter mLiveAdapter);
    }



    private void removeListeners(){
        LiveFeedRecyclerView.removeOnScrollListener(mRecyclerViewOnScrollListener);
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


}
