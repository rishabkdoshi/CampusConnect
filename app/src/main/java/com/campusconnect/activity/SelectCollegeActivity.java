package com.campusconnect.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.campusconnect.R;
import com.campusconnect.adapter.CollegeListAdapterActivity;
import com.campusconnect.bean.CollegeListInfoBean;
import com.campusconnect.communicator.CCWebService;
import com.campusconnect.communicator.ServiceGenerator;
import com.campusconnect.communicator.WebRequestTask;
import com.campusconnect.communicator.WebServiceDetails;
import com.campusconnect.communicator.models.ModelsAddCollege;
import com.campusconnect.communicator.models.ModelsMessageResponse;
import com.campusconnect.constant.AppConstants;
import com.campusconnect.utility.NetworkAvailablity;
import com.campusconnect.utility.SharedpreferenceUtility;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by RK on 23-09-2015.
 */
public class SelectCollegeActivity extends AppCompatActivity {

    @Bind(R.id.rv_college_list)
    RecyclerView collegeListRecyclerView;
    @Bind(R.id.tv_college_not_found)
    TextView tv_not_found;

    @Bind(R.id.btn_guest)
    TextView btn_guest;
    CCWebService mCCService;

    private static final String LOG_TAG = "SelectCollegeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_college);
        ButterKnife.bind(this);

        SpannableString guest_login_underline = new SpannableString("Visiting NITK for Incident");
        guest_login_underline.setSpan(new UnderlineSpan(), 0, guest_login_underline.length(), 0);
        btn_guest.setText(guest_login_underline);

        Typeface r_reg = Typeface.createFromAsset(getAssets(), "font/Roboto_Regular.ttf");
        tv_not_found.setTypeface(r_reg);
        btn_guest.setTypeface(r_reg);

        Calendar df = Calendar.getInstance();

        if(df.get(Calendar.DATE)<=7&&df.get(Calendar.MONTH)==2&&df.get(Calendar.YEAR)==2016){


            btn_guest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    int sdk = android.os.Build.VERSION.SDK_INT;
//                    if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
//                        btn_guest.setBackgroundDrawable(R.drawable.guest_login_rounded_clicked);
//                    } else {
//                        btn_guest.setBackground(R.drawable.guest_login_rounded_clicked);
//                    }
                    SharedpreferenceUtility.getInstance(SelectCollegeActivity.this).putBoolean(AppConstants.IS_INCI_GUEST,true);

                    Intent intent_temp = new Intent(v.getContext(), Signup_2Activity.class);
                    v.getContext().startActivity(intent_temp);
                    ((Activity) v.getContext()).finish();
                }

            });

        }else{
            btn_guest.setVisibility(View.GONE);

        }

        collegeListRecyclerView.setHasFixedSize(false);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        collegeListRecyclerView.setLayoutManager(llm);
        collegeListRecyclerView.setItemAnimator(new DefaultItemAnimator());

        tv_not_found.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CollegeNotFoundDialog getdetailsDialog = new CollegeNotFoundDialog((Activity) v.getContext());
                Window window = getdetailsDialog.getWindow();
                window.setLayout(450, ViewGroup.LayoutParams.WRAP_CONTENT);
                getdetailsDialog.show();
            }

        });

        if (NetworkAvailablity.hasInternetConnection(SelectCollegeActivity.this)) {
            GetAllCollegesWebAPI();

        } else {
            Toast.makeText(SelectCollegeActivity.this, "Network is not available.", Toast.LENGTH_SHORT).show();
        }
    }

    private void GetAllCollegesWebAPI() {
        JSONObject jsonObject = new JSONObject();
        List<NameValuePair> param = new ArrayList<NameValuePair>();
        String url = WebServiceDetails.DEFAULT_BASE_URL + "getColleges";
        new WebRequestTask(SelectCollegeActivity.this, param, _handler, WebRequestTask.GET, jsonObject, WebServiceDetails.PID_SELECT_COLLEGE,
                true, url).execute();
    }

    private final Handler _handler = new Handler() {
        public void handleMessage(Message msg) {
            int response_code = msg.what;
            if (response_code != 0) {
                String strResponse = (String) msg.obj;
                Log.v("Response", strResponse);
                if (strResponse != null && strResponse.length() > 0) {
                    switch (response_code) {
                        case WebServiceDetails.PID_SELECT_COLLEGE: {
                            try {

                                List<CollegeListInfoBean> list = new ArrayList<CollegeListInfoBean>();
                                JSONObject collegeObj = new JSONObject(strResponse);
                                if (collegeObj.has("collegeList")) {
                                    JSONArray collegeArr = collegeObj.getJSONArray("collegeList");
                                    CollegeListInfoBean bean = new CollegeListInfoBean();
                                    bean.setName("Select your campus");
                                    list.add(0, bean);
                                    for (int i = 0; i < collegeArr.length(); i++) {
                                        JSONObject obj = collegeArr.getJSONObject(i);
                                        String abbreviation = obj.optString("abbreviation");
                                        String location = obj.optString("location");
                                        String name = obj.optString("name");
                                        String collegeid = obj.optString("collegeId");


                                        bean = new CollegeListInfoBean();
                                        bean.setCollegeId(collegeid);
                                        bean.setLocation(location);
                                        bean.setName(name);
                                        list.add(i + 1, bean);
                                    }
                                    CollegeListAdapterActivity cl = new CollegeListAdapterActivity(list, SelectCollegeActivity.this);
                                    collegeListRecyclerView.setAdapter(cl);

                                } else {
                                    Toast.makeText(SelectCollegeActivity.this, "error", Toast.LENGTH_SHORT).show();

                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                        break;

                        default:
                            break;
                    }
                } else {
                    Toast.makeText(SelectCollegeActivity.this, "Network Not Available", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(SelectCollegeActivity.this, "Network Not Available", Toast.LENGTH_LONG).show();
            }
        }
    };

    public class CollegeNotFoundDialog extends Dialog {

        public Activity c;
        public Dialog d;
        Context context;

        @Bind(R.id.b_submit)
        Button submit;
        @Bind(R.id.et_name)
        EditText client_name;
        @Bind(R.id.et_college_name)
        EditText college_name;
        @Bind(R.id.et_email_id)
        EditText email_ID;
        @Bind(R.id.et_phone_no)
        EditText phone_no;
        @Bind(R.id.et_location)
        EditText location;

        CCWebService mCCService;

        public CollegeNotFoundDialog(Activity a) {
            super(a);
            // TODO Auto-generated constructor stub
            this.c = a;
            this.context = context;
            mCCService = ServiceGenerator.createService(
                    CCWebService.class,
                    CCWebService.BASE_URL);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.get_details_dialog);
            ButterKnife.bind(this);

            Typeface r_reg = Typeface.createFromAsset(getAssets(), "font/Roboto_Regular.ttf");

            client_name.setTypeface(r_reg);
            college_name.setTypeface(r_reg);
            email_ID.setTypeface(r_reg);
            phone_no.setTypeface(r_reg);
            submit.setTypeface(r_reg);
            location.setTypeface(r_reg);

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String name=client_name.getText().toString();
                    String email=email_ID.getText().toString();
                    String phone=phone_no.getText().toString();
                    String lo=location.getText().toString();
                    String coName=college_name.getText().toString();


                    ModelsAddCollege modelsAddCollege=new ModelsAddCollege();
                    modelsAddCollege.setCollegeName(coName);
                    modelsAddCollege.setEmail(email);
                    modelsAddCollege.setLocation(lo);
                    modelsAddCollege.setPhone(phone);
                    modelsAddCollege.setName(name);


                    Call addCollege=mCCService.addCollege(modelsAddCollege);

                    addCollege.enqueue(callback);


//                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://campusconnect.cc/"));
//                    startActivity(browserIntent);
                }

            });
        }


        private Callback<ModelsMessageResponse> callback = new Callback<ModelsMessageResponse>() {
            @Override
            public void onResponse(Response<ModelsMessageResponse> response, Retrofit retrofit) {
                Toast.makeText(SelectCollegeActivity.this,"Thank you for your interest, we'll get back shortly",Toast.LENGTH_LONG);
                dismiss();
            }

            @Override
            public void onFailure(Throwable t) {
                //Timber.d("onFailure() : mQuery - " + mQuery);

            }
        };
    }


}
