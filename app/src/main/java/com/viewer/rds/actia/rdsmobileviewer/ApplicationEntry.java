package com.viewer.rds.actia.rdsmobileviewer;

import android.app.Application;

import com.viewer.rds.actia.rdsmobileviewer.utils.CacheDataManager;
import com.viewer.rds.actia.rdsmobileviewer.utils.DownloadManager;
import com.viewer.rds.actia.rdsmobileviewer.volley.VolleyRequestManager;

/**
 * Created by igaglioti on 29/07/2014.
 * This class extends whole Application and gives a convenient way to
 * have an unique entry/exit point for general objects initialization
 */
public final class ApplicationEntry extends Application {

    @Override
    public void onTerminate() {
        VolleyRequestManager.getInstance(this).getRequestQueue().stop();
        DownloadManager.getInstance().unbindRDSService(this);
        super.onTerminate();
    }

    @Override
    public void onCreate() {
        //DownloadManager.get().startRDService(this);
        VolleyRequestManager.getInstance(this).getRequestQueue().start();
        DownloadManager.getInstance().bindRDService(this);
        CacheDataManager.get().setContext(this);
        super.onCreate();
    }
}
