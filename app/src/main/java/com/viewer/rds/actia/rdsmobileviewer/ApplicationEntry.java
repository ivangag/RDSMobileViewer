package com.viewer.rds.actia.rdsmobileviewer;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.viewer.rds.actia.rdsmobileviewer.utils.CacheDataManager;
import com.viewer.rds.actia.rdsmobileviewer.utils.DownloadRDSManager;
import com.viewer.rds.actia.rdsmobileviewer.volley.VolleyRequestManager;

/**
 * Created by igaglioti on 29/07/2014.
 * This class extends whole Application and gives a convenient way to
 * have an unique entry/exit point for general objects initialization
 */
public final class ApplicationEntry extends Application {

    @Override
    public void onTerminate() {
        super.onTerminate();
        VolleyRequestManager.getInstance(this).getRequestQueue().stop();
        DownloadRDSManager.getInstance().unbindRDSService(this);
        ActiveAndroid.dispose();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //DownloadManager.get().startRDService(this);
        VolleyRequestManager.getInstance(this).getRequestQueue().start();
        DownloadRDSManager.getInstance().bindRDService(this);
        CacheDataManager.get().setContext(this);
        ActiveAndroid.initialize(this);
    }
}
