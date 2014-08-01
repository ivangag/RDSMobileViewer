package com.viewer.rds.actia.rdsmobileviewer.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.viewer.rds.actia.rdsmobileviewer.DownloadRequestSchema;
import com.viewer.rds.actia.rdsmobileviewer.IRDSClientResponse;
import com.viewer.rds.actia.rdsmobileviewer.IRDSClientRequest;
import com.viewer.rds.actia.rdsmobileviewer.ResultOperation;
import com.viewer.rds.actia.rdsmobileviewer.utils.DownloadManager;

public class RDSViewerServiceAsync extends Service {
    public RDSViewerServiceAsync() {
    }

    /**
     * Logging tag.
     */
    private final static String TAG =
            RDSViewerServiceAsync.class.getCanonicalName();


    /**
     * Object that can invoke HTTP GET requests on URLs.
     */
    private final static AndroidHttpClient mClient
            = AndroidHttpClient.newInstance("");

    /**
     * Hook method called each time a Started Service is sent an
     * Intent via startService().
     */
    public int onStartCommand(Intent intent,
                              int flags,
                              int startId) {

        Log.i(TAG,"onStartCommand");
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.i(TAG,"onBind");
       return mRDSRequestImpl;
    }

    IRDSClientRequest.Stub mRDSRequestImpl = new IRDSClientRequest.Stub() {
        /**
         * Implement the AIDL RDSClientRequest fetchRemoteData()
         * method, which forwards to DownloadUtils fecthRemoteDataRaw() to
         * obtain the results from the Acronym Web service and
         * then sends the results back to the Activity via a
         * callback.
         */
        @Override
        public void fetchRemoteData(IRDSClientResponse callback, DownloadRequestSchema downloadRequest) throws RemoteException {
            // Call the Acronym Web service to get the list of
            // possible expansions of the designated acronym.
            //Debug.waitForDebugger();
            ResultOperation resOp =
                    DownloadManager.FetchingRemoteData(mClient,
                            downloadRequest, false);

            Log.d(TAG, "DownloadRequest: " + downloadRequest.getDownloadRequestType() + " Status: " + resOp.isStatus() + "OtherInfo: " + resOp.getOtherInfo() );

            // Invoke a one-way callback to send list of acronym
            // expansions back to the AcronymActivity.
            callback.sendRemoteDataResult(resOp,downloadRequest);
        }
    };


    public static Intent makeIntent(Context context) {
        return new Intent(context,
                RDSViewerServiceAsync.class);
    }

    public void onDestroy() {
        Log.i(TAG,"onDestroy");
        mClient.close();
        super.onDestroy();
    }
}
