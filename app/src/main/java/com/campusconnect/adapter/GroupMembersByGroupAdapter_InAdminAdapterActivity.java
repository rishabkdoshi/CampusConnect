package com.campusconnect.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.campusconnect.R;
import com.campusconnect.communicator.CCWebService;
import com.campusconnect.communicator.ServiceGenerator;
import com.campusconnect.communicator.models.ModelsPersonalResponse;
import com.campusconnect.communicator.models.ModelsUnjoinClubMiniForm;
import com.campusconnect.constant.AppConstants;
import com.campusconnect.utility.CircularImageView;
import com.campusconnect.utility.SharedpreferenceUtility;
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
public class GroupMembersByGroupAdapter_InAdminAdapterActivity extends
        RecyclerView.Adapter<GroupMembersByGroupAdapter_InAdminAdapterActivity.GroupMembersByGroupViewHolder> {

    private List<ModelsPersonalResponse> GroupMembersByGroupList;
    private Context context;

    String clubId;
    CCWebService mCCService;

    public GroupMembersByGroupAdapter_InAdminAdapterActivity(List<ModelsPersonalResponse> GroupMembersByGroupList, Context context, String clubId) {
        this.GroupMembersByGroupList = GroupMembersByGroupList;
        this.context = context;
        this.clubId = clubId;
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
                R.layout.activity_card_layout_member_inadmin, viewGroup, false);

        return new GroupMembersByGroupViewHolder(itemView);
    }

    public class GroupMembersByGroupViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_member_name) TextView member_name;
        @Bind(R.id.tv_member_batch) TextView member_batch;
        @Bind(R.id.tv_member_branch) TextView member_branch;
        @Bind(R.id.tv_remove) TextView remove;
        @Bind(R.id.member_card)
        CardView member_card;
        @Bind(R.id.group_icon_notification)
        CircularImageView member_icon;

        public GroupMembersByGroupViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);


            mCCService = ServiceGenerator.createService(
                    CCWebService.class,
                    CCWebService.BASE_URL);

            Typeface r_reg = Typeface.createFromAsset(v.getContext().getAssets(), "font/Roboto_Regular.ttf");
            Typeface r_lig = Typeface.createFromAsset(v.getContext().getAssets(), "font/Roboto_Light.ttf");

            member_name.setTypeface(r_reg);
            member_batch.setTypeface(r_lig);
            member_branch.setTypeface(r_lig);

            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DeleteMemberConfirmationDialog confirmDialog = new DeleteMemberConfirmationDialog((Activity) v.getContext(), getAdapterPosition());
                    Window window = confirmDialog.getWindow();
                    window.setLayout(450, ViewGroup.LayoutParams.WRAP_CONTENT);
                    confirmDialog.show();
                }
            });


        }

    }

    //Dialog start region
    public class DeleteMemberConfirmationDialog extends Dialog implements
            View.OnClickListener {

        int adapter_position;

        public Activity c;
        public Dialog d;
        public TextView yes;
        public TextView no;
        public TextView dialog_info;
        Context context;


        public DeleteMemberConfirmationDialog(Activity a, int adapter_pos) {
            super(a);
            // TODO Auto-generated constructor stub
            this.adapter_position = adapter_pos;
            this.c = a;
            this.context = context;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.member_confirmation_dialog);

            dialog_info = (TextView) findViewById(R.id.tv_dialog_info);
            dialog_info.setText("Are you sure you want to remove the member?");

            yes = (TextView) findViewById(R.id.btn_yes);
            no = (TextView) findViewById(R.id.btn_no);

            yes.setOnClickListener(this);
            no.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_yes: {

                    String fromPid = SharedpreferenceUtility.getInstance(context).getString(AppConstants.PERSON_PID);
                    String pid = GroupMembersByGroupList.get(adapter_position).getPid();

                    ModelsUnjoinClubMiniForm modelsUnjoinClubMiniForm=new ModelsUnjoinClubMiniForm();
                    modelsUnjoinClubMiniForm.setPid(pid);
                    modelsUnjoinClubMiniForm.setFromPid(fromPid);
                    modelsUnjoinClubMiniForm.setClubId(clubId);
                    Call removeMember=mCCService.unjoinClub(modelsUnjoinClubMiniForm);
                    removeMember.enqueue(callBack);

                    GroupMembersByGroupList.remove(adapter_position);
                    notifyItemRemoved(adapter_position);
                    dismiss();
                    break;
                }
                case R.id.btn_no:
                    dismiss();
                    break;
                default:
                    break;
            }
        }

    }

    private Callback<Void> callBack = new Callback<Void>() {
        @Override
        public void onResponse(Response<Void> response, Retrofit retrofit) {

        }

        @Override
        public void onFailure(Throwable t) {
            //Timber.d("onFailure() : mQuery - " + mQuery);

        }
    };
    //Dialog end region
}
