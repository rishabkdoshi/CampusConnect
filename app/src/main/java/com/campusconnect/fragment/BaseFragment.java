package com.campusconnect.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.campusconnect.CCApplication;


import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import timber.log.Timber;

/**
 * Created by etiennelawlor on 6/13/15.
 */
public abstract class BaseFragment extends Fragment {

    // region Member Variables
    protected List<Call> mCalls;
    // endregion

    // region Lifecycle Methods
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate()");

        mCalls = new ArrayList<>();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Timber.d("onViewCreated()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Timber.d("onStop()");
    }

    @Override
    public void onResume(){
        super.onResume();
        Timber.d("onResume()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Timber.d("onDestroyView()");

        String className = this.getClass().toString();
        Timber.d("onDestroyView() : className - "+ className);
        if(this instanceof CampusFeedFragment){
            String query = ((CampusFeedFragment)this).getQuery();
            Timber.d("onDestroyView() : query - "+ query);
        }

        Timber.d("onDestroyView() : mCalls.size() - " + mCalls.size());

        for(final Call call : mCalls){
            Timber.d("onDestroyView() : call.cancel() - "+call.toString());

            try {
                call.cancel();
            } catch (NetworkOnMainThreadException e){
                Timber.d("onDestroyView() : NetworkOnMainThreadException thrown");
                e.printStackTrace();
            }

//            new CancelTask().execute(call);

//            OkHttpClient client = new OkHttpClient();
//            client.getDispatcher().getExecutorService().execute(new Runnable() {
//                @Override
//                public void run() {
//                    call.cancel();
//                }
//            });
        }

        mCalls.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

//        RefWatcher refWatcher = CCApplication.getRefWatcher(getActivity());
//        refWatcher.watch(this);
    }
    // region

    // region Inner Classes

    private class CancelTask extends AsyncTask<Call, Void, Void >{
        @Override
        protected Void doInBackground(Call... params) {
            Timber.d("doInBackground() : call.cancel()");

            Call call = params[0];
            call.cancel();

            return null;
        }
    }

    // endregion
}
