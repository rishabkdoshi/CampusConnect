package com.campusconnect.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//import com.activeandroid.query.Select;
import com.campusconnect.R;
import com.campusconnect.activity.InAdminGroupPageActivity;
import com.campusconnect.bean.GroupBean;
import com.campusconnect.communicator.models.ModelsClubMiniForm;
import com.campusconnect.database.DBHandler;
import com.campusconnect.utility.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rkd on 28/12/15.
 */
public class ClubListAdapterActivity extends
        RecyclerView.Adapter<ClubListAdapterActivity.ClubListViewHolder> {
    //private static List<CollegeListInfoBean> CollegeList;
    private static List<ModelsClubMiniForm> groupList;

    static int pos;
    static Context context;

    private DBHandler db;

    public ClubListAdapterActivity(List<ModelsClubMiniForm> groupList,Context context) {
        this.context=context;
        db = new DBHandler(context);
        this.groupList=setUpGroups(groupList);

    }

    private List<ModelsClubMiniForm> setUpGroups(List<ModelsClubMiniForm> groupList) {
        List<ModelsClubMiniForm> modelsClubMiniForms = new ArrayList<>();

        for (int i=0;i<groupList.size();i++) {

            ModelsClubMiniForm group=groupList.get(i);

            ModelsClubMiniForm modelsClubMiniForm = db.getClubByClubId(group.getClubId());

//            List<ModelsClubMiniForm> club = new Select()
//                    .from(ModelsClubMiniForm.class)
//                    .where("clubId = ?", group.getClubId())
//                    .orderBy("id ASC")
//                    .execute();
            modelsClubMiniForms.add(modelsClubMiniForm);

        }
        ModelsClubMiniForm modelsClubMiniForm = new ModelsClubMiniForm();
        modelsClubMiniForm.setAbbreviation("Select the club");
        modelsClubMiniForms.add(0,modelsClubMiniForm);

        return modelsClubMiniForms;
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    @Override
    public void onBindViewHolder(ClubListViewHolder group_listViewHolder, int i) {
        try {

            ModelsClubMiniForm ci = groupList.get(i);


            group_listViewHolder.club_name.setText(ci.getAbbreviation());
            if (i == 0) {
                group_listViewHolder.club_name.setTypeface(null, Typeface.BOLD);
                group_listViewHolder.club_name.setGravity(Gravity.CENTER);
                group_listViewHolder.club_list.setClickable(false);
                group_listViewHolder.group_icon_admin.setVisibility(View.GONE);
                group_listViewHolder.tv_followers.setVisibility(View.GONE);
                group_listViewHolder.tv_members.setVisibility(View.GONE);
                group_listViewHolder.tv_followers_count.setVisibility(View.GONE);
                group_listViewHolder.tv_members_count.setVisibility(View.GONE);
            } else {
                group_listViewHolder.club_name.setTypeface(null, Typeface.NORMAL);
                group_listViewHolder.club_name.setGravity(Gravity.LEFT);
                group_listViewHolder.tv_members_count.setText(ci.getMemberCount());
                group_listViewHolder.tv_followers_count.setText(ci.getFollowerCount());
                try {
                    Picasso.with(context).load(ci.getPhotoUrl()).into(group_listViewHolder.group_icon_admin);
                } catch (Exception e) {
                    Picasso.with(context).load(R.mipmap.default_image).into(group_listViewHolder.group_icon_admin);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public ClubListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.activity_card_layout_club_list, viewGroup, false);

        return new ClubListViewHolder(itemView);
    }

    private List<ModelsClubMiniForm> listAll(String clubId) {
        // This is how you execute a query
//        return new Select()
//                .from(ModelsClubMiniForm.class)
//                .orderBy("id ASC")
//                .where("clubId = ?", clubId)
//                .execute();

//    return null;

        return db.getAllGroups();
    }

    public static class ClubListViewHolder extends RecyclerView.ViewHolder {

        CardView club_list;
        TextView club_name;
        CircularImageView group_icon_admin;
        View t_divider, i_divider;
        TextView tv_followers,tv_members;
        TextView tv_members_count,tv_followers_count;

        public ClubListViewHolder(View v) {
            super(v);

            club_list = (CardView) v.findViewById(R.id.club_list);
            club_name = (TextView) v.findViewById(R.id.tv_club_name);
//            t_divider = (View) v.findViewById(R.id.title_divider);
//            i_divider = (View) v.findViewById(R.id.item_divider);
            tv_followers_count = (TextView) v.findViewById(R.id.tv_followers_count);
            tv_members_count = (TextView) v.findViewById(R.id.tv_members_count);
            group_icon_admin = (CircularImageView) v.findViewById(R.id.group_icon_admin);
            tv_followers = (TextView) v.findViewById(R.id.tv_text_followers);
            tv_members = (TextView) v.findViewById(R.id.tv_text_members);

            club_list.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pos = getAdapterPosition();
                    Intent intent_temp = new Intent(v.getContext(), InAdminGroupPageActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("club", groupList.get(pos));
                    intent_temp.putExtras(bundle);
                    v.getContext().startActivity(intent_temp);
                    ((Activity)context).finish();
                }
            });
        }
    }
}