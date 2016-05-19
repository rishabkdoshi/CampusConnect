package com.campusconnect.activity;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.campusconnect.R;
import com.campusconnect.communicator.CCWebService;
import com.campusconnect.communicator.ServiceGenerator;
import com.campusconnect.communicator.models.ModelsHashTag;
import com.campusconnect.communicator.models.ModelsHashTagList;
import com.campusconnect.communicator.models.ModelsScoreBoard;
import com.campusconnect.communicator.models.ModelsScoreBoardList;
import com.campusconnect.communicator.models.ModelsSubscribeMatch;
import com.campusconnect.constant.AppConstants;
import com.campusconnect.utility.SharedpreferenceUtility;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by RK on 05/11/2015.
 */
public class SlamDunkScorecardActivity extends ActionBarActivity {

    @Bind(R.id.btn_close)
    Button close;
    @Bind(R.id.btn_refresh)
    Button refresh;
    @Bind(R.id.tv_slam_dunk)
    TextView slam_dunk_title;
    @Bind(R.id.tv_subscribe)
    TextView subscribe;

    @Bind(R.id.tv_subscribed)
    TextView subscribed;

    @Bind(R.id.tv_group)
    TextView group;
    @Bind(R.id.tv_competition)
    TextView competition;
    @Bind(R.id.tv_quarter_title_text)
    TextView quarter_title;
    @Bind(R.id.tv_team1_name)
    TextView team1_title;
    @Bind(R.id.tv_team2_name)
    TextView team2_title;
    @Bind(R.id.tv_team1_score)
    TextView score_team1;
    @Bind(R.id.tv_team2_score)
    TextView score_team2;
    @Bind(R.id.tv_quarter)
    TextView quarter;

    CCWebService mCCService;


    String DEFAULT_ROOT_URL = "https://campus-connect-2015.appspot.com/_ah/api/";
    String DEFAULT_SERVICE_PATH = "campusConnectApis/v1/";
    String BASE_URL = DEFAULT_ROOT_URL + DEFAULT_SERVICE_PATH;


    static String team1,team2,qtr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slamdunk_scorecard);
        ButterKnife.bind(this);

        mCCService = ServiceGenerator.createService(CCWebService.class,BASE_URL);


        Typeface r_med = Typeface.createFromAsset(getAssets(), "font/Roboto_Medium.ttf");
        slam_dunk_title.setTypeface(r_med);

        Typeface r_reg = Typeface.createFromAsset(getAssets(), "font/Roboto_Regular.ttf");
        group.setTypeface(r_reg);
        competition.setTypeface(r_reg);
        quarter_title.setTypeface(r_reg);
        team1_title.setTypeface(r_reg);
        team2_title.setTypeface(r_reg);
        close.setTypeface(r_reg);
        refresh.setTypeface(r_reg);

        Typeface scoreboard = Typeface.createFromAsset(getAssets(), "font/Scoreboard.ttf");
        score_team1.setTypeface(scoreboard);
        score_team2.setTypeface(scoreboard);
        quarter.setTypeface(scoreboard);

        subscribe.setVisibility(View.GONE);
        subscribed.setVisibility(View.GONE);


        SpannableString underlining_subscribe = new SpannableString("SUBSCRIBE");
        underlining_subscribe.setSpan(new UnderlineSpan(), 0, underlining_subscribe.length(), 0);
        subscribe.setText(underlining_subscribe);

        SpannableString underlining_subscribed = new SpannableString("SUBSCRIBED");
        underlining_subscribed.setSpan(new UnderlineSpan(), 0, underlining_subscribed.length(), 0);
        subscribed.setText(underlining_subscribed);

        final Shader shader = new LinearGradient(0, 0, 0, 120,
                new int[]{Color.rgb(56, 56, 56), Color.rgb(160, 160, 160)},
                new float[]{0, 1}, Shader.TileMode.CLAMP
        );

        final Shader shader2 = new LinearGradient(0, 0, 0, 120,
                new int[]{Color.rgb(56, 56, 56), Color.rgb(160, 160, 160)},
                new float[]{0, 1}, Shader.TileMode.CLAMP
        );

        score_team1.getPaint().setShader(shader);
        score_team2.getPaint().setShader(shader2);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SlamDunkScorecardActivity.this.overridePendingTransition(0, R.anim.fadeout_scorecard);
                finish();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Call getScores=mCCService.getScoreBoard();

        getScores.enqueue(callBack);


        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Call getScores=mCCService.getScoreBoard();

                getScores.enqueue(callBack);

                /*//Get the value from the server
                if(score_team1.getText().equals("--"))
                    SharedpreferenceUtility.getInstance(SlamDunkScorecardActivity.this).putString(AppConstants.TEAM1_SCORE, "12");

                //Get the value from the server
                if(score_team2.getText().equals("--"))
                    SharedpreferenceUtility.getInstance(SlamDunkScorecardActivity.this).putString(AppConstants.TEAM2_SCORE, "23");
*/
                if(!(score_team1.getText().equals("--"))) {
                    score_team1.setText(SharedpreferenceUtility.getInstance(SlamDunkScorecardActivity.this).getString(AppConstants.TEAM1_SCORE));
                    score_team2.setText(SharedpreferenceUtility.getInstance(SlamDunkScorecardActivity.this).getString(AppConstants.TEAM2_SCORE));
                    quarter.setText(SharedpreferenceUtility.getInstance(SlamDunkScorecardActivity.this).getString(AppConstants.QUARTER));
                }


                //replace 57 and 36with the value from the server for team1 and team2 respectively - RK
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Get the current values from the server
                        SharedpreferenceUtility.getInstance(SlamDunkScorecardActivity.this).putString(AppConstants.TEAM1_SCORE, team1);
                        SharedpreferenceUtility.getInstance(SlamDunkScorecardActivity.this).putString(AppConstants.TEAM2_SCORE, team2);
                        SharedpreferenceUtility.getInstance(SlamDunkScorecardActivity.this).putString(AppConstants.QUARTER, qtr);

                        scoreboardflipAnim(score_team1, SharedpreferenceUtility.getInstance(SlamDunkScorecardActivity.this).getString(AppConstants.TEAM1_SCORE));
                        scoreboardflipAnim(score_team2, SharedpreferenceUtility.getInstance(SlamDunkScorecardActivity.this).getString(AppConstants.TEAM2_SCORE));
                        scoreboardflipAnim(quarter, SharedpreferenceUtility.getInstance(SlamDunkScorecardActivity.this).getString(AppConstants.TEAM2_SCORE));

                    }
                }, 100);

            }
        });

    }

    private Callback<ModelsScoreBoardList> callBack = new Callback<ModelsScoreBoardList>() {
        @Override
        public void onResponse(Response<ModelsScoreBoardList> response, Retrofit retrofit) {
            ModelsScoreBoardList modelsScoreBoardList = response.body();
            final ModelsScoreBoard modelsScoreBoard=modelsScoreBoardList.getItems().get(0);

            scoreboardflipAnim(score_team1,modelsScoreBoard.getScore1());
            scoreboardflipAnim(score_team2, modelsScoreBoard.getScore2());
            scoreboardflipAnim(quarter, modelsScoreBoard.getQuarter());
            team1_title.setText(modelsScoreBoard.getTeam1());
            team2_title.setText(modelsScoreBoard.getTeam2());
            competition.setText(modelsScoreBoard.getGender());
            group.setText(modelsScoreBoard.getRound());

            team1=modelsScoreBoard.getScore1();
            team2=modelsScoreBoard.getScore2();
            qtr=modelsScoreBoard.getQuarter();

            if(SharedpreferenceUtility.getInstance(SlamDunkScorecardActivity.this).getBoolean("hasSubscribed" + modelsScoreBoard.getMatchId())==null){
                SharedpreferenceUtility.getInstance(SlamDunkScorecardActivity.this).putBoolean("hasSubscribed"+modelsScoreBoard.getMatchId(),false);
            }

            final CCWebService mCCService = ServiceGenerator.createService(CCWebService.class,CCWebService.BASE_URL);

            final ModelsSubscribeMatch modelsSubscribeMatch = new ModelsSubscribeMatch();

            String pid=SharedpreferenceUtility.getInstance(SlamDunkScorecardActivity.this).getString(AppConstants.PERSON_PID);

            modelsSubscribeMatch.setPid(pid);
            modelsSubscribeMatch.setMatchId(modelsScoreBoard.getMatchId());

            //subscribe.setVisibility(View.VISIBLE);
            //has subscribed to match
//            if(SharedpreferenceUtility.getInstance(SlamDunkScorecardActivity.this).getBoolean("hasSubscribed"+modelsScoreBoard.getMatchId())){
//
//                subscribed.setVisibility(View.VISIBLE);
//                SpannableString underlining_subscribed = new SpannableString("SUBSCRIBED");
//                underlining_subscribed.setSpan(new UnderlineSpan(), 0, underlining_subscribed.length(), 0);
//                subscribed.setText(underlining_subscribed);
//                subscribed.setTextColor(Color.rgb(250, 209, 86));
//                subscribed.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        mCCService.unSubscribeToMatch(modelsSubscribeMatch).enqueue(subscribeCallBack);
//                        SharedpreferenceUtility.getInstance(SlamDunkScorecardActivity.this).putBoolean("hasSubscribed" + modelsScoreBoard.getMatchId(), false);
//
//
//                        SpannableString underlining_subscribe = new SpannableString("SUBSCRIBE");
//                        underlining_subscribe.setSpan(new UnderlineSpan(), 0, underlining_subscribe.length(), 0);
//                        subscribe.setVisibility(View.VISIBLE);
//                        subscribed.setVisibility(View.GONE);
//
//                        subscribe.setText(underlining_subscribe);
//                        subscribe.setTextColor(Color.rgb(56, 56, 56));
//
//                    }
//                });
//
//            }
//            //hasnt subscribed to match
//            else{
//
//                subscribe.setVisibility(View.VISIBLE);
//                SpannableString underlining_subscribe = new SpannableString("SUBSCRIBE");
//                underlining_subscribe.setSpan(new UnderlineSpan(), 0, underlining_subscribe.length(), 0);
//                subscribe.setText(underlining_subscribe);
//                subscribe.setTextColor(Color.rgb(56, 56, 56));
//
//                subscribe.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        mCCService.subscribeToMatch(modelsSubscribeMatch).enqueue(subscribeCallBack);
//                        SharedpreferenceUtility.getInstance(SlamDunkScorecardActivity.this).putBoolean("hasSubscribed" + modelsScoreBoard.getMatchId(), true);
//
//
//                        SpannableString underlining_subscribed = new SpannableString("SUBSCRIBED");
//                        underlining_subscribed.setSpan(new UnderlineSpan(), 0, underlining_subscribed.length(), 0);
//                        subscribed.setVisibility(View.VISIBLE);
//                        subscribe.setVisibility(View.GONE);
//
//                        subscribed.setText(underlining_subscribed);
//                        subscribed.setTextColor(Color.rgb(250, 209, 86));
//                    }
//                });
//
//            }

            //New code
            if(SharedpreferenceUtility.getInstance(SlamDunkScorecardActivity.this).getBoolean("hasSubscribed"+modelsScoreBoard.getMatchId())) {
                subscribed.setVisibility(View.VISIBLE);
                subscribe.setVisibility(View.GONE);
            }
            else{
                subscribe.setVisibility(View.VISIBLE);
                subscribed.setVisibility(View.GONE);
            }



            subscribe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mCCService.subscribeToMatch(modelsSubscribeMatch).enqueue(subscribeCallBack);
                    SharedpreferenceUtility.getInstance(SlamDunkScorecardActivity.this).putBoolean("hasSubscribed" + modelsScoreBoard.getMatchId(), true);

                    subscribed.setVisibility(View.VISIBLE);
                    subscribe.setVisibility(View.GONE);
                }
            });

            subscribed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mCCService.unSubscribeToMatch(modelsSubscribeMatch).enqueue(subscribeCallBack);
                    SharedpreferenceUtility.getInstance(SlamDunkScorecardActivity.this).putBoolean("hasSubscribed" + modelsScoreBoard.getMatchId(), false);

                    subscribe.setVisibility(View.VISIBLE);
                    subscribed.setVisibility(View.GONE);

                }
            });


        }

        @Override
        public void onFailure(Throwable t) {
            //Timber.d("onFailure() : mQuery - " + mQuery);

        }
    };


    private Callback<Void> subscribeCallBack = new Callback<Void>() {
        @Override
        public void onResponse(Response<Void> response, Retrofit retrofit) {
//            Toast.makeText(SlamDunkScorecardActivity.this, "This post has been flagged for the admin.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailure(Throwable t) {
            //Timber.d("onFailure() : mQuery - " + mQuery);

        }
    };

    public void scoreboardflipAnim(final TextView v, final String new_score){
        ObjectAnimator animRotate = ObjectAnimator.ofFloat(v, "rotationX", 0.0f, -90f);
        animRotate.setDuration(1400);
        animRotate.setInterpolator(new AccelerateInterpolator());
        animRotate.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                v.setText(new_score);
            }
        }, 1200);

        ObjectAnimator animRotate_appear = ObjectAnimator.ofFloat(v, "rotationX", 90.0f, 0.0f);
        animRotate_appear.setStartDelay(1200);
        animRotate_appear.setDuration(1400);
        animRotate_appear.setInterpolator(new DecelerateInterpolator());
        animRotate_appear.start();
    }
}
