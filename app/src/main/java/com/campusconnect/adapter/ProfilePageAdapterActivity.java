package com.campusconnect.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
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

//import com.activeandroid.query.Select;
import com.campusconnect.R;
import com.campusconnect.activity.GroupPageActivity;
import com.campusconnect.communicator.models.ModelsClubMiniForm;
import com.campusconnect.communicator.models.ModelsProfileMiniForm;
import com.campusconnect.database.DBHandler;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.campusconnect.R.drawable.default_profile;


/**
 * Created by RK on 11-09-2015.
 */
public class ProfilePageAdapterActivity extends
        RecyclerView.Adapter<ProfilePageAdapterActivity.GroupsViewHolder> {

    ProfileInfoViewHolder holder1;
    GroupsJoinedListHolder holder2;

    ModelsProfileMiniForm modelsProfileMiniForm;
    List<ModelsClubMiniForm> GroupsJoinedList;

    private DBHandler db;

    int posi = 0;

    Context context;

    public ProfilePageAdapterActivity(List<ModelsClubMiniForm> GroupsJoinedList, Context context, ModelsProfileMiniForm modelsProfileMiniForm) {
        this.GroupsJoinedList = GroupsJoinedList;
        this.context = context;
        this.modelsProfileMiniForm=modelsProfileMiniForm;
        db = new DBHandler(context);
    }

    @Override
    public int getItemCount() {
        return GroupsJoinedList.size();
    }

    @Override
    public void onBindViewHolder(GroupsViewHolder groupViewHolder, int i) {
        if (getItemViewType(i) == 0) {
            holder1 = (ProfileInfoViewHolder) groupViewHolder;
            String url = modelsProfileMiniForm.getPhotoUrl();
            try {
                Picasso.with(context).load(url).into(holder1.profile_image);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            holder1.profile_name.setText(modelsProfileMiniForm.getName());
            holder1.tv_branch.setText(modelsProfileMiniForm.getBranch());
            holder1.tv_batch_of.setText("Batch of " + modelsProfileMiniForm.getBatch());

            if(modelsProfileMiniForm.getTags()!=null&&modelsProfileMiniForm.getTags().size()>0) {
                holder1.profile_tags.setText(modelsProfileMiniForm.getTags().get(0));
            }


            if (GroupsJoinedList.size() == 1) {
                holder1.grps_heading.setVisibility(View.GONE);
                holder1.no_grps_heading.setVisibility(View.VISIBLE);
            }
        } else {
            if (GroupsJoinedList.size() > 0 && GroupsJoinedList.size() != 1) {
                holder2 = (GroupsJoinedListHolder) groupViewHolder;
                Log.d("ProfilePageAdapter", String.valueOf(i));
                holder2.group_joined.setText(this.GroupsJoinedList.get(i - 1).getAbbreviation());
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

        public GroupsViewHolder(View v) {
            super(v);

        }

    }

    public class GroupsJoinedListHolder extends ProfilePageAdapterActivity.GroupsViewHolder {

        @Bind(R.id.tv_group_joined_name)
        TextView group_joined;
        @Bind(R.id.cv_groups_joined)
        CardView card_g_joined;

        public GroupsJoinedListHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            card_g_joined.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    posi = getAdapterPosition() - 1;


                    ModelsClubMiniForm modelsClubMiniForm = db.getClubByClubId(GroupsJoinedList.get(posi).getClubId());

//                    List<ModelsClubMiniForm> modelsClubMiniForms = new Select().from(ModelsClubMiniForm.class).where("clubId = ?", GroupsJoinedList.get(posi).getClubId()).execute();
                    if (modelsClubMiniForm!=null) {
//                        ModelsClubMiniForm modelsClubMiniForm = modelsClubMiniForms.get(0);
                        Intent intent_temp = new Intent(v.getContext(), GroupPageActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("club", modelsClubMiniForm);
                        intent_temp.putExtras(bundle);
                        v.getContext().startActivity(intent_temp);
                    }

//                    Intent intent_temp = new Intent(v.getContext(), GroupPageActivity.class);
//
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("BEAN",GroupsJoinedList.get(posi));
//                  //  bundle.putString("G_ID", (String) ProfilePageAdapterActivity.this.GroupsJoinedList.get(posi));
//                    intent_temp.putExtras(bundle);
//
//                    v.getContext().startActivity(intent_temp);

                }
            });

        }
    }

    public class ProfileInfoViewHolder extends ProfilePageAdapterActivity.GroupsViewHolder {

        @Bind(R.id.iv_gmail_icon)
        ImageView gmail_icon;
        @Bind(R.id.profile_image)
        ImageView profile_image;
        @Bind(R.id.profile_name)
        TextView profile_name;
        @Bind(R.id.profile_tag)
        TextView profile_tags;
        @Bind(R.id.tv_branch)
        TextView tv_branch;
        @Bind(R.id.tv_batch_of)
        TextView tv_batch_of;
        @Bind(R.id.tv_groups_joined_text)
        TextView grps_heading;
        @Bind(R.id.tv_groups_not_joined_text)
        TextView no_grps_heading;

        public ProfileInfoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            Typeface r_reg = Typeface.createFromAsset(itemView.getContext().getAssets(), "font/Roboto_Regular.ttf");
            Typeface r_lig = Typeface.createFromAsset(itemView.getContext().getAssets(), "font/Roboto_Light.ttf");

            profile_name.setTypeface(r_reg);
            profile_tags.setTypeface(r_reg);
            tv_branch.setTypeface(r_reg);
            tv_batch_of.setTypeface(r_reg);
            grps_heading.setTypeface(r_lig);
            no_grps_heading.setTypeface(r_lig);

            gmail_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    String email = modelsProfileMiniForm.getEmail();
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
