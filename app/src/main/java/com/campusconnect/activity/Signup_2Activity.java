package com.campusconnect.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.campusconnect.R;
import com.campusconnect.communicator.CCWebService;
import com.campusconnect.communicator.ServiceGenerator;
import com.campusconnect.communicator.WebRequestTask;
import com.campusconnect.communicator.WebServiceDetails;
import com.campusconnect.communicator.models.ModelsProfileMiniForm;
import com.campusconnect.communicator.models.ModelsProfileResponse;
import com.campusconnect.communicator.models.ModelsProfileRetrievalMiniForm;
import com.campusconnect.constant.AppConstants;
import com.campusconnect.gcm.RegistrationIntentService;
import com.campusconnect.utility.LogUtility;
import com.campusconnect.utility.NetworkAvailablity;
import com.campusconnect.utility.SharedpreferenceUtility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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
 * Created by RK on 23-09-2015.
 */
public class Signup_2Activity extends Activity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    @Bind(R.id.btn_sign_in)
    SignInButton btnSignIn;

    @Bind(R.id.tv_welcome_message)
    TextView welcome_msg;

    @Bind(R.id.bk_college_image)
    ImageView bk_college;

    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    private static final int RC_SIGN_IN = 0;
    private String mEmailAccount = "";

    private static final String TAG = "Signup_2Activity";

    File follows;

    SharedPreferences sharedPreferences;
    String college_name;


    CCWebService mCCService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_2);
        ButterKnife.bind(this);

        //Setting up background image
        if (SharedpreferenceUtility.getInstance(this).getString(AppConstants.COLLEGE_ID).equals("4847453903781888")){
            bk_college.setImageResource(R.drawable.nitk_app);
        }else if(SharedpreferenceUtility.getInstance(this).getString(AppConstants.COLLEGE_ID).equals("5749882031702016")){
            bk_college.setImageResource(R.drawable.signup_back);
        }

        sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREFS, Context.MODE_PRIVATE);
        college_name = sharedPreferences.getString(AppConstants.COLLEGE_NAME, null);
        if(college_name!=null) {
            welcome_msg.setText("Welcome to \n" + college_name);
        }else{
            if(SharedpreferenceUtility.getInstance(Signup_2Activity.this).getBoolean(AppConstants.IS_INCI_GUEST)){
                bk_college.setImageResource(R.drawable.nitk_app);
                welcome_msg.setText("Welcome to \n" + "Incident-2016");
            }else{
                bk_college.setImageResource(R.drawable.signup_back);
            }
        }

        boolean gcmstatus = sharedPreferences.getBoolean("gcm_generated", false);
        if (gcmstatus == false) {
            Log.i(TAG, "Getting GCM Token Now");
            Intent m = new Intent(Signup_2Activity.this, RegistrationIntentService.class);
            startService(m);
        }

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkAvailablity.hasInternetConnection(Signup_2Activity.this)) {
                    signInWithGplus();
                } else {
                    Toast.makeText(Signup_2Activity.this, "Network is not available.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mCCService = ServiceGenerator.createService(
                CCWebService.class,
                CCWebService.BASE_URL);


        mGoogleApiClient = new GoogleApiClient.Builder(this).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).
                addApi(Plus.API, Plus.PlusOptions.builder().build()).
                addScope(Plus.SCOPE_PLUS_LOGIN).build();
    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    private void resolveSignInError() {
        try {
            if (mConnectionResult.hasResolution()) {
                try {
                    mIntentInProgress = true;
                    mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
                } catch (IntentSender.SendIntentException e) {
                    mIntentInProgress = false;
                    mGoogleApiClient.connect();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            mConnectionResult = result;

            if (mSignInClicked) {
                resolveSignInError();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        mSignInClicked = false;
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
        getProfileInformation();
        String gcm_token = SharedpreferenceUtility.getInstance(Signup_2Activity.this).getString(AppConstants.GCM_TOKEN);
        String email = SharedpreferenceUtility.getInstance(Signup_2Activity.this).getString(AppConstants.EMAIL_KEY);

        ModelsProfileRetrievalMiniForm modelsProfileRetrievalMiniForm = new ModelsProfileRetrievalMiniForm();
        modelsProfileRetrievalMiniForm.setEmail(email);
        modelsProfileRetrievalMiniForm.setGcmId(gcm_token);


        Call profileGcm = mCCService.profileGCM(modelsProfileRetrievalMiniForm);
        profileGcm.enqueue(callback);



//        webApi(email, gcm_token);
    }

    private Callback<ModelsProfileResponse> callback = new Callback<ModelsProfileResponse>() {
        @Override
        public void onResponse(Response<ModelsProfileResponse> response, Retrofit retrofit) {
            if (response != null) {
                if (response.isSuccess()) {
//                    Log.d("findVid",response.raw());

                    ModelsProfileResponse modelsProfileResponse = response.body();


                    if (modelsProfileResponse.getSuccess().equalsIgnoreCase("true")) {
                        ModelsProfileMiniForm modelsProfileMiniForm = modelsProfileResponse.getResult();

                        if (modelsProfileMiniForm != null) {


                            SharedpreferenceUtility.getInstance(Signup_2Activity.this).putString(AppConstants.BATCH, modelsProfileMiniForm.getBatch());
                            SharedpreferenceUtility.getInstance(Signup_2Activity.this).putString(AppConstants.BRANCH, modelsProfileMiniForm.getBranch());
                            SharedpreferenceUtility.getInstance(Signup_2Activity.this).putString(AppConstants.PHONE, modelsProfileMiniForm.getPhone());
                            SharedpreferenceUtility.getInstance(Signup_2Activity.this).putString(AppConstants.COLLEGE_ID, modelsProfileMiniForm.getCollegeId());
                            SharedpreferenceUtility.getInstance(Signup_2Activity.this).putString(AppConstants.PERSON_PID, modelsProfileMiniForm.getPid());
                            try {
                                SharedpreferenceUtility.getInstance(Signup_2Activity.this).putString(AppConstants.PERSON_TAGS, modelsProfileMiniForm.getTags().get(0));
                            } catch (Exception e) {

                            }
                            //  SharedpreferenceUtility.getInstance(Signup_2Activity.this).putString(AppConstants.FOLLOW, followsStr);
                            // SharedpreferenceUtility.getInstance(Signup_2Activity.this).putString(AppConstants.FOLLOW_NAMES, followsNames);

                            if (modelsProfileMiniForm.getIsAlumni().equalsIgnoreCase("Y")) {
                                SharedpreferenceUtility.getInstance(Signup_2Activity.this).putString(AppConstants.PROFILE_CATEGORY, AppConstants.ALUMNI);
                            } else {
                                SharedpreferenceUtility.getInstance(Signup_2Activity.this).putString(AppConstants.PROFILE_CATEGORY, AppConstants.STUDENT);
                            }
                        }
                        SharedpreferenceUtility.getInstance(Signup_2Activity.this).putBoolean(AppConstants.LOG_IN_STATUS, Boolean.TRUE);
                        SharedpreferenceUtility.getInstance(Signup_2Activity.this).putBoolean(AppConstants.IS_FIRST_TIME_CAMPUS_FEED, Boolean.FALSE);
                        SharedpreferenceUtility.getInstance(Signup_2Activity.this).putBoolean(AppConstants.IS_FIRST_TIME_MY_FEED, Boolean.FALSE);
                        SharedpreferenceUtility.getInstance(Signup_2Activity.this).putBoolean(AppConstants.IS_FIRST_TIME_GROUPS, Boolean.FALSE);
                        SharedpreferenceUtility.getInstance(Signup_2Activity.this).putBoolean(AppConstants.IS_LIVE_FIRST_TIME, Boolean.FALSE);
                        startActivity(new Intent(Signup_2Activity.this, MainActivity.class));
                        finish();
                    } else {

                        SharedpreferenceUtility.getInstance(Signup_2Activity.this).putBoolean(AppConstants.LOG_IN_STATUS, Boolean.TRUE);
                        SharedpreferenceUtility.getInstance(Signup_2Activity.this).putBoolean(AppConstants.IS_FIRST_TIME_CAMPUS_FEED, Boolean.TRUE);
                        SharedpreferenceUtility.getInstance(Signup_2Activity.this).putBoolean(AppConstants.IS_FIRST_TIME_MY_FEED, Boolean.TRUE);
                        SharedpreferenceUtility.getInstance(Signup_2Activity.this).putBoolean(AppConstants.IS_FIRST_TIME_GROUPS, Boolean.TRUE);

                        if(SharedpreferenceUtility.getInstance(Signup_2Activity.this).getBoolean(AppConstants.IS_INCI_GUEST)){

                            ModelsProfileMiniForm modelsProfileMiniForm=new ModelsProfileMiniForm();
                            modelsProfileMiniForm.setEmail(SharedpreferenceUtility.getInstance(Signup_2Activity.this).getString(AppConstants.EMAIL_KEY));
                            modelsProfileMiniForm.setName(SharedpreferenceUtility.getInstance(Signup_2Activity.this).getString(AppConstants.PERSON_NAME));
                            modelsProfileMiniForm.setGcmId(SharedpreferenceUtility.getInstance(Signup_2Activity.this).getString(AppConstants.GCM_TOKEN));
                            modelsProfileMiniForm.setPhotoUrl(SharedpreferenceUtility.getInstance(Signup_2Activity.this).getString(AppConstants.PHOTO_URL));
                            modelsProfileMiniForm.setCollegeId("4847453903781888");
                            modelsProfileMiniForm.setIsAlumni("Guest");
                            modelsProfileMiniForm.setBatch("");
                            modelsProfileMiniForm.setBranch("");
                            modelsProfileMiniForm.setPhone("");

                            mCCService = ServiceGenerator.createService(
                                    CCWebService.class,
                                    CCWebService.BASE_URL);


                            Call createProfile=mCCService.createProfile(modelsProfileMiniForm);
                            createProfile.enqueue(callBack);
                        }else {
                            startActivity(new Intent(Signup_2Activity.this, GetProfileDetailsActivity.class));
                        }
                        finish();


                    }

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


    private Callback<ModelsProfileMiniForm> callBack = new Callback<ModelsProfileMiniForm>() {
        @Override
        public void onResponse(Response<ModelsProfileMiniForm> response, Retrofit retrofit) {

            if (response != null) {
                if (response.isSuccess()) {
//                    Log.d("findVid",response.raw());
                    ModelsProfileMiniForm modelsProfileMiniForm = response.body();
                    Log.d("pid",response.body().getPid());
                    SharedpreferenceUtility.getInstance(Signup_2Activity.this).putString(AppConstants.COLLEGE_ID, modelsProfileMiniForm.getCollegeId());
                    SharedpreferenceUtility.getInstance(Signup_2Activity.this).putString(AppConstants.PERSON_PID, modelsProfileMiniForm.getPid());
                    Intent intent_temp = new Intent(Signup_2Activity.this, MainActivity.class);
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

    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                Log.i(TAG, "Name: " + personName + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + email
                        + ", Image: " + personPhotoUrl);

                String photo = personPhotoUrl.substring(0, personPhotoUrl.lastIndexOf('=')) + "=200";
                SharedpreferenceUtility.getInstance(Signup_2Activity.this).putString(AppConstants.EMAIL_KEY, email);
                SharedpreferenceUtility.getInstance(Signup_2Activity.this).putString(AppConstants.PERSON_NAME, personName);
                SharedpreferenceUtility.getInstance(Signup_2Activity.this).putBoolean(AppConstants.LOG_IN_STATUS, Boolean.TRUE);
                SharedpreferenceUtility.getInstance(Signup_2Activity.this).putString(AppConstants.PHOTO_URL, photo);

            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }


    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }


    private void signOutFromGplus() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.commit();
            SharedpreferenceUtility.getInstance(Signup_2Activity.this).putBoolean(AppConstants.LOG_IN_STATUS, Boolean.FALSE);
        }
    }
//
//    private void webApi(String email, String gcmId) {
//        JSONObject jsonObject = new JSONObject();
//
//        try {
//            jsonObject.put("email", "" + email);
//            jsonObject.put("gcmId", "" + gcmId);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        List<NameValuePair> param = new ArrayList<NameValuePair>();
//        String url = WebServiceDetails.DEFAULT_BASE_URL + "profileGCM";
//        Log.i(TAG, "get profile" + url);
//        new WebRequestTask(Signup_2Activity.this, param, _handler, WebRequestTask.POST, jsonObject, WebServiceDetails.PID_GET_GCM_PROFILE,
//                true, url).execute();
//    }

    private final Handler _handler = new Handler() {
        public void handleMessage(Message msg) {
            int response_code = msg.what;
            if (response_code != 0) {
                String strResponse = (String) msg.obj;
                Log.v("Response", strResponse);
                if (strResponse != null && strResponse.length() > 0) {
                    switch (response_code) {
                        case WebServiceDetails.PID_GET_GCM_PROFILE: {
                            try {
                                JSONObject jsonResponse = new JSONObject(strResponse);


                                if (jsonResponse.getString("success").equalsIgnoreCase("true")) {
                                    JSONObject JsonResultObj = jsonResponse.optJSONObject("result");

                                    if (strResponse != null) {

                                        String name = JsonResultObj.optString("name");
                                        String pid = JsonResultObj.optString("pid");
                                        String batch = JsonResultObj.optString("batch");
                                        String collegeId = JsonResultObj.optString("collegeId");
                                        String phone = JsonResultObj.optString("phone");
                                        String branch = JsonResultObj.optString("branch");
                                        String isAlumni = JsonResultObj.optString("isAlumni");
                                        String email = JsonResultObj.optString("email");
                                        String kind = JsonResultObj.optString("kind");
                                        String etag = JsonResultObj.optString("etag");
                                        String admin = JsonResultObj.optString("isAdmin");
                                        String superadmin = JsonResultObj.optString("isSuperAdmin");


                                        if (admin.equals("Y")) {
                                            SharedpreferenceUtility.getInstance(Signup_2Activity.this).putBoolean(AppConstants.IS_ADMIN, true);
                                        } else {
                                            SharedpreferenceUtility.getInstance(Signup_2Activity.this).putBoolean(AppConstants.IS_ADMIN, false);
                                        }

                                        if (superadmin.equals("Y")) {
                                            SharedpreferenceUtility.getInstance(Signup_2Activity.this).putBoolean(AppConstants.IS_SUP_ADMIN, true);
                                        } else {
                                            SharedpreferenceUtility.getInstance(Signup_2Activity.this).putBoolean(AppConstants.IS_SUP_ADMIN, false);
                                        }

                                        SharedpreferenceUtility.getInstance(Signup_2Activity.this).putString(AppConstants.BATCH, batch);
                                        SharedpreferenceUtility.getInstance(Signup_2Activity.this).putString(AppConstants.BRANCH, branch);
                                        SharedpreferenceUtility.getInstance(Signup_2Activity.this).putString(AppConstants.PHONE, phone);
                                        SharedpreferenceUtility.getInstance(Signup_2Activity.this).putString(AppConstants.COLLEGE_ID, collegeId);
                                        SharedpreferenceUtility.getInstance(Signup_2Activity.this).putString(AppConstants.PERSON_PID, pid);
                                        //  SharedpreferenceUtility.getInstance(Signup_2Activity.this).putString(AppConstants.FOLLOW, followsStr);
                                        // SharedpreferenceUtility.getInstance(Signup_2Activity.this).putString(AppConstants.FOLLOW_NAMES, followsNames);

                                        if (isAlumni.equalsIgnoreCase("Y")) {
                                            SharedpreferenceUtility.getInstance(Signup_2Activity.this).putString(AppConstants.PROFILE_CATEGORY, AppConstants.ALUMNI);
                                        } else {
                                            SharedpreferenceUtility.getInstance(Signup_2Activity.this).putString(AppConstants.PROFILE_CATEGORY, AppConstants.STUDENT);
                                        }
                                    }
                                    SharedpreferenceUtility.getInstance(Signup_2Activity.this).putBoolean(AppConstants.LOG_IN_STATUS, Boolean.TRUE);
                                    startActivity(new Intent(Signup_2Activity.this, MainActivity.class));
                                    finish();
                                } else {
                                    SharedpreferenceUtility.getInstance(Signup_2Activity.this).putBoolean(AppConstants.LOG_IN_STATUS, Boolean.TRUE);
                                    startActivity(new Intent(Signup_2Activity.this, GetProfileDetailsActivity.class));
                                    finish();
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
                    Toast.makeText(Signup_2Activity.this, "Network not available", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(Signup_2Activity.this, "Network not available", Toast.LENGTH_LONG).show();
            }
        }
    };
}
