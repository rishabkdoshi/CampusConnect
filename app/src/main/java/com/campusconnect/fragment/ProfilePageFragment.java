package com.campusconnect.fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.activeandroid.query.Select;
import com.campusconnect.R;
import com.campusconnect.activity.GetProfileDetailsActivity;
import com.campusconnect.adapter.ProfilePageAdapterActivity;
import com.campusconnect.bean.GroupBean;
import com.campusconnect.communicator.models.ModelsClubMiniForm;
import com.campusconnect.communicator.models.ModelsProfileMiniForm;
import com.campusconnect.communicator.models.ModelsProfileResponse;
import com.campusconnect.constant.AppConstants;
import com.campusconnect.database.DBHandler;
import com.campusconnect.database.DatabaseHandler;
import com.campusconnect.supportClasses.MyScrollListenerProfilePage;
import com.campusconnect.supportClasses.ProfilePage_infoActivity;
import com.campusconnect.utility.DividerItemDecoration;
import com.campusconnect.utility.NetworkAvailablity;
import com.campusconnect.utility.SharedpreferenceUtility;

import java.util.ArrayList;
import java.util.List;


public class ProfilePageFragment extends Fragment {

    RecyclerView groups_joined;
    int top = 0;
    TextView profile_title;
    ImageButton noti, profile, home, calendar, search;
    ImageView editImage;
    View mRootView;

    DBHandler db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView != null) {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (parent != null)
                parent.removeView(mRootView);
        }

        mRootView = inflater.inflate(R.layout.activity_profile_page, container, false);

        profile_title = (TextView) mRootView.findViewById(R.id.tv_profile_text);
        Typeface r_med = Typeface.createFromAsset(mRootView.getContext().getAssets(), "font/Roboto_Medium.ttf");
        profile_title.setTypeface(r_med);

        groups_joined = (RecyclerView) mRootView.findViewById(R.id.recycler_groups);
        editImage = (ImageView) mRootView.findViewById(R.id.ib_create_post);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        groups_joined.setLayoutManager(llm);
        groups_joined.setHasFixedSize(true);
        groups_joined.setItemAnimator(new DefaultItemAnimator());

        db = new DBHandler(getActivity());

        List<ModelsClubMiniForm> GroupList;
        Bundle bundle = getArguments();

        ModelsProfileMiniForm modelsProfileMiniForm=null;
        if(bundle!=null) {
            modelsProfileMiniForm = bundle.getParcelable("profile");
        }
        if (modelsProfileMiniForm != null) {
            GroupList = getClubData(modelsProfileMiniForm.getFollows());
        } else {
            modelsProfileMiniForm = new ModelsProfileMiniForm();
            modelsProfileMiniForm.setPhotoUrl(SharedpreferenceUtility.getInstance(getContext()).getString(AppConstants.PHOTO_URL));
            modelsProfileMiniForm.setPid(SharedpreferenceUtility.getInstance(getContext()).getString(AppConstants.PERSON_PID));
            modelsProfileMiniForm.setBatch(SharedpreferenceUtility.getInstance(getContext()).getString(AppConstants.BATCH));
            modelsProfileMiniForm.setBranch(SharedpreferenceUtility.getInstance(getContext()).getString(AppConstants.BRANCH));
            modelsProfileMiniForm.setEmail(SharedpreferenceUtility.getInstance(getContext()).getString(AppConstants.EMAIL_KEY));
            modelsProfileMiniForm.setName(SharedpreferenceUtility.getInstance(getContext()).getString(AppConstants.PERSON_NAME));

            String tags=SharedpreferenceUtility.getInstance(getContext()).getString(AppConstants.PERSON_TAGS);
            List<String> tlist=new ArrayList<>();
            tlist.add(tags);
            modelsProfileMiniForm.setTags(tlist);
            GroupList = getFollowingClubData();
        }
        if (GroupList == null || GroupList.size() == 0) {
            GroupList = new ArrayList<ModelsClubMiniForm>();
        }
        if (GroupList.size() == 0) {
            ModelsClubMiniForm gb = new ModelsClubMiniForm();
            GroupList.add(0, gb);
        } else {
            //Adding to 1st entry to GroupList again,as the first entry doesnt seem to come up
            GroupList.add(GroupList.size(), GroupList.get(0));
        }
        ProfilePageAdapterActivity gj = new ProfilePageAdapterActivity(
                GroupList, getContext(), modelsProfileMiniForm);
        groups_joined.setAdapter(gj);

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editPage = new Intent(getActivity(), GetProfileDetailsActivity.class);
                startActivity(editPage);
            }
        });


        try {
            groups_joined.setOnScrollListener(new MyScrollListenerProfilePage(getActivity()) {

                @Override
                public void onMoved(int distance) {
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    top += dy;
                }
            });

        } catch (InflateException e) {
            e.printStackTrace();
        }
        return mRootView;
    }

    private List<ModelsClubMiniForm> getClubData(List<String> follows) {
        List<ModelsClubMiniForm> modelsClubMiniForms = new ArrayList<>();

        for (String follow : follows) {
            ModelsClubMiniForm modelsClubMiniForm=db.getClubByClubId(follow);

//            List<ModelsClubMiniForm> club = new Select()
//                    .from(ModelsClubMiniForm.class)
//                    .where("clubId = ?", follow)
//                    .orderBy("id ASC")
//                    .execute();
            modelsClubMiniForms.add(modelsClubMiniForm);

        }

        return modelsClubMiniForms;
    }



    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        overridePendingTransition(R.anim.pushfront, R.anim.centertoright);

        home = (ImageButton) findViewById(R.id.ib_home);
        noti = (ImageButton) findViewById(R.id.ib_notification);
        profile = (ImageButton) findViewById(R.id.ib_profile);
        calendar = (ImageButton) findViewById(R.id.ib_calendar);
        search = (ImageButton) findViewById(R.id.ib_search);


        noti.setOnClickListener(this);
        home.setOnClickListener(this);
        calendar.setOnClickListener(this);
        search.setOnClickListener(this);
    }*/

//TODO  changes in the listner
  /*  @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_search:
                Intent intent_serch = new Intent(v.getContext(), SearchFragment.class);
                startActivity(intent_serch);
                finish();
                break;
            case R.id.ib_home:
                Intent intent_home = new Intent(v.getContext(), HomeFragment.class);
                startActivity(intent_home);
                overridePendingTransition(R.anim.pushfront, R.anim.centertoright);
                finish();
                break;
            case R.id.ib_notification:
                Intent intent_noti = new Intent(v.getContext(), NotificationFragment.class);
                startActivity(intent_noti);
                finish();
                break;
            case R.id.ib_calendar:
                Intent intent_cal = new Intent(v.getContext(), CalenderFragment.class);
                startActivity(intent_cal);
                finish();
                break;
        }
    }*/

/*

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
*/


    /* @Override
     public boolean onOptionsItemSelected(MenuItem item) {
         // Handle action bar item clicks here. The action bar will
         // automatically handle clicks on the Home/Up button, so long
         // as you specify a parent activity in AndroidManifest.xml.
         int id = item.getItemId();

         //noinspection SimplifiableIfStatement
         if (id == R.id.action_settings) {
             return true;
         }
         return super.onOptionsItemSelected(item);
     }
 */


    public List<ModelsClubMiniForm> getFollowingClubData() {
//        return new Select()
//                .from(ModelsClubMiniForm.class)
//                .where("isFollower = ?", "Y")
//                .orderBy("id ASC")
//                .execute();

        return db.getAllFollowingClubs();
    }


}
