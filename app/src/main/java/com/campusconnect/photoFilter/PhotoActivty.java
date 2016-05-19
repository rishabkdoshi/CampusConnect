package com.campusconnect.photoFilter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.campusconnect.R;
import com.campusconnect.activity.MainActivity;
import com.campusconnect.adapter.TagArrayAdapter;
import com.campusconnect.adt.TagViewHolder;
import com.campusconnect.adt.TrendingTags;
import com.campusconnect.communicator.CCWebService;
import com.campusconnect.communicator.ServiceGenerator;
import com.campusconnect.communicator.models.ModelsHashTag;
import com.campusconnect.communicator.models.ModelsLiveFeed;
import com.campusconnect.constant.AppConstants;
import com.campusconnect.database.DBHandler;
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
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class PhotoActivty extends AppCompatActivity {
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Uri mImageUri;
    public static ImageView mImageView;
    String mCurrentPhotoPath;
    public static int pos;
    public static LayerDrawable filters[];
    Bitmap filter;
    LayerDrawable layerDrawable;
    Drawable[] layers;
    private int screenwidth;
    public static EditText comment;
    public static Bitmap bitmap;
    View photofilter_container;
    FloatingActionButton camera,server;
    int poss=0;
    String imageUrlForUpload = "";
    private String sendTags="";
    private TrendingTags[] tags;
    private ImageButton mAddTag;


    private ArrayAdapter<TrendingTags> tagListAdapter;
    private ArrayList<TrendingTags> currentTags;


    CCWebService mCCService;


    String DEFAULT_ROOT_URL = "https://campus-connect-live.appspot.com/_ah/api/";
    String DEFAULT_SERVICE_PATH = "campusConnectApis/v1/";
    String BASE_URL = DEFAULT_ROOT_URL + DEFAULT_SERVICE_PATH;
    private DBHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        mCCService = ServiceGenerator.createService(CCWebService.class, BASE_URL);
        db=new DBHandler(PhotoActivty.this);
        currentTags = new ArrayList<>();
//        filterResult = new ArrayList<>();
//        filterTags = new ArrayList<>();


        photofilter_container = (View) findViewById(R.id.photo_with_filter_container);
        mImageView = (ImageView) findViewById(R.id.imageView);
        comment = (EditText) findViewById(R.id.message_et);
        comment.setEnabled(false);
                poss=0;
        screenwidth = 0;
        mAddTag = (ImageButton) findViewById(R.id.add_tag);
        mAddTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTagToPost(v);
            }
        });


        //kmc
        if(SharedpreferenceUtility.getInstance(PhotoActivty.this).getString(AppConstants.COLLEGE_ID).equals("5749882031702016")) {
            filters = new LayerDrawable[9];
        }else{
            filters = new LayerDrawable[7];
        }

        ArrayList<TrendingTags> ttList = new ArrayList<TrendingTags>();

        tagListAdapter = new TagArrayAdapter(PhotoActivty.this, ttList);



        camera = (FloatingActionButton) findViewById(R.id.fab);
        server = (FloatingActionButton) findViewById(R.id.fab_second);
        server.setVisibility(View.GONE);
        photofilter_container.setVisibility(View.GONE);

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            ex.printStackTrace();
            // Error occurred while creating the File
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            mImageUri = Uri.fromFile(photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                    mImageUri);
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
//
//        camera.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Log.d("Camera", "Here");
//
//                }
//            });

    }


    public void addTagToPost(View view) {
        LayoutInflater li = LayoutInflater.from(PhotoActivty.this);
        View promptsView = li.inflate(R.layout.tag_dialog, null);

        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(PhotoActivty.this);
        alertDialogBuilder.setView(promptsView);

        final TextView title = (TextView) promptsView.findViewById(R.id.tag1);
        title.setText("Trending Tags");
        final ListView filterList = (ListView) promptsView.findViewById(R.id.tag_list);

        // When item is tapped, toggle checked properties of CheckBox and tags.
        filterList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View item,
                                    int position, long id) {
                TrendingTags tag = tagListAdapter.getItem(position);
                tag.toggleChecked();
                TagViewHolder viewHolder = (TagViewHolder) item.getTag();
                viewHolder.getCheckBox().setChecked(tag.isChecked());
            }
        });


        // Create and populate tags..
//        tags = (TrendingTags[]) PhotoActivty.this.getLastNonConfigurationInstance();
        if (tags == null) {
            List<ModelsHashTag> getFromDb=db.getAllHashTags();

            tags = new TrendingTags[getFromDb.size()];

            for(int i=getFromDb.size()-1;i>=0;i--){
                String name=getFromDb.get(i).getName().replace(" ","");
                String timestamp = getFromDb.get(i).getStartTime();
                tags[getFromDb.size()-i-1]=new TrendingTags(name,timestamp);
            }
        }

        ArrayList<TrendingTags> ttList = new ArrayList<TrendingTags>();
        ttList.addAll(Arrays.asList(tags));

        // Set our custom array adapter as the ListView's adapter.
        tagListAdapter = new
                TagArrayAdapter(PhotoActivty.this, ttList);
        filterList.setAdapter(tagListAdapter);

        //subscribeWithPresence();
        //history();

        currentTags.clear();
        sendTags="";


        if(tags.length>0){
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                int count = 0;

                                for (int j = 0; j < tags.length; j++) {
                                    if (tags[j].isChecked() == true) {
                                            count++;
                                            currentTags.add(tags[j]);
                                            sendTags=sendTags+"#"+tags[j];


                                        String tag = "";
                                        ArrayList<TrendingTags> copyCurrentTemp = new ArrayList<TrendingTags>();
                                        copyCurrentTemp = currentTags;
                                        if (copyCurrentTemp != null && copyCurrentTemp.size() != 0) {
                                            for (int i = 0; i < copyCurrentTemp.size(); i++) {
                                                tag += ("#" + copyCurrentTemp.get(i).getName());
                                            }

                                            String intermediateMsg = comment.getText().toString();
                                            intermediateMsg+=" "+tag+" ";
                                            comment.setText(intermediateMsg);
                                            comment.setSelection(comment.getText().toString().length());
                                            copyCurrentTemp.clear();
                                        }
                                    }

                                }
                            }
                        })

                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        }else{
            Toast.makeText(PhotoActivty.this,"Start a trend",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        // put your code here...
        if(mImageView.getDrawable()==null){



        }else{

            String imageUrl = SharedpreferenceUtility.getInstance(PhotoActivty.this).getString("ImageLiveUpload");


            camera.setImageResource(R.mipmap.send_icon);
            server.setVisibility(View.GONE);
            photofilter_container.setVisibility(View.VISIBLE);



            camera.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String imageUrl=SharedpreferenceUtility.getInstance(PhotoActivty.this).getString("ImageLiveUpload");

                    SharedpreferenceUtility.getInstance(PhotoActivty.this).putString("ImageLiveUpload", "");

                    ModelsLiveFeed modelsLiveFeed = new ModelsLiveFeed();

                    String collegeId = SharedpreferenceUtility.getInstance(v.getContext()).getString(AppConstants.COLLEGE_ID);
                    modelsLiveFeed.setCollegeId(collegeId);
                    Date cDate = new Date();
                    String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cDate);

                    modelsLiveFeed.setTimestamp(timestamp);


                    modelsLiveFeed.setImageUrl(imageUrl);

                    String personUrl = SharedpreferenceUtility.getInstance(v.getContext()).getString(AppConstants.PHOTO_URL);
                    modelsLiveFeed.setPersonPhotoUrl(personUrl);



                    modelsLiveFeed.setTags(sendTags);
                    modelsLiveFeed.setDescription(comment.getText().toString());

                    modelsLiveFeed.setName(SharedpreferenceUtility.getInstance(v.getContext()).getString(AppConstants.PERSON_NAME));



                    Call commentPost = mCCService.liveComments(modelsLiveFeed);
                    commentPost.enqueue(callBack);

                    finish();


                }
            });

        }
    }


    // region Callbacks
    private Callback<Void> callBack = new Callback<Void>() {
        @Override
        public void onResponse(Response<Void> response, Retrofit retrofit) {

        }

        @Override
        public void onFailure(Throwable t) {
            //Timber.d("onFailure() : mQuery - " + mQuery);

        }
    };




    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        Log.d("image",image.length()+"");
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            this.grabImage();
        }
    }

    public void grabImage()
    {
        this.getContentResolver().notifyChange(mImageUri, null);
        ContentResolver cr = this.getContentResolver();
        try
        {
            bitmap = MediaStore.Images.Media.getBitmap(cr, mImageUri); //image from camera
            Log.d("bitmap",bitmap.getHeight()+" "+bitmap.getWidth());
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int height = displaymetrics.heightPixels;
            int width = displaymetrics.widthPixels;
            screenwidth = width;
            scaleImageKeepAspectRatio();
            Log.d("bitmap",bitmap.getHeight()+" "+bitmap.getWidth());
            Gallery.bitmap=bitmap;


            //kmc
            if(SharedpreferenceUtility.getInstance(PhotoActivty.this).getString(AppConstants.COLLEGE_ID).equals("5749882031702016")) {
//                filters = new LayerDrawable[9];
                String fileName;
                for(int i = 0;i<9;i++) {
                    if(i==0||i==1) {
                        fileName = "cc" + i;
                    }else{
                        //i=2, kmc1
                        fileName = "kmc" + (i-1);
                    }
                    int resID= getApplicationContext().getResources().getIdentifier(fileName,"drawable",getApplicationContext().getPackageName());
                    filter = decodeSampledBitmapFromResource(getResources(), resID, bitmap.getWidth(),bitmap.getHeight());

                    layers = new Drawable[2];
                    layers[0] = new BitmapDrawable(getResources(),bitmap);
                    layers[1] = new BitmapDrawable(getResources(),filter);
                    layerDrawable = new LayerDrawable(layers);
                    filters[i]=layerDrawable;
                }

            }else{
//                filters = new LayerDrawable[7];
                String fileName;
                for(int i = 0;i<7;i++) {
                    if(i==0||i==1) {
                        fileName = "cc" + i;
                    }else{
                        //i=2, kmc1
                        fileName = "nitk" + (i-1);
                    }
                    int resID= getApplicationContext().getResources().getIdentifier(fileName,"drawable",getApplicationContext().getPackageName());
                    filter = decodeSampledBitmapFromResource(getResources(), resID, bitmap.getWidth(),bitmap.getHeight());

                    layers = new Drawable[2];
                    layers[0] = new BitmapDrawable(getResources(),bitmap);
                    layers[1] = new BitmapDrawable(getResources(),filter);
                    layerDrawable = new LayerDrawable(layers);
                    filters[i]=layerDrawable;
                }

            }



            Intent i = new Intent(PhotoActivty.this,Gallery.class);
            startActivity(i);
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
            Log.d("vav", "Failed to load", e);
        }
    }


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
    public void scaleImageKeepAspectRatio()
    {
        int imageWidth = bitmap.getWidth();
        int imageHeight = bitmap.getHeight();
        int newHeight = (imageHeight * screenwidth)/imageWidth;
        bitmap = Bitmap.createScaledBitmap(bitmap, screenwidth, newHeight, false);

    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
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


}