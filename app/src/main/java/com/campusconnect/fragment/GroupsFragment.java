package com.campusconnect.fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

//import com.activeandroid.ActiveAndroid;
//import com.activeandroid.query.Delete;
//import com.activeandroid.query.Select;
import com.campusconnect.R;
import com.campusconnect.activity.CreateGroupActivity;
import com.campusconnect.activity.FlashActivity;
import com.campusconnect.activity.GroupPageActivity;
import com.campusconnect.activity.RequestsSuperAdmin;
import com.campusconnect.activity.SelectAdminClub;
import com.campusconnect.adapter.GroupListAdapter;
import com.campusconnect.communicator.CCWebService;
import com.campusconnect.communicator.ServiceGenerator;
import com.campusconnect.communicator.models.ModelsClubListResponse;
import com.campusconnect.communicator.models.ModelsClubMiniForm;
import com.campusconnect.communicator.models.ModelsClubRetrievalMiniForm;
import com.campusconnect.constant.AppConstants;
import com.campusconnect.database.DBHandler;
import com.campusconnect.database.DatabaseHandler;
import com.campusconnect.utility.DividerItemDecoration;
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
 * Created by rkd on 21/1/16.
 */
public class GroupsFragment extends BaseFragment implements GroupListAdapter.OnItemClickListener {
    @Bind(R.id.rv_group_list)
    RecyclerView groupListRecyclerView;

//    @Bind(R.id.create_group_group)
//    RelativeLayout create_group;
//
//    @Bind(R.id.b_create_group)
//    TextView create_group_text;

    @Bind(R.id.error_tv)
    TextView mErrorTextView;

    @Bind(R.id.error_ll)
    LinearLayout mErrorLinearLayout;

    @Bind(R.id.tv_show_blank)
    TextView mEmptyView;

    private GroupListAdapter mClubAdapter;

    private LinearLayoutManager mLayoutManager;
    private CCWebService mCCService;

    DBHandler db;
    // endregion


    // region Callbacks
    private Callback<ModelsClubListResponse> mFindGroupsFetchCallback = new Callback<ModelsClubListResponse>() {
        @Override
        public void onResponse(Response<ModelsClubListResponse> response, Retrofit retrofit) {
            if (response != null) {
                if (response.isSuccess()) {
                    ModelsClubListResponse modelsClubList = response.body();
                    if (modelsClubList != null) {
                        List<ModelsClubMiniForm> clubItems = modelsClubList.getList();
                        if (clubItems != null || clubItems.size() > 0) {
                            mClubAdapter.addAll(clubItems);
                            saveToDb(clubItems);
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

    private void saveToDb(List<ModelsClubMiniForm> clubItems) {
//        ActiveAndroid.beginTransaction();
//        try {
//            for (int i = 0; i < clubItems.size(); i++) {
//                clubItems.get(i).save();
//            }
//            ActiveAndroid.setTransactionSuccessful();
//        }
//        finally {
//            ActiveAndroid.endTransaction();
//        }
        for (int i = 0; i < clubItems.size(); i++) {
                db.addGroupItem(clubItems.get(i));
            }
    }

    // endregion

    public void setUpAdapter() {
        if (SharedpreferenceUtility.getInstance(getContext()).getInt("GroupsLoad") % 2 == 0) {

            //delete all clubs
//            new Delete().from(ModelsClubMiniForm.class).execute();
            db.deleteAllClubs();


            String pid = SharedpreferenceUtility.getInstance(this.getContext()).getString(AppConstants.PERSON_PID);
            String collegeId = SharedpreferenceUtility.getInstance((this.getContext())).getString(AppConstants.COLLEGE_ID);
            Call findClubsCall = mCCService.getClubList(collegeId, pid);
            findClubsCall.enqueue(mFindGroupsFetchCallback);
            SharedpreferenceUtility.getInstance(getContext()).putInt("GroupsLoad", 1);

        } else {
            List<ModelsClubMiniForm> clubItems = listAll();
            mClubAdapter.addAll(clubItems);
        }
    }

    private List<ModelsClubMiniForm> listAll() {
            // This is how you execute a query
//        return new Select()
//                    .from(ModelsClubMiniForm.class)
//                    .orderBy("id ASC")
//                    .execute();
//    return null;
        return db.getAllGroups();
    }

    // region Constructors
    public GroupsFragment() {
        mCCService = ServiceGenerator.createService(
                CCWebService.class,
                CCWebService.BASE_URL);

    }
    // endregion

    // region Factory Methods
    public static GroupsFragment newInstance() {
        return new GroupsFragment();
    }

    public static GroupsFragment newInstance(Bundle extras) {
        GroupsFragment fragment = new GroupsFragment();
        fragment.setArguments(extras);
        return fragment;
    }
    // endregion

    // region Lifecycle Methods

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mCCService = ServiceGenerator.createService(
                CCWebService.class,
                CCWebService.BASE_URL);

        setHasOptionsMenu(true);
//        BusProvider.getInstance().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_groups, container, false);
        ButterKnife.bind(this, rootView);
        db = new DBHandler(getActivity());


//        create_group.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent_temp = new Intent(v.getContext(), CreateGroupActivity.class);
//                startActivity(intent_temp);
//
//            }
//        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Typeface r_reg = Typeface.createFromAsset(view.getContext().getAssets(), "font/Roboto_Regular.ttf");
//        create_group_text.setTypeface(r_reg);

        groupListRecyclerView.setHasFixedSize(false);

        mLayoutManager = new LinearLayoutManager(getActivity());
        groupListRecyclerView.setLayoutManager(mLayoutManager);

        mClubAdapter = new GroupListAdapter(view.getContext());
        //mFeedAdapter.setOnItemClickListener(this);

        groupListRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        groupListRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        groupListRecyclerView.setAdapter(mClubAdapter);

        setUpAdapter();


    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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

    @Override
    public void onItemClick(int position, View view) {
        ModelsClubMiniForm club = mClubAdapter.getItem(position);

        if (club != null) {
            Intent intent = new Intent(getActivity(), FlashActivity.class);

            Bundle bundle = new Bundle();
            bundle.putParcelable("club", club);

            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}
