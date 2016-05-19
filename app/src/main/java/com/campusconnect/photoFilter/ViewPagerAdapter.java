package com.campusconnect.photoFilter;

/**
 * Created by Black Ops on 22-02-2016.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.campusconnect.R;
import com.campusconnect.constant.AppConstants;
import com.campusconnect.utility.SharedpreferenceUtility;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.Charset;

public class ViewPagerAdapter extends PagerAdapter {
    // Declare Variables
    Context context;
    LayerDrawable[] flag;
    Bitmap bitmap;
    ImageView imgflag;
    public ViewPagerAdapter(Context context, LayerDrawable[] flag,Bitmap bitmap) {
        this.flag = flag;
        this.context=context;
        this.bitmap=bitmap;
        }

    @Override
    public int getCount() {
        return flag.length ;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        // Declare Variables

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.viewpager_item, container,
                false);
        imgflag=(ImageView)itemView.findViewById(R.id.flag);
        if(position==0)
        {
            Toast.makeText(context,"Swipe right for filters",Toast.LENGTH_LONG).show();
        }
            imgflag.setImageDrawable(flag[position]);
            // Add viewpager_item.xml to ViewPager

            ((ViewPager) container).addView(itemView);
            FloatingActionButton fab = (FloatingActionButton) itemView.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    Drawable icon = flag[position];
                    Bitmap bitmap = Bitmap.createBitmap(icon.getIntrinsicWidth(),icon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    icon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                    icon.draw(canvas);
                    final Bitmap APKicon = bitmap;


                    Uri selectedImageUri = getImageUri(context, APKicon);
                    File finalFile = new File(getPath(selectedImageUri, ((Gallery) context)));
                    if (finalFile.exists()) {
                        showAlertDialog(finalFile);
                    }

                    Log.d("FAB", "Here");
                    PhotoActivty.pos = position;
                    PhotoActivty.mImageView.setImageDrawable(flag[position ]);
                    PhotoActivty.comment.setEnabled(true);
                    PhotoActivty.comment.bringToFront();
                }
            });

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((RelativeLayout) object);

    }



    public class Blob extends AsyncTask<Void, Integer, String> {
        String url;
        File imageFIle;

        public Blob(String Url) {
            this.url = Url;
        }

        private ProgressDialog dialog;
        private Context context;
        private Handler handler;
        public static final int POST = 1;
        private boolean showDialog;
        String responseStringfianl;

        private int type;


        public Blob(Context context, File imageFIle) {
            this.context = context;
            this.url = url;
            this.imageFIle = imageFIle;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                dialog = new ProgressDialog(context);
                dialog.setCancelable(false);
                dialog.setMessage("Uploading Image...");
                dialog.show();

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

        String imageUrlForUpload = "";

        @Override
        protected void onPostExecute(String result) {
            try {
                if (dialog != null)
                    dialog.dismiss();
                super.onPostExecute(result);

                imageUrlForUpload = result;

                SharedpreferenceUtility.getInstance(((Gallery) context)).putString("ImageLiveUpload",imageUrlForUpload);

                        ((Gallery) context).finish();

                Log.d("imageurl",imageUrlForUpload);
//                   Toast.makeText(PhotoActivty.this, "" + result, Toast.LENGTH_SHORT).show();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpget = new HttpGet("http://campus-connect-2015.appspot.com/get_upload_url");
                httpget.setHeader("Content-Type", "application/json");
                httpget.setHeader("Accept-Encoding", "gzip");
                //  httpget.setHeader("");
                //  entity.setContentType("application/x-www-form-urlencoded;charset=UTF-8");//text/plain;charset=UTF-8
                HttpResponse response = httpClient.execute(httpget);
                int responsecode = response.getStatusLine().getStatusCode();
                String responseString = EntityUtils.toString(response.getEntity());
                Log.v("", "responsessdf : " + responseString);
                if (responseString != null) {
                    SharedpreferenceUtility.getInstance(((Gallery) context)).putString(AppConstants.BLOB_URL, responseString);
                }


                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(responseString);
                String BOUNDARY = "Boundary-8B33EF29-2436-47F6-A415-62EF61F62D14";

                FileBody fileBody = new FileBody(imageFIle);
                MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, BOUNDARY, Charset.defaultCharset());
                entity.addPart("file", fileBody);
                httppost.setEntity(entity);
                HttpResponse response1 = httpclient.execute(httppost);
                responseStringfianl = EntityUtils.toString(response1.getEntity());
                Log.e("response String", responseStringfianl.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }

            return responseStringfianl;
        }


    }

    void showAlertDialog(final File imageFile) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Select image");
        alertDialog.setMessage("Do you want to Upload this image?");
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                new Blob(((Gallery) context), imageFile).execute();
            }
        });
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //mImageView.setImageResource(R.mipmap.upload);
//                mImageView.setImageResource(android.R.color.transparent);
//                dialog.cancel();
                Toast.makeText(((Gallery) context),"Select one of the images",Toast.LENGTH_LONG).show();
            }
        });
        alertDialog.show();

    }

    public String getPath(Uri uri, Activity activity) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = activity
                .managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "image", null);
        return Uri.parse(path);
    }
}

