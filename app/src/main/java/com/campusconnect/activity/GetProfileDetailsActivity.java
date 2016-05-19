package com.campusconnect.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.campusconnect.R;
import com.campusconnect.adapter.InterestsAdapter;
import com.campusconnect.communicator.CCWebService;
import com.campusconnect.communicator.ServiceGenerator;
import com.campusconnect.communicator.WebRequestTask;
import com.campusconnect.communicator.WebServiceDetails;
import com.campusconnect.communicator.models.ModelsClubMiniForm;
import com.campusconnect.communicator.models.ModelsComments;
import com.campusconnect.communicator.models.ModelsCommentsListResponse;
import com.campusconnect.communicator.models.ModelsProfileMiniForm;
import com.campusconnect.constant.AppConstants;
import com.campusconnect.database.ModelsTags;
import com.campusconnect.utility.LogUtility;
import com.campusconnect.utility.SharedpreferenceUtility;
import com.google.common.base.Strings;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

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
 * Created by RK on 26-09-2015.
 */
public class GetProfileDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.b_continue)
    Button cont;
    @Bind(R.id.switch_student)
    TextView switch_stu;
    @Bind(R.id.switch_alumni)
    TextView switch_alu;
    @Bind(R.id.et_batch)
    EditText et_batch;
    @Bind(R.id.et_branch)
    AutoCompleteTextView et_branch;
    @Bind(R.id.et_name)
    EditText et_name;
    @Bind(R.id.et_location)
    EditText et_location;
    @Bind(R.id.et_company)
    EditText et_company;
    @Bind(R.id.tv_interests)
    TextView tv_interests;
    @Bind(R.id.bk_college_image)
    ImageView bk_college;

    @Bind(R.id.student_alumni_switch)
    Switch sw_student;

    private static final String LOG_TAG = "GetProfileDetails";

    RecyclerView interests_list;

    ProgressDialog dialog;

    private String mEmailAccount = "";
    private String name = "", batch = "", branch = "", location = "";
    String[] branches = {"Civil", "Mechanical", "Information Technology", "Chemical",
            "Computer Science", "Electronics and Communication Engineering", "Electronics and Electrical Engineering",
            "Metallurgy", "Mining", "MTech", "MCA", "PhD", "MBA"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_profile_details);
        ButterKnife.bind(this);

        Typeface r_reg = Typeface.createFromAsset(getAssets(), "font/Roboto_Regular.ttf");

        //Setting up background image
        if (SharedpreferenceUtility.getInstance(this).getString(AppConstants.COLLEGE_ID).equals("4847453903781888")){
            bk_college.setImageResource(R.drawable.nitk_app);
        }else if(SharedpreferenceUtility.getInstance(this).getString(AppConstants.COLLEGE_ID).equals("5749882031702016")){
            bk_college.setImageResource(R.drawable.signup_back);
        }

//        List<ModelsTags> allTags=listAll();
        if(SharedpreferenceUtility.getInstance(GetProfileDetailsActivity.this).getString(AppConstants.PERSON_TAGS)==null){
            SharedpreferenceUtility.getInstance(GetProfileDetailsActivity.this).putString(AppConstants.PERSON_TAGS, " ;");
        }

        name = SharedpreferenceUtility.getInstance(GetProfileDetailsActivity.this).getString(AppConstants.PERSON_NAME);
        batch = SharedpreferenceUtility.getInstance(GetProfileDetailsActivity.this).getString(AppConstants.BATCH);
        branch = SharedpreferenceUtility.getInstance(GetProfileDetailsActivity.this).getString(AppConstants.BRANCH);
        location = SharedpreferenceUtility.getInstance(GetProfileDetailsActivity.this).getString(AppConstants.COLLEGE_LOCATION);
        mEmailAccount = SharedpreferenceUtility.getInstance(GetProfileDetailsActivity.this).getString(AppConstants.EMAIL_KEY);

        et_name.setText(name);
        et_batch.setText(batch);
        et_branch.setText(branch);
        et_location.setText(location);
        tv_interests.setText(SharedpreferenceUtility.getInstance(GetProfileDetailsActivity.this).getString(AppConstants.PERSON_TAGS));


        //PLEASE ATTACH THE COLLEGE NAME OR THE COLLEGE ID, WHATEVER'S CONVENIENT RKD          -- Rakesh
        if (SharedpreferenceUtility.getInstance(this).getString(AppConstants.COLLEGE_ID).equals("5749882031702016")) {
            et_batch.setHint("Batch");
            et_branch.setVisibility(View.GONE);
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, branches);
        et_branch.setThreshold(0);
        et_branch.setAdapter(adapter);

        et_name.setTypeface(r_reg);
        et_batch.setTypeface(r_reg);
        et_branch.setTypeface(r_reg);
        et_location.setTypeface(r_reg);
        et_company.setTypeface(r_reg);
        tv_interests.setTypeface(r_reg);

        sw_student.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (!isChecked) {
                    SharedpreferenceUtility.getInstance(GetProfileDetailsActivity.this).putString(AppConstants.ALUMNI, "N");
                    et_location.setVisibility(View.INVISIBLE);
                    et_company.setVisibility(View.INVISIBLE);
                    switch_stu.setTextColor(Color.rgb(250, 209, 86));
                    switch_alu.setTextColor(Color.rgb(160, 160, 160));
                } else {
                    SharedpreferenceUtility.getInstance(GetProfileDetailsActivity.this).putString(AppConstants.ALUMNI, "Y");
                    et_company.setVisibility(View.VISIBLE);
                    et_location.setVisibility(View.VISIBLE);
                    switch_alu.setTextColor(Color.rgb(250, 209, 86));
                    switch_stu.setTextColor(Color.rgb(160, 160, 160));
                }

            }
        });

        tv_interests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InterestsSelectionDialog confirmDialog = new InterestsSelectionDialog((Activity) v.getContext());
                Window window = confirmDialog.getWindow();
                window.setLayout(450, 600);
                confirmDialog.show();

            }
        });

        cont.setOnClickListener(this);
    }

    private void createProfile(View v) {


        //String str_phone = et_phone.getText().toString().trim();
        //if (str_phone == null) {
        //    str_phone = "";
        //}
        String str_batch = et_batch.getText().toString().trim();
        if (str_batch == null) {
            str_batch = "";
        }
        String str_branch = et_branch.getText().toString().trim();
        if (str_branch == null) {
            str_branch = "";
        }

        String isAlumini = SharedpreferenceUtility.getInstance(GetProfileDetailsActivity.this).getString(AppConstants.ALUMNI);
        if (isAlumini.equals("")) {
            isAlumini = "N";
        }

        String str_company = et_company.getText().toString().trim();
        if (str_company == null) {
            str_company = "";
        }
        String str_location = et_location.getText().toString().trim();
        if (str_location == null) {
            str_location = "";
        }

        SharedpreferenceUtility.getInstance(GetProfileDetailsActivity.this).putString(AppConstants.BATCH, str_batch);
        //SharedpreferenceUtility.getInstance(GetProfileDetailsActivity.this).putString(AppConstants.PHONE,str_phone);
        SharedpreferenceUtility.getInstance(GetProfileDetailsActivity.this).putString(AppConstants.BRANCH, str_branch);
        String name = SharedpreferenceUtility.getInstance(GetProfileDetailsActivity.this).getString(AppConstants.PERSON_NAME);
        String email = SharedpreferenceUtility.getInstance(GetProfileDetailsActivity.this).getString(AppConstants.EMAIL_KEY);
        String collegeId = SharedpreferenceUtility.getInstance(GetProfileDetailsActivity.this).getString(AppConstants.COLLEGE_ID);
        String gcmId = SharedpreferenceUtility.getInstance(GetProfileDetailsActivity.this).getString(AppConstants.GCM_TOKEN);
        String photoUrl = SharedpreferenceUtility.getInstance(GetProfileDetailsActivity.this).getString(AppConstants.PHOTO_URL);

        ModelsProfileMiniForm modelsProfileMiniForm = new ModelsProfileMiniForm();
        modelsProfileMiniForm.setName(name);
        modelsProfileMiniForm.setPhone("");
        modelsProfileMiniForm.setIsAlumni(isAlumini);
        modelsProfileMiniForm.setEmail(email);
        modelsProfileMiniForm.setCollegeId(collegeId);
        modelsProfileMiniForm.setBranch(str_branch);
        modelsProfileMiniForm.setBatch(str_batch);
        modelsProfileMiniForm.setGcmId(gcmId);
        modelsProfileMiniForm.setPhotoUrl(photoUrl);
        ArrayList<String> tags=new ArrayList<String>();


        String a=SharedpreferenceUtility.getInstance(GetProfileDetailsActivity.this).getString(AppConstants.PERSON_TAGS);
        tags.add(a);
        modelsProfileMiniForm.setTags(tags);

        if (isAlumini.equals("Y")) {
            modelsProfileMiniForm.setCompany(str_company);
            modelsProfileMiniForm.setLocation(str_location);
        }

        CCWebService mCCService = ServiceGenerator.createService(
                CCWebService.class,
                CCWebService.BASE_URL);

        Call createProfile=mCCService.createProfile(modelsProfileMiniForm);
        createProfile.enqueue(callBack);

        dialog = new ProgressDialog(GetProfileDetailsActivity.this);
        dialog.setCancelable(false);
        dialog.setMessage("Signing you up...");
        dialog.show();

    }

    private Callback<ModelsProfileMiniForm> callBack = new Callback<ModelsProfileMiniForm>() {
        @Override
        public void onResponse(Response<ModelsProfileMiniForm> response, Retrofit retrofit) {
            dialog.dismiss();

            if (response != null) {
                if (response.isSuccess()) {
//                    Log.d("findVid",response.raw());
                    ModelsProfileMiniForm modelsProfileMiniForm = response.body();
                    Log.d("pid",response.body().getPid());
                    SharedpreferenceUtility.getInstance(GetProfileDetailsActivity.this).putString(AppConstants.COLLEGE_ID, modelsProfileMiniForm.getCollegeId());
                    SharedpreferenceUtility.getInstance(GetProfileDetailsActivity.this).putString(AppConstants.PERSON_PID, modelsProfileMiniForm.getPid());
                    Intent intent_temp = new Intent(GetProfileDetailsActivity.this, MainActivity.class);
                    startActivity(intent_temp);
                    finish();
                } else {
                    com.squareup.okhttp.Response rawResponse = response.raw();
                    if (rawResponse != null) {
                        LogUtility.logFailedResponse(rawResponse);

                        int code = rawResponse.code();
                        switch (code) {
                            case 500:
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


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_continue:
                createProfile(v);

                break;
        }

    }

//    private List<ModelsTags> listAll() {
//        // This is how you execute a query
//        return new Select()
//                .from(ModelsTags.class)
//                .orderBy("id ASC")
//                .execute();
////    return null;
//
//    }
    private final Handler _handler = new Handler() {
        public void handleMessage(Message msg) {
            int response_code = msg.what;
            if (response_code != 0) {
                String strResponse = (String) msg.obj;
                Log.v("Response", strResponse);
                if (strResponse != null && strResponse.length() > 0) {
                    switch (response_code) {
                        case WebServiceDetails.PID_SAVE_PROFILE: {
                            try {
                                JSONObject profileObj = new JSONObject(strResponse);
                                String pid = profileObj.getString("pid");
                                String collegeid = profileObj.getString("collegeId");
                                Log.v("GetProfileDetails", pid + " " + collegeid);
                                SharedpreferenceUtility.getInstance(GetProfileDetailsActivity.this).putString(AppConstants.COLLEGE_ID, collegeid);
                                SharedpreferenceUtility.getInstance(GetProfileDetailsActivity.this).putString(AppConstants.PERSON_PID, pid);
                                SharedpreferenceUtility.getInstance(GetProfileDetailsActivity.this).putString(AppConstants.PERSON_PID, pid);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
//                            try {


//
//                                List<CollegeListInfoBean> list = new ArrayList<CollegeListInfoBean>();
//                                JSONObject collegeObj = new JSONObject(strResponse);
//                                if (collegeObj.has("collegeList")) {
//                                    JSONArray collegeArr = collegeObj.getJSONArray("collegeList");
//                                    for (int i = 0; i < collegeArr.length(); i++) {
//                                        JSONObject obj = collegeArr.getJSONObject(i);
//                                        String abbreviation = obj.optString("abbreviation");
//                                        String location = obj.optString("location");
//                                        String name = obj.optString("name");
//                                        String collegeid = obj.optString("collegeId");
//
//
//                                        CollegeListInfoBean bean = new CollegeListInfoBean();
//                                        bean.setCollegeId(collegeid);
//                                        bean.setLocation(location);
//                                        bean.setName(name);
//                                        list.add(bean);
//                                    }
//
//
//
//                                } else {
//                                    Toast.makeText(GetProfileDetailsActivity.this, "error", Toast.LENGTH_SHORT).show();
//
//                                }
//
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }

                        }
                        break;

                        default:
                            break;
                    }
                } else {
                    Toast.makeText(GetProfileDetailsActivity.this, "Network Not Available", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(GetProfileDetailsActivity.this, "Network Not Available", Toast.LENGTH_LONG).show();
            }
        }
    };

    public class InterestsSelectionDialog extends Dialog implements
            View.OnClickListener {

        public Activity c;
        public Dialog d;
        InterestsAdapter minterestsAdapter;
        public TextView yes;
        public TextView reset;
        Context context;

        ArrayList<String> selected;

        public InterestsSelectionDialog(Activity a) {
            super(a);
            // TODO Auto-generated constructor stub
            this.c = a;
            this.context = context;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.interests_selection_dialog);


            yes = (TextView) findViewById(R.id.btn_yes);
            reset = (TextView) findViewById(R.id.btn_reset);

//            interests_list = (RecyclerView) findViewById(R.id.gv_interests);
            interests_list = (RecyclerView) findViewById(R.id.rv_interests);

            GridLayoutManager glm = new GridLayoutManager(context,2);
            interests_list.setLayoutManager(glm);
            interests_list.setHasFixedSize(true);
            interests_list.setItemAnimator(new DefaultItemAnimator());

            minterestsAdapter = new InterestsAdapter(c);
            interests_list.setAdapter(minterestsAdapter);


            yes.setOnClickListener(this);
            reset.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_reset: {
                    if(selected!=null) {
                        selected.clear();
                    }

                    Animation fadeOut = new AlphaAnimation(1, 0);
                    fadeOut.setDuration(1000);
                    interests_list.startAnimation(fadeOut);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Animation fadeIn = new AlphaAnimation(0, 1);
                            fadeIn.setDuration(1000);
                            fadeIn.setFillAfter(true);
                            interests_list.startAnimation(fadeIn);
                            interests_list.setAdapter(minterestsAdapter);
                        }
                    }, 900);
                    tv_interests.setText("");
                    //dismiss();
                    break;
                }
                case R.id.btn_yes:
                    selected = (ArrayList<String>) minterestsAdapter.get_selected();
                    for(int i=0;i<selected.size();i++)
                        tv_interests.setText(tv_interests.getText() + selected.get(i)+" ; ");
                    SharedpreferenceUtility.getInstance(GetProfileDetailsActivity.this).putString(AppConstants.PERSON_TAGS, (String) tv_interests.getText());
                    dismiss();
                    break;
                default:
                    break;
            }
        }
    }

}
