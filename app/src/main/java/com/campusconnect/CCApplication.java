package com.campusconnect;

import android.app.Application;
import android.content.Context;
import android.util.Log;

//import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;

import java.io.File;

//import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

/**
 * Created by etiennelawlor on 5/23/15.
 */
public class CCApplication extends Application {

    // region Static Variables
    private static CCApplication sCurrentApplication = null;
    // endregion


    @Override
    public void onCreate() {
        super.onCreate();


        initializeTimber();
        //initializeFlurry();

        sCurrentApplication = this;

    }

    // region Helper Methods
    public static CCApplication getInstance() {
        return sCurrentApplication;
    }

    public static File getCacheDirectory()  {
        return sCurrentApplication.getCacheDir();
    }


//    private void initializeFabric(){
//        if (!Fabric.isInitialized()) {
//            final Fabric fabric = new Fabric.Builder(this)
//                    .kits(new Crashlytics())
//                    .debuggable(true)
//                    .build();
//
//            Fabric.with(fabric);
//        }
//    }


    private void initializeTimber(){
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree() {
                // Add the line number to the tag
                @Override
                protected String createStackElementTag(StackTraceElement element) {
                    return super.createStackElementTag(element) + ":" + element.getLineNumber();
                }
            });
        } else {
            Timber.plant(new ReleaseTree());
        }
    }
//
//    private void initializeFlurry(){
//        FlurryAgent.setLogEnabled(false);
//
//        FlurryAgent.init(this, getString(R.string.flurry_api_key));
//    }
    // endregion

    // region Inner Classes

    /** A tree which logs important information for crash reporting. */
    private static class ReleaseTree extends Timber.Tree {

        private static final int MAX_LOG_LENGTH = 4000;
        @Override
        protected boolean isLoggable(int priority) {
            if(priority == Log.VERBOSE || priority == Log.DEBUG){
                return false;
            }

            // Only log WARN, INFO, ERROR, WTF
            return true;
        }

        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
//            if(isLoggable(priority)){
                //            FakeCrashLibrary.log(priority, tag, message);
//
//            if (t != null) {
//                if (priority == Log.ERROR) {
//                    FakeCrashLibrary.logError(t);
//                } else if (priority == Log.WARN) {
//                    FakeCrashLibrary.logWarning(t);
//                }
//            }

//                Crashlytics.log(priority, tag, message);
//
//                if (t != null) {
//                    if (priority == Log.ERROR) {
//                        Crashlytics.logException(t);
//                    } else if (priority == Log.INFO) {
//                        Crashlytics.log(message);
//                    }
//                }
//
//                // Message is short enough, does not need to be broken into chunks
//                if(message.length() < MAX_LOG_LENGTH){
//                    if(priority == Log.ASSERT) {
//                        Log.wtf(tag, message);
//                    } else {
//                        Log.println(priority, tag, message);
//                    }
//                    return;
//                }
//
//                // Split by line, then ensure each line can fit into Log's maximum length
//                for(int i=0, length = message.length(); i<length; i++) {
//                    int newline = message.indexOf('\n', i);
//                    newline = newline != -1 ? newline : length;
//                    do {
//                        int end = Math.min(newline, i + MAX_LOG_LENGTH);
//                        String part = message.substring(i, end);
//                        if(priority == Log.ASSERT) {
//                            Log.wtf(tag, part);
//                        } else {
//                            Log.println(priority, tag, part);
//                        }
//                        i = end;
//                    } while (i < newline);
//                }
//            }
        }
    }

    // endregion
}