package com.campusconnect.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.campusconnect.R;
import com.campusconnect.activity.GroupPageActivity;
import com.campusconnect.bean.GroupBean;
import com.campusconnect.constant.AppConstants;
import com.campusconnect.utility.SharedpreferenceUtility;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.campusconnect.R.drawable.default_profile;


/**
 * Created by RK on 11-09-2015.
 */
public class ViewProfilePageAdapterActivity extends
        RecyclerView.Adapter<ViewProfilePageAdapterActivity.GroupsViewHolder> {

    ProfileInfoViewHolder holder1;
    GroupsJoinedListHolder holder2;

    @Bind(R.id.profile_image) ImageView profile_image;
    @Bind(R.id.profile_name) TextView profile_name;
    @Bind(R.id.profile_tag) TextView profile_tags;
    @Bind(R.id.tv_branch) TextView tv_branch;
    @Bind(R.id.tv_batch_of) TextView tv_batch_of;
    @Bind(R.id.tv_groups_joined_text) TextView grps_heading;
    @Bind(R.id.tv_groups_not_joined_text) TextView no_grps_heading;

    List<GroupBean> GroupsJoinedList;
    int posi = 0;

    Context context;

    public ViewProfilePageAdapterActivity(List<GroupBean> GroupsJoinedList, Context context) {
        this.GroupsJoinedList = GroupsJoinedList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return GroupsJoinedList.size();
    }

    @Override
    public void onBindViewHolder(GroupsViewHolder groupViewHolder, int i) {
        if (getItemViewType(i) == 0) {
            holder1 = (ProfileInfoViewHolder) groupViewHolder;
            //Log.d("name",SharedpreferenceUtility.getInstance(context).getString(AppConstants.PERSON_NAME));
            String url = SharedpreferenceUtility.getInstance(context).getString(AppConstants.PHOTO_URL);
            Log.d("URL",url);
            try{
                Picasso.with(context).load(url).into(profile_image);
            }catch(IllegalArgumentException e){
                //profile_image.setImageDrawable(default_profile);
                e.printStackTrace();
            }
            profile_name.setText(SharedpreferenceUtility.getInstance(context).getString(AppConstants.PERSON_NAME));
            tv_branch.setText(SharedpreferenceUtility.getInstance(context).getString(AppConstants.BRANCH));
            tv_batch_of.setText("Batch of "+SharedpreferenceUtility.getInstance(context).getString(AppConstants.BATCH));


            if (GroupsJoinedList.size() ==1){
                grps_heading.setVisibility(View.GONE);
                no_grps_heading.setVisibility(View.VISIBLE);
            }
        } else {
            if (GroupsJoinedList.size() > 0 && GroupsJoinedList.size()!=1) {
                holder2 = (GroupsJoinedListHolder) groupViewHolder;
                Log.d("ViewProfilePageAdapter",String.valueOf(i));
                holder2.group_joined.setText(this.GroupsJoinedList.get(i-1).getAbb());
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        int viewType;
        if (position == 0)
            viewType = 0;
        else
            viewType = 1;

        return viewType;
    }

    @Override
    public GroupsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        GroupsViewHolder holder;

        if (i == 0) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.activity_card_layout_profile_info, viewGroup, false);

            holder = new ProfileInfoViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.activity_card_layout_groups_joined, viewGroup, false);
            holder = new GroupsJoinedListHolder(itemView);
        }

        return holder;
    }

    public static class GroupsViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_group_joined_name) TextView group_joined;

        public GroupsViewHolder(View v) {
            super(v);
            ButterKnife.bind(this,v);

        }

    }

    public class GroupsJoinedListHolder extends ViewProfilePageAdapterActivity.GroupsViewHolder {

        @Bind(R.id.cv_groups_joined) CardView card_g_joined;

        public GroupsJoinedListHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            card_g_joined.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent_temp = new Intent(v.getContext(), GroupPageActivity.class);
                    posi = getPosition() - 1;

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("BEAN",GroupsJoinedList.get(posi));
                    //  bundle.putString("G_ID", (String) ViewProfilePageAdapterActivity.this.GroupsJoinedList.get(posi));
                    intent_temp.putExtras(bundle);

                    v.getContext().startActivity(intent_temp);

                }
            });

        }
    }

    public class ProfileInfoViewHolder extends ViewProfilePageAdapterActivity.GroupsViewHolder {

        @Bind(R.id.iv_gmail_icon) ImageView gmail_icon;

        public ProfileInfoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            gmail_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    String email = SharedpreferenceUtility.getInstance(v.getContext()).getString(AppConstants.EMAIL_KEY);
                    sendIntent.setType("plain/text");
                    sendIntent.setData(Uri.parse(email));
                    sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
                    sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                    v.getContext().startActivity(sendIntent);
                }
            });

        }
    }
}
