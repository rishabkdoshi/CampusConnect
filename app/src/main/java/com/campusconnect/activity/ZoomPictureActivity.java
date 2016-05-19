package com.campusconnect.activity;

import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import com.campusconnect.R;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ZoomPictureActivity extends AppCompatActivity {

    @Bind(R.id.picture) ImageView p_image;

    String url;ScaleGestureDetector scaleGestureDetector;
    private Matrix matrix = new Matrix();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_picture);
        ButterKnife.bind(this);

         p_image = (ImageView) findViewById(R.id.picture);

        try {
            url = getIntent().getStringExtra("PICTURE");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (url.isEmpty() || url != null) {
            Picasso.with(ZoomPictureActivity.this).load(url).placeholder(R.drawable.default_card_image).into(p_image);
        }
        else{
            Picasso.with(ZoomPictureActivity.this).load(R.drawable.default_card_image).into(p_image);
        }

    }

}
