package com.campusconnect.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.campusconnect.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by RK on 13/02/2016.
 */

public class InterestsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //FAQ variables start region
    List<String> selected_list = new ArrayList<String>();
    List<Boolean> is_interest_selected = new ArrayList<Boolean>(Arrays.asList(new Boolean[18]));

    InterestsViewHolder interests_viewholder;

    String[] interests = {
            "Animals",
            "Arts",
            "Business",
            "Dance",
            "Debate",
            "Development",
            "Entrepreneurship",
            "Fashion",
            "Finance",
            "Literature",
            "Marketing",
            "Music",
            "Photography",
            "Politics",
            "Religion",
            "Robotics",
            "Sports",
            "Theatre"
    };

    //end region

    //Typeface declaration start region
    Typeface r_reg, r_lig;
    //end region

    Context context;

    // region Constructors
    public InterestsAdapter(Context context) {
        this.context = context;
    }
    // endregion

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.activity_card_layout_interest, parent, false);

        return new InterestsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        interests_viewholder = (InterestsViewHolder) viewHolder;

        is_interest_selected.add(position,false);
        interests_viewholder.interest.setText(interests[position]);

    }

    @Override
    public int getItemCount() {
        return interests.length;
    }

    public List<String> get_selected(){
        return selected_list;
    }

    public void selected_list_clear(){
        if(selected_list!=null)
            selected_list.clear();
    }

    public class InterestsViewHolder extends RecyclerView.ViewHolder {

        TextView interest;

        public InterestsViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

            interest = (TextView) v.findViewById(R.id.tv_interest);

            r_lig = Typeface.createFromAsset(v.getContext().getAssets(), "font/Roboto_Light.ttf");
            r_reg = Typeface.createFromAsset(v.getContext().getAssets(), "font/Roboto_Regular.ttf");

            interest.setTypeface(r_lig);

            interest.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    int pos=getAdapterPosition();

                    if(interest.getCurrentTextColor() == Color.rgb(56, 56, 56)) {

                        selected_list.add(interest.getText().toString());

                        ColorDrawable[] color = {new ColorDrawable(Color.rgb(224, 225, 227)), new ColorDrawable(Color.rgb(56, 56, 56))};
                        TransitionDrawable trans = new TransitionDrawable(color);
                        int sdk = android.os.Build.VERSION.SDK_INT;
                        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            interest.setBackgroundDrawable(trans);
                        } else {
                            interest.setBackground(trans);
                        }
                        trans.startTransition(800);

                        int normalTextColor = Color.argb(0, 155, 155, 155);
                        Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); //make a 1-pixel Bitmap
                        Canvas canvas = new Canvas(bitmap);
                        canvas.drawColor(normalTextColor); //color we want to apply filter to
                        canvas.drawColor(Color.WHITE, PorterDuff.Mode.ADD); //apply filter
                        int pressedTextColor = bitmap.getPixel(0, 0);

                        interest.setTextColor(pressedTextColor);
                    }
                    else {

                        selected_list.remove(interest.getText().toString());

                        ColorDrawable[] color = {new ColorDrawable(Color.rgb(56, 56, 56)), new ColorDrawable(Color.rgb(255, 255, 255))};
                        TransitionDrawable trans = new TransitionDrawable(color);
                        int sdk = android.os.Build.VERSION.SDK_INT;
                        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            interest.setBackgroundDrawable(trans);
                        } else {
                            interest.setBackground(trans);
                        }
                        trans.startTransition(800);

                        int normalTextColor = Color.argb(0, 155, 155, 155);
                        Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        canvas.drawColor(normalTextColor); //color we want to apply filter to
                        canvas.drawColor(Color.rgb(56, 56, 56), PorterDuff.Mode.ADD); //apply filter
                        int pressedTextColor = bitmap.getPixel(0, 0);

                        interest.setTextColor(pressedTextColor);
                        is_interest_selected.add(getAdapterPosition(),false);
                    }

                }
            });


        }

    }
}
