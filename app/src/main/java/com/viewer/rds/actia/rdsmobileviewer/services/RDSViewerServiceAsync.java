package com.viewer.rds.actia.rdsmobileviewer.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import com.viewer.rds.actia.rdsmobileviewer.DownloadRequestSchema;
import com.viewer.rds.actia.rdsmobileviewer.IRDSClientResponse;
import com.viewer.rds.actia.rdsmobileviewer.IRDSClientRequest;
import com.viewer.rds.actia.rdsmobileviewer.ResultOperation;
import com.viewer.rds.actia.rdsmobileviewer.db.RDSDBMapper;
import com.viewer.rds.actia.rdsmobileviewer.utils.DownloadRDSManager;

public class RDSViewerServiceAsync extends Service {
    private static RDSDBMapper mRDSDBMapper;

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
            Log.d(TAG, "fetching remote data started..");
            //Debug.waitForDebugger();
            mRDSDBMapper = RDSDBMapper.get(getApplicationContext());
            mRDSDBMapper.open();
            ResultOperation resOp = ResultOperation.newInstance();


            final boolean mObtainCacheIfExist = downloadRequest.getCacheOption();

            boolean mHasCacheData = false;

            if(!mHasCacheData) {
                Log.d(TAG, "mHasCacheData: " + mHasCacheData);
                resOp = DownloadRDSManager.FetchingRemoteData(mClient, downloadRequest, false);
                Log.d(TAG, "FetchingRemoteData: " + resOp.isStatus());
            }

            Log.d(TAG, "fetching remote data finished..:" + resOp.isStatus());

            //traceParcelDataSize(downloadRequest, resOp);

            if(resOp.isStatus()){
                final String jsonRaw = new String((byte[]) resOp.getClassReturn());
                final String uuid = mRDSDBMapper.saveDownloadToRepository(downloadRequest, jsonRaw);
                //final String uuid = CacheDataManager.get().saveDownloadRepository(downloadRequest, jsonRaw);
                if(uuid != "") {
                    resOp.setClassReturn(uuid);
                    Log.d(TAG,"saving json download data OK: " + uuid);
                }
                else {
                    resOp.setClassReturn(null);
                    resOp.setStatus(false);
                    resOp.setOtherInfo(TAG + " :saving json download data failed!");
                    Log.e(TAG,"saving json download data failed!");
                }
            }
            else{
                resOp.setClassReturn(null);
            }

            // Invoke a one-way callback to send remote data result info
            callback.sendRemoteDataResult(resOp,downloadRequest);
        }
    };

    private void traceParcelDataSize(DownloadRequestSchema downloadRequest, ResultOperation resOp) {
        Parcel _data = Parcel.obtain();
        resOp.writeToParcel(_data, 0);
        downloadRequest.writeToParcel(_data, 0);

        Log.d(TAG, "DownloadRequest: "
                        + downloadRequest.getDownloadRequestType()
                        + " Status: " + resOp.isStatus()
                        + "OtherInfo: " + resOp.getOtherInfo()
                        + "DataSizeRPC: " + String.valueOf(_data.dataSize())
        );
    }


    public static Intent makeIntent(Context context) {
        return new Intent(context,
                RDSViewerServiceAsync.class);
    }

    public void onDestroy() {
        Log.i(TAG,"onDestroy");
        mClient.close();
        mRDSDBMapper.close();
        super.onDestroy();
    }
}
