package com.campusconnect.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.campusconnect.R;
import com.campusconnect.activity.InEventActivity;
import com.campusconnect.activity.MainActivity;
import com.campusconnect.bean.GroupMemberBean;
import com.campusconnect.communicator.CCWebService;
import com.campusconnect.communicator.ServiceGenerator;
import com.campusconnect.communicator.models.ModelsFeed;
import com.campusconnect.communicator.models.ModelsPersonalResponse;
import com.campusconnect.communicator.models.ModelsProfileResponse;
import com.campusconnect.fragment.ProfilePageFragment;
import com.campusconnect.utility.CircularImageView;
import com.squareup.picasso.Picasso;

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
public class GroupMembersByGroupAdapterActivity extends
        RecyclerView.Adapter<GroupMembersByGroupAdapterActivity.GroupMembersByGroupViewHolder> {

    private List<ModelsPersonalResponse> GroupMembersByGroupList;
    private Context context;

    public GroupMembersByGroupAdapterActivity(List<ModelsPersonalResponse> GroupMembersByGroupList, Context context) {
        this.GroupMembersByGroupList = GroupMembersByGroupList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return GroupMembersByGroupList.size();
    }

    @Override
    public void onBindViewHolder(GroupMembersByGroupViewHolder group_membersbygroupViewHolder, int i) {
        ModelsPersonalResponse ci = GroupMembersByGroupList.get(i);
        group_membersbygroupViewHolder.member_batch.setText(ci.getBatch());
        group_membersbygroupViewHolder.member_branch.setText(ci.getBranch());
        group_membersbygroupViewHolder.member_name.setText(ci.getName());

        try {


            Picasso.with(context).load(ci.getPhotoUrl()).into(group_membersbygroupViewHolder.member_icon);

        } catch (Exception e) {
            Picasso.with(context).load(R.mipmap.default_image).into(group_membersbygroupViewHolder.member_icon);
        }
    }

    @Override
    public GroupMembersByGroupViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.activity_card_layout_member, viewGroup, false);

        return new GroupMembersByGroupViewHolder(itemView);
    }




    public class GroupMembersByGroupViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_member_name) TextView member_name;
        @Bind(R.id.tv_member_batch) TextView member_batch;
        @Bind(R.id.tv_member_branch) TextView member_branch;
        @Bind(R.id.member_card) CardView member_card;
        @Bind(R.id.group_icon_notification) CircularImageView member_icon;

        public GroupMembersByGroupViewHolder(View v) {
            super(v);
            ButterKnife.bind(this,v);

            Typeface r_reg = Typeface.createFromAsset(v.getContext().getAssets(), "font/Roboto_Regular.ttf");
            Typeface r_lig = Typeface.createFromAsset(v.getContext().getAssets(), "font/Roboto_Light.ttf");

            member_name.setTypeface(r_reg);
            member_batch.setTypeface(r_lig);
            member_branch.setTypeface(r_lig);

            final CCWebService mCCService = ServiceGenerator.createService(
                    CCWebService.class,
                    CCWebService.BASE_URL);

            member_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int posi = getAdapterPosition();
                    ModelsPersonalResponse personalResponse = GroupMembersByGroupList.get(posi);
                    Log.d("pid here",personalResponse.getName());
                    Log.d("pid here",personalResponse.getPid());

                    Call getProfile=mCCService.getProfile(personalResponse.getPid());
                    getProfile.enqueue(getProfileCallback);

                }
            });
        }

        Callback<ModelsProfileResponse> getProfileCallback = new Callback<ModelsProfileResponse>() {
            @Override
            public void onResponse(Response<ModelsProfileResponse> response, Retrofit retrofit) {
                ModelsProfileResponse modelsProfileResponse=response.body();

                Intent intent_profile = new Intent(context, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("profile", modelsProfileResponse.getResult());
                bundle.putInt("FRAGMENT",5);
                intent_profile.putExtras(bundle);
                context.startActivity(intent_profile);
            }

            @Override
            public void onFailure(Throwable t) {
                //Timber.d("onFailure() : mQuery - " + mQuery);

            }
        };

    }
}

