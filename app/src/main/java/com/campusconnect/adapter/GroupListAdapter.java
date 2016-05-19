package com.campusconnect.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.campusconnect.R;
import com.campusconnect.activity.GroupPageActivity;
import com.campusconnect.bean.GroupBean;
import com.campusconnect.communicator.CCWebService;
import com.campusconnect.communicator.ServiceGenerator;
import com.campusconnect.communicator.models.ModelsClubMiniForm;
import com.campusconnect.communicator.models.ModelsFeed;
import com.campusconnect.communicator.models.ModelsFollowClubMiniForm;
import com.campusconnect.communicator.models.ModelsUpdateStatus;
import com.campusconnect.constant.AppConstants;
import com.campusconnect.database.DBHandler;
import com.campusconnect.database.DatabaseHandler;
import com.campusconnect.utility.SharedpreferenceUtility;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
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
public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.GroupListViewHolder> {

    private List<ModelsClubMiniForm> GroupList;
    int posi = 0;

    public String dbFollow = "1";
    public String dbUnFollow = "0";

    Context context;

    CCWebService mCCService;
    private OnItemClickListener mOnItemClickListener;

    private DBHandler db;


    // region Constructors
    public GroupListAdapter(Context context) {
        GroupList = new ArrayList<>();
        this.context = context;
        mCCService = ServiceGenerator.createService(
                CCWebService.class,
                CCWebService.BASE_URL);
        db = new DBHandler(context);

    }
    // endregion

    public GroupListAdapter(List<ModelsClubMiniForm> GroupList) throws IOException {
        this.GroupList = GroupList;
        mCCService = ServiceGenerator.createService(
                CCWebService.class,
                CCWebService.BASE_URL);
    }

    public interface OnItemClickListener {
        void onItemClick(int position, View view);
    }

    public ModelsClubMiniForm getItem(int position) {
        return GroupList.get(position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    // region Helper Methods
    private void add(ModelsClubMiniForm item) {
        GroupList.add(item);
        notifyItemInserted(GroupList.size() - 1);
    }

    public void addAll(List<ModelsClubMiniForm> clubs) {
        for (ModelsClubMiniForm club : clubs) {
            add(club);
        }
    }

    @Override
    public int getItemCount() {
        return GroupList.size();
    }


    @Override
    public void onBindViewHolder(GroupListViewHolder group_listViewHolder, int i) {
        posi = i;

        group_listViewHolder.group_title.setText(GroupList.get(i).getName());

        String following = GroupList.get(posi).getIsFollower();

        if (GroupList != null && following != null) {

            if (following.equals("Y")) {
                group_listViewHolder.following.setVisibility(View.VISIBLE);
                group_listViewHolder.follow.setVisibility(View.INVISIBLE);
            } else {
                group_listViewHolder.following.setVisibility(View.INVISIBLE);
                group_listViewHolder.follow.setVisibility(View.VISIBLE);
            }
        }
    }

    public void remove(ModelsClubMiniForm item) {
        int position = GroupList.indexOf(item);
        if (position > -1) {
            GroupList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    @Override
    public GroupListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.activity_card_layout_group_list, viewGroup, false);


        return new GroupListViewHolder(itemView);
    }

    public class GroupListViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.group_list_card)
        CardView group_list;

        @Bind(R.id.tv_follow)
        TextView follow;

        @Bind(R.id.tv_following)
        TextView following;

        @Bind(R.id.tv_group_name)
        TextView group_title;

        CCWebService mCCService;

        public GroupListViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);


            mCCService = ServiceGenerator.createService(
                    CCWebService.class,
                    CCWebService.BASE_URL);

            group_list.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent_temp = new Intent(v.getContext(), GroupPageActivity.class);
                    posi = getPosition();
                    ModelsClubMiniForm bean = GroupList.get(posi);
                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("BEAN", bean);
                    bundle.putParcelable("club", bean);
//                    bundle.putString("follow", GroupList.get(posi).getIsFollower());
                    intent_temp.putExtras(bundle);
                    v.getContext().startActivity(intent_temp);
                }
            });

            follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        posi = getPosition();
                        follow.setVisibility(View.INVISIBLE);
                        following.setVisibility(View.VISIBLE);


                        GroupList.get(posi).setIsFollower("Y");
//                        db.addGroupItem(GroupList.get(posi));
//                        GroupList.get(posi).save();
                        db.updateFollowUnfollow(GroupList.get(posi).getClubId(),"Y");


                        ModelsFollowClubMiniForm modelsFollowClubMiniForm = new ModelsFollowClubMiniForm();
                        String pid = SharedpreferenceUtility.getInstance(context).getString(AppConstants.PERSON_PID);
                        modelsFollowClubMiniForm.setFromPid(pid);
                        modelsFollowClubMiniForm.setClubId(GroupList.get(posi).getClubId());
                        Call followClub = mCCService.followClub(modelsFollowClubMiniForm);
                        followClub.enqueue(callback);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            following.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        posi = getPosition();
                        follow.setVisibility(View.VISIBLE);
                        following.setVisibility(View.INVISIBLE);

                        GroupList.get(posi).setIsFollower("N");
//                        db.addGroupItem(GroupList.get(posi));
//                        GroupList.get(posi).save();
                        db.updateFollowUnfollow(GroupList.get(posi).getClubId(),"N");

                        ModelsFollowClubMiniForm modelsFollowClubMiniForm = new ModelsFollowClubMiniForm();
                        String pid = SharedpreferenceUtility.getInstance(context).getString(AppConstants.PERSON_PID);
                        modelsFollowClubMiniForm.setFromPid(pid);
                        modelsFollowClubMiniForm.setClubId(GroupList.get(posi).getClubId());
                        Call unfollowClub = mCCService.unfollowClub(modelsFollowClubMiniForm);
                        unfollowClub.enqueue(callback);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }

        private Callback<Void> callback = new Callback<Void>() {
            @Override
            public void onResponse(Response<Void> response, Retrofit retrofit) {

//            Log.d("findVid", response.body().toString());
                if (response != null) {
                    if (response.isSuccess()) {


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
    }
}