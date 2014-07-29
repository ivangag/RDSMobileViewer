package com.viewer.rds.actia.rdsmobileviewer;

import android.app.Application;

import com.viewer.rds.actia.rdsmobileviewer.utils.DownloadUtility;

/**
 * Created by igaglioti on 29/07/2014.
 */
public class ApplicationEx extends Application {

    @Override
    public void onTerminate() {
        DownloadUtility.getInstance().stopRDSService(this);
        super.onTerminate();
    }

    @Override
    public void onCreate() {
        DownloadUtility.getInstance().startRDService(this);
        super.onCreate();
    }
}
