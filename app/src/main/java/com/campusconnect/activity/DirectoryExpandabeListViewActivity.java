package com.campusconnect.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.campusconnect.R;
import com.campusconnect.adapter.PlacesExpandableListAdapter;
import com.campusconnect.constant.AppConstants;
import com.campusconnect.utility.SharedpreferenceUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by RK on 05/11/2015.
 */
public class DirectoryExpandabeListViewActivity extends ActionBarActivity {

    @Bind(R.id.lvExp_places)
    ExpandableListView list_places;
    @Bind(R.id.cross_button)
    LinearLayout close;
    @Bind(R.id.tv_about)
    TextView about_text;

    PlacesExpandableListAdapter placesAdapter;
    List<String> placesDataHeader;
    HashMap<String, List<String>> placesDataChild;

    Typeface r_med;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory);
        ButterKnife.bind(this);

        r_med = Typeface.createFromAsset(getAssets(), "font/Roboto_Medium.ttf");
        about_text.setTypeface(r_med);

        //Setting up the list
        if (SharedpreferenceUtility.getInstance(this).getString(AppConstants.COLLEGE_ID).equals("4847453903781888") || SharedpreferenceUtility.getInstance(DirectoryExpandabeListViewActivity.this).getBoolean(AppConstants.IS_INCI_GUEST)){
            prepareListDataNITK();
        }else if(SharedpreferenceUtility.getInstance(this).getString(AppConstants.COLLEGE_ID).equals("5749882031702016")){
            prepareListDataKMC();
        }

        placesAdapter = new PlacesExpandableListAdapter(this, placesDataHeader, placesDataChild);
        list_places.setAdapter(placesAdapter);

        if (SharedpreferenceUtility.getInstance(this).getString(AppConstants.COLLEGE_ID).equals("4847453903781888")){
            list_places.expandGroup(0);
            list_places.expandGroup(1);
            list_places.expandGroup(2);
        }else if(SharedpreferenceUtility.getInstance(this).getString(AppConstants.COLLEGE_ID).equals("5749882031702016")){
            list_places.expandGroup(0);
        }

        

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

    }

    /*
     * Preparing the list data
     */
    private void prepareListDataNITK() {
        placesDataHeader = new ArrayList<String>();
        placesDataChild = new HashMap<String, List<String>>();

        // Adding child data
        placesDataHeader.add("Important Contacts:");
        placesDataHeader.add("Food Joints:");
        placesDataHeader.add("Utilities:");

        // Adding child data
        List<String> iImportantContacts = new ArrayList<String>();
        iImportantContacts.add("Beach\n6:00am to 7:00am & 5:00pm to 7:00pm");
        iImportantContacts.add("Library\n8:00am to 8:00pm (Mon to Sat)\n8:00am to 12:00pm (Sun)");
        iImportantContacts.add("A1\n9:30am to 7:30pm");
        iImportantContacts.add("Padmavathi Hospital\n8242477176");
        iImportantContacts.add("Ambulance\n9880375287, 8105520077");

        List<String> foodJoints = new ArrayList<String>();
        foodJoints.add("Food court\n7:30am to 9:30pm");
        foodJoints.add("Nandini\n8:00am to 10:00pm\n9449334104");
        foodJoints.add("Crumbz\n9:30am to 08:30pm\n08242475080");
        foodJoints.add("3rd Block NC\n9740687393");
        foodJoints.add("7th block NC\n7090689469, 8861995818");
        foodJoints.add("8th block NC\n8971146628, 9019216221");
        foodJoints.add("GB NC\n7760921074, 7760921064");
        foodJoints.add("Amul Football\n7:30am to 9:00pm\n9964964321");

        List<String> utilities = new ArrayList<String>();
        utilities.add("Health Care Centre\n9:00am to 6:00pm (Mon to Sat)\n9:00am to 1:00pm (Sun)\nLunch break 1:00pm to 2:00pm");
        utilities.add("Gym\n5:30am to 8:00pm & 4:30pm to 8:00pm (Mon to Sat)\n5:30am to 8:00am (Sun)");
        utilities.add("Men’s Saloon\n8:00am to 8:30pm\nLunch 1pm to 2pm\n(Tue holiday)");
        utilities.add("Mudrika\n9:00am to 5:30pm (Mon to Fri)\n10:00am to 5:00pm (Sat)");
        utilities.add("S.L Laundry\n9:00am to 10:00pm\n7204231287, 9449581122");
        utilities.add("Boys Co-op\n10:00am to 7:30pm\n(Sat & Sun holiday)");
        utilities.add("Girls Co-op\n9:30am to 1:00pm & 4:00pm to 7:30pm");
        utilities.add("Reddy’s Tours & Travels\n9980503090");
        utilities.add("Swimming\n1st Batch 6am to 6:40am (Girls)\n2nd Batch 6:50am to 7:30pm (General)\n3rd Batch 4:00pm to 4:40pm (Students)\n"+
                      "4th Batch 4:45pm to 5:25pm (Students)\n5th Batch 5:30pm to 6:10pm (Students)\n6th Batch 6:15pm to 6:55pm (Learners)"+
                      "7th Batch 7:05pm to 7:45pm (Girls)\nAdditional batches on Sat, Sun & General holidays 8:05am to 8:45am & 3:15pm to 3:55pm\nTue holiday");

        placesDataChild.put(placesDataHeader.get(0), iImportantContacts); // Header, Child data
        placesDataChild.put(placesDataHeader.get(1), foodJoints);
        placesDataChild.put(placesDataHeader.get(2), utilities);
    }

    private void prepareListDataKMC() {
        placesDataHeader = new ArrayList<String>();
        placesDataChild = new HashMap<String, List<String>>();

        // Adding child data
        placesDataHeader.add("Important Contacts:");

        // Adding child data
        List<String> iImportantContacts = new ArrayList<String>();
        iImportantContacts.add("Ujwal Suvarna - President\n9743252420");
        iImportantContacts.add("Prateek Prasoon - General Secretary\n9945344682");
        iImportantContacts.add("Archana Ganapathy - Joint Secretary\n9611470174");
        iImportantContacts.add("Loomila Joe- Lady Representative\n9980578787");
        iImportantContacts.add("Arjun Tandon- Cultural Secretary\n9986073650");
        iImportantContacts.add("Ujjwala Raina- Sports Secretary\n8197573557");
        iImportantContacts.add("Shatadru Seth- Editor\n9902904839");
        iImportantContacts.add("Dr Rekha Thapar - Deputy Director of Student Affairs\n9449082214");

        
        placesDataChild.put(placesDataHeader.get(0), iImportantContacts); // Header, Child data
    }
}
