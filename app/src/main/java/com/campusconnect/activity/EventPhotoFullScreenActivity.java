package com.campusconnect.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.style.TtsSpan;
import android.view.View;

import com.campusconnect.R;
import com.campusconnect.utility.PinchZoom.GestureImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by RK on 04/11/2015.
 */
public class EventPhotoFullScreenActivity extends AppCompatActivity {

    @Bind(R.id.iv_event_photo_fs)
    GestureImageView event_photo_fs;
    @Bind(R.id.fab_download)
    FloatingActionButton downloadImage;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_photo_full_screen);
        ButterKnife.bind(this);

        try {
            url = getIntent().getStringExtra("PICTURE");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
//        if (!(url.isEmpty()) || url != null) {
//            Picasso.with(EventPhotoFullScreenActivity.this).load(url).placeholder(R.drawable.default_card_image).into(event_photo_fs);
//        }
//        else{
//            Picasso.with(EventPhotoFullScreenActivity.this).load(R.drawable.default_card_image).into(event_photo_fs);
//        }
        try {
            Picasso.with(EventPhotoFullScreenActivity.this).load(url).placeholder(R.drawable.default_card_image).into(event_photo_fs);
        } catch (Exception e) {
            e.printStackTrace();
            Picasso.with(EventPhotoFullScreenActivity.this).load(R.drawable.default_card_image).into(event_photo_fs);
        }

        downloadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event_photo_fs.buildDrawingCache();
                Bitmap bmp = event_photo_fs.getDrawingCache();
                SaveImage(bmp);
            }
        });
    }

    private void SaveImage(Bitmap finalBitmap) {

        //To download the file
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Download");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 100000;
        n = generator.nextInt(n);
        String fname = "IMAGE-" + n + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        MediaScannerConnection.scanFile(this, new String[]{      //This is being used because the mount permissions no more work.

                        file.getAbsolutePath()},

                null, new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri)

                    {

                    }
                });
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        //Download notification
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "image/*");

        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification notiDownload = new NotificationCompat.Builder(this)
                .setContentTitle("Download completed.")
                .setContentText(fname)
                .setSmallIcon(getNotificationIcon())
                .setContentIntent(pIntent).build();

        notiDownload.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notiDownload);
        startActivity(intent);

    }

    //Setting icon based on sdk
    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.playstore_lollipop : R.mipmap.play_store;
    }

    // [START receive_message]
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        String title = data.getString("title");

    }

}