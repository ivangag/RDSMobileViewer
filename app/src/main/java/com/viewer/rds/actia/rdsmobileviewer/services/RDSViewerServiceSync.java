package com.viewer.rds.actia.rdsmobileviewer.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.Debug;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.viewer.rds.actia.rdsmobileviewer.ResultOperation;
import com.viewer.rds.actia.rdsmobileviewer.utils.DownloadRequestSchema;
import com.viewer.rds.actia.rdsmobileviewer.VehicleCustom;
import com.viewer.rds.actia.rdsmobileviewer.IRDSGetCall;
import com.viewer.rds.actia.rdsmobileviewer.utils.DownloadUtility;

import java.util.List;

public class RDSViewerServiceSync extends Service {

    /**
     * Logging tag.
     */
    private final static String TAG =
            RDSViewerServiceSync.class.getCanonicalName();

    public RDSViewerServiceSync() {
    }

    /**
     * Object that can invoke HTTP GET requests on URLs.
     */
    private final static AndroidHttpClient mClient
            = AndroidHttpClient.newInstance("");


    /**
     * Factory method that makes an Intent used to start the
     * RDSViewerServiceSync when passed to bindService().
     *
     * @param context
     *            The context of the calling component.
     */
    public static Intent makeIntent(Context context) {
        return new Intent(context,
                RDSViewerServiceSync.class);
    }

    /**
     * Called when a client (e.g., RDSMobileActivity) calls
     * bindService() with the proper Intent.  Returns the
     * implementation of RDSViewerCall, which is implicitly cast as an
     * IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {

        return mRDSServiceCallImpl;
    }


    IRDSGetCall.Stub mRDSServiceCallImpl = new IRDSGetCall.Stub() {
        /**
         * Implement the AIDL AcronymCall expandAcronym() method,
         * which forwards to DownloadUtils getResults() to obtain
         * the results from the Acronym Web service and then
         * returns the results back to the Activity.
         */
        @Override
        public List<VehicleCustom> getVehiclesNotActivated()
                throws RemoteException {


            //final AndroidHttpClient client = getHttpClient();
            //Debug.waitForDebugger();
            // Call the Acronym Web service to get the list of
            // possible expansions of the designated acronym.
           ResultOperation resOp =
                   DownloadUtility.FetchingRemoteData(mClient, DownloadRequestSchema.newInstance(DownloadUtility.DownloadRequestType.VEHICLE_NOT_TRUSTED, false));

            final List<VehicleCustom> res = (List<VehicleCustom>) resOp.getClassReturn();
            Log.d(TAG, "" + "otherInfo:" + resOp.getOtherInfo() + ";"
                    + "status:" + resOp.isStatus() + " results for getVehiclesNotActivated: ");

            // Return the list of acronym expansions back to the
            // AcronymActivity.
            return res;
        }
    };


    public void onDestroy() {
        mClient.close();
        super.onDestroy();
    }

    /*
    public AndroidHttpClient getHttpClient() {
        if(mClient == null)
            mClient = AndroidHttpClient.newInstance("");
        return mClient;
    }
    */
}
