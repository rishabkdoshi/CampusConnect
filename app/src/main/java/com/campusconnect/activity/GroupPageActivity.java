package com.campusconnect.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.campusconnect.R;
import com.campusconnect.adapter.GroupPageAdapterActivity;
import com.campusconnect.adapter.GroupPage_infoActivity;
import com.campusconnect.bean.GroupBean;
import com.campusconnect.communicator.WebRequestTask;
import com.campusconnect.communicator.WebServiceDetails;
import com.campusconnect.communicator.models.ModelsClubMiniForm;
import com.campusconnect.communicator.models.ModelsFeed;
import com.campusconnect.constant.AppConstants;
import com.campusconnect.database.DatabaseHandler;
import com.campusconnect.utility.DividerItemDecoration;
import com.campusconnect.utility.NetworkAvailablity;
import com.campusconnect.utility.SharedpreferenceUtility;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GroupPageActivity extends BaseActivity {

    @Bind(R.id.recycler_group_page)
    RecyclerView group_page;
    @Bind(R.id.tv_group_title)
    TextView group_page_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_group_page);
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        Typeface r_med = Typeface.createFromAsset(getAssets(), "font/Roboto_Medium.ttf");
        group_page_title.setTypeface(r_med);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        group_page.setLayoutManager(llm);
        group_page.setHasFixedSize(true);
        group_page.setItemAnimator(new DefaultItemAnimator());

        final ModelsClubMiniForm club = getIntent().getParcelableExtra("club");

        if (club != null) {
            GroupBean bean = new GroupBean();
            bean.setDescription(club.getDescription());
            bean.setPhotourl(club.getPhotoUrl());
            bean.setMemberCount(club.getMemberCount());
            bean.setFollowCount(club.getFollowerCount());

            if (club.getIsFollower().equals("Y")) {
                bean.setFollow("1");
            } else {
                bean.setFollow("0");
            }

            if (club.getIsMember().equals("Y")) {
                bean.setIsMember("1");
            } else {
                bean.setIsMember("0");
            }

            bean.setAbb(club.getAbbreviation());
            bean.setName(club.getName());
            bean.setAdmin(club.getAdminName());
            bean.setClubId(club.getClubId());
//            bean.setFollowCount(club.getFollowerCount());
//            bean.setMemberCount(club.getMemberCount());

            GroupPageAdapterActivity gp = new GroupPageAdapterActivity(createList_group_page(5), bean, GroupPageActivity.this);
            group_page.setAdapter(gp);
        }
        String frag_location = MainActivity.frag_loc;
        UpdateUi(frag_location);

    }

    private List<GroupPage_infoActivity> createList_group_page(int size) {

        List<GroupPage_infoActivity> result = new ArrayList<GroupPage_infoActivity>();
        for (int i = 1; i <= size; i++) {
            GroupPage_infoActivity ci = new GroupPage_infoActivity();
            result.add(ci);
        }
        return result;
    }

}
